package no.hvl.writers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.*;

public class DeleteFileVisitor implements FileVisitor<Path> {

    private final Path rootDir;

    public DeleteFileVisitor(Path rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if(exc == null){
            return TERMINATE;
        }
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc == null) {
            if(!dir.equals(rootDir)){
                Files.delete(dir);
            }
            return CONTINUE;
        } else {
            throw exc;
        }
    }
}
