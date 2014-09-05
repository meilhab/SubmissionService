package uk.ac.wmin.cpc.submission.helpers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Class managing the logs. For a given prefix, it adds it for every logs.
 * Useful for 'utility' logs.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class LogText {

    private String prefix;
    private Logger logger;

    public LogText(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void log(Level level, String message) {
        logger.log(level, prefix + ": " + message);
    }
    
    public void log(Level level, String message, Throwable thr) {
        logger.log(level, prefix + ": " + message, thr);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}
