package no.hvl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.AbstractTask;
import no.hvl.concepts.AssignmentMetaModel;
import no.hvl.concepts.AssignmentMetaModelBuilder;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.NodeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        AssignmentMetaModel assignmentMetaModel =
                new AssignmentMetaModelBuilder(parser).build();
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

    public void createStartCodeJavaFiles(AssignmentMetaModel assignmentMetaModel){
        List<CompilationUnit> startCodeFiles = assignmentMetaModel.getStartCodeFiles();
        removePafInformation(startCodeFiles);
        //TODO
        //Move to AssignmentMetaModelBuilder so that AssignmentMetaModel can be an immutable record?
        List<AbstractTask> tasks = assignmentMetaModel.getTasks();
        for(AbstractTask task : tasks){
            Node nodeCopy = NodeUtils.findNodeInFiles(startCodeFiles, task.getNode());
            updateTaskNode(task, nodeCopy);
        }
        System.out.println("PARSED FILES:");
        assignmentMetaModel.getParsedFiles().forEach(System.out::println);

        System.out.println("START CODE FILES:");
        assignmentMetaModel.getStartCodeFiles().forEach(System.out::println);
    }

    private void removePafInformation(List<CompilationUnit> files){
        removeNodesAnnotatedWithRemove(files);
        removePafImports(files);
    }

    private void removeNodesAnnotatedWithRemove(List<CompilationUnit> files) {
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = AnnotationUtils
                .getAllAnnotatedNodesInFiles(files, AnnotationNames.REMOVE_NAME);
        NodeUtils.removeNodesFromFiles(files, nodesAnnotatedWithRemove);
    }

    private void removePafImports(List<CompilationUnit> files) {
        for(CompilationUnit file : files){
            AnnotationUtils.removeAnnotationImportsFromFile(file);
        }
    }

    private void updateTaskNode(AbstractTask task, Node nodeCopy){
        BodyDeclaration<?> updatedNode = task.createStartCode();
        AnnotationUtils.removeAnnotationFromNode(updatedNode, AnnotationNames.IMPLEMENT_NAME);
        Optional<Node> parentNode = nodeCopy.getParentNode();
        if(parentNode.isPresent()){
            parentNode.get().replace(nodeCopy, updatedNode);
        }else{
            throw new IllegalStateException(String.format("Can not find parent node of type annotated with @%s"
                    , AnnotationNames.IMPLEMENT_NAME));
        }
    }
}
