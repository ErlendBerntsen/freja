package examples;

import no.hvl.annotations.*;
import testUtils.TestId;

@SuppressWarnings("ALL")
public class Example {

    @TestId(1)
    @Exercise(id = {1,2}, transformOption = TransformOption.REMOVE_EVERYTHING)
    public int fieldVariable;

    @TestId(17)
    @Exercise(id ={4}, transformOption = TransformOption.REMOVE_EVERYTHING)
    public int fullTransformOptionName;

    @TestId(2)
    public int noImplementAnnotation;

    @TestId(24)
    @Exercise(id = {1},  transformOption = TransformOption.REMOVE_BODY)
    public int fieldWithRemoveBodyCopyOption;

    @TestId(29)
    @Exercise(id = {1},  transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public int fieldWithReplaceBodyCopyOption;

    @TestId(31)
    @Exercise(id = {1},  transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "2")
    public int fieldWithReplaceSolutionCopyOption;

    @TestId(32)
    @Exercise(id = {1},  transformOption = TransformOption.REMOVE_SOLUTION)
    public int fieldWithRemoveSolutionCopyOption;

    @TestId(5)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_EVERYTHING)
    public Example(int fieldVariable) {
        this.fieldVariable = fieldVariable;
    }

    @TestId(23)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_BODY)
    public Example(int fieldVariable, int fullTransformOptionName) {
        this.fieldVariable = fieldVariable;
        this.fullTransformOptionName = fullTransformOptionName;
    }

    @TestId(37)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_BODY)
    public Example(int fieldVariable, int fullTransformOptionName, TransformOption transformOption) {
        this.fieldVariable = fieldVariable;
        this.fullTransformOptionName = fullTransformOptionName;
    }

    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_EVERYTHING)
    public void methodToRemove(){

    }

    @TestId(3)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "1")
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
    @Exercise(id = {1,2,3}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "1")
    public String removeStartEndStatements() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(36)
    @Exercise(id = {1,2,3}, transformOption = TransformOption.REPLACE_SOLUTION)
    public String noReplacementId() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(7)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_SOLUTION)
    public String wrongOrder() {
        String x;
        SolutionEnd e;
        x = "blablabla";
        SolutionStart s;
        return x;
    }

    @TestId(8)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "2")
    public String noStartStatement() {
        String x;
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(9)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "2")
    public void startStatementIsLastStatement() {
        String x;
        SolutionStart s;
    }

    @TestId(11)
    @Exercise(id = {1,2,3}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "3")
    public String nonExistingReplacementId() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        x = "blabla";
        return x;
    }

    @TestId(13)
    @Exercise(id = {1,2,3}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "2")
    public void solutionEndIsLastStatement() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(22)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_BODY)
    public void bodyShouldBeRemoved(){
        String x;
        x = "blablabla";
    }

    @TestId(25)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_SOLUTION)
    public void solutionShouldBeRemoved(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(26)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_SOLUTION)
    public String solutionShouldBeRemoved2(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
        return x;
    }

    @TestId(27)
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_SOLUTION)
    public void solutionShouldBeRemoved3(){
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

    @TestId(28)
    @Exercise(id = {1}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void bodyShouldBeReplaced(){
        String x;
    }

    @TestId(30)
    @Exercise(id = {1},  transformOption = TransformOption.REPLACE_BODY)
    public void replaceBodyCopyOptionWithoutReplacementId(){
        String x;
    }


    @TestId(35)
    @Exercise(id = {1,1,1}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void longNumber(){
        String x;
    }

    @TestId(40)
    @Exercise(id = {1,1,1}, transformOption = TransformOption.REPLACE_BODY, replacementId = "5")
    public void shouldHaveThrownExceptionsInDeclarationAfterTransformation(){
        String x;
    }



}
