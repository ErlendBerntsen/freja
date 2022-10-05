package no.hvl.concepts.builders;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import no.hvl.Parser;
import no.hvl.annotations.TargetProject;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.Task;
import no.hvl.exceptions.NodeException;
import no.hvl.utilities.DescriptionReferenceData;

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
        assignment.setDescriptionReferences(findDescriptionReferences());
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
        List<BodyDeclaration<?>> nodesAnnotatedWithExercise = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            nodesAnnotatedWithExercise.addAll(
                    getNodesInFileAnnotatedWith(file, EXERCISE_NAME));
        }
        sortNodesAnnotatedWithExerciseByIdAsc(nodesAnnotatedWithExercise);
        checkExerciseIds(nodesAnnotatedWithExercise);
        return new ArrayList<>(createExercises(nodesAnnotatedWithExercise));
    }

    private List<Exercise> createExercises(List<BodyDeclaration<?>> nodesAnnotatedWithExercise) {
        exercises = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithExercise){
            new ExerciseBuilder(annotatedNode, exercises, replacementMap).build();
        }
        return exercises;
    }

    private List<CompilationUnit> createStartCode(){
        return modifyJavaFiles(TargetProject.START_CODE);
    }

    private List<CompilationUnit> createSolutionCode(){
        return modifyJavaFiles(TargetProject.SOLUTION);
    }

    private List<CompilationUnit> modifyJavaFiles(TargetProject targetProject){
        List<Task> tasks = getTasksFromExercises(exercises);
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        removePafInformation(files, targetProject);
        for(Task task : tasks){
            BodyDeclaration<?> oldTaskNode = findBodyDeclarationCopyInFiles(files, task.getNode());
            BodyDeclaration<?> newTaskNode = createNewTaskNode(targetProject, task, oldTaskNode);
            if(task.getTransformOption().equals(TransformOption.REMOVE_EVERYTHING)){
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

    private void removePafInformation(List<CompilationUnit> files, TargetProject targetProject){
        removeNodesAnnotatedWithRemove(files, targetProject);
        removeAnnotations(files, REPLACEMENT_CODE_NAME);
        removeDescriptionReferenceAnnotations(files);
        removePafImports(files);
    }

    private void removeDescriptionReferenceAnnotations(List<CompilationUnit> files) {
        List<NodeWithAnnotations<?>> nodesWithAnnotations = getAllNodesWithAnnotation(files, DESCRIPTION_REFERENCE_NAME);
        for(NodeWithAnnotations<?> nodeWithAnnotations : nodesWithAnnotations){
            removeAnnotationTypeFromNode(nodeWithAnnotations, DESCRIPTION_REFERENCE_NAME);
        }
    }

    private void removeNodesAnnotatedWithRemove(List<CompilationUnit> files, TargetProject targetProject) {
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = getAllNodesInFilesAnnotatedWith(files, REMOVE_NAME);
        fileNamesToRemove = removeNodesFromFiles(files, nodesAnnotatedWithRemove, targetProject);
    }

    private void removeAnnotations(List<CompilationUnit> files, String annotationName) {
        List<BodyDeclaration<?>> nodes = getAllNodesInFilesAnnotatedWith(files, annotationName);
        for(BodyDeclaration<?> node : nodes){
            removeAnnotationTypeFromNode(node, annotationName);
        }
    }

    private void removePafImports(List<CompilationUnit> files) {
        for(CompilationUnit file : files){
            removeAnnotationImportsFromFile(file);
        }
    }

    private BodyDeclaration<?> createNewTaskNode(TargetProject targetProject, Task task, BodyDeclaration<?> newTaskNode) {
        if(targetProject.equals(TargetProject.SOLUTION)){
            newTaskNode = task.createSolutionCode(newTaskNode);
        }else{
            newTaskNode = task.createStartCode(newTaskNode);
        }
        removeAnnotationTypeFromNode(newTaskNode, EXERCISE_NAME);
        return newTaskNode;
    }

    private void updateTaskNode(Node oldTaskNode, Node newTaskNode){
        Optional<Node> parentNode = oldTaskNode.getParentNode();
        if(parentNode.isPresent()){
            parentNode.get().replace(oldTaskNode, newTaskNode);
        }else{
            throw new IllegalStateException(String.format("Can not find parent node of type annotated with @%s:%n%s"
                    , EXERCISE_NAME, oldTaskNode));
        }
    }

    private List<DescriptionReferenceData> findDescriptionReferences() {
        List<NodeWithAnnotations<?>> annotatedNodes = getAllNodesWithAnnotation(parsedFiles, DESCRIPTION_REFERENCE_NAME);
        List<DescriptionReferenceData> descriptionReferences = new ArrayList<>();
        annotatedNodes.forEach(node -> {
            var descriptionReference = new DescriptionReferenceData(node, getExercisesValueInDescriptionReferenceAnnotation(node));
            descriptionReferences.add(descriptionReference);
        });
        annotatedNodes.forEach(node -> removeAnnotationTypeFromNode(node, DESCRIPTION_REFERENCE_NAME));
        return descriptionReferences;
    }


}
