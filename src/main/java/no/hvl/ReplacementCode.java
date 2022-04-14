package no.hvl;

import no.hvl.annotations.SolutionReplacement;
import no.hvl.dat100example.TODO;

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
