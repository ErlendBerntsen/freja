package no.hvl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.SourceRoot;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.concepts.builders.AssignmentMetaModelBuilder;
import no.hvl.exceptions.NoSourceDirectoryException;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.NodeUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private List<CompilationUnit> compilationUnits;
    private HashMap<String, Replacement> codeReplacements;
    private HashSet<ImportDeclaration> codeReplacementImports;
    private HashSet<String> fileNamesToRemove = new HashSet<>();
    private String directory;
    private static final String START_COMMENT = "TODO - START";
    private static final String END_COMMENT = "TODO - END";

    public Parser() {
        this.compilationUnits = new ArrayList<>();
    }

    public Parser(String directory) {
        this.compilationUnits = new ArrayList<>();
        this.directory = directory;
    }

    public List<CompilationUnit> getCompilationUnitCopies(){
        List<CompilationUnit> compilationUnitCopies = new ArrayList<>();
        compilationUnits.forEach(compilationUnit -> compilationUnitCopies.add(compilationUnit.clone()));
        return compilationUnitCopies;
    }

    public void createAssignmentMetaModel(){
        AssignmentMetaModel assignmentMetaModel = new AssignmentMetaModelBuilder(this).build();
        codeReplacements = assignmentMetaModel.getReplacementsAsHashMap();
        codeReplacementImports = assignmentMetaModel.getReplacementImportDeclarations();
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

    public HashMap<String, Replacement> getCodeReplacements() {
        return codeReplacements;
    }

    public void parseDirectory(String dir) throws IOException {
        var sourceRoot = new SourceRoot(Paths.get(dir));
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");
        compilationUnits = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
    }

    public void parse() throws IOException {
        File sourceDir = findSourceDirectory(directory);
        parseDirectory(sourceDir.getAbsolutePath());
    }

    public File findSourceDirectory(String dir) throws NoSuchFileException {
        File projectDir = new File(dir);
        if(!projectDir.exists()){
            throw new NoSuchFileException(projectDir.getAbsolutePath());
        }

        for(File file : projectDir.listFiles()){
            if("src".equalsIgnoreCase(file.getName())
            || "source".equalsIgnoreCase(file.getName())){
                return file;
            }
        }
        throw new NoSourceDirectoryException(dir);
    }

    public void removeSolutionFromBlockStmt(MethodDeclaration method){
        //TODO Errorhandling, cleaner solution,
        var started = false;
        List <Statement> statementsToRemove = new ArrayList<>();
        if(method.getBody().isPresent()){
            for(Statement statement : method.getBody().get().getStatements()){
                if(!started && NodeUtils.isStartStatement(statement)){
                    method.getBody().get().addOrphanComment(new LineComment(statement.getTokenRange().get(), START_COMMENT));
                    statementsToRemove.add(statement);
                    started = true;
                }
                else if(started){
                    statementsToRemove.add(statement);
                    if(NodeUtils.isEndStatement(statement)){
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
        var solutionReplacement = codeReplacements.get(replacementId).getReplacementCode().clone();
        solutionReplacement.getStatements().getFirst().get().setLineComment(START_COMMENT);
        var replacementBody = new BlockStmt();
        var isSolutionStatement = false;
        var methodHasEndStatement = false;

        for(Statement statement : methodBody.getStatements()){
            if(NodeUtils.isStartStatement(statement)){
                isSolutionStatement = true;
                solutionReplacement.getStatements().forEach(replacementBody::addStatement);
            }

            if(NodeUtils.isEndStatement(statement)){
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
            method.asMethodDeclaration().setBody(codeReplacements.get(replacementId).getReplacementCode());
        }
        else if (method.isConstructorDeclaration()){
            method.asConstructorDeclaration().setBody(codeReplacements.get(replacementId).getReplacementCode());
        }
    }

    public CompilationUnit modifyAllAnnotatedNodesInFile(CompilationUnit file, String annotationName){
        var annotatedNodes = AnnotationUtils.getNodesInFileAnnotatedWith(file, annotationName);
        annotatedNodes.forEach(annotatedNode -> modifyAnnotatedNode(file, annotatedNode));
        AnnotationUtils.removeAnnotationImportsFromFile(file);
        return file;
    }

    public void modifyAnnotatedNode(CompilationUnit file, BodyDeclaration<?> annotatedNode){
        var copyValueExpression = AnnotationUtils.getAnnotationMemberValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
        var copyValue = CopyOption.getCopy(copyValueExpression.asFieldAccessExpr().getNameAsString());
        switch (copyValue){
            case REPLACE_SOLUTION -> {
                // TODO error handling
                // TODO handle rest of cases
                // TODO create test
                codeReplacementImports.forEach(file::addImport);
                var id = AnnotationUtils.getAnnotationMemberValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME,  AnnotationNames.IMPLEMENT_ID_NAME);
                replaceSolutionInMethodBody(NodeUtils.castToCallableDeclaration(annotatedNode), id.asStringLiteralExpr().asString());
            }
            case REPLACE_BODY -> {
                codeReplacementImports.forEach(file::addImport);
                var id = AnnotationUtils.getAnnotationMemberValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_ID_NAME);
                replaceBody((MethodDeclaration) annotatedNode, id.asStringLiteralExpr().asString());
            }
            case REMOVE_EVERYTHING -> {
                annotatedNode.remove();
            }
        }
        AnnotationUtils.removeAnnotationTypeFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
    }

    public List<CompilationUnit> createStartCodeProject(){
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllNodesInFilesAnnotatedWith(files, AnnotationNames.REMOVE_NAME);
        fileNamesToRemove = NodeUtils.removeNodesFromFiles(files, nodesToRemove);
        return files.stream()
                .map(cu -> modifyAllAnnotatedNodesInFile(cu, AnnotationNames.IMPLEMENT_NAME))
                .collect(Collectors.toList());

    }

    public List<CompilationUnit> createSolutionProject(){
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllNodesInFilesAnnotatedWith(files, AnnotationNames.REMOVE_NAME);
        fileNamesToRemove = NodeUtils.removeNodesFromFiles(files, nodesToRemove);
        var annotatedNodes = AnnotationUtils.getAllNodesInFilesAnnotatedWith(files, AnnotationNames.IMPLEMENT_NAME);
        for(var annotatedNode : annotatedNodes){
            AnnotationUtils.removeAnnotationTypeFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
        }
        for(CompilationUnit file : files){
            AnnotationUtils.removeAnnotationImportsFromFile(file);
            NodeUtils.removeSolutionStartAndEndStatementsFromNodes(annotatedNodes);
        }

        return files;
    }


}
