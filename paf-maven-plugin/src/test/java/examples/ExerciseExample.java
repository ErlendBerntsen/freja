package examples;

import no.hvl.annotations.Exercise;
import testUtils.TestId;

import static no.hvl.annotations.TransformOption.*;

@SuppressWarnings("ALL")
public class ExerciseExample {

    @TestId(15)
    @Exercise(id = {1}, transformOption = REMOVE_EVERYTHING)
    int task1;

    @TestId(16)
    @Exercise(id = {2, 1}, transformOption = REMOVE_EVERYTHING)
    int task2;

    @TestId(18)
    @Exercise(id = {3, 1}, transformOption = REMOVE_EVERYTHING)
    int task3;

    @TestId(19)
    @Exercise(id = {3, 2}, transformOption = REMOVE_EVERYTHING)
    int task4;

    @TestId(20)
    @Exercise(id = {4}, transformOption = REMOVE_EVERYTHING)
    int task5;

    @TestId(21)
    @Exercise(id = {4}, transformOption = REMOVE_EVERYTHING)
    int task6;

    @TestId(33)
    @Exercise(id = {5,1}, transformOption = REMOVE_EVERYTHING)
    int task7;

    @TestId(34)
    @Exercise(id = {5,1}, transformOption = REMOVE_EVERYTHING)
    int task8;
}
