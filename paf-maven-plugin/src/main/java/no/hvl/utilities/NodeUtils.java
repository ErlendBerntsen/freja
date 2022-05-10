package no.hvl.utilities;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NodeUtils {

    public NodeUtils() {
    }

    public NodeList<Node> removeCommentsFromNodes(NodeList<?> nodes){
        NodeList<Node> nodesWithoutComments = new NodeList<>();
        nodes.forEach(node -> nodesWithoutComments.add(node.clone().removeComment()));
        return nodesWithoutComments;
    }
}
