package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.concepts.Assignment;
import no.hvl.utilities.NodeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ProjectWriter {
    public static final String SOLUTION_PROJECT_NAME = "solution";
    public static final String START_CODE_PROJECT_NAME = "startcode";

    private final String targetDirectoryPath;
    private final String sourceDirectoryPath;
    private final Assignment assignment;
    private List<PathMatcher> pathMatchersToIgnore;

    public ProjectWriter(String sourceDirectoryPath, String targetDirectoryPath, Assignment assignment)
            throws NoSuchFileException {
        checkPathExists(sourceDirectoryPath);
        checkPathExists(targetDirectoryPath);
        this.sourceDirectoryPath = sourceDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
        this.assignment = assignment;
        createPathMatchersToIgnore();
    }

    private void checkPathExists(String path) throws NoSuchFileException {
        if(!fileOrDirExists(Path.of(path))){
            throw new NoSuchFileException(path);
        }
    }

    private boolean fileOrDirExists(Path path){
        return path.toFile().exists();
    }

    private void createPathMatchersToIgnore() {
        this.pathMatchersToIgnore = new ArrayList<>();
        for(String fileName : assignment.getFileNamesToRemove()){
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + fileName);
            pathMatchersToIgnore.add(pathMatcher);
        }
    }

    public void createAllProjects() throws IOException {
        clearTargetDir();
        createSolutionAndStartProject();
    }

    public void clearTargetDir() throws IOException {
        Files.walkFileTree(Path.of(targetDirectoryPath), Set.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, new DeleteFileVisitor(Path.of(targetDirectoryPath)));
    }

    public void createSolutionAndStartProject() throws IOException {
        createProject(START_CODE_PROJECT_NAME, assignment.getStartCodeFiles());
        createProject(SOLUTION_PROJECT_NAME, assignment.getSolutionCodeFiles());
    }

    private void createProject(String name, List<CompilationUnit> modifiedFiles) throws IOException {
        File directory = tryToCreateDirectory(targetDirectoryPath, name);
        copyProject(directory.getAbsolutePath(), getFileNameModifiedFileMap(modifiedFiles));
    }

    private File tryToCreateDirectory(String parentDir, String dirName) throws IOException {
        File dir = new File(parentDir + File.separator + dirName);
        if(fileOrDirExists(dir.toPath())){
            throw new FileAlreadyExistsException(dir.getAbsolutePath());
        }
        if(dir.mkdir()){
            return dir;
        }
        throw new IOException(String.format("Could not create directory: %s for unknown reasons",
                dir.getAbsolutePath()));
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

}
