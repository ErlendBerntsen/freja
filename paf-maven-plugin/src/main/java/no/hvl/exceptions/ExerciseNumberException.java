package no.hvl.exceptions;

import java.util.List;

public class ExerciseNumberException extends IllegalStateException{

    public ExerciseNumberException(List<Integer> exerciseNumber, List<Integer> requiredNumber) {
        super(String.format("The assignment has an exercise with the number %s" +
                " without having an exercise with the number %s", exerciseNumber, requiredNumber));
    }

    public ExerciseNumberException(List<Integer> exerciseNumber) {
        super(String.format("The assignment has an exercise with the number %s." +
                " Exercise numbers are not zero-based and starts counting at 1.", exerciseNumber));
    }



}
