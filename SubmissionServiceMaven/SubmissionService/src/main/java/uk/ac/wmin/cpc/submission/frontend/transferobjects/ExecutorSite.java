/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.transferobjects;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExecutorSite {

    private String type;
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
