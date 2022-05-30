package no.hvl;

import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.Assignment;
import no.hvl.writers.ProjectWriter;

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
        ProjectWriter projectWriter = new ProjectWriter(assignment.getStartCodeFiles(),
                assignment.getSolutionCodeFiles(), assignment.getFileNamesToRemove(), sourcePath, targetPath);
        projectWriter.createProject();
//
//        String startCodePath =  targetPath + File.separator + "startcode";
//        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, assignmentMetaModel.getExercises());
//        descriptionWriter.createFiles();
    }





}
