package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;

import static java.nio.file.FileVisitResult.*;

public class CopyFileVisitor implements FileVisitor<Path> {

    private final Path source;
    private final Path target;
    private HashMap<String, CompilationUnit> modifiedFiles;
    private final HashSet<String> fileNamesToRemove;
    private boolean isVisitingInJavaSourceFolder = false;

    public CopyFileVisitor(Path source, Path target,
                           HashMap<String, CompilationUnit> modifiedFiles, HashSet<String> fileNamesToRemove) {
        this.source = source;
        this.target = target;
        this.modifiedFiles = modifiedFiles;
        this.fileNamesToRemove = fileNamesToRemove;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(isJavaSourceFolder(dir)){
            isVisitingInJavaSourceFolder = true;
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
        //TODO error handling
        File newFile = new File(target.resolve(source.relativize(file)).toString());
        newFile.createNewFile();
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        FileWriter fileWriter = new FileWriter(newFile);
        fileWriter.write(printer.print(modifiedFiles.get(file.toFile().getName())));
        fileWriter.close();
    }

    private boolean fileShouldBeCopied(Path file){
        String fileName = file.toFile().getName();
        return !fileNamesToRemove.contains(fileName);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return TERMINATE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if(isJavaSourceFolder(dir)){
            isVisitingInJavaSourceFolder = false;
        }
        return CONTINUE;
    }
}
