package no.hvl.concepts.tasks;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.NodeUtils;

import java.util.ArrayList;

public class RemoveBodyTask extends AbstractTask{
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
