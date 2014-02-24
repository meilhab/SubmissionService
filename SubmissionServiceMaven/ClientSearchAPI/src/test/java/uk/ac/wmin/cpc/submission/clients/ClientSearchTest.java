/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.clients;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.shiwa.repository.submission.interfaces.ImplShort;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ClientSearchTest {

    private static ClientSearch client;
    private static String lcid;
    private static String executorSite;

    public ClientSearchTest() {
    }

    @BeforeClass
    public static void prepareClient() {
        client = new ClientSearch();
        client.setExtServiceId("testPortalRepo");
        client.setExtUserId("testPortalRepo");
        client.setUrlSubmissionService("http://submission.cpc.wmin.ac.uk:8080/SubmissionService");
        client.setUrlRepository("http://shiwa-repo.cpc.wmin.ac.uk:8080/shiwa-repo");
//        lcid = "QRCodeMatrixGenerator#1.0";
//        lcid = "HelloAnyoneTaverna2#1.0";
        lcid = "TestTaverna2#1.0";
        executorSite = "OnTheFlyWestFocus";
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getLCIDs method, of class ClientSearch.
     */
//    @Ignore("Not needed yet")
    @Test
    public void testGetLCIDs() throws Exception {
        System.out.println("getLCIDs");

        ImplShort[] result = client.getLCIDs();
        assertNotNull("Result LCIDs null", result);

        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i].getName());
        }
    }

    /**
     * Test of getExecutorSites method, of class ClientSearch.
     */
//    @Ignore("Not needed yet")
    @Test
    public void testGetExecutorSites() throws Exception {
        System.out.println("getExecutorSites");

        String[] result = client.getExecutorSites(lcid);
        assertNotNull("Result ExecutorSites null", result);

        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }
    }

    /**
     * Test of getLCParameters method, of class ClientSearch.
     */
//    @Ignore("Not needed yet")
    @Test
    public void testGetLCParameters() throws Exception {
        System.out.println("getLCParameters");
        assertNotNull("Result LCParameters null", client.getLCParameters(lcid));
    }

    //    @Ignore("Not needed yet")
    @Test
    public void testgetExecutorSiteInformation() throws Exception {
        System.out.println("getExecutorSiteInformation");
        assertNotNull("Result ExecutorSiteInformation null",
                client.getExecutorSiteInformation(lcid, executorSite));
    }
}