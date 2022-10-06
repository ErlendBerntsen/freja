package no.hvl.exceptions;

import com.github.javaparser.ast.Node;

public class NoFileFoundException extends IllegalArgumentException{
    public NoFileFoundException(Node node) {
        super(String.format("Could not find the file that contains the node: %n%s%n%n" +
                "Are you sure this node was parsed from a file?\"", node.toString()));
    }
}
