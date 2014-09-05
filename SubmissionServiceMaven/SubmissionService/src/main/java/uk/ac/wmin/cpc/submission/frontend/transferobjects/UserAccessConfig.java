package uk.ac.wmin.cpc.submission.frontend.transferobjects;

import java.io.Serializable;

/**
 * Data to authenticate a portal and user on the SHIWA Repository and get the 
 * selected implementations if existing.
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
