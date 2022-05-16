package no.hvl.concepts;


import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.utilities.NodeUtils;

import java.util.List;

public class SolutionBuilder {

    private BlockStmt body;
    private Solution solution;

    public SolutionBuilder(BlockStmt body){
        this.body = body;
    }

    public Solution build(){
        solution = new Solution();
        solution.setStatements(findSolutionStatements());
        return solution;
    }

    private List<Statement> findSolutionStatements() {
        Integer startStatementIndex = findStartStatementIndex();
        Integer endStatementIndex = findEndStatementIndex();
        if(endStatementIndex <= startStatementIndex){
            throw new IllegalStateException("SolutionEnd statement must be after SolutionStart statement.");
        }
        return body.getStatements().subList(startStatementIndex, endStatementIndex);
    }

    private Integer findStartStatementIndex(){
        var statements = body.getStatements();
        for(int i = 0; i < statements.size(); i++){
            if(NodeUtils.isStartStatement(statements.get(i))){
                return i;
            }
        }
        throw new IllegalStateException("Cant build solution without a SolutionStart statement");
    }

    private Integer findEndStatementIndex(){
        var statements = body.getStatements();
        for(int i = 0; i < statements.size(); i++){
            if(NodeUtils.isEndStatement(statements.get(i))){
                return i;
            }
        }
        return statements.size();
    }


}
