package no.hvl.concepts.builders;


import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import no.hvl.concepts.Solution;
import no.hvl.exceptions.NodeException;

import java.util.List;

import static no.hvl.utilities.AnnotationNames.*;
import static no.hvl.utilities.NodeUtils.*;

public class SolutionBuilder {

    private final BlockStmt body;

    public SolutionBuilder(BlockStmt body){
        this.body = body;
    }

    public Solution build(){
        Solution solution = new Solution();
        solution.setStatementsIncludingSolutionMarkers(findSolutionStatements());
        return solution;
    }

    private List<Statement> findSolutionStatements() {
        Integer startStatementIndex = findStartStatementIndex();
        Integer endStatementIndex = findEndStatementIndex();
        if(endStatementIndex <= startStatementIndex){
            throw new NodeException(body, String.format("%s statement must be after %s statement.",
                    SOLUTION_END_NAME, SOLUTION_START_NAME));
        }
        return body.getStatements().subList(startStatementIndex, endStatementIndex);
    }

    private Integer findStartStatementIndex(){
        var statements = body.getStatements();
        for(int i = 0; i < statements.size(); i++){
            if(isStartStatement(statements.get(i))){
                if(i == statements.size() - 1){
                    throw new NodeException(body,
                            String.format("%s statement cant be the last statement in a code block",
                            SOLUTION_START_NAME));
                }
                return i;
            }
        }
        throw new NodeException(body, String.format("Cant build solution without a %s statement",
                SOLUTION_START_NAME));
    }

    private Integer findEndStatementIndex(){
        var statements = body.getStatements();
        for(int i = 0; i < statements.size(); i++){
            if(isEndStatement(statements.get(i))){
                return i + 1;
            }
        }
        return statements.size();
    }


}
