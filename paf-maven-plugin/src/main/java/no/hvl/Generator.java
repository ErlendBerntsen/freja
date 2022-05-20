package no.hvl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import no.hvl.concepts.AbstractTask;
import no.hvl.concepts.AssignmentMetaModel;
import no.hvl.concepts.AssignmentMetaModelBuilder;
import no.hvl.concepts.TaskOperations;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.NodeUtils;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        AssignmentMetaModel assignmentMetaModel =
                new AssignmentMetaModelBuilder(parser.getCompilationUnitCopies()).build();
        createStartCodeJavaFiles(assignmentMetaModel);

//        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
//        List<CompilationUnit> solutionProject = parser.createSolutionProject();
//
//        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
//                sourcePath, targetPath);
//        projectWriter.createProject();
//
//        String startCodePath =  targetPath + File.separator + "startcode";
//        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, parser.getExercises());
//        descriptionWriter.createFiles();
    }

    public List<CompilationUnit> createStartCodeJavaFiles(AssignmentMetaModel assignmentMetaModel){
        //TODO
        //Get list of all files
        //Remove Nodes Annotated With @Remove
        //Remove implement annotations
        //Remove all annotation import from all files
        //Modify all nodes annotated with @Implement
            //Modify according to copyOption value. Use createStartCode from TaskOperations interface
        //Return modified files
        List<AbstractTask> tasks = assignmentMetaModel.getTasks();
        for(AbstractTask task : tasks){
            //TODO add check optional get
            Node parentNode = task.getNode().getParentNode().get();
            parentNode.replace(task.getNode(), task.createStartCode());
        }
        assignmentMetaModel.getFiles().forEach(System.out::println);
        return new ArrayList<>();
    }
}
