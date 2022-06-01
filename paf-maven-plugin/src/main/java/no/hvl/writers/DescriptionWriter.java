package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.utilities.FileUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class DescriptionWriter {

    public static final String DESCRIPTIONS_FOLDER_NAME = "descriptions";
    private final String rootFolderPath;
    private String descriptionsDirPath;
    private StringBuilder content = new StringBuilder();
    private final List<Exercise>  exercises;

    public DescriptionWriter(String rootFolderPath, List<Exercise> exercises) {
        this.rootFolderPath = rootFolderPath;
        this.exercises = exercises;
    }

    public void createExerciseDescriptions() throws IOException {
        createDescriptionDir();
        createDescriptionFiles();
    }

    public void createDescriptionDir() throws IOException {
        File descriptionsDir = tryToCreateDirectory(rootFolderPath, DESCRIPTIONS_FOLDER_NAME);
        this.descriptionsDirPath = descriptionsDir.getAbsolutePath();
    }

    public void createFiles() {
        //TODO Remove
        for (Exercise exercise : exercises){
            try {
                createFileAttributes(exercise);
                createTemplate(exercise);
                File descriptionFile = new File(descriptionsDirPath + File.separator + "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc");
                FileWriter fileWriter = new FileWriter(descriptionFile);
                fileWriter.write(content.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void createDescriptionFiles() throws IOException {
        for (Exercise exercise : exercises){
            String fileName = "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc";
            File file = tryToCreateFile(descriptionsDirPath, fileName);
            String content = createFileContent(exercise);
            writeContentToFile(file, content);
        }
    }

    public String createFileContent(Exercise exercise) {
        return createAttributes(exercise);
    }

    public String createAttributes(Exercise exercise) {
        content = new StringBuilder();
        content.append(createExerciseAttributes(exercise));
        for(Task task : exercise.getTasksIncludingSubExercises()){
            content.append(createTaskAttributes(task));
        }
        return content.toString();
    }

    public String createFileAttributes(Exercise exercise){
        //TODO remove
        content = new StringBuilder();
        var exercisesWithFile = getExercisesWithFile(exercise, new ArrayList<>());
        for(var exerciseWithFile : exercisesWithFile){
            content.append(createExerciseAttributes(exerciseWithFile));
        }
        for(Task task : exercise.getTasks()){
            content.append(createTaskAttributes(task));
        }
        return content.toString();
    }

    public String createExerciseAttributes(Exercise exercise){
        String exerciseName = "Exercise" + exercise.getFullNumberAsString();
        CompilationUnit file = exercise.getFile();
        String packageName = getPackageName(file);
        String fileName = getFileName(file);
        String fileSimpleName = getFileSimpleName(file);
        String attribute = createAttribute(exerciseName + "Package", packageName, true);
        attribute += createAttribute(exerciseName + "FileName", fileName, true);
        attribute += createAttribute(exerciseName + "FileSimpleName", fileSimpleName,true);
        return attribute;
    }

    private String getPackageName(CompilationUnit file) {
        Optional<PackageDeclaration> packageDeclaration = file.getPackageDeclaration();
        if(packageDeclaration.isPresent()){
            return packageDeclaration.get().getNameAsString();
        }
        return "Unknown package";
    }

    private String getFileSimpleName(CompilationUnit file) {
        Optional<TypeDeclaration<?>> typeDeclaration = file.getPrimaryType();
        if(typeDeclaration.isPresent()){
            return typeDeclaration.get().getNameAsString();
        }
        return "Unknown simple file name";
    }

    private String createAttribute(String key, String value, boolean addMacro){
        String attribute = createAttributeKey(key);
        attribute += createAttributeValue(value, addMacro);
        attribute += createNewLine();
        return attribute;
    }

    private String createAttributeKey(String key) {
        return ":" + key + ": ";
    }

    private String createAttributeValue(String value, boolean addMacro) {
        return (addMacro? createInlineLiteralPassMacro(value) : value);
    }

    private String createInlineLiteralPassMacro(String value){
        return "pass:normal[`+" + value + "+`]";
    }

    private String createNewLine() {
        return "\n";
    }

    public String createTaskAttributes(Task task){
        String taskName = "Task" + task.getFullNumberAsString();
        String fullName = getFullName(task);
        String attribute =  createAttribute(taskName + "FullName", fullName, false);
        String simpleName = getTaskSimpleName(task);
        attribute += createAttribute(taskName + "SimpleName", simpleName, true);
        String type = getTypeAsString(task);
        attribute += createAttribute(taskName + "Type", type, true);
        return attribute;
    }

    private String getTypeAsString(Task task) {
        Node node = task.getNode();
        String className = node.getClass().getSimpleName();
        return className.replace("Declaration", "");
    }

    private String getFullName(Task task){
        BodyDeclaration<?> node = task.getNode();
        BodyDeclaration<?> nodeClone = node.clone();
        removeUnneededNodes(nodeClone);
        String line = getFirstLineOfNode(nodeClone);
        line = removeCurlyBracketAtLineEndIfPresent(line);
        line = replaceReservedSigns(line);
        line = line.stripTrailing();
        return line;

    }

    private void removeUnneededNodes(BodyDeclaration<?> node) {
        node.setAnnotations(new NodeList<>());
        if(node instanceof NodeWithMembers){
            ((NodeWithMembers<?>) node).setMembers(new NodeList<>());
        }
        removeAllComments(node);
    }

    private String getFirstLineOfNode(BodyDeclaration<?> nodeClone) {
        String nodeAsString = nodeClone.toString();
        Optional<String> firstLine = nodeAsString.lines().findFirst();
        if(firstLine.isPresent()){
            return firstLine.get();
        }
        throw new IllegalArgumentException("There are no lines of code in the node: " + nodeAsString);
    }

    private String removeCurlyBracketAtLineEndIfPresent(String line) {
        line = line.stripTrailing();
        char[] lineCharArray  = line.toCharArray();
        if(lineCharArray[lineCharArray.length-1] == '{'){
            return line.substring(0, line.length()-1);
        }
        return line;
    }

    private String replaceReservedSigns(String line) {
        line = line.replace("<", "{lt}");
        line = line.replace(">", "{gt}");
        return line;
    }

    private String getTaskSimpleName(Task task){
        BodyDeclaration<?> node = task.getNode();
        if(node.isFieldDeclaration()){
            VariableDeclarator firstField = node.asFieldDeclaration().getVariable(0);
            return tryToGetSimpleName(firstField);
        }else{
            return tryToGetSimpleName(node);
        }
    }


    private void createTemplate(Exercise exercise){
        content.append("\n= *Exercise ").append(exercise.getNumberAmongSiblingExercises()).append("*\n");
        createExerciseTemplate(exercise,1);
    }

    private void createExerciseTemplate(Exercise exercise, int level){
        content.append("\n");
        for(Exercise subExercise : exercise.getSubExercises()){
            content.append(".".repeat(level)).append(" ");
            if(subExercise.hasTasks()){
                content.append("The starting code for this exercise can be found in the file ")
                        .append(getExerciseFileNameAttribute(subExercise))
                        .append(", which you can find in the package ")
                        .append(getExerciseFilePackageAttribute(subExercise))
                        .append(". Your task is to implement the following:\n\n");

                for(Task task : subExercise.getTasks()){
                    createTaskTemplate(task, level);
                }
            }
            createExerciseTemplate(subExercise, level + 1);

        }
    }

    private void createTaskTemplate(Task task, int level){
        content.append("*".repeat(level)).append(" A ").append(getTaskTypeAttribute(task)).append(":\n")
                .append("+\n")
                .append("[source, java, subs=\"attributes+\"]\n")
                .append("----\n")
                .append(getTaskFullNameAttribute(task)).append("\n")
                .append("----\n\n");
    }

    private List<Exercise> getExercisesWithFile(Exercise exercise, List<Exercise> exercises){
        if(exercise.getFile() != null){
            exercises.add(exercise);
        }
        for(Exercise subExercise : exercise.getSubExercises()){

            exercises.addAll(getExercisesWithFile(subExercise, exercises));
        }
        return exercises.stream().distinct().collect(Collectors.toList());
    }


    private String getExerciseFileNameAttribute(Exercise exercise){
        return getExerciseAttribute(exercise, "FileName");
    }

    private String getExerciseFileSimpleNameAttribute(Exercise exercise){
        return getExerciseAttribute(exercise, "FileSimpleName");
    }

    private String getExerciseFilePackageAttribute(Exercise exercise){
        return getExerciseAttribute(exercise, "Package");
    }

    private String getExerciseAttribute(Exercise exercise, String attribute){
        return "{Exercise" + exercise.getFullNumberAsString() +  attribute + "}";
    }

    private String getTaskTypeAttribute(Task task){
        return getTaskAttribute(task, "Type");
    }

    private String getTaskFullNameAttribute(Task task){
        return getTaskAttribute(task, "FullName");
    }

    private String getTaskAttribute(Task task, String attribute){
        return "{Task" + task.getFullNumberAsString() + attribute + "}";
    }

    public String getDescriptionsDirPath() {
        return descriptionsDirPath;
    }
}
