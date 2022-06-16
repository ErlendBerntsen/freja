package no.hvl.concepts;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.List;
import java.util.Objects;

public class Replacement {

    private String id;
    private CompilationUnit file;
    private BlockStmt replacementCode;
    private List<ImportDeclaration> requiredImports;
    public static final String START_COMMENT = "TODO - START";
    public static final String END_COMMENT = "TODO - END";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replacement that = (Replacement) o;
        return Objects.equals(id, that.id) && Objects.equals(file, that.file) && replacementCode.equals(that.replacementCode) && Objects.equals(requiredImports, that.requiredImports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, file, replacementCode, requiredImports);
    }
}
