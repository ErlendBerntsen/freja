package no.hvl.concepts.tasks;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.NodeUtils;

public class RemoveBodyTask extends Task {
    public static final CopyOption copyOption = CopyOption.REMOVE_BODY;

    public RemoveBodyTask(BodyDeclaration<?> node, String fullNumberAsString) {
        super(node, fullNumberAsString, copyOption);
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        BlockStmt codeBlock = NodeUtils.getBlockStmtFromBodyDeclaration(nodeToUpdate);
        codeBlock.setStatements(new NodeList<>());
        return nodeToUpdate;
    }
}
