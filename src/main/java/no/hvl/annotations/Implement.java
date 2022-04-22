package no.hvl.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Implement {

    int[] number() default {1};
    Copy copy() default Copy.KEEP_SKELETON;
    String replacementId() default "";

}
