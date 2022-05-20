package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;

public class Exercise {

    private int numberAmongSiblingExercises;
    private List<Exercise> subExercises;
    private List<Task> tasks;
    private List<AbstractTask> abstractTasks;
    private CompilationUnit file;
    private String fullNumberAsString;

    public Exercise() {
        subExercises = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    public int getNumberAmongSiblingExercises() {
        return numberAmongSiblingExercises;
    }

    public void setNumberAmongSiblingExercises(int numberAmongSiblingExercises) {
        this.numberAmongSiblingExercises = numberAmongSiblingExercises;
    }

    public List<Exercise> getSubExercises() {
        return subExercises;
    }

    public void setSubExercises(List<Exercise> subExercises) {
        this.subExercises = subExercises;
    }

    public void addTask(Task task){
        this.tasks.add(task);
    }

    public void addAbstractTask(AbstractTask task){
        this.abstractTasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public CompilationUnit getFile() {
        return file;
    }

    public void setFile(CompilationUnit file) {
        this.file = file;
    }

    public String getFullNumberAsString() {
        return fullNumberAsString;
    }

    public void setFullNumberAsString(String fullNumberAsString) {
        this.fullNumberAsString = fullNumberAsString;
    }

    public String convertNumberArrayToString(int[] number){
        var taskNumberString = new StringBuilder();
        for(int digit : number){
            taskNumberString.append(digit);
            taskNumberString.append("_");
        }
        return taskNumberString.toString();
    }

    public boolean hasTasks(){
        return !tasks.isEmpty();
    }


    public int getAmountOfTasks(){
        return tasks.size();
    }
}
