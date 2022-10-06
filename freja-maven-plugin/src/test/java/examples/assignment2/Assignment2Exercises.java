package examples.assignment2;

import no.hvl.annotations.Exercise;
import no.hvl.annotations.ReplacementCode;
import no.hvl.annotations.SolutionStart;

import static no.hvl.annotations.TransformOption.REPLACE_SOLUTION;

@SuppressWarnings("ALL")
public class Assignment2Exercises {

    @Exercise(id = {1}, transformOption = REPLACE_SOLUTION, replacementId = "1")
    public String helloWorld(){
        SolutionStart s;
        String str = "Hello World!";
        return str;
    }

    @ReplacementCode(id = "1")
    public void replacement(){
        throw new UnsupportedOperationException("Not implemented");
    }

    @ReplacementCode(id = "1")
    public void replacement2(){
        throw new UnsupportedOperationException("Not implemented!");
    }
}
