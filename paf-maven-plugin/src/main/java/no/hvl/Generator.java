package no.hvl;

import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.Assignment;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.io.IOException;

public class Generator {
    private final String sourcePath;
    private final String targetPath;

    public Generator(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public void generate() throws IOException {
        Parser parser = new Parser(sourcePath);
        parser.parse();
        Assignment assignment = new AssignmentBuilder(parser).build();
        ProjectWriter projectWriter = new ProjectWriter(sourcePath, targetPath, assignment);
        projectWriter.createAllProjects();

        String startCodePath =  targetPath + File.separator + ProjectWriter.START_CODE_PROJECT_NAME;
        var descriptionWriter = new DescriptionWriter(startCodePath, assignment.getExercises(),
                projectWriter.getDescriptionMap());
        descriptionWriter.createExerciseDescriptions();
    }





}
