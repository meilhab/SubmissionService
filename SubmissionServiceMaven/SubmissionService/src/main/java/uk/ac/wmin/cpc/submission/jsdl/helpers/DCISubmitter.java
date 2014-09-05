package uk.ac.wmin.cpc.submission.jsdl.helpers;

import hu.sztaki.lpds.dcibridge.client.SubbmitterJaxWSIMPL;
import hu.sztaki.lpds.dcibridge.client.SubmitterFace;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault;
import org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;

/**
 * Class allows a submission of a JSDL on a DCI Bridge
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class DCISubmitter {

    private SubmitterFace client;
    private static final String dciBridgeLocation =
            Configuration.getPropertiesDataLoaded().getDEFAULT_DCIBRIDGE_LOCATION();

    public DCISubmitter() {
        this(dciBridgeLocation);
    }

    public DCISubmitter(String dciBridgeLocation) {
        this.client = new SubbmitterJaxWSIMPL();
        ((SubbmitterJaxWSIMPL) this.client).setServiceURL(dciBridgeLocation);
        ((SubbmitterJaxWSIMPL) this.client).setServiceID("/BESFactoryService?wsdl");
    }

    /**
     * Submit directly a JSDLItem
     * @param item item representing a JSDL file
     * @return ID of the submitted workflow
     * @throws NotAuthorizedFault
     * @throws NotAcceptingNewActivitiesFault
     * @throws InvalidRequestMessageFault
     * @throws UnsupportedFeatureFault
     * @throws Exception 
     */
    public String submitJSDLItem(JSDLItem item)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        return submitActivity(item.getExecutableJSDL());
    }

    /**
     * Submit a JSDL
     * @param jobDefinition JSDL to submit
     * @return ID of the submitted workflow
     * @throws NotAuthorizedFault
     * @throws NotAcceptingNewActivitiesFault
     * @throws InvalidRequestMessageFault
     * @throws UnsupportedFeatureFault
     * @throws Exception 
     */
    public String submitJobDefinition(JobDefinitionType jobDefinition)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        CreateActivityType activityJsdl = new CreateActivityType();
        activityJsdl.setActivityDocument(new ActivityDocumentType());
        activityJsdl.getActivityDocument().setJobDefinition(jobDefinition);

        return submitActivity(activityJsdl);
    }

    /**
     * Submit a execution enabled JSDL
     * @param activity execution enabled JSDL
     * @return ID of the submitted workflow
     * @throws NotAuthorizedFault
     * @throws NotAcceptingNewActivitiesFault
     * @throws InvalidRequestMessageFault
     * @throws UnsupportedFeatureFault
     * @throws Exception 
     */
    private String submitActivity(CreateActivityType activity)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        CreateActivityResponseType response = client.createActivity(activity);
        W3CEndpointReference idResponse = response.getActivityIdentifier();

        return DCITools.getIDFromW3CEndPointReference(idResponse);
    }

    // Get rid of the certificate, can be remove if needed
    static {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain,
                            final String authType) {
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain,
                            final String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        } catch (final NoSuchAlgorithmException | KeyManagementException e) {
        }
    }
}
