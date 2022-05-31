import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.writers.ProjectWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectWriterTest {

    private ProjectWriter projectWriter;
    private String srcDirPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        getSrcDirPath();
        tempFolder.create();
        String targetDir = tempFolder.getRoot().getPath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        Assignment assignment = new AssignmentBuilder(parser).build();
        projectWriter = new ProjectWriter(srcDirPath, targetDir, assignment);
    }

    private void getSrcDirPath() {
        String pafMavenPluginPath = System.getProperty("user.dir");
        File file = new File(pafMavenPluginPath);
        String parentPath = file.getParent();
        srcDirPath = parentPath + File.separator + "paf-test-example";
    }

    @Test
    void testDirectoriesAreCopied() throws IOException {
        projectWriter.copyProject();
        File srcDir = new File(srcDirPath);
        List<String> originalDirectories = new ArrayList<>();
        getAllDirectoryNames(srcDir, originalDirectories);
        File targetDir = new File(tempFolder.getRoot().getPath());
        List<String> copiedDirectories = new ArrayList<>();
        getAllDirectoryNames(targetDir, copiedDirectories);
        assertEquals(originalDirectories, copiedDirectories);
    }

    private void getAllDirectoryNames(File dir, List<String> directories){
        for(File file : Objects.requireNonNull(dir.listFiles())){
            if(file.isDirectory()){
                directories.add(file.getName());
                getAllDirectoryNames(file, directories);
            }
        }
    }
}
