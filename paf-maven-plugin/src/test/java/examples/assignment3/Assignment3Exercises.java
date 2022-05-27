package examples.assignment3;

import no.hvl.annotations.CopyOption;
import no.hvl.annotations.Implement;
import no.hvl.annotations.Remove;

public class Assignment3Exercises {

    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    int task;

    @Remove
    int fieldShouldBeRemoved;
}
