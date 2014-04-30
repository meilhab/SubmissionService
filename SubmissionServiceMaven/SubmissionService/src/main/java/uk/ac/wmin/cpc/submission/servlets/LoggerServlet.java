/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.frontend.helpers.PropertiesManager;
import uk.ac.wmin.cpc.submission.frontend.impl.WSCodeListServiceImpl;
import uk.ac.wmin.cpc.submission.frontend.impl.WSExecutionServiceImpl;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebServlet(name = "LoggerServlet", loadOnStartup = 1,
        urlPatterns = {"/LoggerServlet"})
public class LoggerServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        loadLog4jFile();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("Log4j Level Configurator for the submission service<br />");

        String level = req.getParameter("level");
        String reloadFile = req.getParameter("reloadLevel");

        if (level != null && !level.isEmpty()) {
            setLogLevel(out, level.toUpperCase());
            setLevelToPropertiesFile(level.toUpperCase());
        } else if (reloadFile != null && !reloadFile.isEmpty()
                && Boolean.parseBoolean(reloadFile)) {
            out.println((loadLog4jFile()
                    ? "Log4j reloading successful<br />"
                    : "Log4j reloading failure<br />"));
        } else {
            setLogLevel(out, null);
            out.println("No parameters given (set log level or reload "
                    + "properties)<br />");
        }
    }

    private static void setLogLevel(PrintWriter out, String level) {
        Level log4jLevel = getLevel(level);
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();

        while (loggers != null && loggers.hasMoreElements()) {
            Logger logger = loggers.nextElement();
            if (log4jLevel != null) {
                logger.setLevel(log4jLevel);
            }
            out.println("Log level is set to (" + logger.getLevel() + ") for: "
                    + logger.getName() + "<br />");

            if (logger.isDebugEnabled()) {
                logger.debug("log4j loaded successfully (" + logger.getLevel() + ")");
            }
        }
    }

    public static boolean loadLog4jFile() {
        String log4jFile = Configuration.getLog4jFile();
        PrintWriter out = new PrintWriter(System.out);
        URL pathLog4jFile;

        if (log4jFile != null && !log4jFile.isEmpty()
                && (pathLog4jFile = LoggerServlet.class.getResource(log4jFile)) != null) {
            DOMConfigurator.configure(pathLog4jFile);
            setLogLevel(out, getLevelFromPropertiesFile());
            Enumeration<Logger> loggers = LogManager.getCurrentLoggers();

            while (loggers != null && loggers.hasMoreElements()) {
                Logger logger = loggers.nextElement();
                logger.info("log4j loaded successfully (" + logger.getLevel() + ")");
            }

            return true;
        }

        BasicConfigurator.configure();
        setLogLevel(out, getLevelFromPropertiesFile());
        Logger logger = LogManager.getRootLogger();
        logger.warn("Default log4j loaded (" + logger.getLevel() + ")");

        return false;
    }

    private static String getLevelFromPropertiesFile() {
        try {
            PropertiesManager manager = new PropertiesManager();
            manager.readProperties();
            return manager.getPropertiesData().getDEFAULT_LOGGING_MODE();
        } catch (Exception ex) {
            return null;
        }
    }

    private void setLevelToPropertiesFile(String level) {
        try {
            PropertiesManager manager = new PropertiesManager();
            manager.readProperties();
            String levelFile = manager.getPropertiesData().getDEFAULT_LOGGING_MODE();

            if (getLevel(level) != null && level != null && levelFile != null
                    && !level.equals(levelFile)) {
                manager.getPropertiesData().setDEFAULT_LOGGING_MODE(level.toString());
                manager.writeProperties();
            }
        } catch (Exception ex) {
        }
    }

    private static Level getLevel(String level) {
        Level log4jLevel = null;

        if (level != null) {
            switch (level) {
                case "ALL":
                    log4jLevel = Level.ALL;
                    break;
                case "DEBUG":
                    log4jLevel = Level.DEBUG;
                    break;
                case "ERROR":
                    log4jLevel = Level.ERROR;
                    break;
                case "FATAL":
                    log4jLevel = Level.FATAL;
                    break;
                case "INFO":
                    log4jLevel = Level.INFO;
                    break;
                case "OFF":
                    log4jLevel = Level.OFF;
                    break;
                case "TRACE":
                    log4jLevel = Level.TRACE;
                    break;
                case "WARN":
                    log4jLevel = Level.WARN;
                    break;
            }
        }

        return log4jLevel;
    }

    public static Logger getMainLogger() {
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            String item = stackTraceElement.getClassName();
            if (item.equals(WSCodeListServiceImpl.class.getName())) {
                return LogManager.getLogger(WSCodeListServiceImpl.class);
            } else if (item.equals(WSExecutionServiceImpl.class.getName())) {
                return LogManager.getLogger(WSExecutionServiceImpl.class);
            }
        }

        return LogManager.getRootLogger();
    }
}
