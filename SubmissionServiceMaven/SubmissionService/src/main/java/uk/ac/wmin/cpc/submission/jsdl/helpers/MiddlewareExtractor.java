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
 * This class extracts needed information for a specific backend instance
 * (middleware).
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class MiddlewareExtractor {

    /**
     * Return the resource name for a middleware.
     * @param middleware backend instance corresponding to the middleware
     * @return resource name
     * @throws IllegalArgumentException 
     */
    public static String getResource(BeInstance middleware)
            throws IllegalArgumentException {
        if (middleware == null) {
            throw new IllegalArgumentException("Middleware misconfigured");
        }
        String resourceName = getResourceName(getDCIName(middleware));

        return getAttribute(middleware.getAttributes(), resourceName);
    }

    /**
     * Get an object corresponding to the DCI name used by the middleware.
     * @param middleware backend instance corresponding to the middleware
     * @return DCI name
     * @throws IllegalArgumentException 
     */
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

    /**
     * Get an object corresponding to the operating system used by the middleware.
     * @param middleware backend instance corresponding to the middleware
     * @return operating system
     * @throws IllegalArgumentException 
     */
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

    /**
     * Get the Job Manager for a middleware (used for GT2/4).
     * @param middleware backend instance corresponding to the middleware
     * @return job manager
     * @throws IllegalArgumentException 
     */
    public static String getJobManager(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.GT_2 && valueEnum != DCINameEnumeration.GT_4) {
            throw new IllegalArgumentException("Misconfiguration with gt* detected");
        }

        return getAttribute(middleware.getAttributes(), "Job manager");
    }

    /**
     * Get the site used for a specific middleware (GT2/4).
     * @param middleware backend instance corresponding to the middleware
     * @return site name
     * @throws IllegalArgumentException 
     */
    public static String getSiteName(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.GT_2 && valueEnum != DCINameEnumeration.GT_4) {
            throw new IllegalArgumentException("Misconfiguration with gt* detected");
        }

        return getAttribute(middleware.getAttributes(), "Site name");
    }

    /**
     * Get the name of the queue to use for a specific middleware (PBS)
     * @param middleware backend instance corresponding to the middleware
     * @return name of the queue
     * @throws IllegalArgumentException 
     */
    public static String getQueueName(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.PBS) {
            throw new IllegalArgumentException("Misconfiguration with PBS detected");
        }

        return getAttribute(middleware.getAttributes(), "Queue name");
    }

    /**
     * Get a concatenation of a tool name and version for a specific middleware
     * (UNICORE).
     * @param middleware backend instance corresponding to the middleware
     * @return tool name and version
     * @throws IllegalArgumentException 
     */
    public static String getToolNameAndVersion(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.UNICORE) {
            throw new IllegalArgumentException("Misconfiguration with UNICORE detected");
        }

        return getAttribute(middleware.getAttributes(), "Tool name") + " "
                + getAttribute(middleware.getAttributes(), "Tool version");
    }

    /**
     * Get the grid name associated to a specific middleware (UNICORE).
     * @param middleware backend instance corresponding to the middleware
     * @return grid name
     * @throws IllegalArgumentException 
     */
    public static String getGridName(BeInstance middleware)
            throws IllegalArgumentException {
        DCINameEnumeration valueEnum = getDCIName(middleware);

        if (valueEnum != DCINameEnumeration.UNICORE) {
            throw new IllegalArgumentException("Misconfiguration with UNICORE detected");
        }

        return getAttribute(middleware.getAttributes(), "Grid name");
    }

    /**
     * Extract the value of an attribute from a list.
     * @param attributes list of attributes
     * @param nameAttribute name of the attribute
     * @return value of the desired attribute
     * @throws IllegalArgumentException 
     */
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

    /**
     * Get the name of the field corresponding to the field resource that has 
     * to be looking at for a specific DCI. This is basically where to execute it 
     * (WestFocus, vlemed, etc.).
     * @param valueEnum DCI name
     * @return name of the field corresponding to the resource
     * @throws IllegalArgumentException 
     */
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
            case UNICORE:
                return "Grid name";
            default:
                throw new IllegalArgumentException("(" + valueEnum.value()
                        + ") is not supported by the Submission Service");
        }
    }
}
