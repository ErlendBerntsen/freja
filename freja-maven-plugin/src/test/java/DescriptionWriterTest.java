import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.builders.ExerciseBuilder;
import no.hvl.concepts.tasks.Task;
import no.hvl.exceptions.NodeException;
import no.hvl.utilities.DescriptionReferenceData;
import no.hvl.writers.DescriptionWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import testUtils.ExamplesParser;
import testUtils.TestUtils;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.utilities.FileUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class DescriptionWriterTest {

    private String targetDirPath;
    private Assignment assignment;
    private DescriptionWriter descriptionWriter;

    @Rule
    private final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        String srcDirPath = getFrejaTestExamplePath();
        tempFolder.create();
        targetDirPath = tempFolder.getRoot().getPath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        assignment = new AssignmentBuilder(parser).build();
        descriptionWriter = new DescriptionWriter(targetDirPath, assignment,
                new HashMap<>(),false);
    }

    @Test
    void testCreatingDescriptionDirectory() throws IOException {
        descriptionWriter.createDescriptionDir();
        List<String> directories = getAllDirectoryNames(new File(targetDirPath));
        assertEquals(1, directories.size());
        assertTrue(directories.contains("descriptions"));
    }

    @Test
    void testCreatingExerciseDescriptionFiles() throws IOException {
        descriptionWriter.createDescriptionDir();
        descriptionWriter.createDescriptionFiles();
        List<String> fileNames = getAllFileNames(new File(targetDirPath));
        assertEquals(assignment.getExercises().size(), fileNames.size());
        for(Exercise exercise : assignment.getExercises()){
            assertTrue(fileNames.contains("Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc"));
        }
    }

    @Test
    void testCreatingExerciseAdocAttributes() {
        Exercise exercise = assignment.getExercises().get(0);
        String attributes = descriptionWriter.createExerciseAttributes(exercise);
        List<String> lines = attributes.lines().collect(Collectors.toList());
        String name = "Exercise" + exercise.getFullIdAsString();
        String packageAttribute = ":" + name + "Package: pass:normal[`+no.hvl.dat100ptc.oppgave1+`]";
        String fileNameAttribute  = ":" + name + "FileName: pass:normal[`+GPSPoint.java+`]";
        String fileSimpleNameAttribute  = ":" + name + "FileSimpleName: pass:normal[`+GPSPoint+`]";
        assertEquals(packageAttribute, lines.get(0));
        assertEquals(fileNameAttribute, lines.get(1));
        assertEquals(fileSimpleNameAttribute, lines.get(2));
    }

    @Test
    void testCreatingTaskAdocAttributes() {
        Exercise exercise = assignment.getExercises().get(0);
        Task task = exercise.getSubExercises().get(2).getTasks().get(0);
        String attributes = descriptionWriter.createTaskAttributes(task);
        List<String> lines = attributes.lines().collect(Collectors.toList());
        String name = "Task" + task.getFullIdAsString();
        String fullNameAttribute = ":" + name + "FullName: public String toString()";
        String simpleNameAttribute  = ":" + name + "SimpleName: pass:normal[`+toString+`]";
        String typeAttribute  = ":" + name + "Type: pass:normal[`+Method+`]";
        assertEquals(fullNameAttribute, lines.get(0));
        assertEquals(simpleNameAttribute, lines.get(1));
        assertEquals(typeAttribute, lines.get(2));
    }

    @Test
    void testCreatingAllAdocAttributes() {
        Exercise rootExercise = assignment.getExercises().get(0);
        String allAttributes = descriptionWriter.createAttributes(rootExercise);
        assertAllAdocAttributesAreCreated(allAttributes, rootExercise);
    }

    private void assertAllAdocAttributesAreCreated(String stringWithAttributes, Exercise rootExercise){
        for(Exercise exercise : rootExercise.getAllExercisesWithTask()){
            assertTrue(stringWithAttributes.contains(descriptionWriter.createExerciseAttributes(exercise)));
        }
        assertFalse(rootExercise.getTasksIncludingAllSubExerciseTasks().isEmpty());
        for(Task task : rootExercise.getTasksIncludingAllSubExerciseTasks()){
            assertTrue(stringWithAttributes.contains(descriptionWriter.createTaskAttributes(task)));
        }
    }

    @Test
    void testCreatingContentContainsAllAdocAttributes(){
        Exercise rootExercise = assignment.getExercises().get(0);
        String content = descriptionWriter.createFileContent(rootExercise);
        assertAllAdocAttributesAreCreated(content, rootExercise);
    }

    @Test
    void testWritingContentToFile() throws IOException {
        descriptionWriter.createExerciseDescriptions();
        for(Exercise exercise : assignment.getExercises()){
            String fileName = "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc";
            File file = new File(descriptionWriter.getDescriptionsDirPath() + File.separator + fileName);
            String fileContent =  getContentFromFile(file);
            String content = descriptionWriter.createFileContent(exercise);
            assertEquals(content, fileContent);
        }
    }

    @Test
    void testCreatingTemplateTitle(){
        Exercise exercise = assignment.getExercises().get(0);
        String title ="\n= *Exercise 1*\n";
        assertEquals(title, descriptionWriter.createTitle(exercise));
    }

    @Test
    void testCreatingTemplateIntroduction(){
        Exercise exercise = assignment.getExercises().get(0).getSubExercises().get(2);
        String introduction = """
                The starting code for this exercise can be found in the file {Exercise1_3_FileName}, which you can find in the package {Exercise1_3_Package}. Your task is to implement the following:

                """;
        assertEquals(introduction, descriptionWriter.createExerciseIntroductionTemplate(exercise));
    }

    @Test
    void testCreatingTemplateTask(){
        Task task = assignment.getExercises().get(0).getSubExercises().get(2).getTasks().get(0);
        String taskTemplate = """
                A {Task1_3_1_Type}:

                [source, java, subs="attributes+"]
                ----
                {Task1_3_1_FullName}
                ----

                """;
        assertEquals(taskTemplate, descriptionWriter.createTaskTemplate(task));
    }

    @Test
    void testCreatingTemplateLists(){
        Exercise exercise = assignment.getExercises().get(0);
        String templateList = createTemplateList(exercise, 1);
        String expectedList =
                ". \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n" +
                ". \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n"
                        +"* \n" +
                ". \n"
                        +"* \n";
        assertEquals(expectedList, templateList);
    }

    private String createTemplateList(Exercise exercise, int level) {
        StringBuilder list = new StringBuilder();
        for(Exercise subExercise : exercise.getSubExercises()){
            list.append(descriptionWriter.createListItem(".", level));
            list.append("\n");
            if(subExercise.hasTasks()){
                for(int i = 0 ; i < subExercise.getTasks().size(); i++){
                    list.append(descriptionWriter.createListItem("*", level));
                    list.append("\n");

                }
            }
            list.append(createTemplateList(subExercise, level + 1));
        }
        return list.toString();
    }

    @Test
    void testCreatingNestedTemplateList() throws IOException {
        ExamplesParser examplesParser = new ExamplesParser();
        examplesParser.init();
        BodyDeclaration<?> node = TestUtils.getNodeWithId(examplesParser.parser.getCompilationUnitCopies(), 35);
        Exercise exercise = new ExerciseBuilder(node, new ArrayList<>(), examplesParser.replacementMap).build();
        Exercise rootExercise = getRootExercise(exercise);
        String templateList = createTemplateList(rootExercise, 1);
        String expectedList =
                ". \n"
                        +".. \n"
                        +"** \n";
        assertEquals(expectedList, templateList);

    }

    private Exercise getRootExercise(Exercise exercise) {
        while(exercise.getParentExercise().isPresent()){
            exercise = exercise.getParentExercise().get();
        }
        return exercise;
    }

    @Test
    void testKeepingOldDescription() throws IOException {
        descriptionWriter.createFileContent(assignment.getExercises().get(0));
        HashMap<String, String> oldDescriptions = descriptionWriter.getDescriptionsMap();
        String oldDescription = oldDescriptions.get("Exercise1.adoc");
        Parser parser = new Parser();
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        var newDescriptionWriter = new DescriptionWriter(targetDirPath, assignment,
                oldDescriptions, true);
        Exercise rootExercise = assignment.getExercises().get(0);
        String newDescription = newDescriptionWriter.createTemplate(rootExercise);
        assertEquals(oldDescription, newDescription);
    }

    @Test
    void testOverwritingOldDescription() throws IOException {
        descriptionWriter.createFileContent(assignment.getExercises().get(0));
        HashMap<String, String> oldDescriptions = descriptionWriter.getDescriptionsMap();
        Parser parser = new Parser();
        parser.parseDirectory("src/test/java/examples/assignment1");
        Assignment assignment = new AssignmentBuilder(parser).build();
        var newDescriptionWriter = new DescriptionWriter(targetDirPath, assignment,
                oldDescriptions, false);
        Exercise rootExercise = assignment.getExercises().get(0);
        newDescriptionWriter.createFileContent(rootExercise);
        String newDescription = newDescriptionWriter.getDescriptionsMap().get("Exercise1.adoc");
        String expectedDescription = newDescriptionWriter.createTemplate(rootExercise);
        assertEquals(expectedDescription, newDescription);
    }

    @Test
    void testKeepingOldDescriptionThatIsNonexistent() {
        Exercise rootExercise = assignment.getExercises().get(0);
        var newDescriptionWriter = new DescriptionWriter(targetDirPath, assignment,
                new HashMap<>(), true);
        String expectedDescription = descriptionWriter.createTemplate(rootExercise);
        String actualDescription = newDescriptionWriter.createTemplate(rootExercise);
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void testCreatingDescriptionTemplateWithNoSubExercises(){
        Exercise rootExercise = assignment.getExercises().get(1);
        String template = descriptionWriter.createTemplate(rootExercise);
        String expected = """
                
                = *Exercise 2*
                                
                The starting code for this exercise can be found in the file {Exercise2_FileName}, which you can find in the package {Exercise2_Package}. Your task is to implement the following:
                                
                * A {Task2_1_Type}:
                                
                [source, java, subs="attributes+"]
                ----
                {Task2_1_FullName}
                ----
                
                """;
        assertEquals(expected, template);
    }


    @Test
    void testCreatingDescriptionReferenceAttribute() {
        List<String> lines = getAttributeLines(1);
        assertTrue(lines.contains(":DescriptionReference_1: pass:normal[`+reference+`]"));
    }

    private List<String> getAttributeLines(int exerciseNumber){
        Exercise exercise = assignment.getExercises().get(exerciseNumber);
        String attributes = descriptionWriter.createAttributes(exercise);
        return  attributes.lines().collect(Collectors.toList());
    }

    @Test
    void testCreatingDescriptionReferenceAttributeForMultipleExercises() {
        List<String> lines = getAttributeLines(1);
        assertTrue(lines.contains(":DescriptionReference_2: pass:normal[`+reference2+`]"));
        lines = getAttributeLines(0);
        assertTrue(lines.contains(":DescriptionReference_1: pass:normal[`+reference2+`]"));
    }

    @Test
    void testCreatingNamedDescriptionReference() {
        List<String> lines = getAttributeLines(1);
        assertTrue(lines.contains(":NamedReference: pass:normal[`+reference3+`]"));
    }
}
