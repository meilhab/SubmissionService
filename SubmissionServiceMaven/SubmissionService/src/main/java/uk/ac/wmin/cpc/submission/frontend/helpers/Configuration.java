/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.helpers;

import java.io.IOException;
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

    private static String EXECUTABLE_STORAGE_LOCATION;
    private static final String EXECUTABLE_NAME = "execute.bin";
    private static final String FILE_SYSTEM_NAME = "dcibridge.outputs.zip";
    private static String SERVER_LOCATION;
    private static String DEFAULT_STORAGE_LOCATION;
    private static String DEFAULT_REPOSITORY_LOCATION;
    private static String DEFAULT_DCIBRIDGE_LOCATION;
    private static String DEFAULT_CLEANING_EXECUTABLE;
    private static String DEFAULT_LOGGING_MODE;
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            configurationWithProperties(sce);
            System.out.println("Properties file loaded");
        } catch (Exception ex) {
            System.out.println("XML Config loaded (" + ex.getMessage() + ")");
            configurationWithWebXML(sce);
        }

        treatFolders();
        saveAsProperties();

        try {
            FilesHelper.initializeFolder(DEFAULT_STORAGE_LOCATION);
            FilesHelper.initializeFolder(EXECUTABLE_STORAGE_LOCATION);
        } catch (IOException ex) {
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new CleaningProcess(
                EXECUTABLE_STORAGE_LOCATION), 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdownNow();
    }

    private void configurationWithProperties(ServletContextEvent sce) throws Exception {
        PropertiesManager manager = new PropertiesManager();
        manager.readProperties();
        ServletContext context = sce.getServletContext();

        if ((SERVER_LOCATION = manager.getDefaultServerLocation()) == null) {
            SERVER_LOCATION = context.getInitParameter("server-location");
        }
        if ((DEFAULT_DCIBRIDGE_LOCATION = manager.getDefaultDciBridgeLocation()) == null) {
            DEFAULT_DCIBRIDGE_LOCATION = context.getInitParameter("default-dcibridge-location");
        }
        if ((DEFAULT_REPOSITORY_LOCATION = manager.getDefaultRepositoryLocation()) == null) {
            DEFAULT_REPOSITORY_LOCATION = context.getInitParameter("default-repository-location");
        }
        if ((DEFAULT_STORAGE_LOCATION = manager.getDefaultStorageLocation()) == null) {
            DEFAULT_STORAGE_LOCATION = context.getInitParameter("default-storage-location");
        }
        if ((DEFAULT_CLEANING_EXECUTABLE = manager.getDefaultCleaningExecutable()) == null) {
            DEFAULT_CLEANING_EXECUTABLE = context.getInitParameter("default-cleaning-executable");
        }
        if ((DEFAULT_LOGGING_MODE = manager.getDefaultLoggingMode()) == null) {
            DEFAULT_LOGGING_MODE = context.getInitParameter("default-log4j-level");
        }
    }

    private void configurationWithWebXML(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        SERVER_LOCATION = context.getInitParameter("server-location");
        DEFAULT_DCIBRIDGE_LOCATION = context.getInitParameter("default-dcibridge-location");
        DEFAULT_REPOSITORY_LOCATION = context.getInitParameter("default-repository-location");
        DEFAULT_STORAGE_LOCATION = context.getInitParameter("default-storage-location");
        DEFAULT_CLEANING_EXECUTABLE = context.getInitParameter("default-cleaning-executable");
        DEFAULT_LOGGING_MODE = context.getInitParameter("default-log4j-level");
    }

    private void treatFolders() {
        if (DEFAULT_STORAGE_LOCATION == null) {
            DEFAULT_STORAGE_LOCATION = "/tmp/submissionService";
        }
        DEFAULT_STORAGE_LOCATION += DEFAULT_STORAGE_LOCATION.endsWith("/") ? "" : "/";

        EXECUTABLE_STORAGE_LOCATION = DEFAULT_STORAGE_LOCATION + "executables";

        Path path = Paths.get(DEFAULT_STORAGE_LOCATION);
        System.setProperty("my.log4j.submission", path.getParent().toString()
                + "/logs");
    }

    private void saveAsProperties() {
        try {
            PropertiesManager manager = new PropertiesManager();
            manager.setDefaultCleaningExecutable(DEFAULT_CLEANING_EXECUTABLE);
            manager.setDefaultDciBridgeLocation(DEFAULT_DCIBRIDGE_LOCATION);
            manager.setDefaultLoggingMode(DEFAULT_LOGGING_MODE);
            manager.setDefaultRepositoryLocation(DEFAULT_REPOSITORY_LOCATION);
            manager.setDefaultServerLocation(SERVER_LOCATION);
            manager.setDefaultStorageLocation(DEFAULT_STORAGE_LOCATION);
            manager.writeProperties();
        } catch (Exception ex) {
            System.out.println("Cannot write into properties file ("
                    + ex.getMessage() + ")");
        }
    }

    public static String getExecutableName() {
        return EXECUTABLE_NAME;
    }

    public static String getExecutableStorageLocation() {
        return EXECUTABLE_STORAGE_LOCATION;
    }

    public static String getServerLocation() {
        return SERVER_LOCATION;
    }

    public static String getDefaultRepository() {
        return DEFAULT_REPOSITORY_LOCATION;
    }

    public static String getDefaultDCIBridge() {
        return DEFAULT_DCIBRIDGE_LOCATION;
    }

    public static String getDefaultCleaningExecutable() {
        return DEFAULT_CLEANING_EXECUTABLE;
    }

    public static String getFileSystemName() {
        return FILE_SYSTEM_NAME;
    }
}
