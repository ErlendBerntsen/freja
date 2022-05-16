package no.hvl.concepts;

import com.github.javaparser.ast.body.BodyDeclaration;

import com.github.javaparser.ast.stmt.BlockStmt;
import no.hvl.annotations.CopyOption;
import no.hvl.utilities.NodeUtils;

import java.util.Optional;

public class Task {
    private BodyDeclaration<?> node;
    private String fullNumberAsString;
    private CopyOption copyOption;
    private Optional<Solution> solution ;

    public Task(BodyDeclaration<?> node) {
        this.node = node;
        this.solution = Optional.empty();
    }

    public BodyDeclaration<?> getNode() {
        return node;
    }

    public void setNode(BodyDeclaration<?> node) {
        this.node = node;
    }

    public String getFullNumberAsString() {
        return fullNumberAsString;
    }

    public void setFullNumberAsString(String fullNumberAsString) {
        this.fullNumberAsString = fullNumberAsString;
    }

    public void setSolution(Optional<Solution> solution){
        this.solution = solution;
    }

    public String convertNumberArrayToString(int[] number, int taskNumber){
        var taskNumberString = new StringBuilder();
        for(int digit : number){
            taskNumberString.append(digit);
            taskNumberString.append("_");
        }
        taskNumberString.append(taskNumber).append("_");
        return taskNumberString.toString();
    }

    public boolean hasSolution(){
        return solution.isPresent();
    }

    public void removeSolutionFromNode(){
        if(!hasSolution()){
            throw new IllegalStateException("This task does not have a solution to be removed");
        }
        BlockStmt blockStmt = NodeUtils.getBlockStmtFromBodyDeclaration(node);
        removeSolutionFromBlockStmt(blockStmt);
        //TODO insert start/end comments
    }

    private void removeSolutionFromBlockStmt(BlockStmt blockStmt) {
        blockStmt.getStatements().removeAll(solution.get().getStatements());
    }


}
