package no.hvl.concepts.builders;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.concepts.Replacement;
import no.hvl.exceptions.NodeException;
import no.hvl.utilities.NodeUtils;

import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class ReplacementBuilder {

    private final BodyDeclaration<?> annotatedNode;
    private Replacement replacement;

    public ReplacementBuilder (BodyDeclaration<?> annotatedNode){
        this.annotatedNode = annotatedNode;
    }

    public Replacement build(){
        replacement = new Replacement();
        replacement.setId(findId());
        replacement.setReplacementCode(findReplacementCode());
        replacement.setFile(NodeUtils.findFile(annotatedNode));
        replacement.setRequiredImports(findRequiredImports());
        return replacement;
    }

    private String findId(){
        Expression idExpression = getAnnotationMemberValue(annotatedNode,
                REPLACEMENT_CODE_NAME,
                REPLACEMENT_CODE_ID_NAME);
        return idExpression.asStringLiteralExpr().asString();
    }

    private BlockStmt findReplacementCode(){
        if(nodeHasBlockStmt(annotatedNode)){
            BlockStmt replacementCodeBlock = getBlockStmtFromBodyDeclaration(annotatedNode);
            if(replacementCodeBlock.isEmpty()){
                throw new NodeException(replacementCodeBlock,
                        String.format("Types annotated with @%s can not have an empty body.", REPLACEMENT_CODE_NAME));
            }
            return replacementCodeBlock;
        }
        throw new NodeException(annotatedNode,
                String.format("Types annotated with @%s must have a body.", REPLACEMENT_CODE_NAME));
    }

    private List<ImportDeclaration> findRequiredImports(){
        List<ImportDeclaration> importDeclarations = replacement.getFile().getImports();
        return getNewListWithoutAnnotationImports(importDeclarations);
    }

    public static Replacement getDefaultReplacement(BodyDeclaration<?> node){
        var defaultReplacement = new Replacement();
        String errorMessage = node.isConstructorDeclaration() ? getConstructorMessage(node) : getMethodMessage(node);
        defaultReplacement.setReplacementCode(getDefaultReplacementCode(errorMessage));
        return defaultReplacement;
    }

    private static String getConstructorMessage(BodyDeclaration<?> node){
        CompilationUnit file = NodeUtils.findFile(node);
        String className = NodeUtils.getFileSimpleName(file);
        return String.format("The constructor for the class %s is not implemented", className);
    }

    private static String getMethodMessage(BodyDeclaration<?> node){
        String methodName = NodeUtils.tryToGetSimpleName(node);
        return String.format("The method %s is not implemented", methodName);
    }

    private static BlockStmt getDefaultReplacementCode(String message) {
        return StaticJavaParser.parseBlock(String.format("""
                         { \s
                        throw new UnsupportedOperationException(\"%s\");
                        }""",message));
    }

}
