package uk.ac.wmin.cpc.submission.frontend.helpers;

/**
 * Properties of the system.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class PropertiesData {

    private String SERVER_LOCATION;
    private String DEFAULT_STORAGE_LOCATION;
    private String DEFAULT_REPOSITORY_LOCATION;
    private String DEFAULT_DCIBRIDGE_LOCATION;
    private String DEFAULT_CLEANING_EXECUTABLE;
    private String DEFAULT_LOGGING_MODE;

    public PropertiesData() {
    }

    public void setPropertiesData(PropertiesData data) {
        SERVER_LOCATION = data.getSERVER_LOCATION();
        DEFAULT_STORAGE_LOCATION = data.getDEFAULT_STORAGE_LOCATION();
        DEFAULT_REPOSITORY_LOCATION = data.getDEFAULT_REPOSITORY_LOCATION();
        DEFAULT_DCIBRIDGE_LOCATION = data.getDEFAULT_DCIBRIDGE_LOCATION();
        DEFAULT_CLEANING_EXECUTABLE = data.getDEFAULT_CLEANING_EXECUTABLE();
        DEFAULT_LOGGING_MODE = data.getDEFAULT_LOGGING_MODE();
    }

    public String getSERVER_LOCATION() {
        return SERVER_LOCATION;
    }

    public void setSERVER_LOCATION(String SERVER_LOCATION) {
        this.SERVER_LOCATION = SERVER_LOCATION;
    }

    public String getDEFAULT_STORAGE_LOCATION() {
        return DEFAULT_STORAGE_LOCATION;
    }

    public void setDEFAULT_STORAGE_LOCATION(String DEFAULT_STORAGE_LOCATION) {
        this.DEFAULT_STORAGE_LOCATION = DEFAULT_STORAGE_LOCATION;
    }

    public String getDEFAULT_REPOSITORY_LOCATION() {
        return DEFAULT_REPOSITORY_LOCATION;
    }

    public void setDEFAULT_REPOSITORY_LOCATION(String DEFAULT_REPOSITORY_LOCATION) {
        this.DEFAULT_REPOSITORY_LOCATION = DEFAULT_REPOSITORY_LOCATION;
    }

    public String getDEFAULT_DCIBRIDGE_LOCATION() {
        return DEFAULT_DCIBRIDGE_LOCATION;
    }

    public void setDEFAULT_DCIBRIDGE_LOCATION(String DEFAULT_DCIBRIDGE_LOCATION) {
        this.DEFAULT_DCIBRIDGE_LOCATION = DEFAULT_DCIBRIDGE_LOCATION;
    }

    public String getDEFAULT_CLEANING_EXECUTABLE() {
        return DEFAULT_CLEANING_EXECUTABLE;
    }

    public void setDEFAULT_CLEANING_EXECUTABLE(String DEFAULT_CLEANING_EXECUTABLE) {
        this.DEFAULT_CLEANING_EXECUTABLE = DEFAULT_CLEANING_EXECUTABLE;
    }

    public String getDEFAULT_LOGGING_MODE() {
        return DEFAULT_LOGGING_MODE;
    }

    public void setDEFAULT_LOGGING_MODE(String DEFAULT_LOGGING_MODE) {
        this.DEFAULT_LOGGING_MODE = DEFAULT_LOGGING_MODE;
    }
}
