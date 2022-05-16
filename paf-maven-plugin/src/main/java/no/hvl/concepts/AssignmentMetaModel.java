package no.hvl.concepts;


import com.github.javaparser.ast.ImportDeclaration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AssignmentMetaModel {
    List<Replacement> replacements;

    public AssignmentMetaModel(){
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }

    public HashMap<String, Replacement> getReplacementsAsHashMap(){
        HashMap<String, Replacement> replacementsMap  = new HashMap<>();
        for(Replacement replacement : replacements){
            replacementsMap.put(replacement.getId(), replacement);
        }
        return replacementsMap;
    }

    public HashSet<ImportDeclaration> getReplacementImportDeclarations(){
        HashSet<ImportDeclaration> replacementImportDeclarations = new HashSet<>();
        for(Replacement replacement : replacements){
            replacementImportDeclarations.addAll(replacement.getRequiredImports());
        }
        return replacementImportDeclarations;
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements = replacements;
    }
}
