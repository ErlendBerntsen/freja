package examples;

import no.hvl.annotations.*;
import testUtils.TestId;

@Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
public class Example {



    @TestId(1)
    @Implement(number = {1,2}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public int fieldVariable;

    @TestId(2)
    public int noImplementAnnotation;

    @TestId(5)
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public Example(int fieldVariable) {
        this.fieldVariable = fieldVariable;
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
    @Implement(number = {1,2,3}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "1")
    public void solutionEndIsLastStatement() {
        String x;
        SolutionStart s;
        x = "blablabla";
        SolutionEnd e;
    }

}
