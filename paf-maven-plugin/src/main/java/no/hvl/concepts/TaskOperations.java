package no.hvl.concepts;

import com.github.javaparser.ast.body.BodyDeclaration;

public interface TaskOperations {

    BodyDeclaration<?> createSolutionCode();
    BodyDeclaration<?> createStartCode();
}
