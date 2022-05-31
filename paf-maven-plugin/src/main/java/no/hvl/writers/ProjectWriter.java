package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import no.hvl.concepts.Assignment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProjectWriter {

    private String targetDirectoryPath;
    private String sourceDirectoryPath;
    private HashMap<String, CompilationUnit> startCodeProjectModifiedFiles;
    private HashMap<String, CompilationUnit> solutionProjectModifiedFiles;
    private HashSet<String> fileNamesToRemove;

    public ProjectWriter(List<CompilationUnit> startCodeProject, List<CompilationUnit> solutionProject,
                         HashSet<String> fileNamesToRemove, String sourceDirectoryPath, String targetDirectoryPath) {
        this.fileNamesToRemove = fileNamesToRemove;
        this.startCodeProjectModifiedFiles = new HashMap<>();
        this.solutionProjectModifiedFiles = new HashMap<>();
        startCodeProject.forEach(file -> this.startCodeProjectModifiedFiles.put(file.getStorage().get().getFileName(), file));
        solutionProject.forEach(file -> this.solutionProjectModifiedFiles.put(file.getStorage().get().getFileName(), file));
        this.sourceDirectoryPath = sourceDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
    }

    public ProjectWriter(String sourceDirectoryPath, String targetDirectoryPath, Assignment assignment){
        this.sourceDirectoryPath = sourceDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
        this.fileNamesToRemove = assignment.getFileNamesToRemove();
        this.startCodeProjectModifiedFiles = new HashMap<>();
        this.solutionProjectModifiedFiles = new HashMap<>();
        assignment.getSolutionCodeFiles().forEach(file ->
                this.startCodeProjectModifiedFiles.put(file.getStorage().get().getFileName(), file));
        assignment.getSolutionCodeFiles().forEach(file ->
                this.solutionProjectModifiedFiles.put(file.getStorage().get().getFileName(), file));

    }

    public void createProject(){
        File sourceDirectory = new File(sourceDirectoryPath);
        File targetDirectory = new File(targetDirectoryPath);
        emptyTargetDirectory(targetDirectory);

        File solutionProject = new File(targetDirectory.getAbsolutePath() + File.separator + "solution");
        solutionProject.mkdir();
        createFilesAndDirectories(sourceDirectory, solutionProject, solutionProjectModifiedFiles, false);

        File startCodeProject = new File(targetDirectory.getAbsolutePath() + File.separator + "startcode");
        startCodeProject.mkdir();
        createFilesAndDirectories(sourceDirectory, startCodeProject, startCodeProjectModifiedFiles, false);
    }

    public void copyProject() throws IOException {
        CopyFileVisitor copier = new CopyFileVisitor(Path.of(sourceDirectoryPath), Path.of(targetDirectoryPath));
        Files.walkFileTree(Path.of(sourceDirectoryPath), Set.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, copier);
    }

    private void emptyTargetDirectory(File targetDirectory){
        if(targetDirectory.listFiles() == null)return;
        Arrays.stream(targetDirectory.listFiles()).forEach(file -> {
            if(!file.getName().equals(".git")){
                if(file.isDirectory()){
                    emptyTargetDirectory(file);
                }
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createFilesAndDirectories(File srcDir, File dir, HashMap<String, CompilationUnit> modifiedFiles, boolean isSourceFolder){
        Arrays.stream(srcDir.listFiles()).forEach(file -> {
           if(file.isDirectory() && !file.getName().equals(".git")){
               File newDir = new File(dir.getAbsolutePath() + File.separator + file.getName());
               newDir.mkdir();
               if("src".equals(file.getName())|| "source".equals(file.getName()) || isSourceFolder){
                   createFilesAndDirectories(file, newDir, modifiedFiles, true);
               }else{
                   createFilesAndDirectories(file, newDir, modifiedFiles,false);
               }
           }
           if(file.isFile() && !fileNamesToRemove.contains(file.getName())){

               File newFile = new File(dir.getAbsolutePath() + File.separator + file.getName());
               try {
                   if(!isSourceFolder){
                       Files.copy(file.toPath(), newFile.toPath());
                   }else{
                       newFile.createNewFile();
                       DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
                       FileWriter fileWriter = new FileWriter(newFile);
                       fileWriter.write(printer.print(modifiedFiles.get(file.getName())));
                       fileWriter.close();
                   }

               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });

    }
}
