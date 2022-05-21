package no.hvl.exceptions;

import static no.hvl.utilities.AnnotationNames.IMPLEMENT_NAME;

public class MissingAnnotationException extends IllegalArgumentException{

    public MissingAnnotationException(String memberName) {
        super(String.format("Node is not annotated with \"@%s\" and thus can't get \"%s\" value",
                IMPLEMENT_NAME, memberName));
    }
}
