package no.hvl;

public enum Copy {
    REMOVE_EVERYTHING("REMOVE_EVERYTHING"),
    REMOVE_SOLUTION("REMOVE_SOLUTION"),
    REPLACE_SOLUTION("REPLACE_SOLUTION"),
    REPLACE_BODY("REPLACE_BODY"),
    KEEP_SKELETON("KEEP_SKELETON");

    private final String enumName;

    Copy(String enumName){
        this.enumName = enumName;
    }

    @Override
    public String toString(){
        return enumName;
    }

    public static Copy getCopy(String enumName){
        for(Copy copy : Copy.values()){
            if(copy.toString().equals(enumName)) return copy;
        }
        return Copy.valueOf(enumName);
    }
}
