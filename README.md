# Programming Assignment Framework

## Introduction
This is a framework for developing Java programming assignment in an intuitive and fast way. Programming assignments consists of different artefacts, such as solution, start code, tests, descriptions etc. Currently, these are often manually developed in isolation from another, often with much information that is related or repeated. This is unnecessary time waste and also a breeding ground for causing inconsistencies and other bugs, especially when refactoring. The core idea for this framework is to centralize everything into one project and generating the different artefacts from that. This is done by writing the entire solution to the assignment first, and expanding it with annotations that are related to central concepts in programming assignments. Artefacts such as start code, solution without annotations, and descriptions are generated by running a Maven command.

## Installment

Copy this project to your computer either by cloning, forking or downloading this repository. This project relies on [Maven](https://maven.apache.org/download.cgi) so make sure you have it installed. Open the project and then a terminal and run the command `mvn clean install` to install the plugin into to your local maven repository. Make sure you run this command in the project's root directory, i.e., .../programmingAssignmentFramework. You may need to configure the JDK source and target version properties in the pom file to be compatible with your development environment. 


## Usage
Start by creating a new Maven project in your favorite Java IDE. Open up the pom.xml file in your new project and include the paf-annotations dependency to be able to use the different annotations in your project:

```xml
<dependency>
    <groupId>no.hvl</groupId>
    <artifactId>paf-annotations</artifactId>
    <version>1.1</version>  
</dependency>
```

If this dependency is not found after reloading the pom file or project for some reason, then you can add the jar to the project as an external library. The jar is found in the `target` folder in the `paf-annotations` module in your local project of this framework. 

To be able to execute the plugin to generate the artefacts, you need to add a plugin to the Maven build lifecycle. This plugin has a `configuration` attribute called `targetPath`, where you define the path of the target folder to generate the artefacts in. An example of this is shown below:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>no.hvl</groupId>
            <artifactId>paf-maven-plugin</artifactId>
            <version>1.1</version>
            <configuration>
                <targetPath>C:\Users\Acer\IntelliJProjects\HelloWorldOutput</targetPath>
            </configuration>
        </plugin>
    </plugins>
</build>
```

The `targetPath` configuration option is the only one that is required. There are more options that  are optional:
* `keepOldDescriptions` is a boolean attribute that when set to true, prevents the old descriptions (if any) from being overwritten by the default description templates when regenerating files. See the part on [exercise descriptions](#Exercise Descriptions) for more information. If there aren't any old description files in the target folder than the default template is used instead. This option is set to `false` by default.
* `ignore` is an attribute that takes a list of strings as arguments. This is for specifying files and folders that should be ignored when generating the artefacts. The path of these files and folders should be specified relative to the root folder path using [glob patterns](https://en.wikipedia.org/wiki/Glob_(programming)). This is very similar to how a .gitignore file works. Use the `<ignore>` element tag for both the list and for each list element. An example is shown below with some common patterns:
```xml
<ignore>
    <ignore>.idea</ignore>
    <ignore>HelloWorld.iml</ignore>
    <ignore>src/test</ignore>
    <ignore>src/main/java/no/hvl/IgnoreThisFile.java</ignore>
    <ignore>**IgnoreThisFileToo.java</ignore>
    <ignore>**.txt</ignore>
</ignore>
```

* The first pattern ignores a folder called `.idea` (and all of its content) in the project root folder.
* The second ignores a file called `HelloWorld.iml` in the project root folder.
* Ignores a folder called `test` which is in the `src` folder (the `src` folder is located in the project root folder).
* Ignores a file called `IgnoreThisFile.java` by specifying its complete path from the project root.
* Ignores every file called `IgnoreThisFileToo.java` no matter its location relative to the project root.
* Ignores every file in the project ending with `.txt`

**Make sure your root folder for Java files is either called `src` or `source`**. To generate artefacts from your project run the command `mvn paf:generate` from the terminal. Make sure you are in the same directory as the pom file. This will not be as exciting if you have not used any of the annotations in your project. 

### Annotations

**@Exercise**

This is used as an annotation on constructs that should be implemented in an exercise. It can be used on field variables, constructors and methods. This annotation currently has three members (attributes):
* `int[] id`: This denotes the exercise number in which this construct should be implemented. The reason for this being an array of integers is to be able to denote hierarchies in exercises. For example, exercise 1 might have two subexercises, which would be expressed with the arrays `{1,1}` and `{1,2}` respectively. It is allowed for two different annotations to use the same id. This can be thought of as splitting an exercise into several tasks.
* `TransformOption transformOption`: This gives different options for what to do with the annotated construct when generating the start code. For constructs that contain bodies (methods and constructors), the solution can be wrapped between special statements which can be targeted by this transform option. The different options are:
    * `REMOVE_EVERYTHING`: Removes everything, both the skeleton and the body of the construct. This is the only option that makes sense to use on field variables.
  * `REMOVE_BODY`: Removes the entire body, while the skeleton is kept. 
  * `REPLACE_BODY`: Replaces the entire body with some other code that is specified by the `replacementId` attribute.
  * `REMOVE_SOLUTION`: Removes only the solution from the body, while everything outside the marked solution will be kept, including the skeleton. 
  * `REPLACE_SOLUTION`: Replaces only the solution with some other code that is specified by the `replacementId` attribute.
    
* `String replacementId`: Specifies the id of the `ReplacementCode` annotation to use for replacing some code. This only need to be specified when the `transformOption` is set to `REPLACE_BODY` or `REPLACE_SOLUTION`. If the `replacementId` is not specified when the `transformOption` is set to one of those options, a default replacement will be used instead, which is a statement that throws an exception for unimplemented methods/constructors: 

```java
throw new UnsupportedOperationException("The method <method name> is not implemented");
```

**@ReplacementCode**

This is used to mark some construct whose body can be used as a replacement for some other construct that is annotated with `@Exercise`. For example, all statements within a method body will be used in the replacement if it was annotated with `@ReplacementCode`.  This annotation only has one attribute: `String id` that must be specified. This id is what is referenced by the `replacementId` attribute in the `@Exercise` annotation. 

**@Remove**

This annotation is used to remove constructs entirely from the generated start code. This may be useful in cases where you want to remove a solution, but the solution spans several methods. You probably also want to use this annotation in conjunction with the `@ReplacementCode` annotation as to not bloat the start code with unnecessary and confusing code for students. Using this annotation on a class or interface will remove the entire file from all the generated projects. 

### Marking Solutions

The markers used to capture solutions are technically annotations also. However, they are used somewhat different due to the restrictions on where it is allowed to insert annotations. The `SolutionStart` annotation is used to mark the start of a solution. This is done by inserting a variable declaration statement of type `SolutionStart` on the line before the solution. Note that is doesn't matter what you call the variable, just as long it is of type `SolutionStart`. **Do not include the annotation sign (@) prefix**. The same is done with `SolutionEnd` on the line after the solution to mark its end.

At some point you probably want to include a return statement in the solution. Unfortunately this will not compile, since the `SolutionEnd` statement will be unreachable. As a workaround, you can exclude the `SolutionEnd` statement, and the end of the method body will represent the end of the solution instead. Be careful with replacing solutions that has statements after it. If the replacement code end with a return or throw statement, then you will likely generate code that doesn't compile, due to the same unreachable statement problem explained above. Some examples of solution marking is shown below: 

Solution is completely wrapped:
```java
{
    ...
    SolutionStart s;
    str = "Hello World";
    SolutionEnd e;
    return str;
}
```

Solution with a return statement:
```java
{
    ...
    SolutionStart s;
    return "Hello World";
}
```

## Generated artefacts

So what is actually generated? There will be created two projects in the target path folder that was given in the configuration. One is called `startcode` and the other `solution`. Both projects will copy every file and folder from the source project except the files and folders that are listed in the `ignore` part of the configuration options.  The Java files are modified according to the annotations. The pom file will also be modified to remove dependencies and plugins related to this framework (other dependencies and plugins will be kept). The `solution` project will keep all solutions, but remove all annotations and everything else that is related to this framework. The `startcode` project is similar, but the constructs annotated with `@Exercise` are handled according to the `transformOption`.

### Exercise Descriptions
The `startcode` project will also include a folder called `descriptions` that has a templated description file for each top-level exercise (i.e. each construct that is annotated with `@Exercise` and share the same first digit in `id` will share the same description file). This is done with [AsciiDoc](https://asciidoc.org/) using attributes. These attributes make it possible to quickly refer to information about constructs that are marked with the `@Exercise` annotation. This information includes the construct's simple name, full name, path, filename etc. This information is updated automatically when refactoring the construct, as long as it doesn't change its hierarchical position in the exercise. This ensures consistency between exercise descriptions and source code, since the descriptions are automatically updated when refactoring the source code. An example template description is also included with each exercise. Both PDF and HTML can be generated from the AsciiDoc files. It is highly recommended installing an AsciiDoc plugin for your IDE to make it easier to work it:
* [IntelliJ AsciiDoc plugin](https://plugins.jetbrains.com/plugin/7391-asciidoc)
* [Eclipse AsciiDoc plugin](https://marketplace.eclipse.org/content/asciidoctor-editor)
* [VSCode AsciiDoc plugin](https://marketplace.visualstudio.com/items?itemName=asciidoctor.asciidoctor-vscode)

#### Attributes 

An attribute is a key (string) wrapped in colons on each side, and a value (string) following the key. The attributes are defined at the top of document and can be referenced later by wrapping the key in curly brackets. This attribute reference is then swapped out with its value when generating the output of the document. An asciidoc attribute definition looks like this:

`:key: value`

and is used in text like this:

`The  value of the attribute called "key"  is {key}`

There are created three different attributes for each unique `id` in all of the `@Exercise` annotations: the package name, the file name (with extension), and the simple file name (without extension). The key for these attributes are on the form `Exercise` + exercise id + name (e.g package, fullName, etc.) 

There are also three different attributes for each construct that is annotated with `@Exercise`: the full name (i.e. the source code definition), the simple name (e.g. the method name), and the type of construct (i.e. method, constructor or field variable). The key for these attributes are on the form `Task` + exercise id and task number in exercise + name (e.g fullName, type, etc.) These attributes can be referenced in the exercise description as seen above.

An example template description is created each time when running the maven command, overwriting any previous description. This can be turned off in the maven plugin configurations.

#### Example

The following assigment source code:

```java
package no.hvl;

public class HelloWorld {

    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_BODY)
    public String helloWorld(){
        return "Hello World";
    }
}
```
Would produce the asciidoc file `Exercise1.adoc` :
```asciidoc
:Exercise1_Package: pass:normal[`+no.hvl+`]
:Exercise1_FileName: pass:normal[`+HelloWorld.java+`]
:Exercise1_FileSimpleName: pass:normal[`+HelloWorld+`]
:Task1_1_FullName: public String helloWorldNewName()
:Task1_1_SimpleName: pass:normal[`+helloWorldNewName+`]
:Task1_1_Type: pass:normal[`+Method+`]

= *Exercise 1*

. The starting code for this exercise can be found in the file {Exercise1_FileName}, which you can find in the package {Exercise1_Package}. Your task is to implement the following:

* A {Task1_1_Type}:

[source, java, subs="attributes+"]
----
{Task1_1_FullName}
----
```

The value of the attribute keys may seem a little cryptic. This is just to make the value be in monospace text (this is identical with wrapping some text in backticks in markdown) when swapping out the attribute key. The example description template displays some syntax of asciidoc, such as title, lists, attribute usage, and source code block. Take note of the `subs="attributes+"` in the source code block definition. This allows for using attributes in the source code block. The output of this asciidoc would be similar to something like this in markdown:

# Exercise 1

1. The starting code for this exercise can be found in the file `HelloWorld.java`, which you can find in the package `no.hvl`. Your task is to implement the following:

    * A `Method`:
  ```java
public String helloWorldNewName()
```



    
    




