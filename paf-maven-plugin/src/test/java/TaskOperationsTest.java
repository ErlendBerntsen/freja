import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.annotations.CopyOption;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Replacement;
import no.hvl.concepts.Solution;
import no.hvl.concepts.builders.TaskBuilder;
import no.hvl.concepts.tasks.AbstractTask;
import no.hvl.concepts.tasks.RemoveBodyTask;
import no.hvl.concepts.tasks.RemoveEverythingTask;
import no.hvl.concepts.tasks.ReplaceSolutionTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ExamplesParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.hvl.utilities.NodeUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getNodeWithId;

class TaskOperationsTest extends ExamplesParser {

    @BeforeEach
    public void setUp() throws IOException {
        init();
    }

    @Test
    void testCreatingSolutionCodeDoesNotMutateOriginalNode(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BodyDeclaration<?> nodeClone = node.clone();
        BodyDeclaration<?> nodeCopy = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> solutionCode = task.createSolutionCode(nodeCopy);
        assertEquals(nodeCopy, solutionCode);
        assertEquals(node, nodeClone);
        assertNotEquals(node, nodeCopy);
    }

    @Test
    void testCreatingSolutionCodeRemovesCorrectStatements(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> solutionCode = task.createSolutionCode(nodeToUpdate);
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
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> solutionCode = task.createSolutionCode(nodeToUpdate);
        assertCommentsWerePreserved(node, solutionCode);
    }

    private void assertCommentsWerePreserved(BodyDeclaration<?> node, BodyDeclaration<?> solutionCode) {
        assertEquals(node.getAllContainedComments(), solutionCode.getAllContainedComments());
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskDoesNotMutateOriginalNode(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        BodyDeclaration<?> nodeClone = node.clone();
        BodyDeclaration<?> nodeCopy = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode(nodeCopy);
        assertEquals(nodeCopy, startCode);
        assertEquals(node, nodeClone);
        assertNotEquals(nodeCopy, node);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskRemovesCorrectStatements(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> startCode = task.createStartCode(nodeToUpdate);
        assertSolutionStartEndStatementsWereRemoved(startCode);
        List<Statement> solutionStatements = task.getSolution().getStatementsIncludingSolutionMarkers();
        assertStatementsWerePreserved(node, startCode, solutionStatements);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskDoesNotAddImportsFromSamePackage(){
        BodyDeclaration<?> startCode = getStartCodeFromNodeWithId(6);
        ImportDeclaration todoImport = new ImportDeclaration("examples.TODO", false, false);
        CompilationUnit updatedFile = findFile(startCode);
        assertFalse(updatedFile.getImports().contains(todoImport));
    }

    private BodyDeclaration<?> getStartCodeFromNodeWithId (int targetId){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), targetId);
        AbstractTask task = new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        return task.createStartCode(nodeToUpdate);
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskAddsRequiredImports(){
        BodyDeclaration<?> startCode = getStartCodeFromNodeWithId(6);
        ImportDeclaration listImport = new ImportDeclaration("java.util.List", false, false);
        CompilationUnit updatedFile = findFile(startCode);
        assertTrue(updatedFile.getImports().contains(listImport));
    }

    @Test
    void testCreatingMultipleStartCodeForReplaceSolutionsDoesNotDuplicateImports(){
        List<CompilationUnit> files = parser.getCompilationUnitCopies();
        BodyDeclaration<?> node = getNodeWithId(files, 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        task.createStartCode(nodeToUpdate);
        BodyDeclaration<?> node2 = getNodeWithId(files, 13);
        ReplaceSolutionTask task2 = (ReplaceSolutionTask) new TaskBuilder(node2, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate2 = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> startCode2 = task2.createStartCode(nodeToUpdate2);
        CompilationUnit updatedFile = findFile(startCode2);
        assertTrue(hasExactlyOneListImport(updatedFile));
    }

    private boolean hasExactlyOneListImport(CompilationUnit file){
        ImportDeclaration listImport = new ImportDeclaration("java.util.List", false, false);
        boolean hasListImport = false;
        for(ImportDeclaration importDecl : file.getImports()){
            if(listImport.equals(importDecl)){
                if(hasListImport){
                    return false;
                }
                hasListImport = true;
            }
        }
        return hasListImport;
    }

    @Test
    void testCreatingStartCodeForReplaceSolutionTaskHasStartTodoComment(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 6);
        ReplaceSolutionTask task = (ReplaceSolutionTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> startCode = task.createStartCode(nodeToUpdate);
        assertReplacementCodeHasStartTodoComment(task, startCode);
    }

    private void assertReplacementCodeHasStartTodoComment(ReplaceSolutionTask task, BodyDeclaration<?> startCode){
        Replacement replacement = task.getReplacement();
        Statement firstStatement = getFirstStatementOfReplacementCode(replacement);
        LineComment startComment = new LineComment(Replacement.START_COMMENT);
        firstStatement.setComment(startComment);
        Statement firstStatementInStartCode = findStatementInStartCode(startCode, firstStatement);
        assertEquals(firstStatement, firstStatementInStartCode);
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
    void testCreatingStartCodeForReplaceSolutionTaskWithEndStatementHasEndTodoComment(){
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
        BodyDeclaration<?> nodeToUpdate = findBodyDeclarationCopyInFiles(parser.getCompilationUnitCopies(), node);
        BodyDeclaration<?> startCode = task.createStartCode(nodeToUpdate);
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

    @Test
    void testCreatingStartCodeForRemoveEverythingTask(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 1);
        Optional<Node> parentNode = node.getParentNode();
        if(parentNode.isEmpty()){
            fail("The node must have a parent node");
        }
        RemoveEverythingTask task = (RemoveEverythingTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode(node);
        assertFalse(parentNode.get().getChildNodes().contains(startCode));
    }

    @Test
    void testCreatingStartCodeForRemoveBodyTaskOnMethod(){
        BlockStmt codeBlock = createRemoveBodyTaskWithTestId(22);
        assertTrue(codeBlock.isEmpty());
    }

    @Test
    void testCreatingStartCodeForRemoveBodyTaskOnConstructor(){
        BlockStmt codeBlock = createRemoveBodyTaskWithTestId(23);
        assertTrue(codeBlock.isEmpty());
    }

    private BlockStmt createRemoveBodyTaskWithTestId(int testId){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), testId);
        RemoveBodyTask task = (RemoveBodyTask) new TaskBuilder(node, new Exercise(), replacementMap).build();
        BodyDeclaration<?> startCode = task.createStartCode(node);
        return getBlockStmtFromBodyDeclaration(startCode);
    }

    @Test
    void testCreatingStartCodeForRemoveBodyTaskOnFieldVariable(){
        BodyDeclaration<?> node = getNodeWithId(parser.getCompilationUnitCopies(), 24);
        TaskBuilder taskBuilder =  new TaskBuilder(node, new Exercise(), replacementMap);
        try{
            taskBuilder.build();
            fail("Should throw error");
        }catch (IllegalArgumentException e){
            assertEquals(String.format("The copyOption \"%s\" is not allowed on field variables," +
                    " only on methods and constructors", CopyOption.REMOVE_BODY), e.getMessage());
        }
    }

}
