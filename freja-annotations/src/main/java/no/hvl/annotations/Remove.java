package no.hvl.annotations;

public @interface Remove {
    TargetProject removeFrom() default TargetProject.ALL;
}
