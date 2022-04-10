package no.hvl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationUtils {

    List<AnnotationDeclaration> annotationDeclarations;
    public AnnotationUtils() throws IOException {
        var sourceRoot = new SourceRoot(Paths.get("C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\annotations"));
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");
        annotationDeclarations = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get().findFirst(AnnotationDeclaration.class).get())
                .collect(Collectors.toList());
        System.out.println(annotationDeclarations.toString());
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
        var annotation = node.getAnnotationByName(annotationName).get();
        if(annotation.isMarkerAnnotationExpr()){
            for(AnnotationDeclaration annotationDeclaration : annotationDeclarations){
                if(annotationName.equals(annotationDeclaration.getNameAsString())){
                    return annotationDeclaration.getMembers().stream()
                            .filter(BodyDeclaration::isAnnotationMemberDeclaration)
                            .map(BodyDeclaration::asAnnotationMemberDeclaration)
                            .filter(annotationMemberDeclaration -> memberName.equals(annotationMemberDeclaration.getNameAsString()))
                            .findFirst().get().getDefaultValue().get();
                }
            }
        }

        for(MemberValuePair pair : annotation.asNormalAnnotationExpr().getPairs()){
            if(pair.getName().asString().equals(memberName)){
                return pair.getValue();
            }
        }
        return null;
    }
}
