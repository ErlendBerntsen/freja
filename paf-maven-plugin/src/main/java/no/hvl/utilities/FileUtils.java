package no.hvl.utilities;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    private FileUtils (){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static void checkPathExists(String path) throws NoSuchFileException {
        if(!fileOrDirExists(Path.of(path))){
            throw new NoSuchFileException(path);
        }
    }

    public static boolean fileOrDirExists(Path path){
        return path.toFile().exists();
    }

    public static File tryToCreateDirectory(String parentDir, String dirName) throws IOException {
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

    public static File tryToCreateFile(String parentDir, String fileName) throws IOException {
        File file = new File(parentDir + File.separator + fileName);
        if(fileOrDirExists(file.toPath())){
            throw new FileAlreadyExistsException(file.getAbsolutePath());
        }
        if(file.createNewFile()){
            return file;
        }
        throw new IOException(String.format("Could not create file: %s for unknown reasons",
                file.getAbsolutePath()));
    }

    public static boolean isJavaSourceFolder(Path dir) {
        String dirName = dir.toFile().getName();
        return "src".equalsIgnoreCase(dirName) || "source".equalsIgnoreCase(dirName);
    }

    public static void writeContentToFile(File file, String fileContent) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(fileContent);
        fileWriter.close();
    }

    public static String printFileContentToString(File newFile, HashMap<String, CompilationUnit> modifiedFiles) {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        String fileName = newFile.getName();
        CompilationUnit fileAsNode = modifiedFiles.get(fileName);
        return printer.print(fileAsNode);
    }

    public static String getContentFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String content = reader.lines().collect(Collectors.joining("\n", "", "\n"));
        reader.close();
        return content;
    }



}
