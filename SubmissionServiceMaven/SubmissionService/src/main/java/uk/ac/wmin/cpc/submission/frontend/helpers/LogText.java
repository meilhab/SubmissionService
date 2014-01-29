/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.helpers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
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
