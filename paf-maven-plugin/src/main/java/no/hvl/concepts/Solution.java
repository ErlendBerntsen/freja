package no.hvl.concepts;

import com.github.javaparser.ast.stmt.Statement;

import java.util.List;

public class Solution {
    private List<Statement> statementsIncludingSolutionMarkers;

    public Solution() {
    }

    public List<Statement> getStatementsIncludingSolutionMarkers() {
        return statementsIncludingSolutionMarkers;
    }

    public void setStatementsIncludingSolutionMarkers(List<Statement> statementsIncludingSolutionMarkers) {
        this.statementsIncludingSolutionMarkers = statementsIncludingSolutionMarkers;
    }
}
