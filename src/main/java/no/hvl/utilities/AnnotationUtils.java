package no.hvl.utilities;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.utils.SourceRoot;
import no.hvl.annotations.Copy;
import no.hvl.utilities.AnnotationNames;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationUtils {

    List<AnnotationDeclaration> annotationDeclarations;
    Name annotationsPackageName;
    private static final String ANNOTATIONS_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\annotations";
    private static final String ANNOTATIONS_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\annotations";


    public AnnotationUtils() throws IOException {
        var sourceRoot = new SourceRoot(Paths.get(ANNOTATIONS_PATH_DESKTOP));
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");
        var annotationFiles = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(parseResult -> parseResult.getResult().get())
                .collect(Collectors.toList());

        annotationsPackageName = annotationFiles.get(0).getPackageDeclaration().get().getName();

        annotationDeclarations = annotationFiles.stream()
                .filter(file -> file.findFirst(AnnotationDeclaration.class).isPresent())
                .map(annotationFile -> annotationFile.findFirst(AnnotationDeclaration.class).get())
                .collect(Collectors.toList());
    }

    public Name getAnnotationsPackageName() {
        return annotationsPackageName;
    }

    public List<AnnotationDeclaration> getAnnotationDeclarations() {
        return annotationDeclarations;
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

    public int getTaskNumberAsInt(int[] taskNumber){
        int x = 0;
        for(int number : taskNumber){
            x *= 10;
            x += number;
        }
        return x;
    }
}
