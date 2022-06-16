package no.hvl.utilities;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.annotations.TransformOption;
import no.hvl.exceptions.MissingAnnotationException;
import no.hvl.exceptions.NodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.AnnotationNames.*;

public class AnnotationUtils {
    public static final String ANNOTATIONS_PACKAGE_NAME = "no.hvl.annotations";


    private AnnotationUtils(){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static TransformOption getTransformOptionValueInExerciseAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(EXERCISE_NAME)) {
            var expression = getAnnotationMemberValue(node, EXERCISE_NAME, EXERCISE_TRANSFORM_NAME);
            if(expression.isFieldAccessExpr()){
                return TransformOption.getOption(expression.asFieldAccessExpr().getNameAsString());
            }else{
                return TransformOption.getOption(expression.asNameExpr().getNameAsString());
            }
        }
        throw new MissingAnnotationException((Node) node, EXERCISE_TRANSFORM_NAME);
    }

    public static Expression getAnnotationMemberValue(NodeWithAnnotations<?> node,
                                                      String annotationName, String memberName){
        Optional<AnnotationExpr> annotation = node.getAnnotationByName(annotationName);
        if(annotation.isPresent()){
            return getAnnotationMemberValueFromAnnotationExpr(annotation.get(), memberName);
        }else{
            throw new NodeException((Node) node,
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
        throw new NodeException(annotationExpr,
                String.format("Could not find annotation member \"%s\" in the annotation:%n%s",
                        memberName, annotationExpr));
    }

    public static int[] getIdValueInExerciseAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(EXERCISE_NAME)) {
            var expression = getAnnotationMemberValue(node, EXERCISE_NAME, EXERCISE_ID_NAME);
            return expression.asArrayInitializerExpr().getValues().stream()
                    .mapToInt(value -> value.asIntegerLiteralExpr().asNumber().intValue()).toArray();
        }
        throw new MissingAnnotationException((Node) node, EXERCISE_ID_NAME);
    }

    public static String getReplacementIdInExerciseAnnotation(NodeWithAnnotations<?> node){
        if(node.isAnnotationPresent(EXERCISE_NAME)) {
            Expression expression = getAnnotationMemberValue(node, EXERCISE_NAME, EXERCISE_REPLACEMENT_ID_NAME);
            return expression.asStringLiteralExpr().asString();
        }
        throw new MissingAnnotationException((Node) node, EXERCISE_REPLACEMENT_ID_NAME);
    }

    public static void removeAnnotationImportsFromFile(CompilationUnit file){
        NodeList<ImportDeclaration> nonAnnotationImports = new NodeList<>();
        nonAnnotationImports.addAll(getNewListWithoutAnnotationImports(file.getImports()));
        file.setImports(nonAnnotationImports);
    }

    public static List<ImportDeclaration> getNewListWithoutAnnotationImports
            (List<ImportDeclaration> importDeclarations){
        List<ImportDeclaration> nonAnnotationImportDeclarations = new ArrayList<>();
        for(ImportDeclaration importDeclaration : importDeclarations){
            if(isNonAnnotationImportDeclaration(importDeclaration)){
                nonAnnotationImportDeclarations.add(importDeclaration);
            }
        }
        return nonAnnotationImportDeclarations;
    }

    public static boolean isNonAnnotationImportDeclaration(ImportDeclaration importDeclaration){
        String importQualifier = getImportQualifierAsString(importDeclaration);
        return !ANNOTATIONS_PACKAGE_NAME.equals(importQualifier);
    }

    private static String getImportQualifierAsString(ImportDeclaration importDeclaration){
        if (importDeclaration.isAsterisk()){
            return importDeclaration.getNameAsString();
        }
        Name importName = importDeclaration.getName();
        Optional<Name> importQualifier = importName.getQualifier();
        if(importQualifier.isPresent()){
            return importQualifier.get().asString();
        }
        throw new NodeException(importDeclaration, String.format
                ("Import declaration %s has an unrecognizable format", importDeclaration.getNameAsString()));
    }

    public static List<BodyDeclaration<?>> getAllNodesInFilesAnnotatedWith
            (List<CompilationUnit> files, String annotationName){
        List<BodyDeclaration<?>> allAnnotatedNodes = new ArrayList<>();
        for(CompilationUnit file : files){
            allAnnotatedNodes.addAll(getNodesInFileAnnotatedWith(file, annotationName));
        }
        return allAnnotatedNodes;
    }

    public static List<BodyDeclaration<?>> getNodesInFileAnnotatedWith(CompilationUnit file, String annotationName){
        List<BodyDeclaration<?>> annotatedNodes = new ArrayList<>();
        file.findAll(BodyDeclaration.class,
                bodyDeclaration -> bodyDeclaration.getAnnotationByName(annotationName).isPresent())
                .forEach(annotatedNodes::add);
        return annotatedNodes;
    }

    public static void removeAnnotationTypeFromFile(CompilationUnit file, String annotationName){
        var annotatedNodes = getNodesInFileAnnotatedWith(file, annotationName);
        annotatedNodes.forEach(node -> file.replace(node, (Node) removeAnnotationTypeFromNode(node, annotationName)));
    }

    public static NodeWithAnnotations<?> removeAnnotationTypeFromNode
            (NodeWithAnnotations<?> node, String annotationName){
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

}
