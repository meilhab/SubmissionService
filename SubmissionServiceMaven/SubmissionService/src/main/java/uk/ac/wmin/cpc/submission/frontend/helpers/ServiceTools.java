/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.helpers;

import uk.ac.wmin.cpc.submission.frontend.servlets.LoggerServlet;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ServiceTools {

    public static void checkParam(Object... data)
            throws IllegalParameterException {
        Logger logger = LoggerServlet.getLogger(4);

        for (Object object : data) {
            if (object == null
                    || (object instanceof String && ((String) object).isEmpty())) {
                logger.error("Null or empty param detected");
                throw new IllegalParameterException("Null or empty parameter"
                        + " detected (additional parameter)");
            }
        }
    }

    public static void checkUserAccessConfig(UserAccessConfig userAccessConfig)
            throws IllegalParameterException {
        Logger logger = LoggerServlet.getLogger(4);

        if (userAccessConfig == null) {
            logger.error("No user data given");
            throw new IllegalParameterException("No configuration for the user");
        }

        String extServiceId = userAccessConfig.getExtServiceId();
        String extUserId = userAccessConfig.getExtUserId();

        if (extServiceId == null || extServiceId.isEmpty()) {
            logger.error("Null or empty serviceId detected");
            throw new IllegalParameterException("null or empty parameter detected "
                    + "(serviceId)");
        }

        if (extUserId == null || extUserId.isEmpty()) {
            logger.error("Null or empty userId detected");
            throw new IllegalParameterException("Null or empty parameter detected "
                    + "(userId)");
        }
    }
}
