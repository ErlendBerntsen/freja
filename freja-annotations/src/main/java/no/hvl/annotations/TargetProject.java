package no.hvl.annotations;

public enum TargetProject {
    START_CODE("START_CODE"),
    SOLUTION("SOLUTION"),
    ALL("ALL");

    private final String enumName;

    TargetProject(String enumName){
        this.enumName = enumName;
    }

    @Override
    public String toString(){
        return enumName;
    }

    public static TargetProject getTargetProject(String enumName){
        for(TargetProject targetProject : TargetProject.values()){
            if(targetProject.toString().equals(enumName)) return targetProject;
        }
        return TargetProject.valueOf(enumName);
    }
}
