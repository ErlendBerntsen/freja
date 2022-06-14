package no.hvl;

import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.Assignment;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.io.IOException;

public class Generator {
    private final Configuration config;

    public Generator(Configuration config) {
        this.config = config;
    }

    public void generate() throws IOException {
        Parser parser = new Parser(config.getSourcePath());
        parser.parse();
        Assignment assignment = new AssignmentBuilder(parser).build();
        var projectWriter = new ProjectWriter(config, assignment);
        projectWriter.createAllProjects();
        String startCodePath =  config.getTargetPath() + File.separator + ProjectWriter.START_CODE_PROJECT_NAME;
        var descriptionWriter = new DescriptionWriter(startCodePath, assignment.getExercises(),
                projectWriter.getDescriptionMap(), config.isKeepOldDescriptionTemplates());
        descriptionWriter.createExerciseDescriptions();
    }





}
