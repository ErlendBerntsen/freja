package examples.assignment6;

import no.hvl.annotations.Remove;
import no.hvl.annotations.TargetProject;

public class Assignment6Exercises {

    @Remove(removeFrom = TargetProject.ALL)
    int fieldShouldBeRemovedFromAllProjects1;

    @Remove
    int fieldShouldBeRemovedFromAllProjects2;
}
