package examples;

import no.hvl.annotations.ReplacementCode;
import testUtils.TestId;
import examples.TODO;

import java.io.*;
import java.util.List;

@SuppressWarnings("ALL")
public class ReplacementMethods {

    @TestId(10)
    @ReplacementCode(id = "1")
    public void throwExceptionForUnImplementedConstructor(){
        throw new UnsupportedOperationException(TODO.construtor("GPSPoint"));
    }

    @TestId(14)
    @ReplacementCode(id = "2")
    public void throwExceptionForUnImplementedMethod(){
        throw new UnsupportedOperationException(TODO.method());
    }

    @TestId(12)
    @ReplacementCode(id = "3")
    public void emptyBody(){}

    @TestId(38)
    @ReplacementCode(id = "4")
    public void throwUnhandledCheckedException () throws FileNotFoundException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(""));
    }

    @TestId(39)
    @ReplacementCode(id = "5")
    public void throwMultipleUnhandledCheckedException () throws FileNotFoundException, IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(""));
        System.in.read();
    }
}