import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Parser;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.builders.ReplacementBuilder;
import no.hvl.exceptions.NoFileFoundException;
import no.hvl.exceptions.NodeException;
import no.hvl.utilities.AnnotationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;
import java.util.Optional;

import static no.hvl.utilities.AnnotationUtils.*;
import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getNodeWithId;

class ReplacementBuilderTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testBuildingRegularReplacement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        Replacement replacement = new ReplacementBuilder(node).build();
        assertEquals("1", replacement.getId());
        Statement actualReplacementCode = StaticJavaParser
                .parseStatement("throw new UnsupportedOperationException(TODO.construtor(\"GPSPoint\"));");
        BlockStmt actualReplacementCodeAsBlockStmt = new BlockStmt(new NodeList<>(actualReplacementCode));
        assertEquals(actualReplacementCodeAsBlockStmt, replacement.getReplacementCode());
        Optional<CompilationUnit> file = node.findCompilationUnit();
        assertTrue(file.isPresent());
        assertEquals(file.get(), replacement.getFile());
        for(ImportDeclaration importDecl : file.get().getImports()){
            if(isNonAnnotationImportDeclaration(importDecl)){
                assertTrue(replacement.getRequiredImports().contains(importDecl));
            }
        }
    }

    @Test
    void testBuildingReplacementWithoutReplacementCodeAnnotation(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(NodeException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithoutBody(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        BlockStmt body = getBlockStmtFromBodyDeclaration(node);
        node.remove(body);
        assertThrows(NodeException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithEmptyBody(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 12);
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(NodeException.class, replacementBuilder::build);
    }

    @Test
    void testBuildingReplacementWithoutFile(){
        BodyDeclaration<?> node = StaticJavaParser.parseBodyDeclaration("""
                @ReplacementCode(id = "2")
                public void throwExceptionForUnImplementedMethod(){
                    throw new UnsupportedOperationException(TODO.method());
                }""".indent(4));
        ReplacementBuilder replacementBuilder = new ReplacementBuilder(node);
        assertThrows(NoFileFoundException.class, replacementBuilder::build);
    }

    @Test
    void testGettingDefaultMethodReplacement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 36);
        Replacement defaultReplacement = ReplacementBuilder.getDefaultReplacement(node);
        BlockStmt defaultReplacementBlock =
                StaticJavaParser.parseBlock("""
                        {
                        throw new UnsupportedOperationException("The method noReplacementId is not implemented");
                        }""");
        assertEquals(defaultReplacementBlock, defaultReplacement.getReplacementCode());
    }

    @Test
    void testGettingDefaultConstructorReplacement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 37);
        Replacement defaultReplacement = ReplacementBuilder.getDefaultReplacement(node);
        BlockStmt defaultReplacementBlock =
                StaticJavaParser.parseBlock("""
                        {
                        throw new UnsupportedOperationException("The constructor for the class Example is not implemented");
                        }""");
        assertEquals(defaultReplacementBlock, defaultReplacement.getReplacementCode());
    }
}
