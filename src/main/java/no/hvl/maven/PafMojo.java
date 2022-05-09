package no.hvl.maven;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.Parser;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;

@Mojo(name = "paf", defaultPhase = LifecyclePhase.COMPILE)
public class PafMojo extends AbstractMojo {

    @Parameter(property = "targetPath", required = true)
    String targetPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String projectPath = System.getProperty("user.dir");
        getLog().info("Project path: " + projectPath);
        getLog().info("Target path: " + targetPath);
        try {
            Parser parser = new Parser();
            parser.parseDirectory(parser.findSourceDirectory(projectPath).getAbsolutePath());
            List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
            List<CompilationUnit> solutionProject = parser.createSolutionProject();

            ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
                    projectPath, targetPath);
            projectWriter.createProject();
            DescriptionWriter descriptionWriter = new DescriptionWriter(targetPath, parser.getExercises());
            descriptionWriter.createFiles();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
