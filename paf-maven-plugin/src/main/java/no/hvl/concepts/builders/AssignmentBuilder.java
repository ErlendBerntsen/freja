package no.hvl.concepts.builders;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.Task;
import no.hvl.exceptions.NodeException;

import java.util.*;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.GeneralUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class AssignmentBuilder {

    private final Parser parser;
    private List<CompilationUnit> parsedFiles;
    private HashMap<String, Replacement> replacementMap;
    private List<Exercise> exercises;
    private HashSet<String> fileNamesToRemove;

    public AssignmentBuilder(Parser parser) {
        this.parser = parser;
    }

    public Assignment build() {
        Assignment assignment = new Assignment();
        parsedFiles = parser.getCompilationUnitCopies();
        assignment.setParsedFiles(parsedFiles);
        assignment.setReplacements(findReplacements());
        assignment.setExercises(findExercises());
        assignment.setStartCodeFiles(createStartCode());
        assignment.setSolutionCodeFiles(createSolutionCode());
        assignment.setFileNamesToRemove(fileNamesToRemove);
        return assignment;
    }

    private List<Replacement> findReplacements() {
        List<Replacement> replacements = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            List<BodyDeclaration<?>> nodesAnnotatedWithReplacementCode =
                    getNodesInFileAnnotatedWith(file, REPLACEMENT_CODE_NAME);
            replacements.addAll(createReplacements(nodesAnnotatedWithReplacementCode));
        }
        createReplacementMap(replacements);
        return replacements;
    }

    private List<Replacement> createReplacements(List<BodyDeclaration<?>> nodesAnnotatedWithReplacementCode){
        List<Replacement> replacements = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithReplacementCode){
            replacements.add(new ReplacementBuilder(annotatedNode).build());
        }
        return replacements;
    }

    private void createReplacementMap(List<Replacement> replacements) {
        replacementMap = new HashMap<>();
        for(Replacement replacement : replacements){
            String replacementId = replacement.getId();
            if(replacementMap.containsKey(replacementId)){
                throw new NodeException(replacement.getReplacementCode(),
                        String.format("Type annotated with %s uses an %s that is already defined"
                        , REPLACEMENT_CODE_NAME, REPLACEMENT_CODE_ID_NAME ));
            }
            replacementMap.put(replacementId, replacement);
        }
    }

    private List<Exercise> findExercises() {
        List<BodyDeclaration<?>> nodesAnnotatedWithImplement = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            nodesAnnotatedWithImplement.addAll(
                    getNodesInFileAnnotatedWith(file, IMPLEMENT_NAME));
        }
        sortNodesAnnotatedWithImplementByNumberAsc(nodesAnnotatedWithImplement);
        checkExerciseNumbers(nodesAnnotatedWithImplement);
        return new ArrayList<>(createExercises(nodesAnnotatedWithImplement));
    }

    private List<Exercise> createExercises(List<BodyDeclaration<?>> nodesAnnotatedWithImplement) {
        exercises = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithImplement){
            new ExerciseBuilder(annotatedNode, exercises, replacementMap).build();
        }
        return exercises;
    }

    private List<CompilationUnit> createStartCode(){
        return modifyJavaFiles(false);
    }

    private List<CompilationUnit> createSolutionCode(){
        return modifyJavaFiles(true);
    }

    private List<CompilationUnit> modifyJavaFiles(boolean isSolutionCode){
        List<Task> tasks = getTasksFromExercises(exercises);
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        removePafInformation(files);
        for(Task task : tasks){
            BodyDeclaration<?> oldTaskNode = findBodyDeclarationCopyInFiles(files, task.getNode());
            BodyDeclaration<?> newTaskNode = createNewTaskNode(isSolutionCode, task, oldTaskNode);
            if(task.getCopyOption().equals(CopyOption.REMOVE_EVERYTHING)){
                continue;
            }
            updateTaskNode(oldTaskNode, newTaskNode);
        }
        return files;
    }

    private List<Task> getTasksFromExercises(List<Exercise> exercises) {
        List<Task> tasks = new ArrayList<>();
        for(Exercise exercise : exercises){
            tasks.addAll(exercise.getTasks());
            tasks.addAll(getTasksFromExercises(exercise.getSubExercises()));
        }
        return tasks;
    }

    private void removePafInformation(List<CompilationUnit> files){
        removeNodesAnnotatedWithRemove(files);
        removeReplacementCodeAnnotations(files);
        removePafImports(files);
    }

    private void removeNodesAnnotatedWithRemove(List<CompilationUnit> files) {
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = getAllNodesInFilesAnnotatedWith(files, REMOVE_NAME);
        fileNamesToRemove = removeNodesFromFiles(files, nodesAnnotatedWithRemove);
    }

    private void removeReplacementCodeAnnotations(List<CompilationUnit> files) {
        List<BodyDeclaration<?>> nodes = getAllNodesInFilesAnnotatedWith(files, REPLACEMENT_CODE_NAME);
        for(BodyDeclaration<?> node : nodes){
            removeAnnotationTypeFromNode(node, REPLACEMENT_CODE_NAME);
        }
    }

    private void removePafImports(List<CompilationUnit> files) {
        for(CompilationUnit file : files){
            removeAnnotationImportsFromFile(file);
        }
    }

    private BodyDeclaration<?> createNewTaskNode(boolean isSolutionCode, Task task, BodyDeclaration<?> newTaskNode) {
        if(isSolutionCode){
            newTaskNode = task.createSolutionCode(newTaskNode);
        }else{
            newTaskNode = task.createStartCode(newTaskNode);
        }
        removeAnnotationTypeFromNode(newTaskNode, IMPLEMENT_NAME);
        return newTaskNode;
    }

    private void updateTaskNode(Node oldTaskNode, Node newTaskNode){
        Optional<Node> parentNode = oldTaskNode.getParentNode();
        if(parentNode.isPresent()){
            parentNode.get().replace(oldTaskNode, newTaskNode);
        }else{
            throw new IllegalStateException(String.format("Can not find parent node of type annotated with @%s:%n%s"
                    , IMPLEMENT_NAME, oldTaskNode));
        }
    }

}
