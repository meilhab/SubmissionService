/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.clients;

import java.net.MalformedURLException;
import java.net.URL;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import uk.ac.wmin.cpc.submission.frontend.impl.ExecutionException;
import uk.ac.wmin.cpc.submission.frontend.impl.FileManagementException;
import uk.ac.wmin.cpc.submission.frontend.impl.IllegalParameterException;
import uk.ac.wmin.cpc.submission.frontend.impl.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.impl.WSExecutionService;
import uk.ac.wmin.cpc.submission.frontend.impl.WSExecutionService_Service;
import uk.ac.wmin.cpc.submission.frontend.impl.WrongJSDLException;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ClientDCIBridge {

    private static final String WSDL_LOCATION = "WSExecutionService?wsdl";
//    private static final String CERTIFICATE_LOCATION =
//            "/certificates/certificate_submission_test_cloud";
    private String urlSubmissionService;
    private String urlRepository;

    public ClientDCIBridge() {
        this.urlSubmissionService = null;
        this.urlRepository = null;
    }

    public ClientDCIBridge(String urlSubmissionService, String urlRepository) {
        this.urlSubmissionService = urlSubmissionService;
        this.urlRepository = urlRepository;
    }

    public String getUrlSubmissionService() {
        return urlSubmissionService;
    }

    public void setUrlSubmissionService(String urlSubmissionService) {
        this.urlSubmissionService = urlSubmissionService;
    }

    public String getUrlRepository() {
        return urlRepository;
    }

    public void setUrlRepository(String urlRepository) {
        this.urlRepository = urlRepository;
    }

    private WSExecutionService callSubmissionService()
            throws MalformedURLException, IllegalParameterException {
//        URL certURL = ClientDCIBridge.class.getResource(CERTIFICATE_LOCATION);
//
//        if (certURL == null) {
//            throw new IllegalParameterException(
//                    "Keystore for the submission service not found", null);
//        }
//
//        if (urlSubmissionService == null) {
//            throw new IllegalParameterException(
//                    "URL of the submission service not set up", null);
//        }
//
//        System.setProperty("javax.net.ssl.trustStore", certURL.getPath());
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        URL urlService = new URL((urlSubmissionService.endsWith("/")
                ? urlSubmissionService : urlSubmissionService + "/") + WSDL_LOCATION);

        WSExecutionService_Service service = new WSExecutionService_Service(urlService);
        return service.getWSExecutionService();
    }

    public JobDefinitionType modifyJSDLFile(JobDefinitionType jsdl)
            throws FileManagementException, IllegalParameterException,
            MalformedURLException, RepositoryCommunicationException,
            WrongJSDLException {
        return callSubmissionService().modifyJSDLFile(urlRepository, jsdl);
    }

    public String submitToDciBridge(String dciBridgeLocation, JobDefinitionType jsdl)
            throws ExecutionException, FileManagementException,
            IllegalParameterException, MalformedURLException,
            RepositoryCommunicationException, WrongJSDLException {
        return callSubmissionService().submitToDciBridge(dciBridgeLocation, jsdl);
    }
}
