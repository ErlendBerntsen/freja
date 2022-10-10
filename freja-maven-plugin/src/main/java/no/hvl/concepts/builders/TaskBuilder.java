package no.hvl.concepts.builders;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.*;
import no.hvl.exceptions.MissingAnnotationMemberException;
import no.hvl.exceptions.NodeException;

import java.util.Map;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;


public class TaskBuilder {
    private final BodyDeclaration<?> nodeAnnotatedWithExercise;
    private final Exercise parentExercise;
    private String fullNumberAsString;
    private TransformOption transformOption;
    private final Map<String, Replacement> replacementMap;
    private final String DEFAULT_REPLACEMENT = "DEFAULT_REPLACEMENT";

    public TaskBuilder(BodyDeclaration<?> nodeAnnotatedWithExercise, Exercise parentExercise,
                       Map<String, Replacement> replacementMap) {
        this.nodeAnnotatedWithExercise = nodeAnnotatedWithExercise;
        this.parentExercise = parentExercise;
        this.replacementMap = replacementMap;
    }

    public Task build(){
        transformOption = getTransformOptionValueInExerciseAnnotation(nodeAnnotatedWithExercise);
        int[] exerciseNumber = getIdValueInExerciseAnnotation(nodeAnnotatedWithExercise);
        fullNumberAsString = getFullNumberAsString(exerciseNumber);
        return switch (transformOption) {
            case REMOVE_EVERYTHING -> new RemoveEverythingTask(nodeAnnotatedWithExercise, fullNumberAsString);
            case REMOVE_BODY -> buildRemoveBodyTask();
            case REPLACE_BODY -> buildReplaceBodyTask();
            case REMOVE_SOLUTION -> buildRemoveSolutionTask();
            case REPLACE_SOLUTION -> buildSolutionReplacementTask();
        };
    }

    private Task buildReplaceBodyTask() {
        throwExceptionIfNoBlockStmt();
        Replacement replacement = getReplacement();
        return new ReplaceBodyTask(nodeAnnotatedWithExercise, fullNumberAsString, replacement);
    }

    private Task buildRemoveSolutionTask() {
        throwExceptionIfNoBlockStmt();
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeAnnotatedWithExercise);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        return new RemoveSolutionTask(nodeAnnotatedWithExercise, fullNumberAsString, solution);
    }


    private ReplaceSolutionTask buildSolutionReplacementTask() {
        throwExceptionIfNoBlockStmt();
        Replacement replacement = getReplacement();
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeAnnotatedWithExercise);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        return new ReplaceSolutionTask(nodeAnnotatedWithExercise, fullNumberAsString, solution, replacement);
    }

    private Replacement getReplacement(){
        String replacementId = getReplacementId();
        if(DEFAULT_REPLACEMENT.equals(replacementId)){
            return ReplacementBuilder.getDefaultReplacement(nodeAnnotatedWithExercise);
        }
        if(!replacementMap.containsKey(replacementId)){
            throw new NodeException(nodeAnnotatedWithExercise,
                    String.format("The %s \"%s\" does not match any %s of the %s annotations",
                            EXERCISE_REPLACEMENT_ID_NAME, replacementId, REPLACEMENT_CODE_ID_NAME, REPLACEMENT_CODE_NAME));
        }
        return replacementMap.get(replacementId);
    }

    private String getReplacementId(){
        try{
            return getReplacementIdInExerciseAnnotation(nodeAnnotatedWithExercise);
        }catch (MissingAnnotationMemberException e){
            return DEFAULT_REPLACEMENT;
        }
    }

    private Task buildRemoveBodyTask() {
        throwExceptionIfNoBlockStmt();
        return new RemoveBodyTask(nodeAnnotatedWithExercise, fullNumberAsString);
    }

    private void throwExceptionIfNoBlockStmt(){
        if (!nodeHasBlockStmt(nodeAnnotatedWithExercise)){
            throw new NodeException(nodeAnnotatedWithExercise,
                    String.format("The %s \"%s\" is not allowed on field variables," +
                            " only on methods and constructors", EXERCISE_TRANSFORM_NAME, transformOption));
        }
    }


    private String getFullNumberAsString(int[] exerciseNumber){
        int taskNumberInParentExercise = parentExercise.getAmountOfTasks() + 1;
        return convertNumberArrayToString(exerciseNumber, taskNumberInParentExercise);
    }

    private String convertNumberArrayToString(int[] number, int taskNumber){
        var taskNumberString = new StringBuilder();
        for(int digit : number){
            taskNumberString.append(digit);
            taskNumberString.append("_");
        }
        taskNumberString.append(taskNumber).append("_");
        return taskNumberString.toString();
    }

}
