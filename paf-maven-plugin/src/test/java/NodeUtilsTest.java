import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Parser;
import no.hvl.exceptions.NoFileFoundException;
import no.hvl.exceptions.NodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.*;

class NodeUtilsTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testGettingBodyDeclarationCopyInFiles(){
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        BodyDeclaration<?> node = getNodeWithId(files, 1);
        BodyDeclaration<?> nodeCopy = findBodyDeclarationCopyInFiles(files, node.clone());
        assertEquals(node, nodeCopy);
    }

    @Test
    void testGettingBodyDeclarationThatDoesNotExistInFiles(){
        BodyDeclaration<?> node = new MethodDeclaration();
        BodyDeclaration<?> nodeClone = node.clone();
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        assertThrows(IllegalStateException.class, () -> findBodyDeclarationCopyInFiles(files, nodeClone));
    }

    @Test
    void testRemovingCommentsFromNodes(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        MethodDeclaration method = node.asMethodDeclaration();
        Optional<BlockStmt> methodBody = method.getBody();
        assertTrue(methodBody.isPresent());
        NodeList<Node> nodesWithoutComments = getNodesWithoutComments(methodBody.get().getStatements());
        for(Node nodeWithoutComment : nodesWithoutComments){
            assertTrue(nodeWithoutComment.getAllContainedComments().isEmpty());
        }
    }

    @Test
    void testCheckingNodeHasBlockStmtOnNodeWithBlockStmt(){
        Node nodeWithBlockStmt = new ConstructorDeclaration();
        assertTrue(nodeHasBlockStmt(nodeWithBlockStmt));
    }

    @Test
    void testCheckingNodeHasBlockStmtOnNodeWithOptionalPresentBlockStmt(){
        MethodDeclaration nodeWithOptionalBlockStmt = new MethodDeclaration();
        nodeWithOptionalBlockStmt.setBody(new BlockStmt());
        assertTrue(nodeHasBlockStmt(nodeWithOptionalBlockStmt));
    }

    @Test
    void testCheckingNodeHasBlockStmtOnNodeWithOptionalEmptyBlockStmt(){
        MethodDeclaration nodeWithOptionalBlockStmt = new MethodDeclaration();
        nodeWithOptionalBlockStmt.setBody(null);
        assertFalse(nodeHasBlockStmt(nodeWithOptionalBlockStmt));
    }

    @Test
    void testCheckingNodeHasBlockStmtOnNodeWithoutBlockStmt(){
        ExpressionStmt expressionStmt = new ExpressionStmt();
        assertFalse(nodeHasBlockStmt(expressionStmt));
    }

    @Test
    void testGettingBlockStmtFromMethod(){
        BodyDeclaration<?> method = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        BlockStmt methodBody = getBlockStmtFromBodyDeclaration(method);
        Optional<BlockStmt> actualMethodBody = method.asMethodDeclaration().getBody();
        assertTrue(actualMethodBody.isPresent());
        assertEquals(actualMethodBody.get(), methodBody);
    }

    @Test
    void testGettingBlockStmtFromConstructor(){
        BodyDeclaration<?> constructor = getNodeWithId(parser.getCompilationUnitCopies(), 5);
        BlockStmt constructorBody = getBlockStmtFromBodyDeclaration(constructor);
        BlockStmt actualConstructorBody = constructor.asConstructorDeclaration().getBody();
        assertEquals(actualConstructorBody, constructorBody);
    }

    @Test
    void testGettingBlockStmtFromFieldVariable(){
        BodyDeclaration<?> fieldVariable = getNodeWithId(parser.getCompilationUnitCopies(), 2);
        assertThrows(NodeException.class, () -> getBlockStmtFromBodyDeclaration(fieldVariable));
    }

    @Test
    void testCheckingStartStatementIsStartStatement(){
        Statement startStatement = StaticJavaParser.parseStatement("SolutionStart s;");
        assertTrue(isStartStatement(startStatement));
    }

    @Test
    void testCheckingEndStatementIsNotStartStatement(){
        Statement endStatement = StaticJavaParser.parseStatement("SolutionEnd e;");
        assertFalse(isStartStatement(endStatement));
    }

    @Test
    void testCheckingEndStatementIsEndStatement(){
        Statement endStatement = StaticJavaParser.parseStatement("SolutionEnd e;");
        assertTrue(isEndStatement(endStatement));
    }

    @Test
    void testCheckingStartStatementIsNotEndStatement(){
        Statement startStatement = StaticJavaParser.parseStatement("SolutionStart s;");
        assertFalse(isEndStatement(startStatement));
    }

    @Test
    void testRemovingStartAndEndStatement(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        removeSolutionStartAndEndStatementsFromNode(node);
        List<Statement> statements = node.findAll(Statement.class);
        for(Statement statement : statements){
            assertFalse(isStartStatement(statement));
            assertFalse(isEndStatement(statement));
        }
    }

    @Test
    void testRemovingStartAndEndStatementFromMultipleNodes(){
        BodyDeclaration<?> nodeWithStartAndEndStatements = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BodyDeclaration<?> nodeWithStartStatement = getNodeWithId(parser.getCompilationUnitCopies(), 3);

        removeSolutionStartAndEndStatementsFromNodes(List.of(nodeWithStartStatement, nodeWithStartAndEndStatements));
        List<Statement> statements = nodeWithStartAndEndStatements.findAll(Statement.class);
        statements.addAll(nodeWithStartStatement.findAll(Statement.class));
        for(Statement statement : statements){
            assertFalse(isStartStatement(statement));
            assertFalse(isEndStatement(statement));
        }
    }

    @Test
    void testReplacingStatementsFromBlock(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BlockStmt methodBody = getBlockStmtFromBodyDeclaration(node);
        List<List<Statement>> beforeReplacedAfter = getBeforeReplacedToBeReplacedAndAfterReplacedStatements(methodBody);
        BodyDeclaration<?> node2 = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        BlockStmt replacementCode = getBlockStmtFromBodyDeclaration(node2);
        replaceStatements(methodBody, beforeReplacedAfter.get(1), replacementCode);
        assertStatementsAreCorrectlyReplaced(methodBody, beforeReplacedAfter, replacementCode.getStatements());
    }

    private List<List<Statement>> getBeforeReplacedToBeReplacedAndAfterReplacedStatements(BlockStmt blockStmt){
        List<Statement> statements = blockStmt.getStatements();
        List<Statement> statementBefore = statements.subList(0, 1);
        List<Statement> statementsToBeReplaced = statements.subList(1, 4);
        List<Statement> statementsAfter = statements.subList(4, statements.size());
        return List.of(statementBefore, statementsToBeReplaced, statementsAfter);
    }

    private void assertStatementsAreCorrectlyReplaced(BlockStmt methodBody, List<List<Statement>> beforeReplacedAfter,
                                          List<Statement> replacementStatements) {
        List<Statement> methodStatements = methodBody.getStatements();
        assertEquals(beforeReplacedAfter.get(0), methodStatements.subList(0, 1));
        assertEquals(replacementStatements, methodStatements.subList(1, 1 + replacementStatements.size()));
        assertEquals(beforeReplacedAfter.get(2),
                methodStatements.subList(1 + replacementStatements.size(), methodStatements.size()));
    }

    @Test
    void testReplacingStatementWithNoTargetStatements(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BlockStmt methodBody = getBlockStmtFromBodyDeclaration(node);
        List<Statement> targetStatements = new ArrayList<>();
        assertThrows(NodeException.class,
                () -> replaceStatements(methodBody, targetStatements, methodBody));
    }

    @Test
    void testReplacingStatementWithTargetStatementsNotInTheCodeBlock(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BlockStmt methodBody = getBlockStmtFromBodyDeclaration(node);
        BodyDeclaration<?> node2 = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        BlockStmt otherMethodBody = getBlockStmtFromBodyDeclaration(node2);
        List<Statement> targetStatements = otherMethodBody.getStatements();
        assertThrows(NodeException.class,
                () -> replaceStatements(methodBody, targetStatements, methodBody));
    }

    @Test
    void testFindingFileFromNode(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        Optional<CompilationUnit> expectedFile = node.findCompilationUnit();
        assertTrue(expectedFile.isPresent());
        assertEquals(expectedFile.get(), findFile(node));
    }

    @Test
    void testFindingFileFromNodeWithoutFile(){
        Statement endStatement = StaticJavaParser.parseStatement("SolutionEnd e;");
        assertThrows(NoFileFoundException.class, () -> findFile(endStatement));
    }
}
