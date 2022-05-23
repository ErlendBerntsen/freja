package no.hvl.concepts.tasks;

import com.github.javaparser.ast.body.BodyDeclaration;

public interface TaskOperations {

    BodyDeclaration<?> createSolutionCode();
    BodyDeclaration<?> createStartCode();
}
