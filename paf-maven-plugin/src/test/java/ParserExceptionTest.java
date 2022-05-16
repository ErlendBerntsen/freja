import no.hvl.Parser;
import no.hvl.exceptions.NoSourceDirectoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;

public class ParserExceptionTest {

    Parser parser;

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
    }

    @Test
    public void emptyDirectoryShouldThrowNoSourceDirectoryException(){
        File emptyDir = new File(System.getProperty("user.dir") + File.separator + "emptyDir");
        if(emptyDir.mkdir()){
            assertThrows(NoSourceDirectoryException.class, () -> parser.findSourceDirectory(emptyDir.getAbsolutePath()));
            emptyDir.delete();
        }
    }

    @Test
    public void nonExistingDirectoryShouldThrowNoSuchFileException(){
        assertThrows(NoSuchFileException.class, () -> parser.findSourceDirectory(""));
    }
}
