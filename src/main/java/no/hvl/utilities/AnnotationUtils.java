package no.hvl.utilities;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import no.hvl.annotations.Copy;
import no.hvl.utilities.AnnotationNames;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationUtils {

    Name annotationsPackageName = new Name("no.hvl.annotations");

    public AnnotationUtils() {
    }

    public Name getAnnotationsPackageName() {
        return annotationsPackageName;
    }

    public Optional<Copy> getCopyValue (NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(AnnotationNames.IMPLEMENT_NAME)) {
            var expression = getAnnotationValue(node, AnnotationNames.IMPLEMENT_NAME, AnnotationNames.IMPLEMENT_COPY_NAME);
            return Optional.of(Copy.getCopy(expression.asFieldAccessExpr().getNameAsString()));
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

    public int getTaskNumberAsInt(int[] taskNumber){
        int x = 0;
        for(int number : taskNumber){
            x *= 10;
            x += number;
        }
        return x;
    }
}
