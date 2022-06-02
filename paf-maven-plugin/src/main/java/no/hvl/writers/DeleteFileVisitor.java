package no.hvl.writers;

import no.hvl.utilities.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import static java.nio.file.FileVisitResult.*;
import static no.hvl.utilities.FileUtils.*;

public class DeleteFileVisitor implements FileVisitor<Path> {

    private final Path rootDir;
    private final HashMap<String, String> descriptionMap;
    private boolean isVisitingInDescriptionsFolder;

    public DeleteFileVisitor(Path rootDir, HashMap<String, String> descriptionMap) {
        this.rootDir = rootDir;
        this.descriptionMap = descriptionMap;
        isVisitingInDescriptionsFolder = false;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
        if(isDescriptionsFolder(dir)){
            isVisitingInDescriptionsFolder = true;
        }
        return CONTINUE;
    }

    private boolean isDescriptionsFolder(Path dir) {
        return dir.toFile().getName().equals(DescriptionWriter.DESCRIPTIONS_FOLDER_NAME);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(isVisitingInDescriptionsFolder){
            File pathAsFile = file.toFile();
            String fileName = pathAsFile.getName();
            descriptionMap.put(fileName, getContentFromFile(pathAsFile));
        }
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
        if(isDescriptionsFolder(dir)){
            isVisitingInDescriptionsFolder = false;
        }
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
