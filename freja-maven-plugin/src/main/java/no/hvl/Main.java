package no.hvl;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import no.hvl.utilities.AnnotationNames;

import java.io.IOException;
import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;

public class Main {


    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\FrejaTutorial";
    private static final String ASSIGNMENT_PROJECT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-jplab11-annotated";

    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\FrejaTutorialOutput";
    private static final String TARGET_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\testOutput";

    public static void main (String[] args) throws IOException {
        Configuration config = new Configuration("C:\\Users\\Erlend\\eclipse-workspace\\TestProject", TARGET_PATH_DESKTOP);
        //countExpressions(config);
        Generator generator = new Generator(config);
        generator.generate();
    }

    private static void countExpressions(Configuration config) throws IOException {
        Parser parser = new Parser(config.getSourcePath());
        parser.parse();
        int expressions = 0;
        int numberOfFrejaAnnotations = 0;
        for(CompilationUnit file : parser.getCompilationUnitCopies()){
            List<Expression> nodes = file.findAll(Expression.class);
            for(Expression node : nodes){
                if(node.isAnnotationExpr())
                    if(frejaAnnotations().contains(node.asAnnotationExpr().getNameAsString())){
                        numberOfFrejaAnnotations++;
                    }
            }
            expressions += nodes.size();

        }
        System.out.println("Freja annotations in project: " + numberOfFrejaAnnotations);
        System.out.println("Expressions in project: " + expressions);
        System.out.println("Java files in project: " + parser.getCompilationUnitCopies().size());
    }

    private static List<String> frejaAnnotations(){
        return List.of(DESCRIPTION_REFERENCE_NAME, EXERCISE_NAME, REPLACEMENT_CODE_NAME, REMOVE_NAME);
    }

}