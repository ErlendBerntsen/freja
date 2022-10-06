package no.hvl.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Exercise {

    int[] id();
    TransformOption transformOption();
    String replacementId() default "";

}
