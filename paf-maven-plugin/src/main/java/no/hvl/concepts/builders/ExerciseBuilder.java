package no.hvl.concepts.builders;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.tasks.Task;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Replacement;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class ExerciseBuilder {

    private final BodyDeclaration<?> annotatedNode;
    private final List<Exercise> createdExercises;
    private final HashMap<String, Replacement> replacementMap;
    private int[] fullNumberAsIntArray;

    public ExerciseBuilder(BodyDeclaration<?> annotatedNode, List<Exercise> createdExercises,
                           HashMap<String, Replacement> replacementMap) {
        this.annotatedNode = annotatedNode;
        this.createdExercises = createdExercises;
        this.replacementMap = replacementMap;
    }

    public Exercise build(){
        fullNumberAsIntArray = getNumberValueInImplementAnnotation(annotatedNode);
        Exercise parentExerciseForTask = findParentExerciseOfTaskOrCreateNewParent();
        parentExerciseForTask.setFile(findFile(annotatedNode));
        addTaskToParentExercise(parentExerciseForTask);
        return parentExerciseForTask;
    }

    private Exercise findParentExerciseOfTaskOrCreateNewParent(){
        Optional<Exercise> ancestorExercise =  findAncestorExercise(createdExercises);
        if(ancestorExercise.isPresent()){
            return findParentExercise(ancestorExercise.get(), 0);
        }else{
            return findParentExercise(createNewRootExercise(), 0);
        }
    }

    private Optional<Exercise> findAncestorExercise(List<Exercise> exercises){
        for(Exercise exercise : exercises){
            if(isAncestorExercise(exercise, 0)){
                return Optional.of(exercise);
            }
        }
        return Optional.empty();
    }

    private boolean isAncestorExercise(Exercise exercise, int indexInNumberArray) {
        return exercise.getNumberAmongSiblingExercises() == fullNumberAsIntArray[indexInNumberArray];
    }

    private Exercise findParentExercise(Exercise ancestorExercise, int indexInNumberArray){
        if(isParentExercise(ancestorExercise, indexInNumberArray)){
            return ancestorExercise;
        }
        for(Exercise subExercise : ancestorExercise.getSubExercises()){
            if(isAncestorExercise(subExercise, indexInNumberArray + 1)){
                return findParentExercise(subExercise, indexInNumberArray + 1);
            }
        }
        Exercise subExercise = createNewSubExercise(ancestorExercise);
        return findParentExercise(subExercise, indexInNumberArray + 1);
    }

    private boolean isParentExercise(Exercise exercise, int indexInNumberArray){
        if(isAncestorExercise(exercise, indexInNumberArray)){
            return (indexInNumberArray == fullNumberAsIntArray.length - 1);
        }
        return false;
    }

    private Exercise createNewSubExercise(Exercise parentExercise) {
        Exercise subExercise = new Exercise();
        parentExercise.addSubExercise(subExercise);
        subExercise.setParentExercise(parentExercise);
        int numberAmongSiblingExercises = parentExercise.getAmountOfSubExercises();
        subExercise.setNumberAmongSiblingExercises(numberAmongSiblingExercises);
        String fullNumber = parentExercise.getFullNumberAsString() + numberAmongSiblingExercises + "_";
        subExercise.setFullNumberAsString(fullNumber);
        return subExercise;
    }

    private Exercise createNewRootExercise(){
        Exercise rootExercise = new Exercise();
        rootExercise.setNumberAmongSiblingExercises(fullNumberAsIntArray[0]);
        rootExercise.setFullNumberAsString(fullNumberAsIntArray[0] + "_");
        createdExercises.add(rootExercise);
        return rootExercise;
    }

    private void addTaskToParentExercise(Exercise parentExerciseForTask) {
        Task task = new TaskBuilder(annotatedNode, parentExerciseForTask, replacementMap).build();
        parentExerciseForTask.addAbstractTask(task);
    }

}
