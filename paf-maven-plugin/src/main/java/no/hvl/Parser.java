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
        var annotatedNodes = AnnotationUtils.getAnnotatedNodesInFile(file, annotationName);
        annotatedNodes.forEach(annotatedNode -> modifyAnnotatedNode(file, annotatedNode));
        AnnotationUtils.removeAnnotationImportsFromFile(file);
        return file;
    }

    public void modifyAnnotatedNode(CompilationUnit file, BodyDeclaration<?> annotatedNode){
        var copyValueExpression = AnnotationUtils.getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
        var copyValue = CopyOption.getCopy(copyValueExpression.asFieldAccessExpr().getNameAsString());
        switch (copyValue){
            case REPLACE_SOLUTION -> {
                // TODO error handling
                // TODO handle rest of cases
                // TODO create test
                codeReplacementImports.forEach(file::addImport);
                var id = AnnotationUtils.getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME,  AnnotationNames.IMPLEMENT_ID_NAME);
                replaceSolutionInMethodBody(NodeUtils.castToCallableDeclaration(annotatedNode), id.asStringLiteralExpr().asString());
            }
            case REPLACE_BODY -> {
                codeReplacementImports.forEach(file::addImport);
                var id = AnnotationUtils.getAnnotationValue(annotatedNode, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_ID_NAME);
                replaceBody((MethodDeclaration) annotatedNode, id.asStringLiteralExpr().asString());
            }
            case REMOVE_EVERYTHING -> {
                annotatedNode.remove();
            }
        }
        AnnotationUtils.removeAnnotationFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
    }

    public List<CompilationUnit> createStartCodeProject(){
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        NodeUtils.removeNodes(files, nodesToRemove, fileNamesToRemove);
        return files.stream()
                .map(cu -> modifyAllAnnotatedNodesInFile(cu, AnnotationNames.IMPLEMENT_NAME))
                .collect(Collectors.toList());

    }

    public List<CompilationUnit> createSolutionProject(){
        var files = getCompilationUnitCopies();
        var nodesToRemove = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        NodeUtils.removeNodes(files, nodesToRemove, fileNamesToRemove);
        var annotatedNodes = AnnotationUtils.getAllAnnotatedNodesInFiles(files, AnnotationNames.IMPLEMENT_NAME);
        for(var annotatedNode : annotatedNodes){
            AnnotationUtils.removeAnnotationFromNode(annotatedNode, AnnotationNames.IMPLEMENT_NAME);
        }
        for(CompilationUnit file : files){
            AnnotationUtils.removeAnnotationImportsFromFile(file);
            NodeUtils.removeSolutionStartAndEndStatementsFromFile(annotatedNodes);
        }

        return files;
    }

    public List<Exercise> getExercises(){
        List<BodyDeclaration<?>> nodesAnnotatedWithImplement = AnnotationUtils
                .getAllAnnotatedNodesInFiles(getCompilationUnitCopies(), AnnotationNames.IMPLEMENT_NAME);
        sortTasks(nodesAnnotatedWithImplement);
        return createExercises(nodesAnnotatedWithImplement);
    }

    private List<Exercise> createExercises(List<BodyDeclaration<?>> nodesAnnotatedWithImplement) {
        List<Exercise> exercises = new ArrayList<>();
        nodesAnnotatedWithImplement.forEach(nodeAnnotatedWithImplement -> {
            int[] number = AnnotationUtils.getNumberValueInImplementAnnotation(nodeAnnotatedWithImplement);
            Exercise exercise = findExerciseOrCreateNewOne(number, 0, exercises);
            exercise.setFullNumberAsString(exercise.convertNumberArrayToString(number));
            var exerciseTask = new Task(nodeAnnotatedWithImplement);
            exerciseTask.setFullNumberAsString(exerciseTask.convertNumberArrayToString(number, exercise.getTasks().size() + 1));
            Optional<Solution> solution = Optional.empty();
            if(NodeUtils.isNodeWithBlockStmt(nodeAnnotatedWithImplement)){
                var blockStmt = NodeUtils.getBlockStmtFromBodyDeclaration(nodeAnnotatedWithImplement);
                if(NodeUtils.blockStmtHasSolution(blockStmt)){
                    solution = Optional.of(new SolutionBuilder(blockStmt).build());
                }
            }
            exerciseTask.setSolution(solution);
            exercise.addTask(exerciseTask);
            //TODO handle exceptions
            exercise.setFile(nodeAnnotatedWithImplement.findCompilationUnit().get());
        });
        return exercises;
    }


    private void sortTasks(List<BodyDeclaration<?>> tasks) {
        tasks.sort((o1, o2) -> {
            var o1number = AnnotationUtils.getNumberValueInImplementAnnotation(o1);
            var o2number = AnnotationUtils.getNumberValueInImplementAnnotation(o2);
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
    }

    private Exercise findExerciseOrCreateNewOne(int[] number, int index, List<Exercise> exercises){
        for(Exercise exercise : exercises){
            if(exercise.getNumberAmongSiblingExercises() == number[index]){
                if(index == number.length - 1){
                    return exercise;
                }
                return findExerciseOrCreateNewOne(number, ++index, exercise.getSubExercises());
            }
        }
        var exercise = new Exercise();
        exercise.setNumberAmongSiblingExercises(number[index]);
        exercises.add(exercise);
        if(index == number.length - 1){
            return exercise;
        }
        return findExerciseOrCreateNewOne(number, ++index, exercise.getSubExercises());

    }

}
