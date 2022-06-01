package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.FileVisitResult.*;

public class CopyFileVisitor implements FileVisitor<Path> {

    private final Path source;
    private final Path target;
    private final HashMap<String, CompilationUnit> modifiedFiles;
    private final List<PathMatcher> pathMatchersToIgnore;
    private boolean isVisitingInJavaSourceFolder = false;

    public CopyFileVisitor(Path source, Path target,
                           HashMap<String, CompilationUnit> modifiedFiles, List<PathMatcher> pathMatchersToIgnore) {
        this.source = source;
        this.target = target;
        this.modifiedFiles = modifiedFiles;
        this.pathMatchersToIgnore = pathMatchersToIgnore;
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

    private boolean isJavaSourceFolder(Path dir) {
        String dirName = dir.toFile().getName();
        return "src".equalsIgnoreCase(dirName) || "source".equalsIgnoreCase(dirName);
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
            String fileContent = printFileContentToString(newFile);
            writeContentToFile(newFile, fileContent);
        }else{
            throw new FileAlreadyExistsException(newFile.getAbsolutePath());
        }
    }

    private void writeContentToFile(File file, String fileContent) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(fileContent);
        fileWriter.close();
    }

    private String printFileContentToString(File newFile) {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        String fileName = newFile.getName();
        CompilationUnit fileAsNode = modifiedFiles.get(fileName);
        return printer.print(fileAsNode);
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
