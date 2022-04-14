package no.hvl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.SourceRoot;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    private AnnotationUtils annotationUtils = new AnnotationUtils();
    private List<CompilationUnit> compilationUnits;
    private Map<String, BlockStmt> solutionReplacements = new HashMap<>();
    private static final String START_COMMENT = "TODO - START";
    private static final String END_COMMENT = "TODO - END";
    private static final String IMPLEMENT_ANNOTATION_NAME = "Implement";
    private static final String SOLUTION_REPLACEMENT_ANNOTATION_NAME = "SolutionReplacement";
    private static final String SOLUTION_START_ANNOTATION_NAME = "SolutionStart";
    private static final String SOLUTION_END_ANNOTATION_NAME = "SolutionEnd";
    private static final String SOLUTION_REPLACEMENT_ANNOTATION_ID_NAME = "id";
    private static final String IMPLEMENT_ANNOTATION_ID_NAME = "replacementId";
    private static final String IMPLEMENT_ANNOTATION_COPY_NAME = "copy";

    public Parser() throws IOException {
        this.compilationUnits = new ArrayList<>();
    }

    public List<CompilationUnit> getCompilationUnits(){
        return compilationUnits;
    }

    public Map<String, BlockStmt> getSolutionReplacements() {
        return solutionReplacements;
    }

    public String getStartComment() {
        return START_COMMENT;
    }

    public String getEndComment() {
        return END_COMMENT;
    }

    public void parseDirectory(String dir) throws IOException {
        var sourceRoot = new SourceRoot(Paths.get(dir));
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");
        compilationUnits = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
    }

    public void parseFile(String filePath) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
        compilationUnits.clear();
        compilationUnits.add(cu);
    }

    public void saveSolutionReplacements(String filePath) throws FileNotFoundException{
        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
        solutionReplacements = getAllSolutionReplacementsInFile(cu);
    }

    public List<BodyDeclaration<?>> getAllAnnotatedNodes(String annotationName){
        List<BodyDeclaration<?>> allAnnotatedNodes = new ArrayList<>();
        for(CompilationUnit compilationUnit : compilationUnits){
            allAnnotatedNodes.addAll(getAnnotatedNodesInFile(compilationUnit, annotationName));
        }
        return allAnnotatedNodes;
    }

    public List<BodyDeclaration<?>> getAnnotatedNodesInFile(CompilationUnit file, String annotationName){
        List<BodyDeclaration<?>> annotatedNodes = new ArrayList<>();
        file.findAll(BodyDeclaration.class,
                bodyDeclaration -> bodyDeclaration.getAnnotationByName(annotationName).isPresent())
                .forEach(annotatedNodes::add);
        return annotatedNodes;
    }

    public Map<String, BlockStmt> getAllSolutionReplacementsInFile(CompilationUnit file){
        //TODO ERror handling
        HashMap<String, BlockStmt> solutionReplacements = new HashMap<>();
        getAnnotatedNodesInFile(file, SOLUTION_REPLACEMENT_ANNOTATION_NAME).stream()
                .filter(BodyDeclaration::isMethodDeclaration)
                .forEach(method ->
                        solutionReplacements.put(
                                getAnnotationValue(method, SOLUTION_REPLACEMENT_ANNOTATION_NAME, SOLUTION_REPLACEMENT_ANNOTATION_ID_NAME)
                                        .asStringLiteralExpr().asString()
                                , method.asMethodDeclaration().getBody().get()));
        return solutionReplacements;
    }

    public void removeAnnotationsFromFile(CompilationUnit file, String annotationName){
        var annotatedNodes = getAnnotatedNodesInFile(file, annotationName);
        annotatedNodes.forEach(node -> file.replace(node, (Node) removeAnnotationFromNode(node, annotationName)));
    }

    public NodeWithAnnotations<?> removeAnnotationFromNode(NodeWithAnnotations<?> node, String annotationName){
        NodeList<AnnotationExpr> annotations = new NodeList<>();
        for(var annotation : node.getAnnotations()){
            AnnotationExpr annotationExpr = (AnnotationExpr) annotation;
            if(annotationExpr.getName().asString().equals(annotationName)){
                continue;
            }
            annotations.add((AnnotationExpr) annotation);
        }
        node.setAnnotations(annotations);
        return node;
    }

    public void removeSolution(MethodDeclaration method){
        //TODO Errorhandling, cleaner solution,
        var started = false;
        List <Statement> statementsToRemove = new ArrayList<>();
        if(method.getBody().isPresent()){
            for(Statement statement : method.getBody().get().getStatements()){
                if(!started && isStartStatement(statement)){
                    method.getBody().get().addOrphanComment(new LineComment(statement.getTokenRange().get(), START_COMMENT));
                    statementsToRemove.add(statement);
                    started = true;
                }
                else if(started){
                    statementsToRemove.add(statement);
                    if(isEndStatement(statement)){
                        method.getBody().get().addOrphanComment(new LineComment(statement.getTokenRange().get(), END_COMMENT));
                        break;
                    }
                }
            }
           statementsToRemove.forEach(Node::remove);
        }
    }

    public void replaceSolutionInMethodBody(CallableDeclaration<?> method, String replacementId){
        //TODO preservation of comments?
        if(method.isMethodDeclaration()){
            var methodDeclaration = method.asMethodDeclaration();
            methodDeclaration.setBody(replaceSolution(methodDeclaration.getBody().get(), replacementId));
        }
        else if (method.isConstructorDeclaration()){
            var constructorDeclaration = method.asConstructorDeclaration();
            constructorDeclaration.setBody(replaceSolution(constructorDeclaration.getBody(), replacementId));
        }
    }

    private BlockStmt replaceSolution(BlockStmt methodBody, String replacementId){
        var solutionReplacement = solutionReplacements.get(replacementId).clone();
        solutionReplacement.getStatements().getFirst().get().setLineComment(START_COMMENT);
        var replacementBody = new BlockStmt();
        var isSolutionStatement = false;
        var methodHasEndStatement = false;

        for(Statement statement : methodBody.getStatements()){
            if(isStartStatement(statement)){
                isSolutionStatement = true;
                solutionReplacement.getStatements().forEach(replacementBody::addStatement);
            }

            if(isEndStatement(statement)){
                replacementBody.addOrphanComment(new LineComment(statement.getTokenRange().get(), END_COMMENT));
                isSolutionStatement = false;
                methodHasEndStatement = true;
                continue;
            }

            //Statements that should be kept
            if(!isSolutionStatement){
                replacementBody.addStatement(statement);
            }
        }

        if(!methodHasEndStatement){
            replacementBody.addOrphanComment(
                    new LineComment(methodBody.getStatements().getLast().get().getTokenRange().get(), END_COMMENT));
        }
        return replacementBody;
    }

    public void replaceBody(CallableDeclaration<?> method, String replacementId){
        if(method.isMethodDeclaration()){
            method.asMethodDeclaration().setBody(solutionReplacements.get(replacementId));
        }
        else if (method.isConstructorDeclaration()){
            method.asConstructorDeclaration().setBody(solutionReplacements.get(replacementId));
        }
    }

    public Expression getAnnotationValue(NodeWithAnnotations<?> node, String annotationName, String memberName){
        //TODO ERror handling
        var annotation = node.getAnnotationByName(annotationName).get().asNormalAnnotationExpr();
        for(MemberValuePair pair : annotation.getPairs()){
            if(pair.getName().asString().equals(memberName)){
                return pair.getValue();
            }
        }
        return null;
    }

    public boolean isStartStatement(Statement statement){
        if(statement.isExpressionStmt()){
            Expression expression = statement.asExpressionStmt().getExpression();
            if(expression.isVariableDeclarationExpr()){
                VariableDeclarationExpr variableDeclarationExpr = expression.asVariableDeclarationExpr();
                return SOLUTION_START_ANNOTATION_NAME.equals(variableDeclarationExpr.getElementType().toString());
            }
        }
        return false;
    }

    public boolean isEndStatement(Statement statement){
        if(statement.isExpressionStmt()){
            Expression expression = statement.asExpressionStmt().getExpression();
            if(expression.isVariableDeclarationExpr()){
                VariableDeclarationExpr variableDeclarationExpr = expression.asVariableDeclarationExpr();
                return SOLUTION_END_ANNOTATION_NAME.equals(variableDeclarationExpr.getElementType().toString());
            }
        }
        return false;
    }

    public CompilationUnit modifyAllAnnotatedNodesInFile(CompilationUnit file, String annotationName){
        var annotatedNodes = getAnnotatedNodesInFile(file, annotationName);
        annotatedNodes.forEach(this::modifyAnnotatedNode);
        List<ImportDeclaration> importDeclarations = List.copyOf(file.getImports());
        importDeclarations.forEach(importDeclaration -> {
            if(importDeclaration.getName().getQualifier().isPresent()
                && importDeclaration.getName().getQualifier().get()
                    .equals(annotationUtils.getAnnotationsPackageName())){
                importDeclaration.remove();

            }
        });
        return file;
    }

    public void modifyAnnotatedNode(BodyDeclaration<?> annotatedNode){
        var copyValueExpression = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_COPY_NAME);
        var copyValue = Copy.getCopy(copyValueExpression.asFieldAccessExpr().getNameAsString());
        switch (copyValue){
            case REPLACE_SOLUTION -> {
                // TODO error handling
                // TODO handle rest of cases
                // TODO create test
                var id = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_ID_NAME);
                replaceSolutionInMethodBody(castToCallableDeclaration(annotatedNode), id.asStringLiteralExpr().asString());
            }
            case REPLACE_BODY -> {
                var id = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_ID_NAME);
                replaceBody((MethodDeclaration) annotatedNode, id.asStringLiteralExpr().asString());
            }
            case REMOVE_EVERYTHING -> {
                annotatedNode.remove();
            }
        }
        removeAnnotationFromNode(annotatedNode, IMPLEMENT_ANNOTATION_NAME);
    }

    private CallableDeclaration<?> castToCallableDeclaration(BodyDeclaration<?> bodyDeclaration){
        if(bodyDeclaration.isMethodDeclaration()){
            return bodyDeclaration.asMethodDeclaration();
        }else{
            return bodyDeclaration.asConstructorDeclaration();
        }
    }

    public void print(){
        compilationUnits.forEach(compilationUnit -> compilationUnit.getTypes()
                .forEach(typeDeclaration -> System.out.println(typeDeclaration.toString())));
    }

}
