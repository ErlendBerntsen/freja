package no.hvl.annotations;

public enum TransformOption {
    REMOVE_EVERYTHING("REMOVE_EVERYTHING"),
    REMOVE_BODY("REMOVE_BODY"),
    REPLACE_BODY("REPLACE_BODY"),
    REMOVE_SOLUTION("REMOVE_SOLUTION"),
    REPLACE_SOLUTION("REPLACE_SOLUTION");

    private final String enumName;

    TransformOption(String enumName){
        this.enumName = enumName;
    }

    @Override
    public String toString(){
        return enumName;
    }

    public static TransformOption getOption(String enumName){
        for(TransformOption transformOption : TransformOption.values()){
            if(transformOption.toString().equals(enumName)) return transformOption;
        }
        return TransformOption.valueOf(enumName);
    }
}
