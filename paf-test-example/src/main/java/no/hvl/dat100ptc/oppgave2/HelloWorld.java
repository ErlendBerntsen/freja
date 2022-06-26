package no.hvl.dat100ptc.oppgave2;

import no.hvl.annotations.Exercise;
import no.hvl.annotations.TransformOption;

public class HelloWorld {

    @Exercise(id = {2}, transformOption = TransformOption.REMOVE_BODY)
    public void printHelloWorld(){
        System.out.println("Hello World");
    }
}
