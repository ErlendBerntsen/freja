package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;
import no.hvl.utilities.GeneralUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssignmentMetaModelBuilder {

    private List<CompilationUnit> files;
    private AssignmentMetaModel assignmentMetaModel;
    private HashMap<String, Replacement> replacementMap;

    public AssignmentMetaModelBuilder(List<CompilationUnit> files) {
        this.files = files;
    }

    public AssignmentMetaModel build(){
        assignmentMetaModel = new AssignmentMetaModel();
        assignmentMetaModel.setFiles(files);
        assignmentMetaModel.setReplacements(findReplacements());
        assignmentMetaModel.setExercises(findExercises());
        //Create exercises
            //Create tasks (need replacement map)
                //Create solutions (if relevant)
        return assignmentMetaModel;
    }

    private List<Replacement> findReplacements() {
        List<Replacement> replacements = new ArrayList<>();
        for(CompilationUnit file : files){
            var nodesAnnotatedWithReplacementCode =
                    AnnotationUtils.getAnnotatedNodesInFile(file, AnnotationNames.REPLACEMENT_CODE_NAME);
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
                        ,AnnotationNames.REPLACEMENT_CODE_NAME, AnnotationNames.REPLACEMENT_CODE_ID_NAME ));
            }
            replacementMap.put(replacementId, replacement);
        }
    }

    private List<Exercise> findExercises() {
        List<BodyDeclaration<?>> nodesAnnotatedWithImplement = new ArrayList<>();
        for(CompilationUnit file : files){
            nodesAnnotatedWithImplement.addAll(
                    AnnotationUtils.getAnnotatedNodesInFile(file, AnnotationNames.IMPLEMENT_NAME));
        }
        GeneralUtils.sortNodesAnnotatedWithImplementByNumberAsc(nodesAnnotatedWithImplement);
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
