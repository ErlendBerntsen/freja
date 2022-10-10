package no.hvl.annotations;

public @interface DescriptionReference {
    int[] exercises();
    String attributeName() default "";
}
