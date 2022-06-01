import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.concepts.tasks.Task;
import no.hvl.writers.DescriptionWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

public class DescriptionWriterTest {

    private String targetDirPath;
    private Assignment assignment;
    private DescriptionWriter descriptionWriter;

    @Rule
    private final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        String srcDirPath = getPafTestExamplePath();
        tempFolder.create();
        targetDirPath = tempFolder.getRoot().getPath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        assignment = new AssignmentBuilder(parser).build();
        descriptionWriter = new DescriptionWriter(targetDirPath, assignment.getExercises());
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
        String name = "Exercise" + exercise.getFullNumberAsString();
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
        String name = "Task" + task.getFullNumberAsString();
        String fullNameAttribute = ":" + name + "FullName: public String toString()";
        String simpleNameAttribute  = ":" + name + "SimpleName: pass:normal[`+toString+`]";
        String typeAttribute  = ":" + name + "Type: pass:normal[`+Method+`]";
        assertEquals(fullNameAttribute, lines.get(0));
        assertEquals(simpleNameAttribute, lines.get(1));
        assertEquals(typeAttribute, lines.get(2));
    }

    @Test
    void testCreatingAllAdocAttributes() {
        Exercise exercise = assignment.getExercises().get(0);
        String allAttributes = descriptionWriter.createAttributes(exercise);
        assertTrue(allAttributes.contains(descriptionWriter.createAttributes(exercise)));
        for(Task task : exercise.getTasksIncludingSubExercises()){
            assertTrue(allAttributes.contains(descriptionWriter.createTaskAttributes(task)));
        }
    }

    @Test
    void testWritingContentToFile() throws IOException {
        descriptionWriter.createExerciseDescriptions();
        for(Exercise exercise : assignment.getExercises()){
            String fileName = "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc";
            File file = new File(descriptionWriter.getDescriptionsDirPath() + File.separator + fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String attributes = descriptionWriter.createAttributes(exercise);
            assertLinesMatch(attributes.lines(), reader.lines());
        }
    }
}
