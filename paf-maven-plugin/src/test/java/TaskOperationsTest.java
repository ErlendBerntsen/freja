import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.Parser;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.Solution;
import no.hvl.concepts.builders.ReplacementBuilder;
import no.hvl.concepts.builders.TaskBuilder;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.tasks.ReplaceSolutionTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getNodeWithId;

class TaskOperationsTest {
    private Parser parser;
    private HashMap<String, Replacement> replacementMap;
    private static final String TEST_EXAMPLE_RELATIVE_PATH = "src/test/java/examples";

    @BeforeEach
    public void init() throws IOException {
        parser = new Parser();
        parser.parseDirectory(TEST_EXAMPLE_RELATIVE_PATH);
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 10);
        Replacement replacement = new ReplacementBuilder(node).build();
        replacementMap = new HashMap<>();
        replacementMap.put(replacement.getId(), replacement);
    }

    @Test
    void testCreatingSolutionCodeDoesNotMutateOriginalNode(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BodyDeclaration<?> nodeClone = node.clone();
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> solutionCode = task.createSolutionCode();
        assertEquals(node, nodeClone);
        assertNotEquals(node, solutionCode);
    }

    @Test
    void testCreatingSolutionCodeRemovesCorrectStatements(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> solutionCode = task.createSolutionCode();
        assertSolutionStartEndStatementsWereRemoved(solutionCode);
        assertStatementsWerePreserved(node, solutionCode, new ArrayList<>());
    }

    private void assertSolutionStartEndStatementsWereRemoved(BodyDeclaration<?> solutionCode) {
        BlockStmt solutionCodeBlock = getBlockStmtFromBodyDeclaration(solutionCode);
        for(Statement statement : solutionCodeBlock.getStatements()){
            assertFalse(isStartStatement(statement));
            assertFalse(isEndStatement(statement));
        }
    }

    private void assertStatementsWerePreserved(BodyDeclaration<?> node, BodyDeclaration<?> solutionCode,
                                               List<Statement> statementsThatShouldNotBePreserved) {
        BlockStmt originalCodeBlock = getBlockStmtFromBodyDeclaration(node);
        BlockStmt solutionCodeBlock = getBlockStmtFromBodyDeclaration(solutionCode);
        NodeList<Statement> solutionStatements = solutionCodeBlock.getStatements();
        for(Statement originalStatement : originalCodeBlock.getStatements()){
            if(isEndStatement(originalStatement) || isStartStatement(originalStatement)
                    || statementsThatShouldNotBePreserved.contains(originalStatement)){
                continue;
            }
            assertTrue(solutionStatements.contains(originalStatement));
        }
    }

    @Test
    void testCreatingSolutionCodePreservesComments(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 3);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> solutionCode = task.createSolutionCode();
        assertCommentsWerePreserved(node, solutionCode);
    }

    private void assertCommentsWerePreserved(BodyDeclaration<?> node, BodyDeclaration<?> solutionCode) {
        assertEquals(node.getAllContainedComments(), solutionCode.getAllContainedComments());
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskDoesNotMutateOriginalNode(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BodyDeclaration<?> nodeClone = node.clone();
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode();
        assertEquals(node, nodeClone);
        assertNotEquals(node, startCode);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskRemovesCorrectStatements(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode();
        assertSolutionStartEndStatementsWereRemoved(startCode);
        List<Statement> solutionStatements = task.getSolution().getStatementsIncludingSolutionMarkers();
        assertStatementsWerePreserved(node, startCode, solutionStatements);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskHasRequiredImports(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode();
        ImportDeclaration importDeclaration = new ImportDeclaration("examples.TODO", false, false);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskHasStartTodoComment(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode();
        assertReplacementCodeHasStartTodoComment(task, startCode);
    }

    private void assertReplacementCodeHasStartTodoComment(ReplaceSolutionTask task, BodyDeclaration<?> startCode){
        Replacement replacement = task.getReplacement();
        Statement firstStatement = getFirstStatementOfReplacementCode(replacement);
        Statement firstStatementInStartCode = findStatementInStartCode(startCode, firstStatement);
        LineComment startComment = new LineComment(Replacement.START_COMMENT);
        Optional<Comment> firstStatementComment = firstStatementInStartCode.getComment();
        assertTrue(firstStatementComment.isPresent());
        assertEquals(startComment, firstStatementComment.get());
    }

    private Statement findStatementInStartCode(BodyDeclaration<?> startCode, Statement firstStatement) {
        BlockStmt startCodeBlock = getBlockStmtFromBodyDeclaration(startCode);
        for(Statement statement : startCodeBlock.getStatements()){
            if(statement.equals(firstStatement)){
                return statement;
            }
        }
        throw new IllegalStateException(
                String.format("Cant find statement %s in start code: %n%s", firstStatement, startCode));
    }

    private Statement getFirstStatementOfReplacementCode(Replacement replacement) {
        BlockStmt replacementCode = replacement.getReplacementCode();
        return replacementCode.getStatements().get(0);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskHasWithEndStatementHasEndTodoComment(){
        testCreatingStartCodeForReplaceSolutionTaskHasEndTodoComment(6);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskWithoutEndSolutionStatementHasEndTodoComment(){
        testCreatingStartCodeForReplaceSolutionTaskHasEndTodoComment(3);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskWithEndSolutionAsLastStatementHasEndTodoComment(){
        testCreatingStartCodeForReplaceSolutionTaskHasEndTodoComment(13);
    }

    private void testCreatingStartCodeForReplaceSolutionTaskHasEndTodoComment(int testId){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), testId);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode();
        assertReplacementCodeHasEndTodoComment(task.getSolution(), startCode);
    }

    private void assertReplacementCodeHasEndTodoComment(Solution solution, BodyDeclaration<?> startCode){
        List<Statement> solutionStatements = solution.getStatementsIncludingSolutionMarkers();
        int lastStatementIndex = solutionStatements.size() - 1;
        Statement lastStatement = solutionStatements.get(lastStatementIndex);
        BlockStmt startCodeBlock = getBlockStmtFromBodyDeclaration(startCode);
        assertTrue(endCommentIsRightAfterReplacementCode(startCodeBlock.getOrphanComments(), lastStatement));
    }

    private boolean endCommentIsRightAfterReplacementCode(List<Comment> orphanCommentsInBody, Statement lastStatement){
        for(Comment orphanComment : orphanCommentsInBody){
            if(Replacement.END_COMMENT.equals(orphanComment.getContent()) &&
                orphanComment.getTokenRange().equals(lastStatement.getTokenRange())){
                return true;
            }
        }
        return false;
    }


}
