package no.hvl.utilities;

public final class AnnotationNames {

    private AnnotationNames(){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static final String IMPLEMENT_NAME = "Implement";
    public static final String IMPLEMENT_NUMBER_NAME = "number";
    public static final String IMPLEMENT_ID_NAME = "replacementId";
    public static final String IMPLEMENT_COPY_NAME = "copyOption";

    public static final String REPLACEMENT_CODE_NAME = "ReplacementCode";
    public static final String REPLACEMENT_CODE_ID_NAME = "id";

    public static final String REMOVE_NAME = "Remove";

    public static final String SOLUTION_START_NAME = "SolutionStart";

    public static final String SOLUTION_END_NAME = "SolutionEnd";

}
