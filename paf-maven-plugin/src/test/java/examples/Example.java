package examples;

import no.hvl.annotations.*;
import testUtils.TestId;

@SuppressWarnings("ALL")
public class Example {

    @TestId(1)
    @Implement(number = {1,2}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public int fieldVariable;

    @TestId(17)
    @Implement(number={4}, copyOption = no.hvl.annotations.CopyOption.REMOVE_EVERYTHING)
    public int fullCopyOptionName;

    @TestId(2)
    public int noImplementAnnotation;

    @TestId(24)
    @Implement(number = {1},  copyOption = CopyOption.REMOVE_BODY)
    public int fieldWithRemoveBodyCopyOption;

    @TestId(29)
    @Implement(number = {1},  copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public int fieldWithReplaceBodyCopyOption;

    @TestId(31)
    @Implement(number = {1},  copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public int fieldWithReplaceSolutionCopyOption;

    @TestId(32)
    @Implement(number = {1},  copyOption = CopyOption.REMOVE_SOLUTION)
    public int fieldWithRemoveSolutionCopyOption;

    @TestId(5)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public Example(int fieldVariable) {
        this.fieldVariable = fieldVariable;
    }

    @TestId(23)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_BODY)
    public Example(int fieldVariable, int fullCopyOptionName) {
        this.fieldVariable = fieldVariable;
        this.fullCopyOptionName = fullCopyOptionName;
    }

    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public void methodToRemove(){

    }

    @TestId(3)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "1")
    public String helloWorld() {
        /**
         * Doc comment should be removed
         */
        /*
        Block comments should also be removed
         */
        //Orphan comment should also be removed
        //Comment should be removed
        String str;
        SolutionStart s;
        str = "Hello World";
        return str;
    }

    @TestId(6)
    @Implement(number = {1,2,3}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "1")
    public String removeStartEndStatements() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(7)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_SOLUTION)
    public String wrongOrder() {
        String x;
        SolutionEnd e;
        x = "blablabla";
        SolutionStart s;
        return x;
    }

    @TestId(8)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public String noStartStatement() {
        String x;
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(9)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public void startStatementIsLastStatement() {
        String x;
        SolutionStart s;
    }

    @TestId(11)
    @Implement(number = {1,2,3}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "3")
    public String nonExistingReplacementId() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(13)
    @Implement(number = {1,2,3}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public void solutionEndIsLastStatement() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(22)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_BODY)
    public void bodyShouldBeRemoved(){
        String x;
        x = "blablabla";
    }

    @TestId(25)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_SOLUTION)
    public void solutionShouldBeRemoved(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(26)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_SOLUTION)
    public String solutionShouldBeRemoved2(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        return x;
    }

    @TestId(27)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_SOLUTION)
    public void solutionShouldBeRemoved3(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(28)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void bodyShouldBeReplaced(){
        String x;
    }

    @TestId(30)
    @Implement(number = {1},  copyOption = CopyOption.REPLACE_BODY)
    public void replaceBodyCopyOptionWithoutReplacementId(){
        String x;
    }


    @TestId(35)
    @Implement(number = {1,1,1}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void longNumber(){
        String x;
    }


}
