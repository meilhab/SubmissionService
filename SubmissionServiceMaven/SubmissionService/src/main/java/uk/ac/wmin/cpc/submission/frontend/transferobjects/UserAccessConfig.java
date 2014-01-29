/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.transferobjects;

import java.io.Serializable;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class UserAccessConfig implements Serializable {

    private String extServiceId;
    private String extUserId;

    public UserAccessConfig() {
    }

    public UserAccessConfig(String extServiceId, String extUserId) {
        this.extServiceId = extServiceId;
        this.extUserId = extUserId;
    }

    public String getExtServiceId() {
        return extServiceId;
    }

    public void setExtServiceId(String extServiceId) {
        this.extServiceId = extServiceId;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }
}
