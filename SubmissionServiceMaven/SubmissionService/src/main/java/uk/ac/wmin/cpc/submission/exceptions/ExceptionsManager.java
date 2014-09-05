package uk.ac.wmin.cpc.submission.exceptions;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import org.apache.log4j.Logger;
import org.shiwa.repository.submission.service.DatabaseProblemException;
import org.shiwa.repository.submission.service.ForbiddenException;
import org.shiwa.repository.submission.service.NotFoundException;
import org.shiwa.repository.submission.service.UnauthorizedException;

/**
 * This class is managing the error messages shared between the two web 
 * services. It simplifies the code in the web services by taking a java 
 * exception and returning a custom serializable exception.
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExceptionsManager {

    /**
     * Treat all common exceptions between services.
     * @param ex exception caught
     * @param logger provided logger
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    private static void commonExceptions(Exception ex, Logger logger)
            throws IllegalParameterException, RepositoryCommunicationException {
        if (ex instanceof MalformedURLException) {
            logger.error("Wrong URL Repository", ex);
            throw new IllegalParameterException("URL Repository problem", ex);
        } else if (ex instanceof NotFoundException) {
            logger.error("Wrong Repository URL", ex);
            throw new IllegalParameterException("Wrong Repository URL", ex);
        } else if (ex instanceof DatabaseProblemException) {
            logger.error("Problem detected with Repository database", ex);
            throw new RepositoryCommunicationException("Repository database problem", ex);
        } else if (ex instanceof ForbiddenException) {
            logger.error("Incorrect data provided", ex);
            throw new IllegalParameterException("Incorrect data provided", ex);
        } else if (ex instanceof IllegalArgumentException) {
            logger.error("Problem detected when getting data from Repository", ex);
            throw new IllegalParameterException("Incorrect data retrieved from the Repository", ex);
        } else if (ex instanceof WebServiceException) {
            logger.error("The repository cannot be reached", ex);
            throw new RepositoryCommunicationException("Repository unreachable", ex);
        } 
    }

    /**
     * Manage all exceptions for WSCodeListService. Check the common ones and
     * goes if needed with specifics.
     * @param ex exception caught
     * @param logger provided logger
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    public static void manageExceptionsCodeListService(Exception ex, Logger logger)
            throws IllegalParameterException, RepositoryCommunicationException {
        commonExceptions(ex, logger);

        if (ex instanceof UnauthorizedException) {
            logger.error("Unauthorized access detected", ex);
            throw new RepositoryCommunicationException("Access Repository problem", ex);
        } else if (ex instanceof IOException) {
            logger.error("The repository cannot be reached", ex);
            throw new RepositoryCommunicationException("Repository unreachable", ex);
        } else {
            logger.error("Unreported exception", ex);
            throw new IllegalParameterException("Unreported exception raised: "
                    + ex.getCause(), ex);
        }
    }

    /**
     * Manage all exceptions for WSExecutionService. Check the common ones and
     * goes if needed with specifics.
     * @param ex exception caught
     * @param logger provided logger
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException
     * @throws WrongJSDLException
     * @throws FileManagementException 
     */
    public static void managerExceptionsExecutionService(Exception ex, Logger logger)
            throws IllegalParameterException, RepositoryCommunicationException,
            WrongJSDLException, FileManagementException {
        commonExceptions(ex, logger);

        if (ex instanceof IllegalArgumentException) {
            logger.error("Problem detected when extracting JSDL data", ex);
            throw new WrongJSDLException("Incorrect original JSDL detected", ex);
        } else if (ex instanceof JAXBException) {
            logger.error("Problem detected with generated JSDL", ex);
            throw new WrongJSDLException(
                    "Problem detected during generation of new JSDL", ex);
        } else if (ex instanceof IOException) {
            logger.error("Problem detected with the configuration or the repository", ex);
            throw new FileManagementException("Submission service I/O error detected", ex);
        } else {
            logger.error("Unreported exception", ex);
            throw new IllegalParameterException("Unreported exception raised: "
                    + ex.getCause(), ex);
        }
    }
}
