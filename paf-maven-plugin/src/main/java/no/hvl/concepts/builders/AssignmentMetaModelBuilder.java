package no.hvl.concepts.builders;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.concepts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.GeneralUtils.*;

public class AssignmentMetaModelBuilder {

    private final Parser parser;
    List<CompilationUnit> parsedFiles;
    private HashMap<String, Replacement> replacementMap;

    public AssignmentMetaModelBuilder(Parser parser) {
        this.parser = parser;
    }

    public AssignmentMetaModel build() {
        AssignmentMetaModel assignmentMetaModel = new AssignmentMetaModel();
        parsedFiles = parser.getCompilationUnitCopies();
        assignmentMetaModel.setParsedFiles(parsedFiles);
        assignmentMetaModel.setStartCodeFiles(parser.getCompilationUnitCopies());
        assignmentMetaModel.setSolutionCodeFiles(parser.getCompilationUnitCopies());
        assignmentMetaModel.setReplacements(findReplacements());
        //assignmentMetaModel.setExercises(findExercises());
        //TODO
        //Create exercises
            //Create tasks (need replacement map)
                //Create solutions (if relevant)
        return assignmentMetaModel;
    }

    private List<Replacement> findReplacements() {
        List<Replacement> replacements = new ArrayList<>();
        for(CompilationUnit file : parsedFiles){
            var nodesAnnotatedWithReplacementCode =
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
