package examples;

import static java.lang.Thread.currentThread;

public class TODO {

    public static String method() {

        String methodName = currentThread().getStackTrace()[2].getMethodName();

        return "Metoden " + methodName + " er ikke implementert";
    }


    public static String construtor(String className) {

        return "Konstruktøren for klassen " + className + " er ikke implementert";

    }

}