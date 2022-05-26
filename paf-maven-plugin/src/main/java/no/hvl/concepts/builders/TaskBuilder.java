package no.hvl.concepts.builders;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.tasks.RemoveEverythingTask;
import no.hvl.concepts.tasks.ReplaceSolutionTask;

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

    public AbstractTask build(){
        copyOption = getCopyOptionValueInImplementAnnotation(nodeAnnotatedWithImplement);
        int[] exerciseNumber = getNumberValueInImplementAnnotation(nodeAnnotatedWithImplement);
        fullNumberAsString = getFullNumberAsString(exerciseNumber);
        switch (copyOption){
            case REPLACE_SOLUTION :
                return buildSolutionReplacementTask();

            case REMOVE_EVERYTHING:
                return new RemoveEverythingTask(nodeAnnotatedWithImplement, fullNumberAsString);
            default : throw new IllegalArgumentException(
                    String.format("Could not recognize copyOption: \"%s\"", copyOption.toString()));
        }
    }

    private ReplaceSolutionTask buildSolutionReplacementTask() {
        String replacementId = getReplacementId();
        if(!replacementMap.containsKey(replacementId)){
            throw new IllegalArgumentException(
                    String.format("The %s \"%s\" does not match any %s of the %s annotations",
                            IMPLEMENT_ID_NAME, replacementId, REPLACEMENT_CODE_ID_NAME, REPLACEMENT_CODE_NAME));
        }
        Replacement replacement = replacementMap.get(replacementId);
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeAnnotatedWithImplement);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        return new ReplaceSolutionTask(nodeAnnotatedWithImplement, fullNumberAsString, solution, replacement);
    }

    private String getReplacementId(){
        try{
          return getReplacementIdInImplementAnnotation(nodeAnnotatedWithImplement);
        }catch (IllegalArgumentException e){
            throw new IllegalStateException(
                    String.format("The \"%s\" attribute of @%s must be specified when the \"%s\" is set to %s",
                            IMPLEMENT_ID_NAME, IMPLEMENT_NAME,
                            IMPLEMENT_COPY_NAME, copyOption.toString()));
        }
    }

    private String getFullNumberAsString(int[] exerciseNumber){
        int taskNumberInParentExercise = parentExercise.getAmountOfAbstractTasks() + 1;
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
