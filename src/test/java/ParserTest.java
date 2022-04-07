import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Copy;
import no.hvl.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    Parser parser;
    private final String IMPLEMENT_ANNOTATION_NAME = "Implement";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.saveSolutionReplacements("C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java");
        parser.parseFile("C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\Example.java");
    }

    @Test
    public void onlyNodesAnnotatedWithImplementIsFound(){
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        annotatedNodes
                .forEach(node ->
                        assertEquals(IMPLEMENT_ANNOTATION_NAME, node.getAnnotation(0).asAnnotationExpr().getName().asString()));

    }

    @Test
    public void methodAnnotatedWithImplementIsFound(){
        boolean methodWasFound = false;
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
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
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
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
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
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
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.isClassOrInterfaceDeclaration()){
                classWasFound = true;
            }
        }
        assertTrue(classWasFound);
    }


    @Test
    public void implementedAnnotationShouldBeRemoved(){
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        var annotatedNodeMaybe = annotatedNodes.getFirst();
        if(annotatedNodeMaybe.isPresent()){
            var annotatedNode = annotatedNodeMaybe.get();
            parser.removeAnnotationFromNode(annotatedNode, IMPLEMENT_ANNOTATION_NAME);
            assertTrue(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).isEmpty());
        }

    }

    @Test
    public void allImplementAnnotationsShouldBeRemovedFromFile(){
        parser.removeAnnotationsFromFile(parser.getCompilationUnits().get(0), IMPLEMENT_ANNOTATION_NAME);
        var annotatedNodes = parser.getAnnotatedNodesInFile(parser.getCompilationUnits().get(0), IMPLEMENT_ANNOTATION_NAME);
        assertTrue(annotatedNodes.isEmpty());
    }

    @Test
    public void solutionShouldBeRemoved(){
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("copy") &&
                            Copy.REMOVE_SOLUTION.toString().equals(pairs.getValue().asFieldAccessExpr().getNameAsString())){
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
    public void solutionStartAndEndShouldBeReplacedWithComments(){
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("copy") &&
                            Copy.REMOVE_SOLUTION.toString().equals(pairs.getValue().asFieldAccessExpr().getNameAsString())){
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
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
        for(var annotatedNode : annotatedNodes){
            if(annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().isNormalAnnotationExpr()){
                NormalAnnotationExpr annotationExpr = annotatedNode.getAnnotationByName(IMPLEMENT_ANNOTATION_NAME).get().asNormalAnnotationExpr();
                for(MemberValuePair pairs : annotationExpr.getPairs()){
                    if(pairs.getName().asString().equals("replacementId")){
                        String replacementId = pairs.getValue().asStringLiteralExpr().asString();
                        parser.replaceSolution(annotatedNode.asMethodDeclaration(), replacementId);
                        assertTrue(annotatedNode.asMethodDeclaration()
                        .getBody().get()
                                .getStatements().containsAll(parser.getSolutionReplacements().get(replacementId).getStatements()));
                    }
                }
            }
        }
    }

    @Test
    public void methodBodyShouldBeReplacedWithSpecifiedCode(){
        var annotatedNodes = parser.getAllAnnotatedNodes(IMPLEMENT_ANNOTATION_NAME);
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
