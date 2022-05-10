package no.hvl;

import no.hvl.annotations.Copy;
import no.hvl.annotations.Implement;
import no.hvl.annotations.SolutionStart;

@Implement(number = 1, copy = Copy.REMOVE_EVERYTHING, replacementId = "1")
public class Example {

    @Implement(number = 1, copy = Copy.REMOVE_EVERYTHING, replacementId = "1")
    public int fieldVariable;

    @Implement(number = 1, copy = Copy.REMOVE_EVERYTHING, replacementId = "1")
    public Example() {
    }

    @Implement(number = 1, copy = Copy.REMOVE_EVERYTHING, replacementId = "1")
    public void methodToRemove(){

    }

    @Implement(number = 1, copy = Copy.REPLACE_SOLUTION, replacementId = "2")
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
