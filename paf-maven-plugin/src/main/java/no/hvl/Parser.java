package no.hvl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import no.hvl.exceptions.NoSourceDirectoryException;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private List<CompilationUnit> compilationUnits;
    private String directory;
    
    public Parser() {
        this.compilationUnits = new ArrayList<>();
    }

    public Parser(String directory) {
        this.compilationUnits = new ArrayList<>();
        this.directory = directory;
    }

    public List<CompilationUnit> getCompilationUnitCopies(){
        List<CompilationUnit> compilationUnitCopies = new ArrayList<>();
        compilationUnits.forEach(compilationUnit -> compilationUnitCopies.add(compilationUnit.clone()));
        return compilationUnitCopies;
    }


    public void parseDirectory(String dir) throws IOException {
        var sourceRoot = new SourceRoot(Paths.get(dir));
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");
        compilationUnits = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
    }

    public void parse() throws IOException {
        File sourceDir = findSourceDirectory(directory);
        parseDirectory(sourceDir.getAbsolutePath());
    }

    public File findSourceDirectory(String dir) throws NoSuchFileException {
        File projectDir = new File(dir);
        if(!projectDir.exists()){
            throw new NoSuchFileException(projectDir.getAbsolutePath());
        }

        for(File file : Objects.requireNonNull(projectDir.listFiles())){
            if("src".equalsIgnoreCase(file.getName())
            || "source".equalsIgnoreCase(file.getName())){
                return file;
            }
        }
        throw new NoSourceDirectoryException(dir);
    }
}
