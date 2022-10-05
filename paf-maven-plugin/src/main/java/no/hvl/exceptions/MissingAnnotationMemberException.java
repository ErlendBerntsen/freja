package no.hvl.exceptions;

import com.github.javaparser.ast.Node;

public class MissingAnnotationMemberException extends NodeException {

    public MissingAnnotationMemberException(Node node, String memberName) {
        super(node, String.format("Could not find annotation member \"%s\" in the annotation:%n%s",
                memberName, node));
    }
}