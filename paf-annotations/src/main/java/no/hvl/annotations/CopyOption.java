package no.hvl.annotations;

public enum CopyOption {
    REMOVE_EVERYTHING("REMOVE_EVERYTHING"),
    REMOVE_SOLUTION("REMOVE_SOLUTION"),
    REPLACE_SOLUTION("REPLACE_SOLUTION"),
    REPLACE_BODY("REPLACE_BODY"),
    KEEP_SKELETON("KEEP_SKELETON");

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
