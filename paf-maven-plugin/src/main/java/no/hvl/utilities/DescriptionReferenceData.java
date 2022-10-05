package no.hvl.utilities;

import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

public class DescriptionReferenceData {
    NodeWithAnnotations<?> node;
    int[] exercises;

    public DescriptionReferenceData(NodeWithAnnotations<?> node, int[] exercises) {
        this.node = node;
        this.exercises = exercises;
    }

    public NodeWithAnnotations<?> getNode() {
        return node;
    }

    public void setNode(NodeWithAnnotations<?> node) {
        this.node = node;
    }

    public int[] getExercises() {
        return exercises;
    }

    public void setExercises(int[] exercises) {
        this.exercises = exercises;
    }
}
