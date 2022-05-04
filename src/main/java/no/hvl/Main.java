package no.hvl;


import com.github.javaparser.ast.CompilationUnit;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String REPLACEMENT_CODE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master\\source\\no\\hvl\\dat100ptc\\ReplacementCode.java";
    private static final String REPLACEMENT_CODE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020\\source\\no\\hvl\\dat100ptc\\ReplacementCode.java";

    private static final String ASSIGNMENT_PROJECT_SOURCE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master\\source";
    private static final String ASSIGNMENT_PROJECT_SOURCE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020\\source";

    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-master";
    private static final String ASSIGNMENT_PROJECT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020";

    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\startcode\\";
    private static final String TARGET_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020-output";


    public static void main (String[] args) throws IOException {
        Parser parser = new Parser();
        parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_DESKTOP);
        parser.parseDirectory(ASSIGNMENT_PROJECT_SOURCE_PATH_DESKTOP);

        DescriptionWriter descriptionWriter = new DescriptionWriter(parser.getExercises());
        descriptionWriter.createFiles();

        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
        List<CompilationUnit> solutionProject = parser.createSolutionProject();

        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
                ASSIGNMENT_PROJECT_PATH_DESKTOP, TARGET_PATH_DESKTOP);
        projectWriter.createProject();

    }
}
