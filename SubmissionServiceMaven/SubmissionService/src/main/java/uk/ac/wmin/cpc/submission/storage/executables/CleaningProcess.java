package uk.ac.wmin.cpc.submission.storage.executables;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.helpers.LogText;
import uk.ac.wmin.cpc.submission.storage.FilesHelper;

/**
 * This class manages the cleaning process of the executables.
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class CleaningProcess implements Runnable {

    private LogText logger = new LogText("CLEANING",
            Logger.getLogger("LoggersManager"));
    private String pathToStorage;
    private int CLEAN_HOURS = 6;

    public CleaningProcess() {
        this.pathToStorage = Configuration.getExecutableStorageLocation();
        String defaultCleaning = Configuration.getPropertiesDataLoaded()
                .getDEFAULT_CLEANING_EXECUTABLE();

        try {
            int hours = Integer.parseInt(defaultCleaning);
            if (hours <= 0) {
                throw new Exception();
            }

            CLEAN_HOURS = hours;
        } catch (Exception ex) {
        }

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Deletion fixed to (" + CLEAN_HOURS + " hours)");
        }
    }

    @Override
    public void run() {
        try {
            if (logger.isDebugEnabled()) {
                logger.log(Level.DEBUG, "Process running");
            }

            FilesHelper.initializeFolder(pathToStorage);
            browseUsersFolders(Paths.get(pathToStorage));
        } catch (Exception ex) {
            logger.log(Level.ERROR, "problem detected", ex);
        }
    }

    /**
     * Check if a folder needs to be deleted.
     * @param folder path of the folder
     * @return true if it needs to be deleted, false otherwise
     */
    private boolean checkDeletion(Path folder) {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Checking for: " + folder.getFileName());
        }

        try {
            FileTime time = Files.getLastModifiedTime(folder);
            Calendar calTime = Calendar.getInstance();
            calTime.setTimeInMillis(time.toMillis());
            calTime.add(Calendar.HOUR_OF_DAY, CLEAN_HOURS);

            if (calTime.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                return true;
            }
        } catch (Exception ex) {
            logger.log(Level.ERROR, "Cannot check file", ex);
        }

        return false;
    }

    /**
     * Browse the folders for a user and check if one needs to be deleted.
     * @param userDir path to the concerned user folder
     */
    private void browseApplicationsFolders(Path userDir) {
        try (DirectoryStream<Path> streamUserApp =
                Files.newDirectoryStream(userDir)) {
            for (Path userApp : streamUserApp) {
                if (checkDeletion(userApp)) {
                    logger.log(Level.INFO, "Deletion started for: "
                            + userApp.getFileName());
                    FilesHelper.deleteFolder(userApp);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.ERROR, "Cannot get users list", ex);
        }
    }

    /**
     * Check if the user directory needs to be deleted. If not, check if has
     * sub-folders, if yes, then check if they need to be deleted.
     * @param path path to the user directory
     */
    private void browseUsersFolders(Path path) {
        try (DirectoryStream<Path> streamUserDir = Files.newDirectoryStream(path)) {
            for (Path userDir : streamUserDir) {
                // recursive deletion if userDir not modified anymore
                if (checkDeletion(userDir)) {
                    logger.log(Level.INFO, "Deletion started for: "
                            + userDir.getFileName());
                    FilesHelper.deleteFolder(userDir);
                }

                // if modified and exists, then check inside
                if (Files.exists(userDir)) {
                    browseApplicationsFolders(userDir);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.ERROR, "Cannot get directory list", ex);
        }
    }
}
