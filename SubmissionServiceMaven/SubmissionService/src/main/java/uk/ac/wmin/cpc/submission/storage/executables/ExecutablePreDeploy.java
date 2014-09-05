package uk.ac.wmin.cpc.submission.storage.executables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.helpers.LogText;
import uk.ac.wmin.cpc.submission.storage.FilesHelper;

/**
 * This class manages the creation of the executable in the storage place
 * for a pre-deploy configuration.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExecutablePreDeploy {

    private static LogText logger = new LogText("PREDEPLOY",
            Logger.getLogger("LoggersManager"));
    private Path filePath;
    private String userFolder;
    private String appFolder;
    private String fileName;
    private String STORAGE = Configuration.getExecutableStorageLocation();
    private String server = Configuration.getPropertiesDataLoaded().getSERVER_LOCATION();

    public ExecutablePreDeploy(String userFolder, String appFolder, String fileName) {
        this.userFolder = userFolder;
        this.appFolder = appFolder;
        this.fileName = fileName;
    }

    /**
     * Create a new executable under the $STORAGE/userID/TEMPFile.
     * @throws IOException 
     */
    public void createFile() throws IOException {
        // user folder
        FilesHelper.initializeFolder(STORAGE);
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "STORAGE location recovered (" + STORAGE + ")");
        }

        Path pathFile = Paths.get(STORAGE, userFolder);
        FilesHelper.initializeFolder(pathFile.toString());
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "User location recovered (" + userFolder + ")");
        }

        // temporary folder
        pathFile = Files.createTempDirectory(
                Paths.get(STORAGE, userFolder), appFolder);
        this.appFolder = pathFile.getFileName().toString();
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Temporary folder created (" + appFolder + ")");
        }

        // executable file
        this.filePath = Files.createFile(
                Paths.get(pathFile.toString(), fileName));

        if (!Files.isRegularFile(this.filePath)) {
            logger.log(Level.ERROR, "Cannot create the temporary executable");
            throw new IOException("Cannot create the temporary executable");
        }
        logger.log(Level.INFO, "New executable file created");

        writeIntoFile("#!/bin/bash\n");
    }

    /**
     * Write a new command into the created executable.
     * @param message command to write
     * @throws IOException 
     */
    public void writeIntoFile(String message) throws IOException {
        OpenOption[] options = {
            StandardOpenOption.APPEND,
            StandardOpenOption.WRITE};

        byte[] buffer = message.getBytes();
        Files.write(this.filePath, buffer, options);
    }

    /**
     * Get the URL to access the executable via the getFile servlet.
     * @return URL to download the file
     */
    public String getURL() {
        return server + (server.endsWith("/") ? "" : "/")
                + "getFile?userID=" + userFolder
                + "&folder=" + appFolder;
    }
}
