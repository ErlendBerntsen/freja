package examples;

import no.hvl.annotations.TransformOption;
import no.hvl.annotations.Exercise;
import no.hvl.annotations.Remove;
@SuppressWarnings("ALL")
public class AnnotatedNodesGetterTestClass {

    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_EVERYTHING)
    int annotatedNode1;

    @Remove
    @Exercise(id = {1}, transformOption = TransformOption.REMOVE_EVERYTHING)
    int annotatedNode2;

    @Remove
    int annotatedNode3;
}
