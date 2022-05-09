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
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.SourceRoot;
import no.hvl.annotations.Copy;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Task;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private AnnotationUtils annotationUtils = new AnnotationUtils();
    private List<CompilationUnit> compilationUnits;
    private Map<String, BlockStmt> solutionReplacements = new HashMap<>();
    private HashSet<ImportDeclaration> solutionReplacementsImports = new HashSet<>();
    private HashSet<String> fileNamesToRemove = new HashSet<>();
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
        List<CompilationUnit> compilationUnitCopies = new ArrayList<>();
        compilationUnits.forEach(compilationUnit -> compilationUnitCopies.add(compilationUnit.clone()));
        return compilationUnitCopies;
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

    public HashSet<String> getFileNamesToRemove() {
        return fileNamesToRemove;
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

    public File findSourceDirectory(String dir){
        File projectDir = new File(dir);
        for(File file : projectDir.listFiles()){
            if("src".equalsIgnoreCase(file.getName())
            || "source".equalsIgnoreCase(file.getName())){
                return file;
            }
        }
        return projectDir;
    }

    public void saveSolutionReplacements(String filePath) throws FileNotFoundException{
        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));
        removeAnnotationImportsFromFile(cu);

        solutionReplacementsImports.addAll(cu.getImports());
        solutionReplacements = getAllSolutionReplacementsInFile(cu);
    }

    public void saveSolutionReplacements(List<CompilationUnit> files){
        for(CompilationUnit file : files){
            var solutionReplacementsInFile = getAllSolutionReplacementsInFile(file);
            if(!solutionReplacementsInFile.isEmpty()){
                solutionReplacements.putAll(solutionReplacementsInFile);
                removeAnnotationImportsFromFile(file);
                //TODO make hashmap so imports dont get added unless needed?
                solutionReplacementsImports.addAll(file.getImports());
            }
        }
    }

    public List<BodyDeclaration<?>> getAllAnnotatedNodesInFiles(List<CompilationUnit> files, String annotationName){
        List<BodyDeclaration<?>> allAnnotatedNodes = new ArrayList<>();
        for(CompilationUnit compilationUnit : files){
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
            if(annotation.getName().asString().equals(annotationName)){
                continue;
            }
            annotations.add(annotation);
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
        annotatedNodes.forEach(annotatedNode -> modifyAnnotatedNode(file, annotatedNode));
        removeAnnotationImportsFromFile(file);
        return file;
    }

    public void modifyAnnotatedNode(CompilationUnit file, BodyDeclaration<?> annotatedNode){
        var copyValueExpression = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_COPY_NAME);
        var copyValue = Copy.getCopy(copyValueExpression.asFieldAccessExpr().getNameAsString());
        switch (copyValue){
            case REPLACE_SOLUTION -> {
                // TODO error handling
                // TODO handle rest of cases
                // TODO create test
                solutionReplacementsImports.forEach(file::addImport);
                var id = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_ID_NAME);
                replaceSolutionInMethodBody(castToCallableDeclaration(annotatedNode), id.asStringLiteralExpr().asString());
            }
            case REPLACE_BODY -> {
                solutionReplacementsImports.forEach(file::addImport);
                var id = getAnnotationValue(annotatedNode, IMPLEMENT_ANNOTATION_NAME, IMPLEMENT_ANNOTATION_ID_NAME);
                replaceBody((MethodDeclaration) annotatedNode, id.asStringLiteralExpr().asString());
            }
            case REMOVE_EVERYTHING -> {
                annotatedNode.remove();
            }
        }
        removeAnnotationFromNode(annotatedNode, IMPLEMENT_ANNOTATION_NAME);
    }

    private void removeAnnotationImportsFromFile(CompilationUnit file){
        List<ImportDeclaration> importDeclarations = List.copyOf(file.getImports());
        importDeclarations.forEach(importDeclaration -> {
            if(importDeclaration.getName().getQualifier().isPresent()
                    && importDeclaration.getName().getQualifier().get()
                    .equals(annotationUtils.getAnnotationsPackageName())){
                importDeclaration.remove();

            }
        });
    }

    private CallableDeclaration<?> castToCallableDeclaration(BodyDeclaration<?> bodyDeclaration){
        if(bodyDeclaration.isMethodDeclaration()){
            return bodyDeclaration.asMethodDeclaration();
        }else{
            return bodyDeclaration.asConstructorDeclaration();
        }
    }

    public List<CompilationUnit> createStartCodeProject(){
        var files = getCompilationUnits();
        saveSolutionReplacements(files);
        var nodesToRemove = getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        removeNodes(files, nodesToRemove);
        return files.stream()
                .map(cu -> modifyAllAnnotatedNodesInFile(cu, AnnotationNames.IMPLEMENT_NAME))
                .collect(Collectors.toList());
    }

    public List<CompilationUnit> createSolutionProject(){
        var files = getCompilationUnits();
        var nodesToRemove = getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        removeNodes(files, nodesToRemove);
        var annotatedNodes = getAllAnnotatedNodesInFiles(files, AnnotationNames.IMPLEMENT_NAME);
        for(var annotatedNode : annotatedNodes){
            removeAnnotationFromNode(annotatedNode, IMPLEMENT_ANNOTATION_NAME);
        }
        for(CompilationUnit file : files){
            removeAnnotationImportsFromFile(file);
            removeSolutionStartAndEndStatementsFromFile(annotatedNodes);
        }
        return files;
    }

    private void removeSolutionStartAndEndStatementsFromFile(List<BodyDeclaration<?>> annotatedNodes) {
        List<Statement> statements = new ArrayList<>();
        annotatedNodes.forEach(node -> statements.addAll(node.findAll(Statement.class,
                statement -> isStartStatement(statement) || isEndStatement(statement))));
        statements.forEach(Node::remove);
    }

    private void removeNodes(List<CompilationUnit> files, List<BodyDeclaration<?>> nodesToRemove){
        nodesToRemove.forEach(node -> {
            if(node.isTypeDeclaration()){
                var compilationUnitMaybe = node.findCompilationUnit();
                if(compilationUnitMaybe.isPresent()){
                    fileNamesToRemove.add(compilationUnitMaybe.get().getStorage().get().getFileName());
                    files.remove(compilationUnitMaybe.get());
                }
            }else{
                node.remove();
            }
        });
    }

    public List<Exercise> getExercises(){
        List<BodyDeclaration<?>> tasks = getAllAnnotatedNodesInFiles(getCompilationUnits(), AnnotationNames.IMPLEMENT_NAME);
        tasks.sort((o1, o2) -> {
            var o1number = annotationUtils.getTaskNumber(o1).get();
            var o2number = annotationUtils.getTaskNumber(o2).get();
            for(int i = 0; i < o1number.length; i++){
                if(i >= o2number.length){
                    return 1;
                }
                int comparison = Integer.compare(o1number[i], o2number[i]);
                if(comparison != 0){
                    return comparison;
                }
            }
            if(o1number.length == o2number.length){
                return 0;
            }
            return -1;
        });

        List<Exercise> exercises = new ArrayList<>();
        tasks.forEach(task -> {
            int[] number = annotationUtils.getTaskNumber(task).get();
            var exercise = findExercise(number, 0, exercises);
            exercise.setFullNumberAsString(exercise.convertNumberArrayToString(number));
            var exerciseTask = new Task(task);
            exerciseTask.setFullNumberAsString(exerciseTask.convertNumberArrayToString(number, exercise.getTasks().size() + 1));
            exercise.addTask(exerciseTask);
            //TODO handle exceptions
            exercise.setFile(task.findCompilationUnit().get());
        });
        return exercises;
    }

    private Exercise findExercise(int[] number, int index, List<Exercise> exercises){
        for(Exercise exercise : exercises){
            if(exercise.getNumber() == number[index]){
                if(index == number.length - 1){
                    return exercise;
                }
                return findExercise(number, ++index, exercise.getSubExercises());
            }
        }
        var exercise = new Exercise(number[index]);
        exercises.add(exercise);
        if(index == number.length - 1){
            return exercise;
        }
        return findExercise(number, ++index, exercise.getSubExercises());

    }

}
