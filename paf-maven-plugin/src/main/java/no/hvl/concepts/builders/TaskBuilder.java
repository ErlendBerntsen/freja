package no.hvl.concepts.builders;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.*;
import no.hvl.exceptions.NodeException;

import java.util.Map;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;


public class TaskBuilder {
    private final BodyDeclaration<?> nodeAnnotatedWithImplement;
    private final Exercise parentExercise;
    private String fullNumberAsString;
    private CopyOption copyOption;
    private final Map<String, Replacement> replacementMap;

    public TaskBuilder(BodyDeclaration<?> nodeAnnotatedWithImplement, Exercise parentExercise,
                       Map<String, Replacement> replacementMap) {
        this.nodeAnnotatedWithImplement = nodeAnnotatedWithImplement;
        this.parentExercise = parentExercise;
        this.replacementMap = replacementMap;
    }

    public Task build(){
        copyOption = getCopyOptionValueInImplementAnnotation(nodeAnnotatedWithImplement);
        int[] exerciseNumber = getNumberValueInImplementAnnotation(nodeAnnotatedWithImplement);
        fullNumberAsString = getFullNumberAsString(exerciseNumber);
        return switch (copyOption) {
            case REMOVE_EVERYTHING -> new RemoveEverythingTask(nodeAnnotatedWithImplement, fullNumberAsString);
            case REMOVE_BODY -> buildRemoveBodyTask();
            case REPLACE_BODY -> buildReplaceBodyTask();
            case REMOVE_SOLUTION -> buildRemoveSolutionTask();
            case REPLACE_SOLUTION -> buildSolutionReplacementTask();
        };
    }

    private Task buildReplaceBodyTask() {
        throwExceptionIfNoBlockStmt();
        Replacement replacement = getReplacement();
        return new ReplaceBodyTask(nodeAnnotatedWithImplement, fullNumberAsString, replacement);
    }

    private Task buildRemoveSolutionTask() {
        throwExceptionIfNoBlockStmt();
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeAnnotatedWithImplement);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        return new RemoveSolutionTask(nodeAnnotatedWithImplement, fullNumberAsString, solution);
    }


    private ReplaceSolutionTask buildSolutionReplacementTask() {
        throwExceptionIfNoBlockStmt();
        Replacement replacement = getReplacement();
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeAnnotatedWithImplement);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        return new ReplaceSolutionTask(nodeAnnotatedWithImplement, fullNumberAsString, solution, replacement);
    }

    private Replacement getReplacement(){
        String replacementId = getReplacementId();
        if(!replacementMap.containsKey(replacementId)){
            throw new NodeException(nodeAnnotatedWithImplement,
                    String.format("The %s \"%s\" does not match any %s of the %s annotations",
                            IMPLEMENT_ID_NAME, replacementId, REPLACEMENT_CODE_ID_NAME, REPLACEMENT_CODE_NAME));
        }
        return replacementMap.get(replacementId);
    }

    private String getReplacementId(){
        try{
          return getReplacementIdInImplementAnnotation(nodeAnnotatedWithImplement);
        }catch (NodeException e){
            throw new NodeException(nodeAnnotatedWithImplement,
                    String.format("The \"%s\" attribute of @%s must be specified when the \"%s\" is set to %s",
                            IMPLEMENT_ID_NAME, IMPLEMENT_NAME,
                            IMPLEMENT_COPY_NAME, copyOption.toString()));
        }
    }

    private Task buildRemoveBodyTask() {
        throwExceptionIfNoBlockStmt();
        return new RemoveBodyTask(nodeAnnotatedWithImplement, fullNumberAsString);
    }

    private void throwExceptionIfNoBlockStmt(){
        if (!nodeHasBlockStmt(nodeAnnotatedWithImplement)){
            throw new NodeException(nodeAnnotatedWithImplement,
                    String.format("The copyOption \"%s\" is not allowed on field variables," +
                            " only on methods and constructors", copyOption));
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
