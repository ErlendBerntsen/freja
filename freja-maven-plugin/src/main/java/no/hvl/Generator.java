package no.hvl;

import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.Assignment;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.MavenWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.util.List;

public class Generator {
    private final Configuration config;
    private static final List<String> pom = List.of("pom.xml", "*pom.xml", "**pom.xml");

    public Generator(Configuration config) {
        this.config = config;
    }

    public void generate() throws Exception {
        Parser parser = new Parser(config.getSourcePath());
        parser.parse();
        Assignment assignment = new AssignmentBuilder(parser).build();
        var projectWriter = new ProjectWriter(config, assignment);
        projectWriter.createAllProjects();
        String startCodePath =  config.getTargetPath() + File.separator + ProjectWriter.START_CODE_PROJECT_NAME;
        var descriptionWriter = new DescriptionWriter(startCodePath, assignment,
                projectWriter.getDescriptionMap(), config.getKeepOldDescriptionTemplates());
        descriptionWriter.createExerciseDescriptions();
        if(config.getFilesToIgnore().stream().noneMatch(pom::contains)){
            var mavenWriter = new MavenWriter("pom.xml", config.getTargetPath());
            mavenWriter.createPomFiles();
        }
    }
}
