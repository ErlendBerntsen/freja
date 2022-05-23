package no.hvl.concepts.builders;


import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.concepts.Solution;
import no.hvl.utilities.NodeUtils;

import java.util.List;

import static no.hvl.utilities.NodeUtils.*;

public class SolutionBuilder {

    private final BlockStmt body;

    public SolutionBuilder(BlockStmt body){
        this.body = body;
    }

    public Solution build(){
        Solution solution = new Solution();
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
            if(isStartStatement(statements.get(i))){
                return i + 1;
            }
        }
        throw new IllegalStateException("Cant build solution without a SolutionStart statement");
    }

    private Integer findEndStatementIndex(){
        var statements = body.getStatements();
        for(int i = 0; i < statements.size(); i++){
            if(isEndStatement(statements.get(i))){
                return i;
            }
        }
        return statements.size();
    }


}
