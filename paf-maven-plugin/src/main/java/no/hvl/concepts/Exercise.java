package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import no.hvl.concepts.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Exercise {

    private int numberAmongSiblingExercises;
    private List<Exercise> subExercises;
    private List<Task> tasks;
    private CompilationUnit file;
    private String fullIdAsString;
    private Optional<Exercise> parentExercise;

    public Exercise() {
        subExercises = new ArrayList<>();
        tasks = new ArrayList<>();
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

    public void addAbstractTask(Task task){
        this.tasks.add(task);
    }

    public CompilationUnit getFile() {
        return file;
    }

    public void setFile(CompilationUnit file) {
        this.file = file;
    }

    public String getFullIdAsString() {
        return fullIdAsString;
    }

    public void setFullIdAsString(String fullIdAsString) {
        this.fullIdAsString = fullIdAsString;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Exercise> getExerciseIncludingAllSubExercises(){
        List<Exercise> allExercises = new ArrayList<>();
        allExercises.add(this);
        for(Exercise subExercise : subExercises){
            allExercises.addAll(subExercise.getExerciseIncludingAllSubExercises());
        }
        return allExercises;
    }

    public List<Exercise> getAllExercisesWithTask(){
        List<Exercise> allExercises = getExerciseIncludingAllSubExercises();
        return allExercises.stream()
                .filter(Exercise::hasTasks)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksIncludingAllSubExerciseTasks(){
        List<Task> allTasks = new ArrayList<>(tasks);
        for(Exercise subExercise : subExercises){
            allTasks.addAll(subExercise.getTasksIncludingAllSubExerciseTasks());
        }
        return allTasks;
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
    }

    public boolean hasTasks(){
        return !tasks.isEmpty();
    }

    public int getAmountOfTasks(){
        return tasks.size();
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
