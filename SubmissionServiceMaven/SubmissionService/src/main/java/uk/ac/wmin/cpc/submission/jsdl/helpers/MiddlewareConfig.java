/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class MiddlewareConfig {

    public static void modifySDLType(BeInstance middleware, JSDLItem jsdl)
            throws IllegalArgumentException {
        jsdl.createSDLType(MiddlewareExtractor.getDCIName(middleware).value(),
                MiddlewareExtractor.getResource(middleware), "");
    }

    public static void configurationResource(BeInstance middleware, JSDLItem jsdl)
            throws IllegalArgumentException, NotFoundException {
        DCINameEnumeration valueEnum = MiddlewareExtractor.getDCIName(middleware);

        switch (valueEnum) {
            case GT_2:
            case GT_4:
                String hostName = MiddlewareExtractor.getSiteName(middleware) + "/"
                        + MiddlewareExtractor.getJobManager(middleware);
                jsdl.createResource(hostName, MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            case GLITE:
            case LOCAL:
                jsdl.createResource("", MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            case PBS:
                String hostname = MiddlewareExtractor.getQueueName(middleware) + "/-";
                jsdl.createResource(hostname, MiddlewareExtractor.getOperatingSystem(middleware));
                break;
            default:
                throw new NotFoundException(
                        "No middleware configuration or not supported configuration "
                        + "detected", null);
        }
    }
}
