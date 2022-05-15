package examples;

import no.hvl.annotations.ReplacementCode;

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