package no.hvl;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Generator {
    private String sourcePath;
    private String targetPath;

    public Generator(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public void generate() throws IOException {
        Parser parser = new Parser();
        parser.parseDirectory(parser.findSourceDirectory(sourcePath).getAbsolutePath());

        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
        List<CompilationUnit> solutionProject = parser.createSolutionProject();

        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
                sourcePath, targetPath);
        projectWriter.createProject();

        String startCodePath =  targetPath + File.separator + "startcode";
        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, parser.getExercises());
        descriptionWriter.createFiles();
    }
}
