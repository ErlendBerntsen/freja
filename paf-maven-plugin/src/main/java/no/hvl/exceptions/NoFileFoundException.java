package no.hvl.exceptions;

import com.github.javaparser.ast.Node;

public class NoFileFoundException extends IllegalArgumentException{
    public NoFileFoundException(Node node) {
        super(String.format("Could not find that contains the node: %n%s", node.toString()));
    }
}
