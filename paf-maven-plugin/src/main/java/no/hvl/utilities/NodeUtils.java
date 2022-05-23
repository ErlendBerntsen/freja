package no.hvl.utilities;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalBlockStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

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

    public NodeList<Node> removeCommentsFromNodes(NodeList<?> nodes){
        NodeList<Node> nodesWithoutComments = new NodeList<>();
        nodes.forEach(node -> nodesWithoutComments.add(node.clone().removeComment()));
        return nodesWithoutComments;
    }

    public static boolean isNodeWithBlockStmt(Node node){
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
        if(isNodeWithBlockStmt(bodyDeclaration)){
            if(bodyDeclaration instanceof  NodeWithBlockStmt){
                return ((NodeWithBlockStmt<?>) bodyDeclaration).getBody();
            }
            if(bodyDeclaration instanceof  NodeWithOptionalBlockStmt){
                return ((NodeWithOptionalBlockStmt<?>)bodyDeclaration).getBody().get();
            }
        }
        throw new IllegalArgumentException("The body declaration does not have a block statement.");
    }

    public static boolean isStartStatement(Statement statement){
        return statementHasVariableDeclarationTypeName(statement, AnnotationNames.SOLUTION_START_NAME);
    }

    public static boolean isEndStatement(Statement statement){
        return statementHasVariableDeclarationTypeName(statement, AnnotationNames.SOLUTION_END_NAME);
    }

    public static boolean statementHasVariableDeclarationTypeName(Statement statement, String variableDeclarationTypeName){
        var variableDeclarationExpr =  getStatementAsVariableDeclarationExpr(statement);
        if(variableDeclarationExpr.isPresent()){
            String actualVariableDeclarationTypeName = variableDeclarationExpr.get().getElementType().asString();
            return actualVariableDeclarationTypeName.equals(variableDeclarationTypeName);
        }
        return false;
    }

    public static Optional<VariableDeclarationExpr> getStatementAsVariableDeclarationExpr(Statement statement){
        if(!statement.isExpressionStmt()){
            return Optional.empty();
        }
        var expressionStmt = statement.asExpressionStmt();

        if(!expressionStmt.getExpression().isVariableDeclarationExpr()){
            return Optional.empty();
        }
        return Optional.of(expressionStmt.getExpression().asVariableDeclarationExpr());
    }

    public static boolean blockStmtHasSolution(BlockStmt blockStmt){
        for(Statement statement : blockStmt.getStatements()){
            if(isStartStatement(statement)){
                return true;
            }
        }
        return false;
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

    public static HashSet<String> removeNodesFromFiles(List<CompilationUnit> files, List<BodyDeclaration<?>> nodesToRemove){
        HashSet<String> fileNamesToRemove = new HashSet<>();
        nodesToRemove.forEach(node -> {
            if(node.isTypeDeclaration()){
                var compilationUnitMaybe = node.findCompilationUnit();
                if(compilationUnitMaybe.isPresent()){
                    fileNamesToRemove.add(compilationUnitMaybe.get().getStorage().get().getFileName());
                    //TODO maybe create copy instead?
                    files.remove(compilationUnitMaybe.get());
                }
            }else{
                node.remove();
            }
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

    public static void replaceStatements(BlockStmt codeBlock, List<Statement> statementsToBeReplaced,
                                         List<Statement>  replacementStatements){
        NodeList<Statement> codeBlockStatements = codeBlock.getStatements();
        Optional<Integer> startIndex = findIndexOfStatement(codeBlockStatements, statementsToBeReplaced.get(0));
        startIndex.ifPresent(integer -> codeBlockStatements.addAll(integer, replacementStatements));
        codeBlockStatements.removeAll(statementsToBeReplaced);
    }

    public static Optional<Integer> findIndexOfStatement(NodeList<Statement> statements, Statement statement) {
        for(int i = 0; i < statements.size(); i++){
            if(statements.get(i).equals(statement)){
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
