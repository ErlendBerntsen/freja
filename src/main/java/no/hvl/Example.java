package no.hvl;

import no.hvl.annotations.Implement;
import no.hvl.annotations.SolutionEnd;
import no.hvl.annotations.SolutionStart;
import org.checkerframework.checker.units.qual.C;

@Implement
public class Example {

    @Implement(copy = Copy.REMOVE_EVERYTHING)
    public int fieldVariable;

    @Implement
    public Example() {
    }

    @Implement(copy = Copy.REPLACE_SOLUTION, replacementId = "2")
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
