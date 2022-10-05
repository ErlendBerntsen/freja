package examples.assignment5;

import no.hvl.annotations.Remove;
import no.hvl.annotations.TargetProject;

public class Assignment5Exercises {

    @Remove(removeFrom = TargetProject.SOLUTION)
    int fieldShouldOnlyBeRemovedFromSolution;
}
