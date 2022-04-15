package no.hvl.dat100example;

import no.hvl.annotations.Remove;
import no.hvl.annotations.SolutionReplacement;

@Remove
public class ReplacementCode {

    @SolutionReplacement(id = "1")
    public void throwExceptionForUnImplementedConstructor(){
        throw new UnsupportedOperationException(TODO.construtor("GPSPoint"));
    }

    @SolutionReplacement(id = "2")
    public void throwExceptionForUnImplementedMethod(){
        throw new UnsupportedOperationException(TODO.method());
    }

}
