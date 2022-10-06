package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.annotations.TransformOption;

import java.util.List;

import static no.hvl.utilities.NodeUtils.*;


public abstract class Task implements TaskOperations {
    private BodyDeclaration<?> node;
    private String fullIdAsString;
    private TransformOption transformOption;

    protected Task(BodyDeclaration<?> node, String fullIdAsString, TransformOption transformOption) {
        this.node = node;
        this.fullIdAsString = fullIdAsString;
        this.transformOption = transformOption;
    }

    public BodyDeclaration<?> getNode() {
        return node;
    }

    public void setNode(BodyDeclaration<?> node) {
        this.node = node;
    }

    public String getFullIdAsString() {
        return fullIdAsString;
    }

    public void setFullIdAsString(String fullIdAsString) {
        this.fullIdAsString = fullIdAsString;
    }

    public TransformOption getTransformOption() {
        return transformOption;
    }

    public void setTransformOption(TransformOption transformOption) {
        this.transformOption = transformOption;
    }

    @Override
    public BodyDeclaration<?> createSolutionCode(BodyDeclaration<?> nodeToUpdate) {
        removeSolutionStartAndEndStatementsFromNode(nodeToUpdate);
        return nodeToUpdate;
    }

}
