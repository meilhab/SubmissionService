package uk.ac.wmin.cpc.submission.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.frontend.helpers.PropertiesData;
import uk.ac.wmin.cpc.submission.helpers.LogText;

/**
 * This servlet's goal is to manage the configuration file by reloading it 
 * when any modification has been applied. This avoid to restart the service.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebServlet(name = "PropertiesServlet", urlPatterns = {"/PropertiesServlet"})
public class PropertiesServlet extends HttpServlet {

    private static LogText logger = new LogText("PROPERTIESSERVLET",
            Logger.getLogger("LoggersManager"));

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("Properties reloader for the submission service<br />");

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Properties reloading requested");
        }

        String reloadFile = request.getParameter("reloadProps");
        if (reloadFile != null && !reloadFile.isEmpty()
                && Boolean.parseBoolean(reloadFile)) {
            try {
                logger.log(Level.INFO, "Properties reloading started");

                reloadProperties(out);

                logger.log(Level.INFO, "Properties reloaded successfully");
                out.println("Properties successfully reloaded<br />");
            } catch (Exception ex) {
                out.println("Problem during the reloading process of all properties<br />");
                out.println(ex.getMessage());
                logger.log(Level.ERROR, "Properties can't be reloaded", ex);
            }
        }
    }

    /**
     * Actualise the system configuration from the configuration file.
     * @param out servlet's output
     * @throws Exception 
     */
    private void reloadProperties(PrintWriter out) throws Exception {
        PropertiesData dataFile = Configuration.getPropertiesDataFromFile();
        PropertiesData backup = Configuration.getPropertiesDataLoaded();

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Cleaning executable ("
                    + dataFile.getDEFAULT_CLEANING_EXECUTABLE() + ")");
            logger.log(Level.DEBUG, "DCI Bridge location ("
                    + dataFile.getDEFAULT_DCIBRIDGE_LOCATION() + ")");
            logger.log(Level.DEBUG, "Logging level ("
                    + dataFile.getDEFAULT_LOGGING_MODE() + ")");
            logger.log(Level.DEBUG, "Default Repository ("
                    + dataFile.getDEFAULT_REPOSITORY_LOCATION() + ")");
            logger.log(Level.DEBUG, "Storage location ("
                    + dataFile.getDEFAULT_STORAGE_LOCATION() + ")");
            logger.log(Level.DEBUG, "Server used ("
                    + dataFile.getSERVER_LOCATION() + ")");
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.log(Level.DEBUG, "Saving the data to file");
            }

            Configuration.getPropertiesDataLoaded().setPropertiesData(dataFile);
            Configuration.saveAsProperties();
            Configuration.actualizeLog4jLocation();
            LoggerServlet.loadLog4jFile();

            if (logger.isDebugEnabled()) {
                logger.log(Level.DEBUG, "Saved");
            }
        } catch (Exception ex) {
            out.println("RollBack process<br />");
            logger.log(Level.ERROR, "Error detected, rollback process launched");

            Configuration.getPropertiesDataLoaded().setPropertiesData(backup);
            Configuration.actualizeLog4jLocation();
            throw new Exception(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Reload properties for the submission service";
    }
}
