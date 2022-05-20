package no.hvl.writers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import no.hvl.concepts.Exercise;
import no.hvl.concepts.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DescriptionWriter {

    private String targetPath;
    private StringBuilder content = new StringBuilder();
    private String fileName ="";
    private List<Exercise>  exercises;

    public DescriptionWriter(String targetPath, List<Exercise> exercises) throws IOException {
        this.exercises = exercises;
        File descriptionsDir = new File(targetPath + File.separator + "descriptions");
        this.targetPath = descriptionsDir.getAbsolutePath();
        descriptionsDir.mkdir();
    }

    public void createFiles() {
        for (Exercise exercise : exercises){
            try {
                createFileAttributes(exercise);
                createTemplate(exercise);
                File descriptionFile = new File(targetPath + File.separator + "Exercise" + exercise.getNumberAmongSiblingExercises() + ".adoc");
                FileWriter fileWriter = new FileWriter(descriptionFile);
                fileWriter.write(content.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void createFileAttributes(Exercise exercise){
        content = new StringBuilder();
        var exercisesWithFile = getExercisesWithFile(exercise, new ArrayList<>());
        for(var exerciseWithFile : exercisesWithFile){
            writeGeneralAttributes(exerciseWithFile);
        }
        var tasks = getTasks(exercise, new ArrayList<>());
        for(Task task : tasks){
            writeTaskAttributes(task);
        }

    }

    private void writeGeneralAttributes(Exercise exercise){
        CompilationUnit file = exercise.getFile();
        CompilationUnit.Storage storage= file.getStorage().get();
        fileName = storage.getFileName();
        String name = "Exercise" + exercise.getFullNumberAsString();
        String packageName = file.getPackageDeclaration().isPresent()? file.getPackageDeclaration().get().getNameAsString()
                : "";
        String typeName = file.getPrimaryType().isPresent()? file.getPrimaryType().get().getNameAsString()
                : "";
        createAttribute(name+ "Package", packageName, true);
        createAttribute(name+ "Filename", fileName, true);
        createAttribute(name + "FileSimpleName", typeName,true);
    }

    private void writeTaskAttributes(Task task){
        String name = "Task" + task.getFullNumberAsString();
        createAttribute(name + "FullName", getFullName(task.getNode()), false);
        createAttribute(name + "SimpleName", getSimpleName(task.getNode()), true);
        String type = task.getNode().getClass().getSimpleName().replace("Declaration", "");

        if(task.getNode().isClassOrInterfaceDeclaration()){
            type = type.replace("Or", "");
            type = task.getNode().asClassOrInterfaceDeclaration().isInterface() ?
                    type.replace("Class", "")
                    : type.replace("Interface", "");
        }
        createAttribute(name + "Type", type, true);

    }



    private String getFullName(BodyDeclaration<?> implementNode){
        var implementNodeClone = implementNode.clone();
        implementNodeClone.removeComment();
        implementNodeClone.setAnnotations(new NodeList<>());
        if(implementNode instanceof NodeWithMembers){
            ((NodeWithMembers<?>) implementNode).setMembers(new NodeList<>());
        }
        var comments = List.copyOf(implementNodeClone.getAllContainedComments());
        comments.forEach(Comment::remove);

        String line = implementNodeClone.toString().lines().findFirst().get();
        if(line.toCharArray()[line.toCharArray().length-1] == '{'){
            line = line.substring(0, line.length()-1);
        }
        line = line.replace("<", "{lt}");
        line = line.replace(">", "{gt}");
        line = line.stripTrailing();
        return line;

    }

    private String getSimpleName(BodyDeclaration<?> implementNode){
        if(implementNode.isClassOrInterfaceDeclaration()){
            return implementNode.asClassOrInterfaceDeclaration().getName().asString();
        }else if(implementNode.isFieldDeclaration()){
            return implementNode.asFieldDeclaration().getVariables().get(0).getName().asString();
        }else if(implementNode.isConstructorDeclaration()){
            return implementNode.asConstructorDeclaration().getName().asString();
        }else if(implementNode.isMethodDeclaration()){
            return implementNode.asMethodDeclaration().getNameAsString();
        }
        return "";
    }

    private void createAttribute(String key, String value, boolean addMacro){
        content.append(":").append(key).append(": ").append(addMacro? createInlineLiteralPassMacro(value) : value).append("\n");
    }

    private String createInlineLiteralPassMacro(String value){
        return "pass:normal[`+" + value + "+`]";
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

    private List<Task> getTasks(Exercise exercise, List<Task> tasks){
        tasks.addAll(exercise.getTasks());
        for(Exercise subExercise : exercise.getSubExercises()){
            getTasks(subExercise, tasks);
        }
        return tasks;
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
}
