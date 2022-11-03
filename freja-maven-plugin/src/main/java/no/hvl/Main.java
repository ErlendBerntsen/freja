package no.hvl;

public class Main {


    private static final String ASSIGNMENT_PROJECT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\FrejaTutorial";
    private static final String ASSIGNMENT_PROJECT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\dat100-jplab11-annotated";

    private static final String TARGET_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\FrejaTutorialOutput";
    private static final String TARGET_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\testOutput";

    public static void main (String[] args) throws Exception {
        Configuration config = new Configuration("C:\\Users\\Erlend\\eclipse-workspace\\TestProject", TARGET_PATH_DESKTOP);
        Generator generator = new Generator(config);
        generator.generate();
    }

}