package no.hvl.maven;

import no.hvl.Generator;
import no.hvl.writers.MavenWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
public class PafMojo extends AbstractMojo {

    @Parameter(property = "targetPath", required = true)
    String targetPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String projectPath = System.getProperty("user.dir");
        getLog().info("Project path: " + projectPath);
        getLog().info("Target path: " + targetPath);
        Generator generator = new Generator(projectPath, targetPath);
        try {
            generator.generate();
            MavenWriter mavenWriter = new MavenWriter("pom.xml", targetPath);
            mavenWriter.createPomFiles();
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException e) {
            e.printStackTrace();
        }

    }
}
