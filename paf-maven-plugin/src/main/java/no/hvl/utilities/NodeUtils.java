package no.hvl.utilities;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalBlockStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.concepts.Replacement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class NodeUtils {

    public NodeUtils() {
    }

    public static BodyDeclaration<?> findBodyDeclarationCopyInFiles
            (List<CompilationUnit> files, BodyDeclaration<?> targetNode) {
        for(CompilationUnit file : files){
            List<BodyDeclaration> bodyDeclarations = file.findAll(BodyDeclaration.class);
            for(BodyDeclaration<?> nodeCopy : bodyDeclarations){
                if(nodeCopy.equals(targetNode)){
                    return nodeCopy;
                }
            }
        }
        throw new IllegalStateException(String.format("Could not find copy of node: %n%s", targetNode));
    }

    public static NodeList<Node> getNodesWithoutComments(NodeList<?> nodes){
        NodeList<Node> nodesWithoutComments = new NodeList<>();
        nodes.forEach(node -> nodesWithoutComments.add(node.clone().removeComment()));
        return nodesWithoutComments;
    }

    public static boolean nodeHasBlockStmt(Node node){
        if(node instanceof NodeWithBlockStmt){
            return true;
        }
        boolean isNodeWithOptionalBlockStmt = node instanceof NodeWithOptionalBlockStmt;
        if(isNodeWithOptionalBlockStmt){
            NodeWithOptionalBlockStmt<?> nodeWithOptionalBlockStmt = (NodeWithOptionalBlockStmt<?>) node;
            return nodeWithOptionalBlockStmt.getBody().isPresent();
        }
        return false;
    }

    public static BlockStmt getBlockStmtFromBodyDeclaration(BodyDeclaration<?> bodyDeclaration){
        if(nodeHasBlockStmt(bodyDeclaration)){
            if(bodyDeclaration instanceof  NodeWithBlockStmt){
                return ((NodeWithBlockStmt<?>) bodyDeclaration).getBody();
            }
            if(bodyDeclaration instanceof  NodeWithOptionalBlockStmt){
                var body = ((NodeWithOptionalBlockStmt<?>) bodyDeclaration).getBody();
                if(body.isPresent()){
                    return body.get();
                }
            }
        }
        throw new IllegalArgumentException(
                String.format("The body declaration does not have a block statement:%n%s", bodyDeclaration));
    }

    public static boolean isStartStatement(Statement statement){
        return statementHasVariableDeclarationTypeName(statement, AnnotationNames.SOLUTION_START_NAME);
    }

    public static boolean isEndStatement(Statement statement){
        return statementHasVariableDeclarationTypeName(statement, AnnotationNames.SOLUTION_END_NAME);
    }

    private static boolean statementHasVariableDeclarationTypeName
            (Statement statement, String variableDeclarationTypeName){
        Optional<VariableDeclarationExpr> variableDeclarationExpr =  getStatementAsVariableDeclarationExpr(statement);
        if(variableDeclarationExpr.isPresent()){
            String actualVariableDeclarationTypeName = variableDeclarationExpr.get().getElementType().asString();
            return actualVariableDeclarationTypeName.equals(variableDeclarationTypeName);
        }
        return false;
    }

    private static Optional<VariableDeclarationExpr> getStatementAsVariableDeclarationExpr(Statement statement){
        if(!statement.isExpressionStmt()){
            return Optional.empty();
        }
        var expressionStmt = statement.asExpressionStmt();

        if(!expressionStmt.getExpression().isVariableDeclarationExpr()){
            return Optional.empty();
        }
        return Optional.of(expressionStmt.getExpression().asVariableDeclarationExpr());
    }

    public static void removeSolutionStartAndEndStatementsFromNodes(List<BodyDeclaration<?>> annotatedNodes) {
        annotatedNodes.forEach(NodeUtils::removeSolutionStartAndEndStatementsFromNode);
    }

    public static void removeSolutionStartAndEndStatementsFromNode(BodyDeclaration<?> annotatedNode) {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(annotatedNode.findAll(Statement.class,
                statement -> isStartStatement(statement) || isEndStatement(statement)));
        statements.forEach(Node::remove);
    }


    public static void replaceStatements(BlockStmt codeBlock, List<Statement> statementsToBeReplaced,
                                         BlockStmt replacementCode){
        if(statementsToBeReplaced.isEmpty()){
            throw new IllegalArgumentException("The list of statements to be replaced can not be empty.");
        }
        List<Statement> codeBlockStatements = codeBlock.getStatements();
        Statement firstStatementToBeReplaced = statementsToBeReplaced.get(0);
        Optional<Integer> startIndex = findIndexOfStatement(codeBlockStatements, firstStatementToBeReplaced);
        if(startIndex.isPresent()){
            NodeList<Statement> newCodeBlock = createNewCodeBlock(startIndex.get(), codeBlockStatements,
                    statementsToBeReplaced, replacementCode.getStatements());
            codeBlock.setStatements(newCodeBlock);
            insertStartTodoComment(replacementCode);
            insertEndTodoComment(codeBlock, statementsToBeReplaced);
        }else{
            throw new IllegalArgumentException(
                    String.format("Can not find the statement:%n%s%n%n in the code block:%n%s",
                            firstStatementToBeReplaced, codeBlock));
        }
    }

    private static Optional<Integer> findIndexOfStatement(List<Statement> statements, Statement statement) {
        for(int i = 0; i < statements.size(); i++){
            if(statements.get(i).equals(statement)){
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private static NodeList<Statement> createNewCodeBlock(Integer startIndexOfStatementsToBeReplaced,
                                                          List<Statement> codeBlockStatements,
                                                          List<Statement> statementsToBeReplaced,
                                                          List<Statement> replacementStatements) {
        List<Statement> newCodeBlock = new ArrayList<>();
        newCodeBlock.addAll(codeBlockStatements.subList(0, startIndexOfStatementsToBeReplaced));
        newCodeBlock.addAll(replacementStatements);
        int endIndexOfStatementsToBeReplaced = startIndexOfStatementsToBeReplaced + statementsToBeReplaced.size();
        newCodeBlock.addAll(codeBlockStatements.subList(endIndexOfStatementsToBeReplaced,
                codeBlockStatements.size()));
        return new NodeList<>(newCodeBlock);
    }

    private static void insertStartTodoComment(BlockStmt replacementCode) {
        Optional<Statement> firstStatement = replacementCode.getStatements().getFirst();
        firstStatement.ifPresent(statement -> statement.setLineComment(Replacement.START_COMMENT));
    }

    private static void insertEndTodoComment(BlockStmt codeBlock, List<Statement> statementsToBeReplaced) {
        Statement lastStatementToBeReplaced = statementsToBeReplaced.get(statementsToBeReplaced.size()-1);
        codeBlock.addOrphanComment(createEndComment(lastStatementToBeReplaced));
    }

    private static LineComment createEndComment(Statement statement){
        Optional<TokenRange> statementTokenRange = statement.getTokenRange();
        if(statementTokenRange.isPresent()){
            return new LineComment(statementTokenRange.get(), Replacement.END_COMMENT);
        }
        throw new IllegalArgumentException(String.format("The statement %s does not have a token range", statement));
    }


    //TODO remove methods below?
    public static HashSet<String> removeNodesFromFiles
            (List<CompilationUnit> files, List<BodyDeclaration<?>> nodesToRemove){
        HashSet<String> fileNamesToRemove = new HashSet<>();
        nodesToRemove.forEach(node -> {
            if(node.isTypeDeclaration()){
                var compilationUnitMaybe = node.findCompilationUnit();
                if(compilationUnitMaybe.isPresent()){
                    fileNamesToRemove.add(compilationUnitMaybe.get().getStorage().get().getFileName());
                    //TODO maybe create copy instead?
                    files.remove(compilationUnitMaybe.get());
                }
            }
            node.remove();

        });
        return fileNamesToRemove;
    }

    public static CallableDeclaration<?> castToCallableDeclaration(BodyDeclaration<?> bodyDeclaration){
        if(bodyDeclaration.isMethodDeclaration()){
            return bodyDeclaration.asMethodDeclaration();
        }else{
            return bodyDeclaration.asConstructorDeclaration();
        }
    }

}
