package no.hvl;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.util.Optional;

public class AnnotationUtils {

    public AnnotationUtils() {
    }

    public Optional<Copy> getCopyValue (NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(AnnotationNames.IMPLEMENT_NAME)) {
            var expression = getAnnotationValue(node, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
            return Optional.of(Copy.getCopy(expression.asFieldAccessExpr().getNameAsString()));
        }
        return Optional.empty();
    }

    public Expression getAnnotationValue(NodeWithAnnotations<?> node, String annotationName, String memberName){
        //TODO ERror handling
        var annotation = node.getAnnotationByName(annotationName).get().asNormalAnnotationExpr();
        for(MemberValuePair pair : annotation.getPairs()){
            if(pair.getName().asString().equals(memberName)){
                return pair.getValue();
            }
        }
        return null;
    }
}
