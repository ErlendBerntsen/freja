package no.hvl;


import java.io.IOException;

public class Main {


    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020";
    private static final String ASSIGNMENT_PROJECT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020";

    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\dat100-prosjekt-complete-2020-generated";
    private static final String TARGET_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-prosjekt-complete-2020-generated";


    public static void main (String[] args) throws IOException {
        Generator generator = new Generator(ASSIGNMENT_PROJECT_PATH_LAPTOP, TARGET_PATH_LAPTOP);
        generator.generate();
    }

}
