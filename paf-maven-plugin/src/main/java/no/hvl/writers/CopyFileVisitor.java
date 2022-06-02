package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.FileVisitResult.*;
import static no.hvl.utilities.FileUtils.*;

public class CopyFileVisitor implements FileVisitor<Path> {

    private final Path source;
    private final Path target;
    private final HashMap<String, CompilationUnit> modifiedFiles;
    private final List<PathMatcher> pathMatchersToIgnore;
    private boolean isVisitingInJavaSourceFolder;

    public CopyFileVisitor(Path source, Path target,
                           HashMap<String, CompilationUnit> modifiedFiles, List<PathMatcher> pathMatchersToIgnore) {
        this.source = source;
        this.target = target;
        this.modifiedFiles = modifiedFiles;
        this.pathMatchersToIgnore = pathMatchersToIgnore;
        this.isVisitingInJavaSourceFolder = false;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(isJavaSourceFolder(dir)){
            isVisitingInJavaSourceFolder = true;
        }
        else if(!fileShouldBeCopied(dir)){
            return SKIP_SUBTREE;
        }
        Path targetDir = target.resolve(source.relativize(dir));
        try {
            Files.copy(dir, targetDir);
        } catch (FileAlreadyExistsException e) {
            if (!Files.isDirectory(targetDir))
                throw e;
        }
        return CONTINUE;
    }

    private boolean fileShouldBeCopied(Path file){
        Path relativePath = source.relativize(file);
        for(PathMatcher pathMatcher : pathMatchersToIgnore){
            if (pathMatcher.matches(relativePath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(fileShouldBeCopied(file)){
            if(isVisitingInJavaSourceFolder){
                createAndWriteToFile(file);
            }
            else{
                Files.copy(file, target.resolve(source.relativize(file)));
            }
        }
        return CONTINUE;
    }

    private void createAndWriteToFile(Path file) throws IOException {
        File newFile = new File(target.resolve(source.relativize(file)).toString());
        if(newFile.createNewFile()){
            String fileContent = printFileContentToString(newFile, modifiedFiles);
            writeContentToFile(newFile, fileContent);
        }else{
            throw new FileAlreadyExistsException(newFile.getAbsolutePath());
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if(exc != null){
            throw exc;
        }
        if(isJavaSourceFolder(dir)){
            isVisitingInJavaSourceFolder = false;
        }
        return CONTINUE;
    }
}
