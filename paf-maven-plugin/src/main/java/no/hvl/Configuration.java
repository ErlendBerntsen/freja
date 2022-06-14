package no.hvl;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private final String sourcePath;
    private final String targetPath;
    private final List<String> filesToIgnore;
    private boolean keepOldDescriptionTemplates;

    public Configuration(String sourcePath, String targetPath,
                         List<String> filesToIgnore, boolean keepOldDescriptionTemplates) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.filesToIgnore = filesToIgnore;
        this.keepOldDescriptionTemplates = keepOldDescriptionTemplates;
    }

    public Configuration(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.filesToIgnore = new ArrayList<>();
        this.keepOldDescriptionTemplates = false;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public List<String> getFilesToIgnore() {
        return filesToIgnore;
    }

    public boolean getKeepOldDescriptionTemplates() {
        return keepOldDescriptionTemplates;
    }
}
