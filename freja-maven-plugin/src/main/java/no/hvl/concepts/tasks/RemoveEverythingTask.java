package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.annotations.TransformOption;

import java.util.Objects;

public class RemoveEverythingTask extends Task {
    public static final TransformOption TRANSFORM_OPTION = TransformOption.REMOVE_EVERYTHING;

    public RemoveEverythingTask(BodyDeclaration<?> node, String fullNumberAsString) {
        super(node, fullNumberAsString, TRANSFORM_OPTION);
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> nodeToUpdate) {
        nodeToUpdate.remove();
        return nodeToUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveEverythingTask task = (RemoveEverythingTask) o;
        return Objects.equals(getNode(), task.getNode())
                && Objects.equals(getFullIdAsString(), task.getFullIdAsString());
    }
}
