package testUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestUtils {

    public static final String TestIdAnnotationName = "TestId";

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

    private static Node findAnnotationWithTestId(List<SingleMemberAnnotationExpr> singleMemberAnnotationExprs, int targetId) {
        for(SingleMemberAnnotationExpr singleMemberAnnotationExpr : singleMemberAnnotationExprs){
            if(isTestIdWithEqualId(singleMemberAnnotationExpr, targetId)){
                return getParentNode(singleMemberAnnotationExpr, targetId);
            }

        }
        throw new IllegalStateException(String.format("Cant find any %s annotation with the value: %s",
                TestIdAnnotationName, targetId));
    }

    private static boolean isTestIdWithEqualId (SingleMemberAnnotationExpr singleMemberAnnotationExpr, int targetId){
        if(TestIdAnnotationName.equals(singleMemberAnnotationExpr.getName().asString())){
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
                    "%s annotation with the value: %d", TestIdAnnotationName, targetId));
        }

    }
}
