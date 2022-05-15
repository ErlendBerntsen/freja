package no.hvl;


import com.github.javaparser.ast.CompilationUnit;
import no.hvl.maven.PafMojo;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {


    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020";
    private static final String ASSIGNMENT_PROJECT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020";

    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-generated";
    private static final String TARGET_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020-generated";


    public static void main (String[] args) throws IOException {
        generate();
    }

    public static void generate() throws IOException {
        Parser parser = new Parser();
        parser.parseDirectory(parser.findSourceDirectory(ASSIGNMENT_PROJECT_PATH_LAPTOP).getAbsolutePath());

        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
        List<CompilationUnit> solutionProject = parser.createSolutionProject();

        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
                ASSIGNMENT_PROJECT_PATH_LAPTOP, TARGET_PATH_LAPTOP);
        projectWriter.createProject();

        String startCodePath =  TARGET_PATH_LAPTOP + File.separator + "startcode";
        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, parser.getExercises());
        descriptionWriter.createFiles();
    }
}
