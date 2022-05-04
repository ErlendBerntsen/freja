package no.hvl.concepts;

import com.github.javaparser.ast.body.BodyDeclaration;

public class Task {
    private BodyDeclaration<?> node;
    private String fullNumberAsString;

    public Task(BodyDeclaration<?> node) {
        this.node = node;
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

    public String convertNumberArrayToString(int[] number, int taskNumber){
        var taskNumberString = new StringBuilder();
        for(int digit : number){
            taskNumberString.append(digit);
            taskNumberString.append("_");
        }
        taskNumberString.append(taskNumber).append("_");
        return taskNumberString.toString();
    }
}
