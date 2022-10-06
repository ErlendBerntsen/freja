package no.hvl;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Configuration {
    private final String sourcePath;
    private final String targetPath;
    private final List<String> filesToIgnore;
    private final boolean keepOldDescriptionTemplates;

    public Configuration(String sourcePath, String targetPath,
                         List<String> filesToIgnore, boolean keepOldDescriptionTemplates) {
        this.sourcePath = sourcePath;
        this.targetPath = getAbsoluteTargetPath(sourcePath, targetPath);
        filesToIgnore.add("**.git");
        this.filesToIgnore = filesToIgnore;
        this.keepOldDescriptionTemplates = keepOldDescriptionTemplates;
    }

    public Configuration(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = getAbsoluteTargetPath(sourcePath, targetPath);
        this.filesToIgnore = List.of("**.git");
        this.keepOldDescriptionTemplates = false;
    }

    public String getAbsoluteTargetPath(String sourcePath, String targetPath){
        if(Path.of(targetPath).isAbsolute()){
            return targetPath;
        }
        return sourcePath + File.separator + targetPath;
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
