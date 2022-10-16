package no.hvl.exceptions;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import static no.hvl.utilities.NodeUtils.*;

public class NodeException extends RuntimeException{
    private String message;
    private static final String UNKNOWN = "UNKNOWN";

    public NodeException(Node node) {
        createErrorMessage(node);
    }

    public NodeException(Node node, String subExceptionMessage){
        createErrorMessage(node);
        message += "Cause: " +  subExceptionMessage;
        message += "\n\nNode:\n" + node.toString();
    }

    private void createErrorMessage(Node node){
        String fileName = tryToGetFileName(node);
        String lineStart = tryToGetLineStart(node);
        String lineEnd = tryToGetLineEnd(node);
        message = String.format("""
                        There was an error with a node @
                        File name: %s
                        Line start: %s
                        Line end: %s
                        
                        """,
                fileName, lineStart, lineEnd);
    }

    private String tryToGetFileName(Node node){
        try{
            CompilationUnit file = findFile(node);
            return getFileName(file);
        }catch (NoFileFoundException e){
            return UNKNOWN;
        }
    }

    private String tryToGetLineStart(Node node){
        try{
            Range range = tryToGetRange(node);
            return String.valueOf(range.begin.line);
        }catch (IllegalArgumentException e){
            return UNKNOWN;
        }
    }

    private String tryToGetLineEnd(Node node){
        try{
            Range range = tryToGetRange(node);
            return String.valueOf(range.end.line);
        }catch (IllegalArgumentException e){
            return UNKNOWN;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }
}
