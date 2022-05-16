package no.hvl.concepts;


import java.util.List;

public class AssignmentMetaModel {
    List<Replacement> replacements;

    public AssignmentMetaModel(){
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements = replacements;
    }
}
