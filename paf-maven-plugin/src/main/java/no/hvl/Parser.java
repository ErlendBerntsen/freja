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
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.exceptions.NoSourceDirectoryException;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;


import java.io.File;
import java.io.FileNotFoundException;
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
    private static final String START_COMMENT = "TODO - START";
    private static final String END_COMMENT = "TODO - END";

    public Parser() {
        this.compilationUnits = new ArrayList<>();
    }

    public List<CompilationUnit> getCompilationUnitCopies(){
        List<CompilationUnit> compilationUnitCopies = new ArrayList<>();
        compilationUnits.forEach(compilationUnit -> compilationUnitCopies.add(compilationUnit.clone()));
        return compilationUnitCopies;
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
        createAssignmentMetaModel();
    }

    private void createAssignmentMetaModel(){
        AssignmentMetaModel assignmentMetaModel = new AssignmentMetaModelBuilder(getCompilationUnitCopies()).build();
        codeReplacements = assignmentMetaModel.getReplacementsAsHashMap();
        codeReplacementImports = assignmentMetaModel.getReplacementImportDeclarations();
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
        var solutionReplacement = codeReplacements.get(replacementId).getReplacementCode().clone();
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
            method.asMethodDeclaration().setBody(codeReplacements.get(replacementId).getReplacementCode());
        }
        else if (method.isConstructorDeclaration()){
            method.asConstructorDeclaration().setBody(codeReplacements.get(replacementId).getReplacementCode());
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
                return AnnotationNames.SOLUTION_START_NAME.equals(variableDeclarationExpr.getElementType().toString());
            }
        }
        return false;
    }

    public boolean isEndStatement(Statement statement){
        if(statement.isExpressionStmt()){
            Expression expression = statement.asExpressionStmt().getExpression();
            if(expression.isVariableDeclarationExpr()){
                VariableDeclarationExpr variableDeclarationExpr = expression.asVariableDeclarationExpr();
                return AnnotationNames.SOLUTION_END_NAME.equals(variableDeclarationExpr.getElementType().toString());
            }
        }
        return false;
    }

    public CompilationUnit modifyAllAnnotatedNodesInFile(CompilationUnit file, String annotationName){
        var annotatedNodes = AnnotationUtils.getAnnotatedNodesInFile(file, annotationName);
        annotatedNodes.forEach(annotatedNode -> modifyAnnotatedNode(file, annotatedNode));
        removeAnnotationImportsFromFile(file);
        return file;
    }

    public void modifyAnnotatedNode(CompilationUnit file, BodyDeclaration<?> annotatedNode){
        var copyValueExpression = getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
        var copyValue = CopyOption.getCopy(copyValueExpression.asFieldAccessExpr().getNameAsString());
        switch (copyValue){
            case REPLACE_SOLUTION -> {
                // TODO error handling
                // TODO handle rest of cases
                // TODO create test
                codeReplacementImports.forEach(file::addImport);
                var id = getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME,  AnnotationNames.IMPLEMENT_ID_NAME);
                replaceSolutionInMethodBody(castToCallableDeclaration(annotatedNode), id.asStringLiteralExpr().asString());
            }
            case REPLACE_BODY -> {
                codeReplacementImports.forEach(file::addImport);
                var id = getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_ID_NAME);
                replaceBody((MethodDeclaration) annotatedNode, id.asStringLiteralExpr().asString());
            }
            case REMOVE_EVERYTHING -> {
                annotatedNode.remove();
            }
        }
        AnnotationUtils.removeAnnotationFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
    }

    private void removeAnnotationImportsFromFile(CompilationUnit file){
        List<ImportDeclaration> importDeclarations = List.copyOf(file.getImports());
        importDeclarations.forEach(importDeclaration -> {
            if(importDeclaration.isAsterisk() &&
                importDeclaration.getNameAsString()
                        .equals(AnnotationUtils.getAnnotationsPackageName().getQualifier().get().asString())){
                importDeclaration.remove();
            }
            if(importDeclaration.getName().getQualifier().isPresent()
                    && importDeclaration.getName().getQualifier().get().asString()
                    .equals(AnnotationUtils.getAnnotationsPackageName().getQualifier().get().asString())){
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
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        removeNodes(files, nodesToRemove);
        return files.stream()
                .map(cu -> modifyAllAnnotatedNodesInFile(cu, AnnotationNames.IMPLEMENT_NAME))
                .collect(Collectors.toList());

    }

    public List<CompilationUnit> createSolutionProject(){
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        removeNodes(files, nodesToRemove);
        var annotatedNodes = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.IMPLEMENT_NAME);
        for(var annotatedNode : annotatedNodes){
            AnnotationUtils.removeAnnotationFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
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
        List<BodyDeclaration<?>> tasks = AnnotationUtils.getAllAnnotatedNodesInFiles(getCompilationUnitCopies(), AnnotationNames.IMPLEMENT_NAME);
        tasks.sort((o1, o2) -> {
            var o1number = AnnotationUtils.getTaskNumber(o1).get();
            var o2number = AnnotationUtils.getTaskNumber(o2).get();
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
            int[] number = AnnotationUtils.getTaskNumber(task).get();
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

    public HashMap<String, Replacement> getCodeReplacements() {
        return codeReplacements;
    }
}
