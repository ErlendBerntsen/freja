package no.hvl;


import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String GPSPOINT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\dat100example\\oppgave1\\GPSPoint.java";
    private static final String REPLACEMENT_CODE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java";
    private static final String GPSPOINT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\GPSPoint.java";
    private static final String REPLACEMENT_CODE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java";


    public static void main (String[] args) throws IOException {
        Parser parser = new Parser();
        parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_LAPTOP);
        parser.parseDirectory("C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\dat100example");
        List<CompilationUnit> files = parser.getCompilationUnits().stream()
                .map(cu -> parser.modifyAllAnnotatedNodesInFile(cu, "Implement"))
                .collect(Collectors.toList());

        ProjectWriter projectWriter = new ProjectWriter(files);
        projectWriter.createProject();

    }
}
