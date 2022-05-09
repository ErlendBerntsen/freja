package no.hvl.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Implement {

    int[] number();
    Copy copy();
    String replacementId();

}
