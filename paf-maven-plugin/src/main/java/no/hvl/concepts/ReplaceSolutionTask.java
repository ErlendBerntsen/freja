package no.hvl.concepts;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.NodeUtils;

public class ReplaceSolutionTask extends AbstractTask implements TaskOperations {
    public static final CopyOption copyOption = CopyOption.REPLACE_SOLUTION;
    private Solution solution;
    private Replacement replacement;

    public ReplaceSolutionTask(BodyDeclaration<?> node, String fullNumberAsString,
                               Solution solution, Replacement replacement) {
        super(node, fullNumberAsString, copyOption);
        this.solution = solution;
        this.replacement = replacement;
    }

    @Override
    public void createSolutionCode() {

    }

    @Override
    public void createStartCode() {
        BodyDeclaration<?> nodeClone = getNode().clone();
        BlockStmt codeBlockWithSolution = NodeUtils.getBlockStmtFromBodyDeclaration(nodeClone);
        BlockStmt replacementCode = replacement.getReplacementCode();
        NodeUtils.replaceStatements(codeBlockWithSolution, solution.getStatements(), replacementCode.getStatements());
    }

}
