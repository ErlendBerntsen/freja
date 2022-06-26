package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.TypeParameter;
import no.hvl.annotations.TransformOption;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.Solution;

import static no.hvl.utilities.NodeUtils.*;

public class ReplaceSolutionTask extends Task {
    public static final TransformOption TRANSFORM_OPTION = TransformOption.REPLACE_SOLUTION;
    private final Solution solution;
    private final Replacement replacement;

    public ReplaceSolutionTask(BodyDeclaration<?> node, String fullNumberAsString,
                               Solution solution, Replacement replacement) {
        super(node, fullNumberAsString, TRANSFORM_OPTION);
        this.solution = solution;
        this.replacement = replacement;
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(nodeToUpdate);
        replaceSolution(codeBlockWithSolution, solution, replacement);
        if(replacement.throwsExceptions()){
            CallableDeclaration<?> nodeAsCallableDeclaration = nodeToUpdate.asCallableDeclaration();
            for(String exception : replacement.getThrownExceptions()){
                nodeAsCallableDeclaration.addThrownException(new TypeParameter(exception));
            }
        }
        return nodeToUpdate;
    }

    public Solution getSolution() {
        return solution;
    }

    public Replacement getReplacement() {
        return replacement;
    }
}
