package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalBlockStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.NodeUtils;

import java.util.List;

public class ReplacementBuilder {

    private BodyDeclaration<?> annotatedNode;
    private Replacement replacement;

    public ReplacementBuilder (BodyDeclaration<?> annotatedNode){
        this.annotatedNode = annotatedNode;
    }

    public Replacement build(){
        replacement = new Replacement();
        replacement.setId(findId());
        replacement.setReplacementCode(findReplacementCode());
        replacement.setFile(findFile());
        replacement.setRequiredImports(findRequiredImports());
        return replacement;
    }

    private String findId(){
        var idExpression = AnnotationUtils.getAnnotationValue(annotatedNode,
                AnnotationNames.REPLACEMENT_CODE_NAME,
                AnnotationNames.REPLACEMENT_CODE_ID_NAME);
        return idExpression.asStringLiteralExpr().asString();
    }

    private BlockStmt findReplacementCode(){
        if(NodeUtils.isNodeWithBlockStmt(annotatedNode)){
            var nodeWithBlockStmt = (NodeWithOptionalBlockStmt<?>) annotatedNode;
            return nodeWithBlockStmt.getBody().get();
        }
        throw new IllegalStateException("Types annotated with @ReplacementCode must have a body.");
    }

    private CompilationUnit findFile(){
        return annotatedNode.findCompilationUnit()
                .orElseThrow(() -> new IllegalStateException("ReplacementCode must be inside a file."));
    }

    private List<ImportDeclaration> findRequiredImports(){
        List<ImportDeclaration> importDeclarations = replacement.getFile().getImports();
        return AnnotationUtils.filterOutAnnotationImports(importDeclarations);
    }

}
