package uk.ac.wmin.cpc.submission.jsdl.helpers;

import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;

/**
 * This class manages the configuration of a middleware in the JSDL.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class MiddlewareConfig {

    /**
     * Create a new SDLType for a given middleware.
     * @param middleware backend instance corresponding to the middleware
     * @param jsdl JSDL file to edit
     * @throws IllegalArgumentException 
     */
    public static void modifySDLType(BeInstance middleware, JSDLItem jsdl)
            throws IllegalArgumentException {
        jsdl.createSDLType(MiddlewareExtractor.getDCIName(middleware).value(),
                MiddlewareExtractor.getResource(middleware), "");
    }

    /**
     * Configure the ResourceType of the JSDl with the middleware information.
     * @param middleware backend instance corresponding to the middleware
     * @param jsdl JSDL file to edit
     * @throws IllegalArgumentException
     * @throws NotFoundException 
     */
    public static void configurationResource(BeInstance middleware, JSDLItem jsdl)
            throws IllegalArgumentException, NotFoundException {
        DCINameEnumeration valueEnum = MiddlewareExtractor.getDCIName(middleware);

        switch (valueEnum) {
            case GT_2:
            case GT_4:
                String hostNameGt = MiddlewareExtractor.getSiteName(middleware) + "/"
                        + MiddlewareExtractor.getJobManager(middleware);
                jsdl.createResource(hostNameGt, MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            case GLITE:
            case LOCAL:
                jsdl.createResource("", MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            case UNICORE: // TODO: maybe change here according to the tests
                String hostNameUnicore = MiddlewareExtractor.getGridName(middleware) + "/"
                        + MiddlewareExtractor.getToolNameAndVersion(middleware);
                jsdl.createResource(hostNameUnicore, MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            case PBS:
                String hostnamePBS = MiddlewareExtractor.getQueueName(middleware) + "/-";
                jsdl.createResource(hostnamePBS, MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            default:
                throw new NotFoundException(
                        "No middleware configuration or not supported configuration "
                        + "detected", null);
        }
    }
}
