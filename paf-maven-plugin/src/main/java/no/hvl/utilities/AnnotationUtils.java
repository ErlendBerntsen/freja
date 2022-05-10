package no.hvl.utilities;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.annotations.CopyOption;

import java.util.Optional;

public class AnnotationUtils {

    Name annotationsPackageName = new Name(new Name("no.hvl.annotations"), "empty");

    public AnnotationUtils() {
    }

    public Name getAnnotationsPackageName() {
        return annotationsPackageName;
    }

    public Optional<CopyOption> getCopyValue (NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(AnnotationNames.IMPLEMENT_NAME)) {
            var expression = getAnnotationValue(node, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
            return Optional.of(CopyOption.getCopy(expression.asFieldAccessExpr().getNameAsString()));
        }
        return Optional.empty();
    }

    public Optional<int[]> getTaskNumber(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(AnnotationNames.IMPLEMENT_NAME)) {
            var expression = getAnnotationValue(node, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_NUMBER_NAME);

            return Optional.of(expression.asArrayInitializerExpr().getValues().stream()
                    .mapToInt(value -> value.asIntegerLiteralExpr().asNumber().intValue()).toArray());
        }
        return Optional.empty();
    }

    public Expression getAnnotationValue(NodeWithAnnotations<?> node, String annotationName, String memberName){
        //TODO ERror handling for unspecified values
        var annotation = node.getAnnotationByName(annotationName).get();
        for(MemberValuePair pair : annotation.asNormalAnnotationExpr().getPairs()){
            if(pair.getName().asString().equals(memberName)){
                return pair.getValue();
            }
        }
        return null;
    }

}
