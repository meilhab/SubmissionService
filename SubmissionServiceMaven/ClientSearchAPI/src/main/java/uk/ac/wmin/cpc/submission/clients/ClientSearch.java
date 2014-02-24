/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.clients;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import uk.ac.wmin.cpc.submission.frontend.impl.IllegalParameterException;
import uk.ac.wmin.cpc.submission.frontend.impl.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.impl.WSCodeListService;
import uk.ac.wmin.cpc.submission.frontend.impl.WSCodeListService_Service;
import uk.ac.wmin.cpc.submission.frontend.interfaces.UserAccessConfig;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import uk.ac.wmin.cpc.submission.frontend.interfaces.ExecutorSite;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ClientSearch {

    private static final String WSDL_LOCATION = "WSCodeListService?wsdl";
//    private static final String CERTIFICATE_LOCATION =
//            "/certificates/certificate_submission_test_cloud";
    private String urlSubmissionService;
    private String urlRepository;
    private String extServiceId;
    private String extUserId;

    public ClientSearch() {
        this.urlSubmissionService = null;
        this.urlRepository = null;
    }

    public ClientSearch(String urlSubmissionService, String repositoryURL) {
        this.urlSubmissionService = urlSubmissionService;
        this.urlRepository = repositoryURL;
    }

    public String getUrlSubmissionService() {
        return urlSubmissionService;
    }

    public void setUrlSubmissionService(String urlSubmissionService) {
        this.urlSubmissionService = urlSubmissionService;
    }

    public void setUrlRepository(String urlRepository) {
        this.urlRepository = urlRepository;
    }

    public void setExtServiceId(String extServiceId) {
        this.extServiceId = extServiceId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    private WSCodeListService callSubmissionService()
            throws MalformedURLException, IllegalParameterException {
//        URL certURL = ClientSearch.class.getResource(CERTIFICATE_LOCATION);
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

        WSCodeListService_Service service = new WSCodeListService_Service(urlService);
        return service.getWSCodeListService();
    }

    private UserAccessConfig prepareUserConfig() {
        UserAccessConfig userAccessConfig = new UserAccessConfig();
        userAccessConfig.setExtServiceId(extServiceId);
        userAccessConfig.setExtUserId(extUserId);

        return userAccessConfig;
    }

    public ImplShort[] getLCIDs()
            throws IllegalParameterException, MalformedURLException,
            RepositoryCommunicationException {
        UserAccessConfig userAccessConfig = prepareUserConfig();

        List<ImplShort> listLCIDs = callSubmissionService().
                getLCIDs(userAccessConfig, urlRepository);
        return listLCIDs.toArray(new ImplShort[listLCIDs.size()]);
    }

    public String[] getExecutorSites(String lcid)
            throws IllegalParameterException, MalformedURLException,
            RepositoryCommunicationException {
        List<String> listExecutorSites = callSubmissionService().
                getExecutorSites(urlRepository, lcid);
        return listExecutorSites.toArray(new String[listExecutorSites.size()]);
    }

    public Parameter[] getLCParameters(String lcid)
            throws IllegalParameterException, MalformedURLException,
            RepositoryCommunicationException {
        List<Parameter> listParameters = callSubmissionService().
                getLCParameters(urlRepository, lcid);
        return listParameters.toArray(new Parameter[listParameters.size()]);
    }

    public ExecutorSite getExecutorSiteInformation(String implName, String executorName)
            throws IllegalParameterException, MalformedURLException,
            RepositoryCommunicationException {
        return callSubmissionService().getExecutorSiteConfiguration(
                urlRepository, implName, executorName);
    }
}
