package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.List;

public class Replacement {

    private String id;
    private CompilationUnit file;
    private BlockStmt replacementCode;
    private List<ImportDeclaration> requiredImports;

    public Replacement(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CompilationUnit getFile() {
        return file;
    }

    public void setFile(CompilationUnit file) {
        this.file = file;
    }

    public BlockStmt getReplacementCode() {
        return replacementCode;
    }

    public void setReplacementCode(BlockStmt replacementCode) {
        this.replacementCode = replacementCode;
    }

    public List<ImportDeclaration> getRequiredImports() {
        return requiredImports;
    }

    public void setRequiredImports(List<ImportDeclaration> requiredImports) {
        this.requiredImports = requiredImports;
    }
}
