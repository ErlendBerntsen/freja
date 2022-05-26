package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import no.hvl.concepts.tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Exercise {

    private int numberAmongSiblingExercises;
    private List<Exercise> subExercises;
    private List<Task> tasks;
    private List<AbstractTask> abstractTasks;
    private CompilationUnit file;
    private String fullNumberAsString;
    private Optional<Exercise> parentExercise;

    public Exercise() {
        subExercises = new ArrayList<>();
        tasks = new ArrayList<>();
        abstractTasks = new ArrayList<>();
        parentExercise = Optional.empty();
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

    public boolean hasTasks(){
        return !tasks.isEmpty();
    }

    public int getAmountOfTasks(){
        return tasks.size();
    }

    public List<AbstractTask> getAbstractTasks() {
        return abstractTasks;
    }

    public void setAbstractTasks(List<AbstractTask> abstractTasks){
        this.abstractTasks = abstractTasks;
    }

    public boolean hasAbstractTasks(){
        return !abstractTasks.isEmpty();
    }

    public int getAmountOfAbstractTasks(){
        return abstractTasks.size();
    }

    public int getAmountOfSubExercises(){
        return subExercises.size();
    }

    public boolean hasParentExercise() {
        return parentExercise.isPresent();
    }

    public Optional<Exercise> getParentExercise() {
        return parentExercise;
    }

    public void setParentExercise(Exercise parentExercise) {
        this.parentExercise = Optional.of(parentExercise);
    }

    public void addSubExercise(Exercise subExercise) {
        subExercises.add(subExercise);
    }
}
