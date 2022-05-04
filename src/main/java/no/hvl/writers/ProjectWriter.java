package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProjectWriter {

    private String targetDirectoryPath = "C:\\Users\\Acer\\IntelliJProjects\\startcode\\";
    private String sourceDirectoryPath = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master";
    private HashMap<String, CompilationUnit> modifiedFiles;
    private HashSet<String> fileNamesToRemove;

    public ProjectWriter(List<CompilationUnit> modifiedFiles, HashSet<String> fileNamesToRemove) {
        this.modifiedFiles = new HashMap<>();
        this.fileNamesToRemove = fileNamesToRemove;
        modifiedFiles.forEach(file -> this.modifiedFiles.put(file.getStorage().get().getFileName(), file));
    }

    public ProjectWriter(List<CompilationUnit> modifiedFiles, HashSet<String> fileNamesToRemove, String sourceDirectoryPath, String targetDirectoryPath) {
        this.modifiedFiles = new HashMap<>();
        this.fileNamesToRemove = fileNamesToRemove;
        modifiedFiles.forEach(file -> this.modifiedFiles.put(file.getStorage().get().getFileName(), file));
        this.sourceDirectoryPath = sourceDirectoryPath;
        this.targetDirectoryPath = targetDirectoryPath;
    }

    public void createProject(){
        File sourceDirectory = new File(sourceDirectoryPath);
        File targetDirectory = new File(targetDirectoryPath);
        emptyTargetDirectory(targetDirectory);
        createFilesAndDirectories(sourceDirectory, targetDirectory, false);
    }

    private void emptyTargetDirectory(File targetDirectory){
        Arrays.stream(targetDirectory.listFiles()).forEach(file -> {
            if(file.isDirectory()){
                emptyTargetDirectory(file);
            }
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createFilesAndDirectories(File srcDir, File dir, boolean isSourceFolder){
        Arrays.stream(srcDir.listFiles()).forEach(file -> {
           if(file.isDirectory()){
               File newDir = new File(dir.getAbsolutePath() + File.separator + file.getName());
               newDir.mkdir();
               if("src".equals(file.getName())|| "source".equals(file.getName()) || isSourceFolder){
                   createFilesAndDirectories(file, newDir, true);
               }else{
                   createFilesAndDirectories(file, newDir, false);
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
