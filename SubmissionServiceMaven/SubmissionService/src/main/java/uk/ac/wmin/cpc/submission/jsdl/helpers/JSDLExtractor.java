/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;
import uri.mbschedulingdescriptionlanguage.MiddlewareType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLExtractor {

    private JSDLItem item;

    private enum ExploreMiddleware {

        NAME, SUBMISSION_SERVICE
    };

    public JSDLExtractor(JSDLItem item) {
        this.item = item;
    }

    public String getImplementationName() throws IllegalArgumentException {
        try {
            String completeName = item.getJobIdentification().getJobName();
            return (completeName.contains(":")
                    ? completeName.substring(0, completeName.lastIndexOf(":"))
                    : completeName);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Getting implementation name", ex);
        }
    }

    public String getWorkflowEngineInstance() throws IllegalArgumentException {
        try {
            String workflowEngineInstance = item.getResources().
                    getCandidateHosts().getHostName().get(0);
            int index = workflowEngineInstance.lastIndexOf("/") + 1;
            return workflowEngineInstance.substring(index);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Getting workflow engine "
                    + "instance name", ex);
        }
    }

    public DCINameEnumeration getMiddlewareName() throws IllegalArgumentException {
        SDLType metabroker = item.getMetabroker();

        if (metabroker != null && metabroker.getConstraints() != null
                && metabroker.getConstraints().getMiddleware() != null) {
            List<MiddlewareType> listMiddlewares =
                    metabroker.getConstraints().getMiddleware();

            if (listMiddlewares != null && listMiddlewares.size() == 1) {
                MiddlewareType middleware = listMiddlewares.get(0);
                return middleware.getDCIName();
            }
        }

        throw new IllegalArgumentException("No middleware information");
    }

    public String getSubmissionServiceLocation() throws IllegalArgumentException {
        SDLType metabroker = item.getMetabroker();

        if (metabroker != null && metabroker.getConstraints() != null
                && metabroker.getConstraints().getMiddleware() != null) {
            List<MiddlewareType> listMiddlewares =
                    metabroker.getConstraints().getMiddleware();

            if (listMiddlewares != null && listMiddlewares.size() == 1) {
                MiddlewareType middleware = listMiddlewares.get(0);
                return middleware.getManagedResource();
            }
        }

        throw new IllegalArgumentException("No middleware information");
    }

    public String getUserID() throws IllegalArgumentException {
        try {
            return item.getPOSIXApplication().getUserName().getValue();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Getting user name", ex);
        }
    }

    public String getStorageURL() throws IllegalArgumentException {
        try {
            List<DataStagingType> listData = item.getOutputFiles();

            for (DataStagingType data : listData) {
                if (data.getFileName().equals("guse.jsdl")) {
                    return data.getTarget().getURI();
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Getting Portal Storage URL", ex);
        }

        throw new IllegalArgumentException("No Portal Storage URL available");
    }
}
