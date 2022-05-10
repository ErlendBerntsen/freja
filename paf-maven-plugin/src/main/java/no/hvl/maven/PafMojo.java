package no.hvl.maven;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.Parser;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.MavenWriter;
import no.hvl.writers.ProjectWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
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

            String startCodePath =  targetPath + File.separator + "startcode";
            DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, parser.getExercises());
            descriptionWriter.createFiles();

            MavenWriter mavenWriter = new MavenWriter(targetPath);
            mavenWriter.createPomFiles();

        } catch (IOException | ParserConfigurationException | TransformerException | SAXException e) {
            e.printStackTrace();

        }

    }
}
