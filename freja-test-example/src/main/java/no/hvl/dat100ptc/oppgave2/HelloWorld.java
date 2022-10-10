package no.hvl.dat100ptc.oppgave2;

import no.hvl.annotations.DescriptionReference;
import no.hvl.annotations.Exercise;
import no.hvl.annotations.TransformOption;

public class HelloWorld {

    @Exercise(id = {2}, transformOption = TransformOption.REMOVE_BODY)
    public void printHelloWorld(){
        System.out.println("Hello World");
    }

    @Exercise(id = {3,1}, transformOption = TransformOption.REMOVE_BODY)
    public void method(@DescriptionReference(exercises = {2}) String reference){}

    @Exercise(id = {3,2}, transformOption = TransformOption.REMOVE_BODY)
    public void method2(@DescriptionReference(exercises = {1, 2}) String reference2){}

    @Exercise(id = {3,3}, transformOption = TransformOption.REMOVE_BODY)
    public void method3(@DescriptionReference(exercises = {2}, attributeName = "NamedReference") String reference3){}
}
