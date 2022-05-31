import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.io.Files;
import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.writers.ProjectWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectWriterTest {

    private Assignment assignment;
    private ProjectWriter projectWriter;
    private String srcDirPath;
    private String targetDirPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        getSrcDirPath();
        tempFolder.create();
        targetDirPath = tempFolder.getRoot().getPath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        assignment = new AssignmentBuilder(parser).build();
        projectWriter = new ProjectWriter(srcDirPath, targetDirPath, assignment);
    }

    private void getSrcDirPath() {
        String pafMavenPluginPath = System.getProperty("user.dir");
        File file = new File(pafMavenPluginPath);
        String parentPath = file.getParent();
        srcDirPath = parentPath + File.separator + "paf-test-example";
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

    private List<String> getAllFileNames(File dir) {
        List<String> fileNames = new ArrayList<>();
        for(File file : Objects.requireNonNull(dir.listFiles())){
            if(file.isFile()){
                fileNames.add(file.getName());
            }
            else if(file.isDirectory()){
                fileNames.addAll(getAllFileNames(file));
            }
        }
        return fileNames;
    }

    private List<String> getAllDirectoryNames(File dir){
        List<String> directories = new ArrayList<>();
        for(File file : Objects.requireNonNull(dir.listFiles())){
            if(file.isDirectory()){
                directories.add(file.getName());
                directories.addAll(getAllDirectoryNames(file));
            }
        }
        return directories;
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
        throw new IllegalStateException();
    }
}
