package no.hvl.annotations;

public enum CopyOption {
    REMOVE_EVERYTHING("REMOVE_EVERYTHING"),
    REMOVE_BODY("REMOVE_BODY"),
    REPLACE_BODY("REPLACE_BODY"),
    REMOVE_SOLUTION("REMOVE_SOLUTION"),
    REPLACE_SOLUTION("REPLACE_SOLUTION");

    private final String enumName;

    CopyOption(String enumName){
        this.enumName = enumName;
    }

    @Override
    public String toString(){
        return enumName;
    }

    public static CopyOption getCopy(String enumName){
        for(CopyOption copyOption : CopyOption.values()){
            if(copyOption.toString().equals(enumName)) return copyOption;
        }
        return CopyOption.valueOf(enumName);
    }
}
