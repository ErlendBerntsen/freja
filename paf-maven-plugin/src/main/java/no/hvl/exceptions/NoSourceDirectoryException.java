package no.hvl.exceptions;

public class NoSourceDirectoryException extends RuntimeException{

    public NoSourceDirectoryException(String dir) {
        super("No source directory found in the directory " + dir + " or any of its subdirectories."
                + "The source directory must be called \"src\" or \"source\". ");
    }
}
