package no.hvl.utilities;

public final class AnnotationNames {

    private AnnotationNames(){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static final String DESCRIPTION_REFERENCE_NAME = "DescriptionReference";
    public static final String DESCRIPTION_REFERENCE_EXERCISES_NAME = "exercises";
    public static final String DESCRIPTION_REFERENCE_ATTRIBUTE_NAME = "attributeName";

    public static final String EXERCISE_NAME = "Exercise";
    public static final String EXERCISE_ID_NAME = "id";
    public static final String EXERCISE_REPLACEMENT_ID_NAME = "replacementId";
    public static final String EXERCISE_TRANSFORM_NAME = "transformOption";

    public static final String REPLACEMENT_CODE_NAME = "ReplacementCode";
    public static final String REPLACEMENT_CODE_ID_NAME = "id";

    public static final String REMOVE_NAME = "Remove";
    public static final String REMOVE_FROM_NAME = "removeFrom";

    public static final String SOLUTION_START_NAME = "SolutionStart";

    public static final String SOLUTION_END_NAME = "SolutionEnd";

}
