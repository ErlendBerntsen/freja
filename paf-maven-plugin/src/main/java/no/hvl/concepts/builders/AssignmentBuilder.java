package no.hvl.concepts.builders;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.concepts.*;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.GeneralUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.GeneralUtils.*;

public class AssignmentBuilder {

    private final Parser parser;
    List<CompilationUnit> parsedFiles;
    private HashMap<String, Replacement> replacementMap;

    public AssignmentBuilder(Parser parser) {
        this.parser = parser;
    }

    public Assignment build() {
        Assignment assignment = new Assignment();
        parsedFiles = parser.getCompilationUnitCopies();
        assignment.setParsedFiles(parsedFiles);
        assignment.setStartCodeFiles(parser.getCompilationUnitCopies());
        assignment.setSolutionCodeFiles(parser.getCompilationUnitCopies());
        assignment.setReplacements(findReplacements());
        assignment.setExercises(findExercises());
        return assignment;
    }

    private List<Replacement> findReplacements() {
        List<Replacement> replacements = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            List<BodyDeclaration<?>> nodesAnnotatedWithReplacementCode =
                    getNodesInFileAnnotatedWith(file, REPLACEMENT_CODE_NAME);
            replacements.addAll(createReplacements(nodesAnnotatedWithReplacementCode));
        }
        createReplacementMap(replacements);
        return replacements;
    }

    private List<Replacement> createReplacements(List<BodyDeclaration<?>> nodesAnnotatedWithReplacementCode){
        List<Replacement> replacements = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithReplacementCode){
            replacements.add(new ReplacementBuilder(annotatedNode).build());
        }
        return replacements;
    }

    private void createReplacementMap(List<Replacement> replacements) {
        replacementMap = new HashMap<>();
        for(Replacement replacement : replacements){
            String replacementId = replacement.getId();
            if(replacementMap.containsKey(replacementId)){
                throw new IllegalStateException(String.format("Type annotated with %s uses an %s that is already defined"
                        , REPLACEMENT_CODE_NAME, REPLACEMENT_CODE_ID_NAME ));
            }
            replacementMap.put(replacementId, replacement);
        }
    }

    private List<Exercise> findExercises() {
        List<BodyDeclaration<?>> nodesAnnotatedWithImplement = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            nodesAnnotatedWithImplement.addAll(
                    getNodesInFileAnnotatedWith(file, IMPLEMENT_NAME));
        }
        sortNodesAnnotatedWithImplementByNumberAsc(nodesAnnotatedWithImplement);
        checkExerciseNumbers(nodesAnnotatedWithImplement);
        return new ArrayList<>(createExercises(nodesAnnotatedWithImplement));
    }

    private List<Exercise> createExercises(List<BodyDeclaration<?>> nodesAnnotatedWithImplement) {
        List<Exercise> exercises = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithImplement){
            new ExerciseBuilder(annotatedNode, exercises, replacementMap).build();
        }
        return exercises;
    }

}
