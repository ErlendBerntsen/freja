package no.hvl.writers;

import no.hvl.utilities.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class MavenWriter {

    private final String targetPath;
    private final Document pomDocument;
    public static final String GROUP_ID = "no.hvl";
    public static final String ANNOTATIONS_ARTIFACT_ID = "paf-annotations";
    public static final String PLUGIN_ARTIFACT_ID = "paf-maven-plugin";


    public MavenWriter(String pomFilePath, String targetPath) throws IOException, SAXException, ParserConfigurationException {
        this.targetPath = targetPath;
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        pomDocument = builder.parse(new File(pomFilePath));
        pomDocument.getDocumentElement().normalize();
    }

    public void createPomFiles() throws TransformerException, NoSuchFileException {
        FileUtils.checkPathExists(targetPath);
        removeAnnotationDependency();
        removePafPlugin();
        String artifactId = getArtifactId();
        createPomFile(artifactId, ProjectWriter.SOLUTION_PROJECT_NAME);
        createPomFile(artifactId, ProjectWriter.START_CODE_PROJECT_NAME);
    }

    public void removeAnnotationDependency(){
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = getProjectChild(nodeList);
        dependencies.removeChild(getChildNodeWithIds(dependencies, GROUP_ID, ANNOTATIONS_ARTIFACT_ID));
        removeElementIfEmpty(dependencies);
    }

    public Node getProjectChild(NodeList nodeList){
        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if("project".equals(node.getParentNode().getNodeName())){
                return node;
            }
        }
        throw new IllegalArgumentException("Could not find any nodes in list that has \"project\" as parent node");
    }

    public Node getChildNodeWithIds(Node node, String groupId, String artifactId){
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            NodeList elementInfo = child.getChildNodes();
            if(elementInfoMatchesIds(elementInfo, groupId, artifactId)){
                return child;
            }
        }
        throw new IllegalArgumentException(String.format("Could not find child with groupId \"%s\" and artifactId \"%s\"" +
                "in the node: \"%s\"", node.getNodeName(), groupId, artifactId));
    }

    private boolean elementInfoMatchesIds(NodeList elementInfo, String groupId, String artifactId) {
        String elementGroupId = "";
        String elementArtifactId = "";
        for(int i = 0; i < elementInfo.getLength(); i++){
            if("groupId".equals(elementInfo.item(i).getNodeName())){
                elementGroupId = elementInfo.item(i).getTextContent();
            }
            if("artifactId".equals(elementInfo.item(i).getNodeName())){
                elementArtifactId  = elementInfo.item(i).getTextContent();
            }
        }
        return (elementGroupId.equals(groupId) && elementArtifactId.equals(artifactId));
    }

    public void removeElementIfEmpty(Node element){
        element.normalize();
        if(element.getChildNodes().getLength() == 1){
            element.getParentNode().removeChild(element);
        }
    }

    public void removePafPlugin(){
        Node buildElement = getProjectChild(pomDocument.getElementsByTagName("build"));
        Node plugins = getChildNodeWithName(buildElement, "plugins");
        plugins.removeChild(getChildNodeWithIds(plugins, GROUP_ID, PLUGIN_ARTIFACT_ID));
        removeElementIfEmpty(plugins);
        removeElementIfEmpty(buildElement);
    }

    public Node getChildNodeWithName(Node node, String childNodeName){
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            if(children.item(i).getNodeName().equals(childNodeName)){
                return children.item(i);
            }
        }
        throw new IllegalArgumentException(
                String.format("Could not find child with name \"%s\" in the node: \"%s\"",
                        childNodeName, node.getNodeName()));
    }

    private void createPomFile(String artifactId, String name) throws TransformerException {
        modifyArtifactId(artifactId + "-" + name);
        saveDomToFile(targetPath + File.separator + name);
    }

    public String getArtifactId(){
        NodeList nodeList = pomDocument.getElementsByTagName("artifactId");
        Node artifactId = getProjectChild(nodeList);
        return artifactId.getTextContent();
    }

    public void modifyArtifactId(String newArtifactId){
        NodeList nodeList = pomDocument.getElementsByTagName("artifactId");
        Node artifactIdNode = getProjectChild(nodeList);
        artifactIdNode.setTextContent(newArtifactId);
    }

    private void saveDomToFile(String path) throws TransformerException {
        DOMSource dom = new DOMSource(pomDocument);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new File(path + File.separator + "pom.xml"));
        transformer.transform(dom, result);
    }

    public Document getPomDocument() {
        return pomDocument;
    }

}
