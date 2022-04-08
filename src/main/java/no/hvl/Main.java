package no.hvl;


import java.io.FileNotFoundException;

public class Main {

    private static final String GPSPOINT_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\GPSPoint.java";
    private static final String REPLACEMENT_CODE_PATH_LAPTOP = "C:\\Users\\Acer\\IntelliJProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java";
    private static final String GPSPOINT_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\GPSPoint.java";
    private static final String REPLACEMENT_CODE_PATH_DESKTOP = "C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java";


    public static void main (String[] args) throws FileNotFoundException {
        Parser parser = new Parser();
        parser.saveSolutionReplacements(REPLACEMENT_CODE_PATH_LAPTOP);
        parser.parseFile(GPSPOINT_PATH_LAPTOP);
        var cu = parser.modifyAllAnnotatedNodesInFile(parser.getCompilationUnits().get(0), "Implement");
        System.out.println(cu.toString());
    }
}
