package no.hvl.concepts.builders;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.*;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.tasks.ReplaceSolutionTask;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.NodeUtils;

import java.util.Map;

import static no.hvl.utilities.AnnotationUtils.*;


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
            case REPLACE_SOLUTION -> {
                return buildSolutionReplacementTask();
            }
            default -> throw new IllegalArgumentException(
                    String.format("Could not recognize copyOption: \"%s\"", copyOption.toString()));
        }
    }

    private ReplaceSolutionTask buildSolutionReplacementTask() {
        String replacementId = getReplacementId();
        BlockStmt codeBlockWithSolution = NodeUtils.getBlockStmtFromBodyDeclaration(nodeAnnotatedWithImplement);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        Replacement replacement = replacementMap.get(replacementId);
        return new ReplaceSolutionTask(nodeAnnotatedWithImplement, fullNumberAsString, solution, replacement);
    }

    private String getReplacementId(){
        try{
          return getReplacementIdInImplementAnnotation(nodeAnnotatedWithImplement);
        }catch (IllegalArgumentException e){
            throw new IllegalStateException(
                    String.format("The \"%s\" attribute of @%s must be specified when the \"%s\" is set to %s",
                            AnnotationNames.IMPLEMENT_ID_NAME, AnnotationNames.IMPLEMENT_NAME,
                            AnnotationNames.IMPLEMENT_COPY_NAME, copyOption.toString()));
        }
    }

    private String getFullNumberAsString(int[] exerciseNumber){
        int taskNumberInParentExercise = parentExercise.getAmountOfTasks() + 1;
        return convertNumberArrayToString(exerciseNumber, taskNumberInParentExercise);
    }

    public String convertNumberArrayToString(int[] number, int taskNumber){
        var taskNumberString = new StringBuilder();
        for(int digit : number){
            taskNumberString.append(digit);
            taskNumberString.append("_");
        }
        taskNumberString.append(taskNumber).append("_");
        return taskNumberString.toString();
    }

}
