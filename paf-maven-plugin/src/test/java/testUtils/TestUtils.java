package testUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final String TEST_ID_ANNOTATION_NAME = "TestId";

    private TestUtils(){
        throw new IllegalStateException("This is a utility class. It is not meant to be instantiated");
    }

    public static NodeWithAnnotations<?> getNodeWithId(List<CompilationUnit> files, int targetId) {
        List<SingleMemberAnnotationExpr> singleMemberAnnotationExprs = getAllSingleMemberAnnotationExprFromFiles(files);
        return (NodeWithAnnotations<?>) findAnnotationWithTestId(singleMemberAnnotationExprs, targetId);
    }

    private static List<SingleMemberAnnotationExpr> getAllSingleMemberAnnotationExprFromFiles(
            List<CompilationUnit> files){
        List<SingleMemberAnnotationExpr> singleMemberAnnotationExprs = new ArrayList<>();
        for(CompilationUnit file : files){
            singleMemberAnnotationExprs.addAll(file.findAll(SingleMemberAnnotationExpr.class));
        }
        return singleMemberAnnotationExprs;
    }

    private static Node findAnnotationWithTestId
            (List<SingleMemberAnnotationExpr> singleMemberAnnotationExprs, int targetId) {
        boolean found = false;
        SingleMemberAnnotationExpr singleMemberAnnotationExprWithTargetId = null;
        for(SingleMemberAnnotationExpr singleMemberAnnotationExpr : singleMemberAnnotationExprs){
            if(isTestIdWithEqualId(singleMemberAnnotationExpr, targetId)){
                if(found){
                    throw new IllegalStateException(
                            String.format("Found two different \"%s\" annotations with the id \"%d\":%n%s%n%n%s",
                            TEST_ID_ANNOTATION_NAME, targetId,
                                    getParentNode(singleMemberAnnotationExprWithTargetId, targetId),
                                    getParentNode(singleMemberAnnotationExpr, targetId)));
                }
                found = true;
                singleMemberAnnotationExprWithTargetId = singleMemberAnnotationExpr;
            }

        }
        if(found){
            return getParentNode(singleMemberAnnotationExprWithTargetId, targetId);
        }
        throw new IllegalStateException(String.format("Cant find any %s annotation with the value: %s",
                TEST_ID_ANNOTATION_NAME, targetId));
    }

    private static boolean isTestIdWithEqualId (SingleMemberAnnotationExpr singleMemberAnnotationExpr, int targetId){
        if(TEST_ID_ANNOTATION_NAME.equals(singleMemberAnnotationExpr.getName().asString())){
            int id = singleMemberAnnotationExpr.getMemberValue().asIntegerLiteralExpr().asNumber().intValue();
            return id == targetId;
        }
        return false;
    }

    private static Node getParentNode(SingleMemberAnnotationExpr singleMemberAnnotationExpr, int targetId){
        Optional<Node> parentNode = singleMemberAnnotationExpr.getParentNode();
        if(parentNode.isPresent()){
            return parentNode.get();
        }else{
            throw new IllegalStateException(String.format("Cant find the parent node of " +
                    "%s annotation with the value: %d", TEST_ID_ANNOTATION_NAME, targetId));
        }

    }
}
