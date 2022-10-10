package no.hvl.utilities;

import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

public class DescriptionReferenceData {
    private NodeWithAnnotations<?> node;
    private int[] exercises;
    private String attributeName;

    public DescriptionReferenceData(NodeWithAnnotations<?> node, int[] exercises, String attributeName) {
        this.node = node;
        this.exercises = exercises;
        this.attributeName = attributeName;
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

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
