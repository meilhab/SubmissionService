package uk.ac.wmin.cpc.submission.storage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.helpers.LogText;

/**
 * This class provides functions to manipulate files and folders.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class FilesHelper {

    private static LogText logger = new LogText("STORAGE",
            Logger.getLogger("LoggersManager"));

    /**
     * Delete a folder and any file under it.
     * @param folder path of the folder
     * @throws IOException 
     */
    public static void deleteFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir,
                    IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }
        });
    }

    /**
     * Create a folder if not existing
     * @param folderName path to the folder
     * @throws IOException 
     */
    public static void initializeFolder(String folderName) throws IOException {
        Path path = Paths.get(folderName);
        if (!Files.exists(path)
                && !Files.isDirectory(Files.createDirectories(path))) {
            logger.log(Level.ERROR, " Cannot create the folder ("
                    + folderName + ")");
            throw new IOException("Cannot create the folder ("
                    + folderName + ")");
        }
    }
}
