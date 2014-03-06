/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeEnumeration;
import org.shiwa.repository.submission.interfaces.BeAttr;
import org.shiwa.repository.submission.interfaces.BeInstance;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;
import static uri.mbschedulingdescriptionlanguage.DCINameEnumeration.GLITE;
import static uri.mbschedulingdescriptionlanguage.DCINameEnumeration.GT_2;
import static uri.mbschedulingdescriptionlanguage.DCINameEnumeration.GT_4;
import static uri.mbschedulingdescriptionlanguage.DCINameEnumeration.LOCAL;
import static uri.mbschedulingdescriptionlanguage.DCINameEnumeration.PBS;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class MiddlewareExtractor {

    public static String getResource(BeInstance middleware)
            throws IllegalArgumentException {
        if (middleware == null) {
            throw new IllegalArgumentException("Middleware misconfigured");
        }
        String resourceName = getResourceName(getDCIName(middleware));

        return getAttribute(middleware.getAttributes(), resourceName);
    }

    public static DCINameEnumeration getDCIName(BeInstance middleware)
            throws IllegalArgumentException {
        if (middleware == null || middleware.getBackend() == null) {
            throw new IllegalArgumentException("No DCI Name detected");
        }

        DCINameEnumeration valueEnum =
                DCINameEnumeration.fromValue(middleware.getBackend().toLowerCase());

        if (valueEnum == null) {
            throw new IllegalArgumentException("Wrong DCI Name gotten from the repository");
        }

        return valueEnum;
    }

    public static OperatingSystemTypeEnumeration getOperatingSystem(BeInstance middleware)
            throws IllegalArgumentException {
        if (middleware == null) {
            throw new IllegalArgumentException("No DCI Name detected");
        }

        String operatingSystem = getAttribute(middleware.getAttributes(), "Operating system");
        OperatingSystemTypeEnumeration valueEnum =
                OperatingSystemTypeEnumeration.fromValue(operatingSystem.toUpperCase());

        if (valueEnum == null) {
            throw new IllegalArgumentException("Wrong Operating System gotten from the repository");
        }

        return valueEnum;
    }

    public static String getJobManager(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.GT_2 && valueEnum != DCINameEnumeration.GT_4) {
            throw new IllegalArgumentException("Misconfiguration with gt* detected");
        }

        return getAttribute(middleware.getAttributes(), "Job manager");
    }

    public static String getSiteName(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.GT_2 && valueEnum != DCINameEnumeration.GT_4) {
            throw new IllegalArgumentException("Misconfiguration with gt* detected");
        }

        return getAttribute(middleware.getAttributes(), "Site name");
    }

    public static String getQueueName(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.PBS) {
            throw new IllegalArgumentException("Misconfiguration with PBS detected");
        }

        return getAttribute(middleware.getAttributes(), "Queue name");
    }

    private static String getAttribute(List<BeAttr> attributes, String nameAttribute)
            throws IllegalArgumentException {
        if (attributes == null || nameAttribute == null) {
            throw new IllegalArgumentException("Attribute (" + nameAttribute
                    + ") cannot be retrieved");
        }

        for (BeAttr beAttr : attributes) {
            if (beAttr.getName().equals(nameAttribute)) {
                return beAttr.getAttrValue();
            }
        }

        throw new IllegalArgumentException("Attribute (" + nameAttribute
                + ") not found");
    }

    private static String getResourceName(DCINameEnumeration valueEnum)
            throws IllegalArgumentException {
        if (valueEnum == null) {
            throw new IllegalArgumentException("Wrong DCI Name given");
        }

        switch (valueEnum) {
            case GT_2:
                return "Grid name";
            case GT_4:
                return "Grid name";
            case GLITE:
                return "VO name";
            case LOCAL:
                return "Resource name";
            case PBS:
                return "Cluster head node";
            default:
                throw new IllegalArgumentException("(" + valueEnum.value()
                        + ") is not supported by the Submission Service");
        }
    }
}
