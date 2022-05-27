package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.annotations.CopyOption;

import java.util.Objects;

public class RemoveEverythingTask extends AbstractTask{
    public static final CopyOption copyOption = CopyOption.REMOVE_EVERYTHING;

    public RemoveEverythingTask(BodyDeclaration<?> node, String fullNumberAsString) {
        super(node, fullNumberAsString, copyOption);
    }

    @Override
    public BodyDeclaration<?> createStartCode(BodyDeclaration<?> noteToUpdate) {
        return noteToUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveEverythingTask task = (RemoveEverythingTask) o;
        return Objects.equals(getNode(), task.getNode())
                && Objects.equals(getFullNumberAsString(), task.getFullNumberAsString());
    }
}
