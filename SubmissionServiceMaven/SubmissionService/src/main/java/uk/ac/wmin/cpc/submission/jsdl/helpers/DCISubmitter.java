/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class DCISubmitter {

    private SubmitterFace client;

    public DCISubmitter() {
        this(Configuration.getDefaultDCIBridge());
    }

    public DCISubmitter(String dciBridgeLocation) {
        this.client = new SubbmitterJaxWSIMPL();
        ((SubbmitterJaxWSIMPL) this.client).setServiceURL(dciBridgeLocation);
        ((SubbmitterJaxWSIMPL) this.client).setServiceID("/BESFactoryService?wsdl");
    }

    public String submitJSDLItem(JSDLItem item)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        return submitActivity(item.getExecutableJSDL());
    }

    public String submitJobDefinition(JobDefinitionType jobDefinition)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        CreateActivityType activityJsdl = new CreateActivityType();
        activityJsdl.setActivityDocument(new ActivityDocumentType());
        activityJsdl.getActivityDocument().setJobDefinition(jobDefinition);

        return submitActivity(activityJsdl);
    }

    private String submitActivity(CreateActivityType activity)
            throws NotAuthorizedFault, NotAcceptingNewActivitiesFault,
            InvalidRequestMessageFault, UnsupportedFeatureFault, Exception {
        CreateActivityResponseType response = client.createActivity(activity);
        W3CEndpointReference idResponse = response.getActivityIdentifier();

        return DCITools.getIDFromW3CEndPointReference(idResponse);
    }

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
