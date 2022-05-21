package no.hvl.utilities;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.annotations.CopyOption;
import no.hvl.exceptions.MissingAnnotationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.AnnotationNames.*;

public class AnnotationUtils {

    private AnnotationUtils(){
        throw new IllegalStateException("This is a utility class. It is not meant to be instantiated");
    }

    public static  Name getAnnotationsPackageName() {
        return new Name(new Name("no.hvl.annotations"), "empty");
    }

    public static CopyOption getCopyOptionValueInImplementAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(IMPLEMENT_NAME)) {
            var expression = getAnnotationMemberValue(node, IMPLEMENT_NAME, IMPLEMENT_COPY_NAME);
            return CopyOption.getCopy(expression.asFieldAccessExpr().getNameAsString());
        }
        throw new MissingAnnotationException(IMPLEMENT_COPY_NAME);
    }

    public static Expression getAnnotationMemberValue(NodeWithAnnotations<?> node,
                                                      String annotationName, String memberName){
        Optional<AnnotationExpr> annotation = node.getAnnotationByName(annotationName);
        if(annotation.isPresent()){
            return getAnnotationMemberValueFromAnnotationExpr(annotation.get(), memberName);
        }else{
            throw new IllegalArgumentException(
                    String.format("Could not find annotation \"%s\" on the node:%n%s", annotationName, node));
        }
    }

    private static Expression getAnnotationMemberValueFromAnnotationExpr(
            AnnotationExpr annotationExpr, String memberName) {
        if(annotationExpr.isNormalAnnotationExpr()){
            NormalAnnotationExpr normalAnnotationExpr = annotationExpr.asNormalAnnotationExpr();
            for(MemberValuePair pair : normalAnnotationExpr.getPairs()){
                if(pair.getName().asString().equals(memberName)){
                    return pair.getValue();
                }
            }
        }
        throw new IllegalArgumentException(
                String.format("Could not find annotation member \"%s\" in the annotation:%n%s",
                        memberName, annotationExpr));
    }

    public static int[] getNumberValueInImplementAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(IMPLEMENT_NAME)) {
            var expression = getAnnotationMemberValue(node, IMPLEMENT_NAME, IMPLEMENT_NUMBER_NAME);
            return expression.asArrayInitializerExpr().getValues().stream()
                    .mapToInt(value -> value.asIntegerLiteralExpr().asNumber().intValue()).toArray();
        }
        throw new MissingAnnotationException(IMPLEMENT_NUMBER_NAME);
    }

    public static String getReplacementIdInImplementAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(IMPLEMENT_NAME)) {
            Expression expression = getAnnotationMemberValue(node, IMPLEMENT_NAME, IMPLEMENT_ID_NAME);
            return expression.asStringLiteralExpr().asString();
        }
        throw new MissingAnnotationException(IMPLEMENT_ID_NAME);
    }

    //TODO Make test for methods below
    public static List<ImportDeclaration> filterOutAnnotationImports(List<ImportDeclaration> importDeclarations){
        List<ImportDeclaration> nonAnnotationImportDeclarations = new ArrayList<>();
        for(ImportDeclaration importDeclaration : importDeclarations){
            if(isNonAnnotationImportDeclaration(importDeclaration)){
                nonAnnotationImportDeclarations.add(importDeclaration);
            }
        }
        return nonAnnotationImportDeclarations;
    }

    private static boolean isNonAnnotationImportDeclaration(ImportDeclaration importDeclaration){
        var importQualifier = getImportQualifierAsString(importDeclaration);
        var annotationPackageQualifier = getAnnotationsPackageName().getQualifier().get().asString();
        return !annotationPackageQualifier.equals(importQualifier);
    }

    private static String getImportQualifierAsString(ImportDeclaration importDeclaration){
        if (importDeclaration.isAsterisk()){
            return importDeclaration.getNameAsString();
        }
        var importName = importDeclaration.getName();
        if(importName.getQualifier().isPresent()){
            return importName.getQualifier().get().asString();
        }
        throw new IllegalStateException("Import declaration " + importDeclaration.getNameAsString() + " has an unrecognizable format.");
    }

    public static List<BodyDeclaration<?>> getAllAnnotatedNodesInFiles(List<CompilationUnit> files, String annotationName){
        List<BodyDeclaration<?>> allAnnotatedNodes = new ArrayList<>();
        for(CompilationUnit file : files){
            allAnnotatedNodes.addAll(getAnnotatedNodesInFile(file, annotationName));
        }
        return allAnnotatedNodes;
    }

    public static List<BodyDeclaration<?>> getAnnotatedNodesInFile(CompilationUnit file, String annotationName){
        List<BodyDeclaration<?>> annotatedNodes = new ArrayList<>();
        file.findAll(BodyDeclaration.class,
                bodyDeclaration -> bodyDeclaration.getAnnotationByName(annotationName).isPresent())
                .forEach(annotatedNodes::add);
        return annotatedNodes;
    }

    public static void removeAnnotationsFromFile(CompilationUnit file, String annotationName){
        var annotatedNodes = AnnotationUtils.getAnnotatedNodesInFile(file, annotationName);
        annotatedNodes.forEach(node -> file.replace(node, (Node) removeAnnotationFromNode(node, annotationName)));
    }

    public static NodeWithAnnotations<?> removeAnnotationFromNode(NodeWithAnnotations<?> node, String annotationName){
        NodeList<AnnotationExpr> annotationsToKeep = new NodeList<>();
        for(var annotation : node.getAnnotations()){
            if(annotation.getName().asString().equals(annotationName)){
                continue;
            }
            annotationsToKeep.add(annotation);
        }
        node.setAnnotations(annotationsToKeep);
        return node;
    }

    public static void removeAnnotationImportsFromFile(CompilationUnit file){
        List<ImportDeclaration> importDeclarations = List.copyOf(file.getImports());
        importDeclarations.forEach(importDeclaration -> {
            if(importDeclaration.isAsterisk() &&
                    importDeclaration.getNameAsString()
                            .equals(AnnotationUtils.getAnnotationsPackageName().getQualifier().get().asString())){
                importDeclaration.remove();
            }
            if(importDeclaration.getName().getQualifier().isPresent()
                    && importDeclaration.getName().getQualifier().get().asString()
                    .equals(AnnotationUtils.getAnnotationsPackageName().getQualifier().get().asString())){
                importDeclaration.remove();

            }
        });
    }


}
