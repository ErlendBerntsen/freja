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

    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public Example() {
    }

    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    public void methodToRemove(){

    }

    @TestId(3)
    @Implement(number = {1}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public String helloWorld() {
        String str;
        SolutionStart s;
        str = "Hello World";
        return str;
    }


//    @Implement(copy = Copy.REMOVE_SOLUTION)
//    public String helloWorld2(){
//        String str;
//        solution: {
//            str = "Hello World";
//        }
//        return str;
//    }

//    @Implement(copy = Copy.REMOVE_SOLUTION)
//    public String helloWorld3(){
//        String str;
//        //SOLUTION_START
//        str = "Hello World";
//        //SOLUTION_END
//        return str;
//    }
//
}
