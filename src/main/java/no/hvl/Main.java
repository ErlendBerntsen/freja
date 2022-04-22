package no.hvl;


import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String REPLACEMENT_CODE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master\\source\\no\\hvl\\dat100ptc\\ReplacementCode.java";
    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master\\source";
    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master";

    public static void main (String[] args) throws IOException {
        Parser parser = new Parser();
        parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_LAPTOP);
        parser.parseDirectory(ASSIGNMENT_PROJECT_PATH_LAPTOP);
        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();

        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, parser.getFileNamesToRemove(),
                "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master", TARGET_PATH_LAPTOP);
        projectWriter.createProject();

    }
}
