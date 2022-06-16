package no.hvl.exceptions;

import com.github.javaparser.ast.Node;

import static no.hvl.utilities.AnnotationNames.EXERCISE_NAME;

public class MissingAnnotationException extends NodeException{

    public MissingAnnotationException(Node node, String memberName) {
        super(node, String.format("Node is not annotated with \"@%s\" and thus can't get \"%s\" value",
                EXERCISE_NAME, memberName));
    }
}
