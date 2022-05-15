import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.NodeUtils;
import no.hvl.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    Parser parser;
    NodeUtils nodeUtils = new NodeUtils();
    AnnotationUtils annotationUtils = new AnnotationUtils();
    private final String IMPLEMENT_ANNOTATION_NAME = "Implement";
    private static final String EXAMPLE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\paf-maven-plugin\\src\\test\\java\\examples\\Example.java";
    private static final String REPLACEMENT_CODE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\paf-maven-plugin\\src\\test\\java\\examples\\ReplacementMethods.java";
    private static final String EXAMPLE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\Example.java";
    private static final String REPLACEMENT_CODE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementMethods.java";

    public ParserTest() throws IOException {
    }

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        //parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_DESKTOP);
        //parser.parseFile(EXAMPLE_PATH_DESKTOP);
        parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_LAPTOP);
        parser.parseFile(EXAMPLE_PATH_LAPTOP);
    }

    @Test
    public void onlyNodesAnnotatedWithImplementIsFound(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        annotatedNodes
                .forEach(node ->
                        assertEquals(IMPLEMENT_ANNOTATION_NAME, node.getAnnotation(0).asAnnotationExpr().getName().asString()));

    }

    @Test
    public void methodAnnotatedWithImplementIsFound(){
        boolean methodWasFound = false;
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.isMethodDeclaration()){
                methodWasFound = true;
            }
        }
        assertTrue(methodWasFound);
    }

    @Test
    public void constructorAnnotatedWithImplementIsFound(){
        boolean constructorWasFound = false;
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.isConstructorDeclaration()){
                constructorWasFound= true;
            }
        }
        assertTrue(constructorWasFound);
    }

    @Test
    public void fieldAnnotatedWithImplementIsFound(){
        boolean fieldWasFound = false;
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.isFieldDeclaration()){
                fieldWasFound= true;
            }
        }
        assertTrue(fieldWasFound);
    }

    @Test
    public void classAnnotatedWithImplementIsFound(){
        boolean classWasFound = false;
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.isClassOrInterfaceDeclaration()){
                classWasFound = true;
            }
        }
        assertTrue(classWasFound);
    }

    @Test
    public void implementedAnnotationShouldBeRemoved(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        var annotatedNode = annotatedNodes.get(0);
        parser.removeAnnotationFromNode(annotatedNode, IMPLEMENT_ANNOTATION_NAME);
        assertTrue(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).isEmpty());
    }

    @Test
    public void allImplementAnnotationsShouldBeRemovedFromFile(){
        CompilationUnit file = parser.getCompilationUnitCopies().get(0);
        parser.removeAnnotationsFromFile(file , IMPLEMENT_ANNOTATION_NAME);
        var annotatedNodes = parser.getAnnotatedNodesInFile(file, IMPLEMENT_ANNOTATION_NAME);
        assertTrue(annotatedNodes.isEmpty());
    }

    @Test
    public void solutionShouldBeRemoved(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("copy") &&
                            CopyOption.REMOVE_SOLUTION.toString().equals(pairs.getValue().asFieldAccessExpr().getNameAsString())){
                        parser.removeSolution(annotatedNode.asMethodDeclaration());
                        for(Statement statement : annotatedNode.asMethodDeclaration().getBody().get().getStatements()){
                            assertTrue(!parser.isStartStatement(statement) && !parser.isEndStatement(statement));
                        }
                    }
                }
            }

        }
    }

    @Test
    public void fieldVariableShouldBeRemoved(){
        var compilationUnit = parser.getCompilationUnitCopies().get(0);
        var removedTypes = removeTypesFromClass(FieldDeclaration.class);
        for (BodyDeclaration<?> removedType : removedTypes){
            assertFalse(compilationUnit.isAncestorOf(removedType));
        }
    }

    @Test
    public void constructorShouldBeRemoved(){
        var compilationUnit = parser.getCompilationUnitCopies().get(0);
        var removedTypes = removeTypesFromClass(ConstructorDeclaration.class);
        for (BodyDeclaration<?> removedType : removedTypes){
            assertFalse(compilationUnit.isAncestorOf(removedType));
        }
    }

    @Test
    public void methodShouldBeRemoved(){
        var compilationUnit = parser.getCompilationUnitCopies().get(0);
        var removedTypes = removeTypesFromClass(MethodDeclaration.class);
        for (BodyDeclaration<?> removedType : removedTypes){
            assertFalse(compilationUnit.isAncestorOf(removedType));
        }
    }

    private <T extends BodyDeclaration<?>> List<BodyDeclaration<?>> removeTypesFromClass(Class<T> type){
        var compilationUnit = parser.getCompilationUnitCopies().get(0);
        var types = compilationUnit.findAll(type);
        List<BodyDeclaration<?>> removedTypes = new ArrayList<>();
        types.forEach(bodyDeclarationType -> {
            var copyValueMaybe = annotationUtils.getCopyValue(bodyDeclarationType);
            if(copyValueMaybe.isPresent()){
                var copyValue = copyValueMaybe.get();
                if(copyValue.equals(CopyOption.REMOVE_EVERYTHING)){
                    bodyDeclarationType.remove();
                    removedTypes.add(bodyDeclarationType);
                }
            }
        });
        return removedTypes;
    }

    @Test
    public void solutionStartAndEndShouldBeReplacedWithComments(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("copy") &&
                            CopyOption.REMOVE_SOLUTION.toString().equals(pairs.getValue().asFieldAccessExpr().getNameAsString())){
                        parser.removeSolution(annotatedNode.asMethodDeclaration());

                        assertTrue(
                                annotatedNode
                                        .asMethodDeclaration()
                                        .getAllContainedComments()
                                        .containsAll(
                                                List.of(new LineComment(parser.getStartComment()), new LineComment(parser.getEndComment()))));

                    }
                }
            }

        }

    }

    @Test
    public void solutionShouldBeReplacedWithSpecifiedCode(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("replacementId")){
                        String replacementId = pairs.getValue().asStringLiteralExpr().asString();
                        parser.replaceSolutionInMethodBody(annotatedNode.asMethodDeclaration(), replacementId);
                        var methodStatements =  nodeUtils.removeCommentsFromNodes(annotatedNode.asMethodDeclaration().getBody().get().getStatements());
                        var solutionStatements = nodeUtils.removeCommentsFromNodes(parser.getSolutionReplacements().get(replacementId).getStatements());
                        assertTrue(methodStatements.containsAll(solutionStatements));
                    }
                }
            }
        }


    }

    @Test
    public void methodBodyShouldBeReplacedWithSpecifiedCode(){
        var annotatedNodes = parser.getAllAnnotatedNodesInFiles(parser.getCompilationUnitCopies(), IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("replacementId")){
                        String replacementId = pairs.getValue().asStringLiteralExpr().asString();
                        parser.replaceBody(annotatedNode.asMethodDeclaration(), replacementId);
                        assertTrue(annotatedNode.asMethodDeclaration()
                                .getBody().get()
                                .getStatements().containsAll(parser.getSolutionReplacements().get(replacementId).getStatements()));
                    }


                }
            }

        }

    }

}
