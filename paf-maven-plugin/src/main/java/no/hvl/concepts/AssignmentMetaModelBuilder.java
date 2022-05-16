package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.utilities.AnnotationNames;
import no.hvl.utilities.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;

public class AssignmentMetaModelBuilder {

    private List<CompilationUnit> files;
    private AssignmentMetaModel assignmentMetaModel;

    public AssignmentMetaModelBuilder(List<CompilationUnit> files) {
        this.files = files;
    }

    public AssignmentMetaModel build(){
        assignmentMetaModel = new AssignmentMetaModel();
        assignmentMetaModel.setReplacements(findReplacements());
        return assignmentMetaModel;
    }

    private List<Replacement> findReplacements() {
        List<Replacement> replacements = new ArrayList<>();
        for(CompilationUnit file : files){
            var nodesAnnotatedWithReplacementCode =
                    AnnotationUtils.getAnnotatedNodesInFile(file, AnnotationNames.REPLACEMENT_CODE_NAME);
            replacements.addAll(createReplacements(nodesAnnotatedWithReplacementCode));
        }
        return replacements;
    }

    private List<Replacement> createReplacements(List<BodyDeclaration<?>> nodesAnnotatedWithReplacementCode){
        List<Replacement> replacements = new ArrayList<>();
        for(BodyDeclaration<?> annotatedNode : nodesAnnotatedWithReplacementCode){
            replacements.add(new ReplacementBuilder(annotatedNode).build());
        }
        return replacements;
    }

}
