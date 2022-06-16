package examples.assignment3;

import no.hvl.annotations.TransformOption;
import no.hvl.annotations.Exercise;
import no.hvl.annotations.Remove;

public class Assignment3Exercises {

    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_EVERYTHING)
    int task;

    @Remove
    int fieldShouldBeRemoved;
}
