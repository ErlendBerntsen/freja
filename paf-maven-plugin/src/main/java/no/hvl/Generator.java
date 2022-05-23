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
        createSolutionCodeJavaFiles(assignmentMetaModel);

//        List<CompilationUnit> startCodeProject = parser.createStartCodeProject();
//        List<CompilationUnit> solutionProject = parser.createSolutionProject();
//
//        ProjectWriter projectWriter = new ProjectWriter(startCodeProject, solutionProject,  parser.getFileNamesToRemove(),
//                sourcePath, targetPath);
//        projectWriter.createProject();
//
//        String startCodePath =  targetPath + File.separator + "startcode";
//        DescriptionWriter descriptionWriter = new DescriptionWriter(startCodePath, assignmentMetaModel.getExercises());
//        descriptionWriter.createFiles();
    }

    private void createSolutionCodeJavaFiles(AssignmentMetaModel assignmentMetaModel){
        modifyJavaFiles(assignmentMetaModel.getSolutionCodeFiles(), assignmentMetaModel.getTasks(), true);
    }

    private void modifyJavaFiles(List<CompilationUnit> files, List<AbstractTask> tasks, boolean isSolutionCode){
        removePafInformation(files);
        for(AbstractTask task : tasks){
            Node oldTaskNode = NodeUtils.findBodyDeclarationCopyInFiles(files, task.getNode());
            BodyDeclaration<?> newTaskNode = createNewTaskNode(isSolutionCode, task);
            updateTaskNode(oldTaskNode, newTaskNode);
        }
    }

    private void removePafInformation(List<CompilationUnit> files){
        removeNodesAnnotatedWithRemove(files);
        removePafImports(files);
    }

    private void removeNodesAnnotatedWithRemove(List<CompilationUnit> files) {
        //TODO Remember that ProjectWriter need to know what file names to remove
        List<BodyDeclaration<?>> nodesAnnotatedWithRemove = AnnotationUtils
                .getAllNodesInFilesAnnotatedWith(files, AnnotationNames.REMOVE_NAME);
        NodeUtils.removeNodesFromFiles(files, nodesAnnotatedWithRemove);
    }

    private void removePafImports(List<CompilationUnit> files) {
        for(CompilationUnit file : files){
            AnnotationUtils.removeAnnotationImportsFromFile(file);
        }
    }

    private BodyDeclaration<?> createNewTaskNode(boolean isSolutionCode, AbstractTask task) {
        BodyDeclaration<?> newTaskNode;
        if(isSolutionCode){
            newTaskNode = task.createSolutionCode();
        }else{
            newTaskNode = task.createStartCode();
        }
        AnnotationUtils.removeAnnotationTypeFromNode(newTaskNode, AnnotationNames.IMPLEMENT_NAME);
        return newTaskNode;
    }

    private void updateTaskNode(Node oldTaskNode, Node newTaskNode){
        Optional<Node> parentNode = oldTaskNode.getParentNode();
        if(parentNode.isPresent()){
            parentNode.get().replace(oldTaskNode, newTaskNode);
        }else{
            throw new IllegalStateException(String.format("Can not find parent node of type annotated with @%s"
                    , AnnotationNames.IMPLEMENT_NAME));
        }
    }

    private void createStartCodeJavaFiles(AssignmentMetaModel assignmentMetaModel){
        //TODO
        //Move to AssignmentMetaModelBuilder so that AssignmentMetaModel can be an immutable record?
        modifyJavaFiles(assignmentMetaModel.getStartCodeFiles(), assignmentMetaModel.getTasks(), false);
    }

}
