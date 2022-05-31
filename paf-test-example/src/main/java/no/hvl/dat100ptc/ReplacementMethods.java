package no.hvl.dat100ptc;

import no.hvl.annotations.Remove;
import no.hvl.annotations.ReplacementCode;
import no.hvl.dat100ptc.TODO;

@Remove
public class ReplacementMethods {

    @ReplacementCode(id = "1")
    public void throwExceptionForUnImplementedConstructor(){
        throw new UnsupportedOperationException(TODO.construtor("GPSPoint"));
    }

    @ReplacementCode(id = "2")
    public void throwExceptionForUnImplementedMethod(){
        throw new UnsupportedOperationException(TODO.method());
    }

}
