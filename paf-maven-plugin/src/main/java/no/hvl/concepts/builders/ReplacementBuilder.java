package no.hvl.concepts.builders;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.concepts.Replacement;

import java.util.List;
import java.util.Optional;

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
        replacement.setFile(findFile());
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
                throw new IllegalArgumentException(
                        String.format("Types annotated with @%s can not have an empty body.", REPLACEMENT_CODE_NAME));
            }
            return replacementCodeBlock;
        }
        throw new IllegalArgumentException(
                String.format("Types annotated with @%s must have a body.", REPLACEMENT_CODE_NAME));
    }



    private CompilationUnit findFile(){
        return annotatedNode.findCompilationUnit()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Types annotated with @%s must be inside a file.", REPLACEMENT_CODE_NAME)));
    }

    private List<ImportDeclaration> findRequiredImports(){
        List<ImportDeclaration> importDeclarations = replacement.getFile().getImports();
        return getNewListWithoutAnnotationImports(importDeclarations);
    }

}
