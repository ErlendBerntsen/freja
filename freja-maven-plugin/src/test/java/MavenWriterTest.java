import no.hvl.Configuration;
import no.hvl.Parser;
import no.hvl.concepts.Assignment;
import no.hvl.concepts.builders.AssignmentBuilder;
import no.hvl.utilities.FileUtils;
import no.hvl.writers.MavenWriter;
import no.hvl.writers.ProjectWriter;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.NoSuchFileException;
import java.util.stream.Stream;

import static no.hvl.writers.MavenWriter.*;
import static no.hvl.writers.ProjectWriter.*;
import static org.junit.jupiter.api.Assertions.*;
import static testUtils.TestUtils.getFrejaTestExamplePath;

class MavenWriterTest {

    private MavenWriter mavenWriter;

    @Rule
    private final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeEach
    void setup() throws ParserConfigurationException, SAXException, IOException {
        String path = "src/test/java/examples/pomfiles/simple/pom.xml";
        mavenWriter = new MavenWriter(path, "");
    }

    @Test
    void testGettingArtifactId() {
        String artifactId = mavenWriter.getArtifactId();
        assertEquals("HelloWorld", artifactId);
    }

    @Test
    void testModifyingArtifactId() {
        mavenWriter.modifyArtifactId("newArtifactId");
        String artifactId = mavenWriter.getArtifactId();
        assertEquals("newArtifactId", artifactId);
    }

    @Test
    void testGettingProjectChild(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = mavenWriter.getProjectChild(nodeList);
        String nodeAsString = """
                    <dependencies>
                            <dependency>
                                <groupId>no.hvl</groupId>
                                <artifactId>freja-annotations</artifactId>
                                <version>1.1</version>
                            </dependency>
                        </dependencies>""";
        assertXmlStringEquals(nodeAsString, getNodeAsString(dependencies));
    }

    private void assertXmlStringEquals(String expected, String actual){
        assertLinesMatch(removeLineAndWhitespaceDifferences(expected), removeLineAndWhitespaceDifferences(actual));
    }

    private Stream<String> removeLineAndWhitespaceDifferences(String string){
        string = string.replace("   ", "");
        string = string.replace(" ", "");
        return string.lines();
    }

    private String getNodeAsString(Node node)  {
        try{
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(node);
            trans.transform(source, result);
            sw.close();
            return sw.toString();
        }catch (TransformerException | IOException e){
            e.printStackTrace();
            fail();
        }
        return "";
    }

    @Test
    void testGettingNonExistingProjectChild(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("");
        try{
            mavenWriter.getProjectChild(nodeList);
            fail("Should throw exception");
        }catch (IllegalArgumentException e){
            assertEquals("Could not find any nodes in list that has \"project\" as parent node", e.getMessage());
        }
    }

    @Test
    void testGettingChildNodeWithIds(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = mavenWriter.getProjectChild(nodeList);
        Node childNode = mavenWriter.getChildNodeWithIds(dependencies, GROUP_ID, ANNOTATIONS_ARTIFACT_ID);
        String childNodeString = """
                <dependency>
                <groupId>no.hvl</groupId>
                <artifactId>freja-annotations</artifactId>
                <version>1.1</version>
                </dependency>""";
        assertXmlStringEquals(childNodeString, getNodeAsString(childNode));
    }

    @Test
    void testGettingChildNodeWithNonexistentIds(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = mavenWriter.getProjectChild(nodeList);
        String id = "thisIdDoesNotExist";
        try{
            mavenWriter.getChildNodeWithIds(dependencies, id, id);
            fail("Should throw exception");
        }catch (IllegalArgumentException e){
            String expectedMessage = String.format("Could not find child with groupId \"%s\" and artifactId \"%s\"" +
                    "in the node: \"%s\"", "dependencies", id, id);
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void testRemovingNonEmptyElement(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = mavenWriter.getProjectChild(nodeList);
        mavenWriter.removeElementIfEmpty(dependencies);
        nodeList = pomDocument.getElementsByTagName("dependencies");
        dependencies = mavenWriter.getProjectChild(nodeList);
        String nodeAsString = """
                    <dependencies>
                            <dependency>
                                <groupId>no.hvl</groupId>
                                <artifactId>freja-annotations</artifactId>
                                <version>1.1</version>
                            </dependency>
                        </dependencies>""";
        assertXmlStringEquals(nodeAsString, getNodeAsString(dependencies));
    }

    @Test
    void testRemovingEmptyElement(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("description");
        Node description = mavenWriter.getProjectChild(nodeList);
        mavenWriter.removeElementIfEmpty(description);
        nodeList = pomDocument.getElementsByTagName("description");
        assertEquals(0, nodeList.getLength());
    }

    @Test
    void testGettingChildName(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("build");
        Node build = mavenWriter.getProjectChild(nodeList);
        Node plugins = mavenWriter.getChildNodeWithName(build, "plugins");
        String nodeAsString = """
                <plugins>
                    <plugin>
                        <groupId>no.hvl</groupId>
                        <artifactId>freja-maven-plugin</artifactId>
                        <version>1.1</version>
                        <configuration>
                            <targetPath>C:\\Users\\Acer\\IntelliJProjects\\HelloWorldOutput</targetPath>
                        </configuration>
                    </plugin>
                </plugins>""";
        assertXmlStringEquals(nodeAsString, getNodeAsString(plugins));

    }

    @Test
    void testGettingNonexistentChildName(){
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("description");
        Node description = mavenWriter.getProjectChild(nodeList);
        try{
            mavenWriter.getChildNodeWithName(description, "nonExistent");
            fail("Should throw exception");
        }catch (IllegalArgumentException e){
            assertEquals("Could not find child with name \"nonExistent\" in the node: \"description\"",
                    e.getMessage());
        }
    }

    @Test
    void testRemovingAnnotationDependency(){
        mavenWriter.removeAnnotationDependency();
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        assertEquals(0, nodeList.getLength());
    }

    @Test
    void testRemovingAnnotationDependencyWithOtherDependencies()
            throws ParserConfigurationException, SAXException, IOException {
        String pomFilePath ="src/test/java/examples/pomfiles/multi/pom.xml";
        MavenWriter mavenWriter = new MavenWriter(pomFilePath, "");
        mavenWriter.removeAnnotationDependency();
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("dependencies");
        Node dependencies = mavenWriter.getProjectChild(nodeList);
        String dependenciesAsString = """
                <dependencies>
                
                    <dependency>
                        <groupId>com.google.guava</groupId>
                        <artifactId>guava</artifactId>
                        <version>31.1-jre</version>
                    </dependency>
                </dependencies>""";
        assertXmlStringEquals(dependenciesAsString, getNodeAsString(dependencies));
    }

    @Test
    void testRemovingFrejaPlugin(){
        mavenWriter.removeFrejalugin();
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("build");
        assertEquals(0, nodeList.getLength());
    }

    @Test
    void testRemovingFrejaPluginWithOtherPlugins()
            throws ParserConfigurationException, SAXException, IOException {
        String pomFilePath ="src/test/java/examples/pomfiles/multi/pom.xml";
        MavenWriter mavenWriter = new MavenWriter(pomFilePath, "");
        mavenWriter.removeFrejalugin();
        Document pomDocument = mavenWriter.getPomDocument();
        NodeList nodeList = pomDocument.getElementsByTagName("build");
        Node build = mavenWriter.getProjectChild(nodeList);
        String buildElement = """
                <build>
                    <plugins>
                   
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-site-plugin</artifactId>
                            <version>3.8.2</version>
                        </plugin>
                    </plugins>
                </build>""";
        assertXmlStringEquals(buildElement, getNodeAsString(build));
    }

    @Test
    void testCreatingPomFilesWithNonexistentTargetPath(){
        try{
            mavenWriter.createPomFiles();
            fail("Should throw exception");
        }catch (Exception e){
            assertTrue(e instanceof NoSuchFileException);
        }
    }

    @Test
    void testCreatingPomFiles() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        tempFolder.create();
        String targetPath = tempFolder.getRoot().getAbsolutePath();
        createProject(targetPath);
        var mavenWriter = new MavenWriter("src/test/java/examples/pomfiles/simple/pom.xml", targetPath);
        mavenWriter.createPomFiles();
        assertPomIsCreatedCorrectly(targetPath, SOLUTION_PROJECT_NAME);
        assertPomIsCreatedCorrectly(targetPath, START_CODE_PROJECT_NAME);
    }

    private void assertPomIsCreatedCorrectly(String targetPath, String folderName) throws IOException {
        File solutionPom = new File(targetPath + File.separator + folderName
                + File.separator + "pom.xml");
        assertTrue(solutionPom.exists());
        String pomAsString = FileUtils.getContentFromFile(solutionPom);
        assertXmlStringEquals(getGeneratedPomAsString(folderName), pomAsString);
    }

    private void createProject(String targetDirPath) throws IOException {
        String srcDirPath = getFrejaTestExamplePath();
        Parser parser = new Parser(srcDirPath);
        parser.parse();
        Assignment assignment = new AssignmentBuilder(parser).build();
        var config = new Configuration(srcDirPath, targetDirPath);
        ProjectWriter projectWriter = new ProjectWriter(config, assignment);
        projectWriter.createSolutionAndStartProject();
    }

    private String getGeneratedPomAsString(String artifactId){
        return String.format("""
                <?xml version="1.0" encoding="UTF-8"standalone="no"?><project xmlns="http://maven.apache.org/POM/4.0.0"xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                                
                    <groupId>org.example</groupId>
                    <artifactId>HelloWorld-%s</artifactId>
                    <version>1.0-SNAPSHOT</version>
                               
                        
                                
                    <properties>
                        <maven.compiler.source>16</maven.compiler.source>
                        <maven.compiler.target>16</maven.compiler.target>
                    </properties>
                                

                    <description>
                    </description>
                                
                </project>""", artifactId);
    }

}
