///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package others;
//
//import java.util.UUID;
//import static junit.framework.TestCase.*;
//import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//import uk.ac.wmin.cpc.submission.clients.ClientDCIBridge;
//
///**
// *
// * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
// */
//public class ClientDCIBridgeTest {
//
//    private static ClientDCIBridge client;
//    private static String IMPLEMENTATION;
//    private static String GROUP;
//    private static String ONTHEFLY;
//    private static String PREDEPLOY;
//    private static String DCIBRIDGELOCATION;
//
//    @Before
//    public void setUp() throws Exception {
//        IMPLEMENTATION = "TestTaverna2v1#1.0";
//        GROUP = "submissionTestGeneration";
//        ONTHEFLY = "OnTheFlyWestFocus";
//        PREDEPLOY = "PreDeployWestFocus";
//        DCIBRIDGELOCATION = "https://shiwa-portal2.cpc.wmin.ac.uk/dci_bridge_service";
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @BeforeClass
//    public static void prepareClient() {
//        client = new ClientDCIBridge();
//        client.setUrlSubmissionService("http://submission.cpc.wmin.ac.uk:8080/SubmissionService");
//        client.setUrlRepository("http://shiwa-repo.cpc.wmin.ac.uk:8080/shiwa-repo");
//    }
//
////    @Ignore("Not needed yet")
//    @Test
//    public void testModifyJSDLFilePortal() throws Exception {
//        System.out.println("testModifyJSDLFilePortal");
//
//        String stringGiven = LoadJSDL.returnPortalData();
//        JobDefinitionType jsdlGiven = Tools.readJSDLFromString(stringGiven);
//        JobDefinitionType jsdlResult = client.modifyJSDLFile(jsdlGiven);
//        String stringResult = Tools.getJSDLXML(jsdlResult);
//
//        assertNotSame(stringGiven, stringResult);
//    }
//    
//    @Ignore("Not needed yet")
//    @Test
//    public void testModifyJSDLFileFilePreDeploy() throws Exception {
//        System.out.println("testModifyJSDLFileFilePreDeploy");
//
//        String stringGiven = LoadJSDL.returnTestWorkflowTaverna2(GROUP,
//                PREDEPLOY, IMPLEMENTATION, UUID.randomUUID().toString());
//        JobDefinitionType jsdlGiven = Tools.readJSDLFromString(stringGiven);
//        JobDefinitionType jsdlResult = client.modifyJSDLFile(jsdlGiven);
//        String stringResult = Tools.getJSDLXML(jsdlResult);
//
//        assertNotSame(stringGiven, stringResult);
//    }
//
//    @Ignore("Not needed yet")
//    @Test
//    public void testModifyJSDLFileOnTheFly() throws Exception {
//        System.out.println("testModifyJSDLFileOnTheFly");
//
//        String stringGiven = LoadJSDL.returnTestWorkflowTaverna2(GROUP,
//                ONTHEFLY, IMPLEMENTATION, UUID.randomUUID().toString());
//        JobDefinitionType jsdlGiven = Tools.readJSDLFromString(stringGiven);
//        JobDefinitionType jsdlResult = client.modifyJSDLFile(jsdlGiven);
//        String stringResult = Tools.getJSDLXML(jsdlResult);
//
//        assertNotSame(stringGiven, stringResult);
//    }
//
//    @Ignore("Not needed yet")
//    @Test
//    public void testModifyAndSubmitToDciBridgePreDeploy() throws Exception {
//        System.out.println("testModifyAndSubmitToDciBridgePreDeploy");
//
//        String stringGiven = LoadJSDL.returnTestWorkflowTaverna2(GROUP + "v1",
//                PREDEPLOY, IMPLEMENTATION, UUID.randomUUID().toString());
//        JobDefinitionType jsdlGiven = Tools.readJSDLFromString(stringGiven);
//        JobDefinitionType jsdl = client.modifyJSDLFile(jsdlGiven);
//        String stringResult = client.submitToDciBridge(DCIBRIDGELOCATION, jsdl);
//
//        assertNotNull(stringResult);
//        assertNotSame("", stringResult);
//    }
//
//    @Ignore("Not needed yet")
//    @Test
//    public void testModifyAndSubmitToDciBridgeOnTheFly() throws Exception {
//        System.out.println("testModifyAndSubmitToDciBridgeOnTheFly");
//
//        String stringGiven = LoadJSDL.returnTestWorkflowTaverna2(GROUP + "v2",
//                ONTHEFLY, IMPLEMENTATION, UUID.randomUUID().toString());
//        JobDefinitionType jsdlGiven = Tools.readJSDLFromString(stringGiven);
//        JobDefinitionType jsdl = client.modifyJSDLFile(jsdlGiven);
//        String stringResult = client.submitToDciBridge(DCIBRIDGELOCATION, jsdl);
//
//        assertNotNull(stringResult);
//        assertNotSame("", stringResult);
//    }
//}
