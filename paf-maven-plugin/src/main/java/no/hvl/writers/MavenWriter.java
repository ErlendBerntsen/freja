package no.hvl.writers;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class MavenWriter {

    private String targetPath;

    public MavenWriter(String targetPath) {
        this.targetPath = targetPath;
    }

    public void createPomFiles() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File("pom.xml"));
        doc.getDocumentElement().normalize();
        removeAnnotationDependency(doc);
        removePafPlugin(doc);

        modifyArtifactId(doc, "solution");
        saveDomToFile(doc, targetPath + File.separator + "solution");
        modifyArtifactId(doc, "startcode");
        saveDomToFile(doc, targetPath + File.separator + "startcode");
    }

    private void saveDomToFile(Document document, String path) throws TransformerException {
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();

        StreamResult result = new StreamResult(new File(path + File.separator + "pom.xml"));
        transformer.transform(dom, result);
    }

    public static void modifyArtifactId(Document document, String postFix){
        NodeList nodeList = document.getElementsByTagName("artifactId");
        Node node = getProjectChild(nodeList);
        node.setTextContent(node.getTextContent() + "-" + postFix);
    }

    public static void removeAnnotationDependency(Document document){
        NodeList nodeList = document.getElementsByTagName("dependencies");
        Node dependencies = getProjectChild(nodeList);
        dependencies.removeChild(getChildNode(dependencies, "no.hvl", "paf-annotations"));
        removeElementIfEmpty(dependencies);
    }

    public static void removePafPlugin(Document document){
        NodeList nodeList = document.getElementsByTagName("build");
        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if("project".equals(node.getParentNode().getNodeName())){
                Node plugins = getChildNode(node, "plugins");
                plugins.removeChild(getChildNode(plugins, "no.hvl", "paf-maven-plugin"));
                removeElementIfEmpty(plugins);
                removeElementIfEmpty(node);
                return;
            }
        }
    }

    public static Node getChildNode(Node node, String childNodeName){
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            if(children.item(i).getNodeName().equals(childNodeName)){
                return children.item(i);
            }
        }
        return null;
    }

    public static Node getChildNode(Node node, String groupId, String artifactId){
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            NodeList elementInfo = children.item(i).getChildNodes();
            String elementGroudId = "";
            String elementArtifactId = "";
            for(int j = 0; j < elementInfo.getLength(); j++){
                if("groupId".equals(elementInfo.item(j).getNodeName())){
                    elementGroudId = elementInfo.item(j).getTextContent();
                }
                if("artifactId".equals(elementInfo.item(j).getNodeName())){
                    elementArtifactId  = elementInfo.item(j).getTextContent();
                }
            }
            if(elementGroudId.equals(groupId) && elementArtifactId.equals(artifactId)){
                return children.item(i);
            }
        }
        return null;
    }

    public static Node getProjectChild(NodeList nodeList){
        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if("project".equals(node.getParentNode().getNodeName())){
                return node;
            }
        }
        return null;
    }

    public static void removeElementIfEmpty(Node element){
        element.normalize();
        if(element.getChildNodes().getLength() == 1){
            element.getParentNode().removeChild(element);
        }
    }
}
