package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.Configuration;
import no.hvl.concepts.Assignment;
import no.hvl.utilities.NodeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static no.hvl.utilities.FileUtils.*;

public class ProjectWriter {
    public static final String SOLUTION_PROJECT_NAME = "solution";
    public static final String START_CODE_PROJECT_NAME = "startcode";

    private final Configuration config;
    private final String targetDirectoryPath;
    private final String sourceDirectoryPath;
    private final Assignment assignment;
    private List<PathMatcher> pathMatchersToIgnore;
    private final HashMap<String, String> descriptionMap;

    public ProjectWriter(Configuration config, Assignment assignment)
            throws NoSuchFileException {
        this.config = config;
        this.sourceDirectoryPath = config.getSourcePath();
        this.targetDirectoryPath = config.getTargetPath();
        checkPathExists(sourceDirectoryPath);
        checkPathExists(targetDirectoryPath);
        this.assignment = assignment;
        this.descriptionMap = new HashMap<>();
        createPathMatchersToIgnore();
    }

    private void createPathMatchersToIgnore() {
        List<String> filesToIgnore = new ArrayList<>(assignment.getFileNamesToRemove());
        List<String> fileNames = addDoubleStars(assignment.getFileNamesToRemove());
        filesToIgnore.addAll(fileNames);
        filesToIgnore.addAll(config.getFilesToIgnore());
        this.pathMatchersToIgnore = new ArrayList<>();
        for(String fileName : filesToIgnore){
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + fileName);
            pathMatchersToIgnore.add(pathMatcher);
        }
    }

    private List<String> addDoubleStars(Collection<String> names){
        List<String> namesWithDoubleStartsPrefix = new ArrayList<>();
        for(String name : names){
            namesWithDoubleStartsPrefix.add("**" + name);
        }
        return namesWithDoubleStartsPrefix;
    }


    public void createAllProjects() throws IOException {
        clearTargetDir();
        createSolutionAndStartProject();
    }

    public void clearTargetDir() throws IOException {
        Files.walkFileTree(Path.of(targetDirectoryPath), Set.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, new DeleteFileVisitor(Path.of(targetDirectoryPath), descriptionMap));
    }

    public void createSolutionAndStartProject() throws IOException {
        createProject(START_CODE_PROJECT_NAME, assignment.getStartCodeFiles());
        createProject(SOLUTION_PROJECT_NAME, assignment.getSolutionCodeFiles());
    }

    private void createProject(String name, List<CompilationUnit> modifiedFiles) throws IOException {
        File directory = tryToCreateDirectory(targetDirectoryPath, name);
        copyProject(directory.getAbsolutePath(), getFileNameModifiedFileMap(modifiedFiles));
    }

    public void copyProject(String targetPath, HashMap<String, CompilationUnit> modifiedFiles)
            throws IOException {
        CopyFileVisitor copier = new CopyFileVisitor(Path.of(sourceDirectoryPath), Path.of(targetPath),
                modifiedFiles, pathMatchersToIgnore);
        Files.walkFileTree(Path.of(sourceDirectoryPath), Set.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, copier);
    }

    private HashMap<String, CompilationUnit> getFileNameModifiedFileMap(List<CompilationUnit> modifiedFiles){
        HashMap<String, CompilationUnit> fileNameModifiedFileMap = new HashMap<>();
        for(CompilationUnit file : modifiedFiles){
            String fileName = NodeUtils.getFileName(file);
            fileNameModifiedFileMap.put(fileName, file);
        }
        return fileNameModifiedFileMap;
    }

    public void addPathMatchersToIgnore(String pattern){
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        pathMatchersToIgnore.add(pathMatcher);
    }

    public HashMap<String, String> getDescriptionMap() {
        return descriptionMap;
    }
}
