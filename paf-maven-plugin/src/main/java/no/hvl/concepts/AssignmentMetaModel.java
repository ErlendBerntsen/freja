package no.hvl.concepts;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AssignmentMetaModel {
    private List<CompilationUnit> files;
    private List<Replacement> replacements;
    private List<Exercise> exercises;

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

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public List<AbstractTask> getTasks(){
        return getTasksFromExercises(exercises);
    }

    private List<AbstractTask> getTasksFromExercises(List<Exercise> exercises) {
        List<AbstractTask> abstractTasks = new ArrayList<>();
        for(Exercise exercise : exercises){
            abstractTasks.addAll(exercise.getAbstractTasks());
            abstractTasks.addAll(getTasksFromExercises(exercise.getSubExercises()));
        }
        return abstractTasks;
    }

    public List<CompilationUnit> getFiles() {
        return files;
    }

    public void setFiles(List<CompilationUnit> files) {
        this.files = files;
    }
}
