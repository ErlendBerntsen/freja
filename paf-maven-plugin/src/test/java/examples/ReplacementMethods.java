package examples;

import no.hvl.annotations.ReplacementCode;
import testUtils.TestId;
import examples.TODO;

public class ReplacementMethods {

    @TestId(10)
    @ReplacementCode(id = "1")
    public void throwExceptionForUnImplementedConstructor(){
        throw new UnsupportedOperationException(TODO.construtor("GPSPoint"));
    }

    @ReplacementCode(id = "2")
    public void throwExceptionForUnImplementedMethod(){
        throw new UnsupportedOperationException(TODO.method());
    }

    @TestId(12)
    @ReplacementCode(id = "2")
    public void emptyBody(){}
}