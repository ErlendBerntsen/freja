import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Parser;
import no.hvl.concepts.Solution;
import no.hvl.concepts.builders.SolutionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class SolutionBuilderTest {

    Parser parser;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
    }

    @Test
    void testBuildingNormalSolution(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(node);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        List<String> stringStatements = List.of("SolutionStart s;", "x = \"blablabla\";", "SolutionEnd e;");
        List<Statement> actualSolution = buildActualSolution(stringStatements);
        assertEquals(actualSolution, solution.getStatementsIncludingSolutionMarkers());
    }

    private List<Statement> buildActualSolution(List<String> statementsAsString){
        List<Statement> actualStatements = new ArrayList<>();
        for(String stringStatement : statementsAsString){
            actualStatements.add(StaticJavaParser.parseStatement(stringStatement));
        }
        return actualStatements;
    }

    @Test
    void testBuildingSolutionWithoutEndStatement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(node);
        Solution solution = new SolutionBuilder(codeBlockWithSolution).build();
        List<String> stringStatements = List.of("SolutionStart s;", "str = \"Hello World\";", "return str;");
        List<Statement> actualSolution = buildActualSolution(stringStatements);
        assertEquals(actualSolution, solution.getStatementsIncludingSolutionMarkers());
    }


    @Test
    void testBuildingSolutionWithoutStartStatement(){
        testIllegalSolutionDefinition(8);
    }

    @Test
    void testBuildingSolutionWithWrongStatementOrder(){
        testIllegalSolutionDefinition(7);
    }

    @Test
    void testBuildingSolutionWithStartStatementAsLastStatement(){
        testIllegalSolutionDefinition(9);
    }

    void testIllegalSolutionDefinition(int nodeIdWithIllegalSolutionDefinition){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), nodeIdWithIllegalSolutionDefinition);
        BlockStmt codeBlockWithSolution = getBlockStmtFromBodyDeclaration(node);
        SolutionBuilder solutionBuilder =  new SolutionBuilder(codeBlockWithSolution);
        assertThrows(IllegalStateException.class, solutionBuilder::build);

    }
}
