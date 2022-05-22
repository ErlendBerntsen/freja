package examples;

import no.hvl.annotations.CopyOption;
import no.hvl.annotations.Implement;
import no.hvl.annotations.Remove;

public class AnnotatedNodesGetterTestClass {

    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    int annotatedNode1;

    @Remove
    @Implement(number = {1}, copyOption = CopyOption.REMOVE_EVERYTHING)
    int annotatedNode2;

    @Remove
    int annotatedNode3;
}
