package examples;

import no.hvl.annotations.Implement;
import testUtils.TestId;

import static no.hvl.annotations.CopyOption.*;

public class ExerciseExample {

    @TestId(15)
    @Implement(number = {1}, copyOption = REMOVE_EVERYTHING)
    int task1;

    @TestId(16)
    @Implement(number = {2, 1}, copyOption = REMOVE_EVERYTHING)
    int task2;

    @TestId(18)
    @Implement(number = {3, 1}, copyOption = REMOVE_EVERYTHING)
    int task3;

    @TestId(19)
    @Implement(number = {3, 2}, copyOption = REMOVE_EVERYTHING)
    int task4;

    @TestId(20)
    @Implement(number = {4}, copyOption = REMOVE_EVERYTHING)
    int task5;

    @TestId(21)
    @Implement(number = {4}, copyOption = REMOVE_EVERYTHING)
    int task6;
}
