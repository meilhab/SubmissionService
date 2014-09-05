package uk.ac.wmin.cpc.submission.repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Level;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.EngineData;
import org.shiwa.repository.submission.interfaces.ImplJSDL;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import org.shiwa.repository.submission.interfaces.WorkflowEngineInstance;
import org.shiwa.repository.submission.service.DatabaseProblemException;
import org.shiwa.repository.submission.service.ForbiddenException;
import org.shiwa.repository.submission.service.NotFoundException;
import org.shiwa.repository.submission.service.SubmissionService;
import org.shiwa.repository.submission.service.SubmissionService_Service;
import org.shiwa.repository.submission.service.UnauthorizedException;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.helpers.LogText;
import uk.ac.wmin.cpc.submission.helpers.ServiceTools;
import uk.ac.wmin.cpc.submission.servlets.LoggerServlet;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;

/**
 * This class manages the connection to the Repository. It communicates 
 * directly with the remote web service.
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class RepositoryWSAccess {

    private static final String WSDL_LOCATION = "SubmissionService?wsdl";
    private static final int TIMEOUT = 10000;
    private String serviceLocation;
    private LogText logger = new LogText("REPOSITORYConnection",
            LoggerServlet.getMainLogger());
    private static final String defaultRepo = Configuration.getPropertiesDataLoaded()
            .getDEFAULT_REPOSITORY_LOCATION();

    public RepositoryWSAccess() throws MalformedURLException {
        this(defaultRepo);
    }

    /**
     * Instanciate the repository.
     * @param urlRepository be like (http://shiwa-repo.cpc.wmin.ac.uk/shiwa-repo)
     * @throws MalformedURLException 
     */
    public RepositoryWSAccess(String urlRepository) throws MalformedURLException {
        if (urlRepository == null || urlRepository.isEmpty()) {
            if ((urlRepository = defaultRepo) == null || urlRepository.isEmpty()) {
                throw new MalformedURLException("Wrong repository URL");
            }
        }

        serviceLocation = (urlRepository.endsWith("/")
                ? urlRepository : urlRepository + "/") + WSDL_LOCATION;
    }

    /**
     * Test if the remote web service can be contacted (timeout of 10s).
     * @throws MalformedURLException
     * @throws IOException 
     */
    private void testConnectionToService()
            throws MalformedURLException, IOException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Test connection to: " + serviceLocation);
        }

        URL url = new URL(serviceLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.connect();
        connection.disconnect();
    }

    /**
     * After testing the connection to the web service, return the object 
     * corresponding to the connection.
     * @return connection to the service
     * @throws MalformedURLException
     * @throws IOException 
     */
    private SubmissionService getConnectionToService()
            throws MalformedURLException, IOException {
        logger.log(Level.INFO, "Service Location: " + serviceLocation);
        testConnectionToService();
        SubmissionService_Service service = new SubmissionService_Service(
                new URL(serviceLocation));
        return service.getSubmissionService();
    }

    /**
     * Get all publicly validated implementations from the repository.
     * @param userAccess portal and user credentials
     * @return list of implementations
     * @throws MalformedURLException
     * @throws UnauthorizedException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public ImplShort[] getAllPublicValidatedImplementations(UserAccessConfig userAccess)
            throws MalformedURLException, UnauthorizedException,
            DatabaseProblemException, ForbiddenException, IOException,
            IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getAllPublicValidatedImplementations");
        }

        ServiceTools.checkUserAccessConfig(userAccess);
        SubmissionService portService = getConnectionToService();
        List<ImplShort> listImplShort =
                portService.getAllPublicValidatedImplementations(
                userAccess.getExtServiceId(), userAccess.getExtUserId());

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Implementations found ("
                    + listImplShort.size() + ")");
        }

        return listImplShort.toArray(new ImplShort[listImplShort.size()]);
    }

    /**
     * Get all workflow engine implementations for a specific implementation.
     * @param implName name of the implementation
     * @return list of workflow engine implementation names
     * @throws MalformedURLException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public String[] getAllWorkflowEngineInstances(String implName)
            throws MalformedURLException, DatabaseProblemException,
            ForbiddenException, IOException, IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getAllWorkflowEngineInstances");
            logger.log(Level.DEBUG, "Implementation (" + implName + ")");
        }

        ServiceTools.checkParam(implName);
        SubmissionService portService = getConnectionToService();
        List<String> listInstances = portService.
                getAllWorkflowEngineInstances(implName);

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "WE Instances found ("
                    + listInstances.size() + ")");
        }

        return listInstances.toArray(new String[listInstances.size()]);
    }

    /**
     * Get all non-fixed parameters for an implementation.
     * @param implName name of the implementation
     * @return list of all non-fixed parameters
     * @throws MalformedURLException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public Parameter[] getAllParameters(String implName)
            throws MalformedURLException, DatabaseProblemException,
            ForbiddenException, IOException, IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getAllParameters");
            logger.log(Level.DEBUG, "Implementation (" + implName + ")");
        }

        ServiceTools.checkParam(implName);
        SubmissionService portService = getConnectionToService();
        List<Parameter> listParams = portService.getLCParameters(implName);

        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "Parameters found ("
                    + listParams.size() + ")");
        }

        return listParams.toArray(new Parameter[listParams.size()]);
    }

    /**
     * Get the full object representing the implementation.
     * @param implName name of the desired implementation
     * @return full implementation object
     * @throws MalformedURLException
     * @throws NotFoundException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public ImplJSDL getFullImplJSDL(String implName)
            throws MalformedURLException, NotFoundException,
            DatabaseProblemException, ForbiddenException, IOException, IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getFullImplJSDL");
            logger.log(Level.DEBUG, "Implementation (" + implName + ")");
        }

        ServiceTools.checkParam(implName);
        SubmissionService portService = getConnectionToService();
        ImplJSDL implementation = portService.getFullImplForJSDL(implName);

        if (implementation == null) {
            throw new NotFoundException("Implementation from JSDL: incorrect "
                    + "return statement", null);
        }

        return implementation;
    }

    /**
     * Get a full workflow engine implementation from a workflow engine name and
     * version, and a workflow engine implementation name.
     * @param engineName  name of the workflow engine
     * @param engineVersion version of the workflow engine
     * @param instanceName name of the workflow engine implementation
     * @return full workflow engine implementation object
     * @throws MalformedURLException
     * @throws NotFoundException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public WorkflowEngineInstance getFullWEIForJSDL(String engineName,
            String engineVersion, String instanceName)
            throws MalformedURLException, NotFoundException,
            DatabaseProblemException, ForbiddenException, IOException,
            IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getFullWEIForJSDL");
            logger.log(Level.DEBUG, "Workflow engine (" + engineName + ")");
            logger.log(Level.DEBUG, "Version (" + engineVersion + ")");
            logger.log(Level.DEBUG, "Instance (" + instanceName + ")");
        }

        ServiceTools.checkParam(engineName, engineVersion, instanceName);
        EngineData engine = new EngineData();
        engine.setEngineName(engineName);
        engine.setEngineVersion(engineVersion);
        engine.setEngineInstanceName(instanceName);

        SubmissionService portService = getConnectionToService();
        WorkflowEngineInstance instance = portService.getFullWEIForJSDL(engine);

        if (instance == null) {
            throw new NotFoundException("Workflow Engine Instance from JSDL: "
                    + "incorrect return statement", null);
        }

        return instance;
    }

    /**
     * Get from a implementation name and workflow engine implementation name 
     * the backend instance (middleware) used.
     * @param implName name of the implementation
     * @param instanceName name of the workflow engine implementation name
     * @return backend instance (middleware)
     * @throws MalformedURLException
     * @throws NotFoundException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     * @throws IOException
     * @throws IllegalParameterException 
     */
    public BeInstance getBackendInstance(String implName, String instanceName)
            throws MalformedURLException, NotFoundException,
            DatabaseProblemException, ForbiddenException, IOException,
            IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.log(Level.DEBUG, "getBackendInstance");
            logger.log(Level.DEBUG, "Implementation name (" + implName + ")");
            logger.log(Level.DEBUG, "Instance (" + instanceName + ")");
        }

        ServiceTools.checkParam(implName, instanceName);
        SubmissionService portService = getConnectionToService();
        BeInstance instance = portService.
                getBackendInstance(implName, instanceName);

        if (instance == null) {
            throw new NotFoundException("Backend instance requested: "
                    + "incorrect return statement", null);
        }

        return instance;
    }
}
