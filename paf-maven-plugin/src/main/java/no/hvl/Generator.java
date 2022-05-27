package no.hvl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.Assignment;
import no.hvl.writers.ProjectWriter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class Generator {
    private String sourcePath;
    private String targetPath;

    public Generator(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public void generate() throws IOException {
        Parser parser = new Parser(sourcePath);
        parser.parse();
        parser.createAssignmentMetaModel();

//        AssignmentMetaModel assignmentMetaModel =
//                new AssignmentMetaModelBuilder(parser).build();
//        createStartCodeJavaFiles(assignmentMetaModel);
//        createSolutionCodeJavaFiles(assignmentMetaModel);

        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
        List<CompilationUnit> solutionProject = parser.createSolutionProject();

        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject, parser.getFileNamesToRemove()
                , sourcePath, targetPath);
        projectWriter.createProject();
//
//        String startCodePath =  targetPath + File.separator + "startcode";
//        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, assignmentMetaModel.getExercises());
//        descriptionWriter.createFiles();
    }





}
