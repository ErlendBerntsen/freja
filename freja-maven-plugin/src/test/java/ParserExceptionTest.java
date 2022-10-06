import no.hvl.Parser;
import no.hvl.exceptions.NoSourceDirectoryException;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;

public class ParserExceptionTest {

    private Parser parser;

    @Rule
    private final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
    }

    @Test
    void emptyDirectoryShouldThrowNoSourceDirectoryException() throws IOException {
        tempFolder.create();
        assertThrows(NoSourceDirectoryException.class,
                () -> parser.findSourceDirectory(tempFolder.getRoot().getAbsolutePath()));
    }

    @Test
    void nonExistingDirectoryShouldThrowNoSuchFileException(){
        assertThrows(NoSuchFileException.class, () -> parser.findSourceDirectory(""));
    }
}
