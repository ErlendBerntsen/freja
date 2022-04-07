package no.hvl;


import java.io.FileNotFoundException;

public class Main {

    public static void main (String[] args) throws FileNotFoundException {
        Parser parser = new Parser();
        parser.saveSolutionReplacements("C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\ReplacementCode.java");
        parser.parseFile("C:\\Users\\Erlend\\IdeaProjects\\programmingAssignmentFramework\\src\\main\\java\\no\\hvl\\GPSPoint.java");
        var cu = parser.modifyAllAnnotatedNodesInFile(parser.getCompilationUnits().get(0), "Implement");
        System.out.println(cu.toString());
    }
}
