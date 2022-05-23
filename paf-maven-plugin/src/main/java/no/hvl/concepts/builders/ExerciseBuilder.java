package no.hvl.concepts.builders;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Replacement;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
        fullNumberAsIntArray = AnnotationUtils.getNumberValueInImplementAnnotation(annotatedNode);
        Exercise parentExerciseForTask = findParentExerciseOfTaskOrCreateNewParent(createdExercises, 0);
        parentExerciseForTask.setFullNumberAsString(getNumberAsString());
        parentExerciseForTask.setFile(findFile());
        addTaskToParentExercise(parentExerciseForTask);
        return parentExerciseForTask;
    }

    private Exercise findParentExerciseOfTaskOrCreateNewParent(List<Exercise> exercises, int indexInNumberArray){
        Optional<Exercise> parentExercise = findParentExercise(exercises, indexInNumberArray);
        return parentExercise.orElseGet(() -> createNewAncestorExercise(exercises, indexInNumberArray));
    }

    private Optional<Exercise> findParentExercise(List<Exercise> exercises, int indexInNumberArray) {
        for(Exercise exercise : exercises){
            if(isParentExercise(exercise, indexInNumberArray)){
                return Optional.of(exercise);
            }
            if(isAncestorExercise(exercise, indexInNumberArray)){
                return findParentExercise( exercise.getSubExercises(), ++indexInNumberArray);
            }
        }
        return Optional.empty();
    }

    private boolean isParentExercise(Exercise exercise, int indexInNumberArray){
        if(isAncestorExercise(exercise, indexInNumberArray)){
            return (indexInNumberArray == fullNumberAsIntArray.length - 1);
        }
        return false;
    }

    private boolean isAncestorExercise(Exercise exercise, int indexInNumberArray) {
        return exercise.getNumberAmongSiblingExercises() == fullNumberAsIntArray[indexInNumberArray];
    }

    private Exercise createNewAncestorExercise(List<Exercise> exercises, int indexInNumberArray) {
        var ancestorExercise = new Exercise();
        ancestorExercise.setNumberAmongSiblingExercises(fullNumberAsIntArray[indexInNumberArray]);
        exercises.add(ancestorExercise);
        if(isParentExercise(ancestorExercise, indexInNumberArray)){
            return ancestorExercise;
        }
        return findParentExerciseOfTaskOrCreateNewParent(ancestorExercise.getSubExercises(), ++indexInNumberArray);
    }


    private String getNumberAsString() {
        int[] number = AnnotationUtils.getNumberValueInImplementAnnotation(annotatedNode);
        var exerciseNumberAsString = new StringBuilder();
        for(int digit : number){
            exerciseNumberAsString.append(digit);
            exerciseNumberAsString.append("_");
        }
        return exerciseNumberAsString.toString();
    }

    private CompilationUnit findFile() {
        Optional<CompilationUnit> file = annotatedNode.findCompilationUnit();
        if(file.isPresent()){
            return file.get();
        }
        throw new IllegalStateException(String.format("Could not find file of type annotated with @%s," +
                ". Are you sure this type was parsed from a file?", AnnotationNames.IMPLEMENT_NAME));
    }

    private void addTaskToParentExercise(Exercise parentExerciseForTask) {
        AbstractTask task = new TaskBuilder(annotatedNode, parentExerciseForTask, replacementMap).build();
        parentExerciseForTask.addAbstractTask(task);
    }

}
