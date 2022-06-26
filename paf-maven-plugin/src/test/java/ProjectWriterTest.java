import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.io.Files;
import no.hvl.Configuration;
import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.writers.DescriptionWriter;
import no.hvl.writers.ProjectWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class ProjectWriterTest {

    private Assignment assignment;
    private ProjectWriter projectWriter;
    private String srcDirPath;
    private String targetDirPath;

    @Rule
    private final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        srcDirPath = getPafTestExamplePath();
        tempFolder.create();
        targetDirPath = tempFolder.getRoot().getPath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        assignment = new AssignmentBuilder(parser).build();
        var config = new Configuration(srcDirPath, targetDirPath);
        projectWriter = new ProjectWriter(config, assignment);
    }


    @Test
    void testStartCodeAndSolutionDirAreCreated() throws IOException {
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> fileNames = List.of(Objects.requireNonNull(targetDir.list()));
        assertEquals(2, fileNames.size());
        assertTrue(fileNames.contains(ProjectWriter.SOLUTION_PROJECT_NAME));
        assertTrue(fileNames.contains(ProjectWriter.START_CODE_PROJECT_NAME));
    }

    @Test
    void testStartCodeAndSolutionDirCopiedFilesAndDirectories() throws IOException {
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> originalFileNames = getAllFileNames(new File(srcDirPath));
        originalFileNames.removeAll(assignment.getFileNamesToRemove());
        List<String> originalDirNames = getAllDirectoryNames(new File(srcDirPath));
        for(File file : Objects.requireNonNull(targetDir.listFiles())){
            assertEquals(originalFileNames, getAllFileNames(file));
            assertEquals(originalDirNames, getAllDirectoryNames(file));
        }
    }


    @Test
    void testRemovedFilesAreNotCopied() throws IOException {
        projectWriter.createSolutionAndStartProject();
        File startCodeDir = new File(targetDirPath + File.separator + ProjectWriter.START_CODE_PROJECT_NAME);
        List<String> startCodeCopiedFileNames = getAllFileNames(startCodeDir);
        File solutionDir = new File(targetDirPath + File.separator + ProjectWriter.SOLUTION_PROJECT_NAME);
        List<String> solutionCopiedFileNames = getAllFileNames(solutionDir);
        HashSet<String> filesToRemove = assignment.getFileNamesToRemove();
        for(String fileName : filesToRemove){
            assertFalse(startCodeCopiedFileNames.contains(fileName));
            assertFalse(solutionCopiedFileNames.contains(fileName));

        }
    }

    @Test
    void testCopiedJavaFilesAreUpdated() throws IOException {
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        for(File file : Objects.requireNonNull(targetDir.listFiles())){
            if(ProjectWriter.SOLUTION_PROJECT_NAME.equals(file.getName())){
                List<File> javaFiles = getJavaFiles(file);
                assertJavaFilesAreUpdated(javaFiles, assignment.getSolutionCodeFiles());
            }
            if(ProjectWriter.START_CODE_PROJECT_NAME.equals(file.getName())){
                List<File> javaFiles = getJavaFiles(file);
                assertJavaFilesAreUpdated(javaFiles, assignment.getStartCodeFiles());
            }
        }
    }

    private List<File> getJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        for(File file : Objects.requireNonNull(dir.listFiles())){
            if(isJavaFile(file)){
                javaFiles.add(file);
            }
            else if(file.isDirectory()){
                javaFiles.addAll(getJavaFiles(file));
            }
        }
        return javaFiles;
    }

    private void assertJavaFilesAreUpdated(List<File> javaFiles, List<CompilationUnit> originalFiles)
            throws FileNotFoundException {
        for(File javaFile : javaFiles){
            CompilationUnit parsedFile = StaticJavaParser.parse(javaFile);
            CompilationUnit originalParsedFile = findCompilationUnitWithName(originalFiles, javaFile.getName());
            assertEquals(originalParsedFile.toString(), parsedFile.toString());
        }
    }

    private boolean isJavaFile(File file){
        return file.isFile() && Files.getFileExtension(file.getName()).equals("java");
    }

    private CompilationUnit findCompilationUnitWithName(List<CompilationUnit> compilationUnits, String name){
        for(CompilationUnit cu : compilationUnits){
            Optional<CompilationUnit.Storage> storage = cu.getStorage();
            if(storage.isPresent() &&
                storage.get().getFileName().equals(name)){
                return cu;
            }
        }
        throw new IllegalStateException(String.format("Could not find compilation unit with the name \"%s\"", name));
    }

    @Test
    void testOverwritingExistingGeneratedProjects() throws IOException {
        projectWriter.createSolutionAndStartProject();
        assertThrows(FileAlreadyExistsException.class, () -> projectWriter.createSolutionAndStartProject());
    }

    @Test
    void testOverwritingExistingJavaFile() throws IOException {
        projectWriter.createSolutionAndStartProject();
        removeAllNonJavaFiles();
        assertThrows(FileAlreadyExistsException.class, () -> projectWriter.createSolutionAndStartProject());
    }

    private void removeAllNonJavaFiles() {
        File targetDir = new File(targetDirPath);
        for(File file : Objects.requireNonNull(targetDir.listFiles())){
            List<File> nonJavaFiles = getAllNonJavaFiles(file);
            for(File nonJavaFile : nonJavaFiles){
                if(!nonJavaFile.delete()){
                    fail("Could not delete non java file");
                }
            }
        }
    }

    private List<File> getAllNonJavaFiles(File dir){
        List<File> nonJavaFiles = new ArrayList<>();
        for(File file : Objects.requireNonNull(dir.listFiles())){
            if(file.isDirectory()){
                nonJavaFiles.addAll(getAllNonJavaFiles(file));
            }
            else if(!isJavaFile(file)){
                nonJavaFiles.add(file);
            }
        }
        return nonJavaFiles;
    }

    @Test
    void testGeneratingInNonExistingSourcePath()  {
        var config = new Configuration("", targetDirPath);
        assertThrows(NoSuchFileException.class, () -> new ProjectWriter(config, assignment));
    }

    @Test
    void testGeneratingInNonExistingTargetPath()  {
        var config = new Configuration(srcDirPath,"asdkjhashdjk");
        assertThrows(NoSuchFileException.class, () -> new ProjectWriter(config, assignment));
    }

    @Test
    void testNotCopyingSpecifiedDirs() throws IOException {
        HashSet<String> filesToRemove = assignment.getFileNamesToRemove();
        filesToRemove.add("test");
        filesToRemove.add("target");
        assignment.setFileNamesToRemove(filesToRemove);
        var config = new Configuration(srcDirPath, targetDirPath);
        projectWriter = new ProjectWriter(config, assignment);
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> createdDirs = getAllDirectoryNames(targetDir);
        assertFalse(createdDirs.contains("test"));
        assertFalse(createdDirs.contains("target"));
    }

    @Test
    void testNotCopyingSpecifiedFiles() throws IOException {
        HashSet<String> filesToRemove = assignment.getFileNamesToRemove();
        filesToRemove.add("paf-test-example.iml");
        assignment.setFileNamesToRemove(filesToRemove);
        var config = new Configuration(srcDirPath, targetDirPath);
        projectWriter = new ProjectWriter(config, assignment);
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> createdFiles = getAllFileNames(targetDir);
        assertFalse(createdFiles.contains("paf-test-example.iml"));
    }

    @Test
    void testNotCopyingDirsUsingGlob() throws IOException {
        projectWriter.addPathMatchersToIgnore("src/test");
        projectWriter.addPathMatchersToIgnore("target");
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> directories = getAllDirectoryNames(targetDir);
        assertFalse(directories.contains("target"));
        assertFalse(directories.contains("test"));
        assertTrue(directories.contains("src"));
    }

    @Test
    void testNotCopyingFileUsingGlob() throws IOException {
        List<String> fileNames = copyWithGlob("src/main/java/no/hvl/dat100ptc/oppgave1/GPSPoint.java");
        assertFalse(fileNames.contains("GPSPoint.java"));
    }

    private List<String> copyWithGlob(String pattern) throws IOException {
        projectWriter.addPathMatchersToIgnore(pattern);
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        return getAllFileNames(targetDir);
    }

    @Test
    void testNotCopyingFileUsingGlobDoubleStar() throws IOException {
        List<String> fileNames = copyWithGlob("**GPSPoint.java");
        assertFalse(fileNames.contains("GPSPoint.java"));
    }

    @Test
    void testNotCopyingFileUsingGlobSingleStar() throws IOException {
        List<String> fileNames = copyWithGlob("*GPSPoint.java");
        assertTrue(fileNames.contains("GPSPoint.java"));
    }

    @Test
    void testClearingTargetDir() throws IOException {
        projectWriter.createSolutionAndStartProject();
        projectWriter.clearTargetDir();
        File targetDir = new File(targetDirPath);
        assertTrue(targetDir.exists());
        int amountOfFiles = Objects.requireNonNull(targetDir.listFiles()).length;
        assertEquals(0, amountOfFiles);
    }

    @Test
    void testGeneratingConsecutiveProjectDoesNotCauseException() throws IOException {
        projectWriter.createAllProjects();
        assertDoesNotThrow(() -> projectWriter.createAllProjects());
    }

    @Test
    void testSavingExerciseDescriptionsBeforeClearingTargetFolder() throws IOException {
        projectWriter.createSolutionAndStartProject();
        DescriptionWriter descriptionWriter = new DescriptionWriter(targetDirPath, assignment.getExercises(),
                new HashMap<>(), false);
        descriptionWriter.createExerciseDescriptions();
        projectWriter.clearTargetDir();
        HashMap<String, String> descriptionMap = projectWriter.getDescriptionMap();
        assertFalse(descriptionMap.isEmpty());
        for(String fileName : descriptionMap.keySet()){
            assertEquals(getDescriptionWithFileName(fileName), descriptionMap.get(fileName));
        }
    }

    private String getDescriptionWithFileName(String fileName) {
        DescriptionWriter descriptionWriter = new DescriptionWriter(targetDirPath, assignment.getExercises(),
                new HashMap<>(), false);
        for(Exercise exercise : assignment.getExercises()){
            String exerciseFileName = "Exercise"  + exercise.getNumberAmongSiblingExercises() + ".adoc";
            if(exerciseFileName.equals(fileName)){
                return descriptionWriter.createTemplate(exercise);
            }
        }
        throw new IllegalStateException("Could not find exercise description with the file name: " + fileName);
    }

    @Test
    void testSavingExerciseDescriptionsBeforeClearingTargetFolderWithNoDescriptions() throws IOException {
        projectWriter.createSolutionAndStartProject();
        projectWriter.clearTargetDir();
        HashMap<String, String> descriptionMap = projectWriter.getDescriptionMap();
        assertTrue(descriptionMap.isEmpty());
    }

    @Test
    void testCopyingNonJavaFileInJavaSourceFolder() throws IOException {
        projectWriter.createSolutionAndStartProject();
        File targetDir = new File(targetDirPath);
        List<String> createdFiles = getAllFileNames(targetDir);
        assertTrue(createdFiles.contains("input.txt"));
    }

    @Test
    void testCreatingProjectsUsingRelativeTargetPath() throws IOException {
        var config = new Configuration(srcDirPath, "output");
        var newProjectWriter = new ProjectWriter(config, assignment);
        newProjectWriter.clearTargetDir();
        List<String> originalFileNames = getAllFileNames(new File(srcDirPath));
        List<String> originalDirNames = getAllDirectoryNames(new File(srcDirPath));
        originalDirNames.remove("output");
        newProjectWriter.createSolutionAndStartProject();
        File targetDir = new File(config.getTargetPath());
        originalFileNames.removeAll(assignment.getFileNamesToRemove());
        for(File file : Objects.requireNonNull(targetDir.listFiles())){
            assertEquals(originalFileNames, getAllFileNames(file));
            assertEquals(originalDirNames, getAllDirectoryNames(file));
        }
        newProjectWriter.clearTargetDir();
    }

}
