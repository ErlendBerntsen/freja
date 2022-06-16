package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.Replacement;
import no.hvl.utilities.NodeUtils;

public class ReplaceBodyTask extends Task {

    private final Replacement replacement;
    public static final TransformOption TRANSFORM_OPTION = TransformOption.REPLACE_BODY;

    public ReplaceBodyTask (BodyDeclaration<?> node, String fullNumberAsString, Replacement replacement){
        super(node, fullNumberAsString, TRANSFORM_OPTION);
        this.replacement = replacement;
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        BlockStmt codeBlock = NodeUtils.getBlockStmtFromBodyDeclaration(nodeToUpdate);
        BlockStmt replacementCodeBlock = replacement.getReplacementCode();
        codeBlock.setStatements(replacementCodeBlock.getStatements());
        return nodeToUpdate;
    }

    public Replacement getReplacement() {
        return replacement;
    }
}
