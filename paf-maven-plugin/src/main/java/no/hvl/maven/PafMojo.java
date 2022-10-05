package no.hvl.maven;

import no.hvl.Configuration;
import no.hvl.Generator;
import no.hvl.writers.MavenWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
public class PafMojo extends AbstractMojo {

    private static final List<String> pom = List.of("pom.xml", "*pom.xml", "**pom.xml");

    @Parameter(property = "targetPath", required = true)
    private String targetPath;

    @Parameter(property = "ignore")
    private List<String> ignore;

    @Parameter(property = "keepOldDescriptions")
    private boolean keepOldDescriptions;

    @Override
    public void execute() {
        String projectPath = System.getProperty("user.dir");
        getLog().info("Project path: " + projectPath);
        getLog().info("Target path: " + targetPath);
        var configuration = new Configuration(projectPath, targetPath, ignore, keepOldDescriptions);
        var generator = new Generator(configuration);
        try {
            generator.generate();
            if(ignore.stream().noneMatch(pom::contains)){
                var mavenWriter = new MavenWriter("pom.xml", targetPath);
                mavenWriter.createPomFiles();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
