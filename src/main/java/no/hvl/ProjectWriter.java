package no.hvl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProjectWriter {

    private String targetDirectoryPath = "C:\\Users\\Acer\\OneDrive\\Documents\\Master\\output\\";
    private String sourceDirectoryPath = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\dat100example";
    private HashMap<String, CompilationUnit> modifiedFiles;
    private HashSet<String> fileNamesToRemove;

    public ProjectWriter(List<CompilationUnit> modifiedFiles, HashSet<String> fileNamesToRemove) {
        this.modifiedFiles = new HashMap<>();
        this.fileNamesToRemove = fileNamesToRemove;
        modifiedFiles.forEach(file -> this.modifiedFiles.put(file.getStorage().get().getFileName(), file));
    }

    public void createProject(){
        File sourceDirectory = new File(sourceDirectoryPath);
        File targetDirectory = new File(targetDirectoryPath);
        createFilesAndDirectories(targetDirectory, sourceDirectory);
    }

    private void createFilesAndDirectories(File dir, File srcDir){
        Arrays.stream(srcDir.listFiles()).forEach(file -> {
           if(file.isDirectory()){
               File newDir = new File(targetDirectoryPath + File.separator + file.getName());
               newDir.mkdir();
               createFilesAndDirectories(newDir, file);
           }
           if(file.isFile() && !fileNamesToRemove.contains(file.getName())){
               File newFile = new File(dir.getAbsolutePath() + File.separator + file.getName());
               try {
                   newFile.createNewFile();
                   DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
                   FileWriter fileWriter = new FileWriter(newFile);
                   fileWriter.write(printer.print(modifiedFiles.get(file.getName())));
                   fileWriter.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });

    }
}
