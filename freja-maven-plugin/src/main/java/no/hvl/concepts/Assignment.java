package no.hvl.concepts;


import com.github.javaparser.ast.CompilationUnit;
import no.hvl.utilities.DescriptionReferenceData;

import java.util.HashSet;
import java.util.List;

public class Assignment {
    private List<CompilationUnit> parsedFiles;
    private List<CompilationUnit> startCodeFiles;
    private List<CompilationUnit> solutionCodeFiles;
    private List<Replacement> replacements;
    private List<Exercise> exercises;
    private HashSet<String> fileNamesToRemove;
    private List<DescriptionReferenceData> descriptionReferences;

    public Assignment(){
    }

    public List<Replacement> getReplacements() {
        return replacements;
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

    public List<CompilationUnit> getParsedFiles() {
        return parsedFiles;
    }

    public void setParsedFiles(List<CompilationUnit> parsedFiles) {
        this.parsedFiles = parsedFiles;
    }

    public List<CompilationUnit> getStartCodeFiles() {
        return startCodeFiles;
    }

    public void setStartCodeFiles(List<CompilationUnit> startCodeFiles) {
        this.startCodeFiles = startCodeFiles;
    }

    public List<CompilationUnit> getSolutionCodeFiles() {
        return solutionCodeFiles;
    }

    public void setSolutionCodeFiles(List<CompilationUnit> solutionCodeFiles) {
        this.solutionCodeFiles = solutionCodeFiles;
    }

    public HashSet<String> getFileNamesToRemove() {
        return fileNamesToRemove;
    }

    public void setFileNamesToRemove(HashSet<String> fileNamesToRemove) {
        this.fileNamesToRemove = fileNamesToRemove;
    }

    public List<DescriptionReferenceData> getDescriptionReferences() {
        return descriptionReferences;
    }

    public void setDescriptionReferences(List<DescriptionReferenceData> descriptionReferences) {
        this.descriptionReferences = descriptionReferences;
    }
}
