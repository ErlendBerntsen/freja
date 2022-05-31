package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.Solution;

import static no.hvl.utilities.NodeUtils.*;

public class ReplaceSolutionTask extends Task {
    public static final CopyOption copyOption = CopyOption.REPLACE_SOLUTION;
    private final Solution solution;
    private final Replacement replacement;

    public ReplaceSolutionTask(BodyDeclaration<?> node, String fullNumberAsString,
                               Solution solution, Replacement replacement) {
        super(node, fullNumberAsString, copyOption);
        this.solution = solution;
        this.replacement = replacement;
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeToUpdate);
        replaceSolution(codeBlockWithSolution, solution, replacement);
        return nodeToUpdate;
    }

    public Solution getSolution() {
        return solution;
    }

    public Replacement getReplacement() {
        return replacement;
    }
}
