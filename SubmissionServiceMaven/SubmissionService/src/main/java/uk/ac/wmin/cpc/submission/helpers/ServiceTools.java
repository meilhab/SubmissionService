package uk.ac.wmin.cpc.submission.helpers;

import uk.ac.wmin.cpc.submission.servlets.LoggerServlet;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;

/**
 * Functions to check the input parameters
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ServiceTools {

    /**
     * Check a list of data and throw an exception if one is null.
     * @param data list of different objects.
     * @throws IllegalParameterException 
     */
    public static void checkParam(Object... data)
            throws IllegalParameterException {
        Logger logger = LoggerServlet.getMainLogger();

        for (Object object : data) {
            if (object == null
                    || (object instanceof String && ((String) object).isEmpty())) {
                logger.error("Null or empty param detected");
                throw new IllegalParameterException("Null or empty parameter"
                        + " detected (additional parameter)");
            }
        }
    }

    /**
     * Check specifically if the credentials aren't null. Throw exception if so.
     * @param userAccessConfig credentials of user and portal
     * @throws IllegalParameterException 
     */
    public static void checkUserAccessConfig(UserAccessConfig userAccessConfig)
            throws IllegalParameterException {
        Logger logger = LoggerServlet.getMainLogger();

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
