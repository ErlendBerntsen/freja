package no.hvl.concepts;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.annotations.CopyOption;


public abstract class AbstractTask {
    private BodyDeclaration<?> node;
    private String fullNumberAsString;
    private CopyOption copyOption;


    protected AbstractTask(BodyDeclaration<?> node, String fullNumberAsString, CopyOption copyOption) {
        this.node = node;
        this.fullNumberAsString = fullNumberAsString;
        this.copyOption = copyOption;
    }

    public BodyDeclaration<?> getNode() {
        return node;
    }

    public void setNode(BodyDeclaration<?> node) {
        this.node = node;
    }

    public String getFullNumberAsString() {
        return fullNumberAsString;
    }

    public void setFullNumberAsString(String fullNumberAsString) {
        this.fullNumberAsString = fullNumberAsString;
    }

    public CopyOption getCopyOption() {
        return copyOption;
    }

    public void setCopyOption(CopyOption copyOption) {
        this.copyOption = copyOption;
    }
}
