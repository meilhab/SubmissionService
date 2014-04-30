/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.helpers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import uk.ac.wmin.cpc.submission.storage.FilesHelper;
import uk.ac.wmin.cpc.submission.storage.executables.CleaningProcess;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebListener
public class Configuration implements ServletContextListener {

    private static final String STORAGE_LOCATION = "storage";
    private static String EXECUTABLE_STORAGE_LOCATION = "executables";
    private static final String EXECUTABLE_NAME = "execute.bin";
    private static final String FILE_SYSTEM_NAME = "dcibridge.outputs.zip";
    private static String LOG4J_FILE;
    private static PropertiesData propertiesFile;
    private static PropertiesData xmlFile;
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        xmlFile = new PropertiesData();
        propertiesFile = new PropertiesData();

        System.out.println("XML file loading...");
        getXMLData(sce);
        recoverLog4JFile(sce);
        System.out.println("XML file loaded");

        try {
            System.out.println("Properties file loading...");
            propertiesFile.setPropertiesData(getPropertiesDataFromFile());
            System.out.println("Properties file loaded");
        } catch (Exception ex) {
            System.out.println("Properties file cannot be loaded ("
                    + ex.getMessage() + ")");
            PropertiesData dataTemp = new PropertiesData();
            dataTemp.setPropertiesData(xmlFile);
            dataTemp.setSERVER_LOCATION(getHostName());
            propertiesFile.setPropertiesData(dataTemp);
        }

        treatFolders();
        actualizeLog4jLocation();
        try {
            saveAsProperties();
        } catch (Exception ex) {
            System.out.println("Cannot write into properties file ("
                    + ex.getMessage() + ")");
        }

        try {
            FilesHelper.initializeFolder(getExecutableStorageLocation());
        } catch (IOException ex) {
            System.out.println("Storage location for executables cannot be initialized");
            System.out.println("(" + getExecutableStorageLocation() + ") "
                    + "not reachable, program is exiting");
            System.exit(1);
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new CleaningProcess(), 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdownNow();
    }

    public static PropertiesData getPropertiesDataLoaded() {
        return propertiesFile;
    }

    public static PropertiesData getPropertiesDataFromFile() throws Exception {
        PropertiesManager manager = new PropertiesManager();
        manager.readProperties();
        PropertiesData dataToReturn = new PropertiesData();
        PropertiesData dataFromFile = manager.getPropertiesData();
        String property;

        property = dataFromFile.getSERVER_LOCATION();
        dataToReturn.setSERVER_LOCATION((property != null ? property : getHostName()));

        property = dataFromFile.getDEFAULT_DCIBRIDGE_LOCATION();
        dataToReturn.setDEFAULT_DCIBRIDGE_LOCATION((property != null ? property
                : xmlFile.getDEFAULT_DCIBRIDGE_LOCATION()));

        property = dataFromFile.getDEFAULT_REPOSITORY_LOCATION();
        dataToReturn.setDEFAULT_REPOSITORY_LOCATION((property != null ? property
                : xmlFile.getDEFAULT_REPOSITORY_LOCATION()));

        property = dataFromFile.getDEFAULT_STORAGE_LOCATION();
        dataToReturn.setDEFAULT_STORAGE_LOCATION((property != null ? property
                : xmlFile.getDEFAULT_STORAGE_LOCATION()));

        property = dataFromFile.getDEFAULT_CLEANING_EXECUTABLE();
        dataToReturn.setDEFAULT_CLEANING_EXECUTABLE((property != null ? property
                : xmlFile.getDEFAULT_CLEANING_EXECUTABLE()));

        property = dataFromFile.getDEFAULT_LOGGING_MODE();
        dataToReturn.setDEFAULT_LOGGING_MODE((property != null ? property
                : xmlFile.getDEFAULT_LOGGING_MODE()));

        return dataToReturn;
    }

    private void getXMLData(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        xmlFile.setSERVER_LOCATION(context.getInitParameter("server-location"));
        xmlFile.setDEFAULT_DCIBRIDGE_LOCATION(context.getInitParameter("default-dcibridge-location"));
        xmlFile.setDEFAULT_REPOSITORY_LOCATION(context.getInitParameter("default-repository-location"));
        xmlFile.setDEFAULT_STORAGE_LOCATION(context.getInitParameter("default-storage-location"));
        xmlFile.setDEFAULT_CLEANING_EXECUTABLE(context.getInitParameter("default-cleaning-executable"));
        xmlFile.setDEFAULT_LOGGING_MODE(context.getInitParameter("default-log4j-level"));
    }

    private void recoverLog4JFile(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        LOG4J_FILE = context.getInitParameter("log4j-file");
    }

    public static void actualizeLog4jLocation() {
        Path path = Paths.get(propertiesFile.getDEFAULT_STORAGE_LOCATION());
        System.setProperty("my.log4j.submission", path.toString()
                + "/logs");
    }

    private void treatFolders() {
        String defaultStorage = propertiesFile.getDEFAULT_STORAGE_LOCATION();

        if (defaultStorage == null) {
            System.out.println("No data for storage location found");
            defaultStorage = "/tmp/submissionService";

            if (!testIfWritableParent(Paths.get(defaultStorage))) {
                System.out.println("Submission Service is going to end");
                System.exit(1);
            }
        } else {
            Path pathStorage = Paths.get(defaultStorage);

            if (!testIfWritableParent(pathStorage)) {
                pathStorage = Paths.get("/tmp/submissionService");

                if (!testIfWritableParent(pathStorage)) {
                    System.out.println("Submission Service is going to end");
                    System.exit(1);
                }

                defaultStorage = pathStorage.toString();
            }
        }
        System.out.println("(" + defaultStorage + ") chosen");
        propertiesFile.setDEFAULT_STORAGE_LOCATION(defaultStorage);
    }

    private boolean testIfWritableParent(Path pathStorage) {
        if (pathStorage == null) {
            System.out.println("(null) storage location found");
            return false;
        }

        System.out.println("(" + pathStorage.toString()
                + ") storage location found and is going to be tested");
        Path existingParent = pathStorage;

        while (existingParent != null && Files.notExists(existingParent)) {
            System.out.println("Visiting (" + existingParent.toString() + ")");
            existingParent = existingParent.getParent();
        }

        if (existingParent == null || !Files.isWritable(existingParent)) {
            System.out.println("Data cannot be saved at ("
                    + pathStorage.toString() + ")");
            return false;
        }

        return true;
    }

    private static String getHostName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            hostname = "http://" + hostname + ":8080/SubmissionService";
            System.out.println("Server location found (" + hostname + ")");
            return hostname;
        } catch (UnknownHostException ex) {
            System.out.println("Server location by default ("
                    + xmlFile.getSERVER_LOCATION() + ")");
            return xmlFile.getSERVER_LOCATION();
        }
    }

    public static void saveAsProperties() throws Exception {
        PropertiesManager manager = new PropertiesManager();
        manager.setPropertiesData(propertiesFile);
        manager.writeProperties();
    }

    public static String getExecutableName() {
        return EXECUTABLE_NAME;
    }

    public static String getExecutableStorageLocation() {
        Path pathExec = Paths.get(propertiesFile.getDEFAULT_STORAGE_LOCATION(),
                STORAGE_LOCATION, EXECUTABLE_STORAGE_LOCATION);
        return pathExec.toString();
    }

    public static String getFileSystemName() {
        return FILE_SYSTEM_NAME;
    }

    public static String getLog4jFile() {
        return LOG4J_FILE;
    }
}
