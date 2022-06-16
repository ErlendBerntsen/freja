package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.Solution;

import static no.hvl.utilities.NodeUtils.*;
import static no.hvl.utilities.NodeUtils.removeSolution;

public class RemoveSolutionTask extends Task {
    public static final TransformOption TRANSFORM_OPTION = TransformOption.REMOVE_SOLUTION;
    private final Solution solution;

    public RemoveSolutionTask (BodyDeclaration<?> node, String fullNumberAsString, Solution solution) {
        super(node, fullNumberAsString, TRANSFORM_OPTION);
        this.solution = solution;
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        BlockStmt codeBlock = getBlockStmtFromBodyDeclaration(nodeToUpdate);
        removeSolution(codeBlock, solution);
        return nodeToUpdate;
    }

    public Solution getSolution() {
        return solution;
    }
}
