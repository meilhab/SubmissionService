package uk.ac.wmin.cpc.submission.frontend.transferobjects;

/**
 * Represents the middleware data.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExecutorSite {

    /**
     * Type of middleware (GT2/4, glite, PBS, etc.).
     */
    private String type;
    /**
     * Resource associated to the middleware (WestFocus, vlemed, etc.).
     */
    private String resource;

    public ExecutorSite() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
