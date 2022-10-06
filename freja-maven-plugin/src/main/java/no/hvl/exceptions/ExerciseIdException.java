package no.hvl.exceptions;

import java.util.List;

public class ExerciseIdException extends IllegalStateException{

    public ExerciseIdException(List<Integer> exerciseId, List<Integer> requiredId) {
        super(String.format("The assignment has an exercise with the id %s" +
                " without having an exercise with the id %s", exerciseId, requiredId));
    }

    public ExerciseIdException(List<Integer> exerciseId) {
        super(String.format("The assignment has an exercise with the id %s." +
                " Exercise ids are not zero-based and starts counting at 1.", exerciseId));
    }



}
