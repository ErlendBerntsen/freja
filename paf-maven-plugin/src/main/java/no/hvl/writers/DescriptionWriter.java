package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static no.hvl.utilities.FileUtils.*;
import static no.hvl.utilities.NodeUtils.*;

public class DescriptionWriter {

    public static final String DESCRIPTIONS_FOLDER_NAME = "descriptions";
    private final String rootFolderPath;
    private String descriptionsDirPath;
    private final List<Exercise>  exercises;
    private final HashMap<String, String> descriptionsMap;
    private final HashMap<String, String> oldDescriptionsMap;


    public DescriptionWriter(String rootFolderPath, List<Exercise> exercises,
                             HashMap<String, String> oldDescriptions) {
        this.rootFolderPath = rootFolderPath;
        this.exercises = exercises;
        this.oldDescriptionsMap = oldDescriptions;
        this.descriptionsMap = new HashMap<>();
    }

    public void createExerciseDescriptions() throws IOException {
        createDescriptionDir();
        createDescriptionFiles();
    }

    public void createDescriptionDir() throws IOException {
        File descriptionsDir = tryToCreateDirectory(rootFolderPath, DESCRIPTIONS_FOLDER_NAME);
        this.descriptionsDirPath = descriptionsDir.getAbsolutePath();
    }

    public void createDescriptionFiles() throws IOException {
        for (Exercise exercise : exercises){
            String fileName = getExerciseFileName(exercise);
            File file = tryToCreateFile(descriptionsDirPath, fileName);
            String content = createFileContent(exercise);
            writeContentToFile(file, content);
        }
    }

    private String getExerciseFileName(Exercise exercise){
        return "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc";
    }

    public String createFileContent(Exercise exercise) {
        String attributes = createAttributes(exercise);
        String template = createTemplate(exercise, false);
        createDescription(exercise, template);
        return attributes + template;
    }

    private void createDescription(Exercise exercise, String template) {
        String fileName = getExerciseFileName(exercise);
        descriptionsMap.put(fileName, template);
    }

    public String createAttributes(Exercise rootExercise) {
        StringBuilder exerciseTemplate = new StringBuilder();
        for(Exercise exercise : rootExercise.getAllExercisesWithTask()){
            exerciseTemplate.append(createExerciseAttributes(exercise));
        }
        for(Task task : rootExercise.getTasksIncludingAllSubExerciseTasks()){
            exerciseTemplate.append(createTaskAttributes(task));
        }
        return exerciseTemplate.toString();
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
        String attributeKey = createAttributeKey(key);
        String attributeValue = createAttributeValue(value, addMacro);
        return attributeKey + attributeValue + createNewLine();
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

    private String getTypeAsString(Task task) {
        Node node = task.getNode();
        String className = node.getClass().getSimpleName();
        return className.replace("Declaration", "");
    }


    public String createTemplate(Exercise exercise, boolean keepOldTemplate){
        //TODO this flag should come from some option file
        if(keepOldTemplate){
            String fileName = getExerciseFileName(exercise);
            return oldDescriptionsMap.get(fileName);
        }
        String template = createTitle(exercise);
        template += createExerciseTemplate(exercise,1);
        return template;
    }

    public String createTitle(Exercise exercise) {
        return createNewLine()
                + "= *Exercise "
                + exercise.getNumberAmongSiblingExercises()
                + "*"
                + createNewLine();
    }

    public String createExerciseTemplate(Exercise exercise, int level){
        StringBuilder exerciseTemplate = new StringBuilder();
        exerciseTemplate.append(createNewLine());
        for(Exercise subExercise : exercise.getSubExercises()){
            exerciseTemplate.append(createListItem(".", level));
            if(subExercise.hasTasks()){
                exerciseTemplate.append(createExerciseIntroductionTemplate(subExercise));
                for(Task task : subExercise.getTasks()){
                    exerciseTemplate.append(createListItem("*", level));
                    exerciseTemplate.append(createTaskTemplate(task));
                }
            }
            exerciseTemplate.append(createExerciseTemplate(subExercise, level + 1));
        }
        return exerciseTemplate.toString();
    }

    public String createListItem(String syntax, int nesting) {
        return syntax.repeat(nesting)  + " ";
    }

    public String createExerciseIntroductionTemplate(Exercise exercise) {
        return "The starting code for this exercise can be found in the file "
                + getExerciseFileNameAttribute(exercise)
                + ", which you can find in the package "
                + getExerciseFilePackageAttribute(exercise)
                + ". Your task is to implement the following:"
                + createNewLine()
                + createNewLine();
    }

    public String createTaskTemplate(Task task){
        return "A " + getTaskTypeAttribute(task) + ":"
                + createNewLine()
                + createJavaCodeBlock(task);
    }

    private String createJavaCodeBlock(Task task) {
        return createNewLine()
                + "[source, java, subs=\"attributes+\"]"
                + createNewLine()
                + "----"
                + createNewLine()
                + getTaskFullNameAttribute(task)
                + createNewLine()
                + "----"
                + createNewLine()
                + createNewLine();
    }

    private String getExerciseFileNameAttribute(Exercise exercise){
        return getExerciseAttribute(exercise, "FileName");
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

    public HashMap<String, String> getDescriptionsMap() {
        return descriptionsMap;
    }
}
