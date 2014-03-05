/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl;

import uk.ac.wmin.cpc.submission.jsdl.helpers.JSDLHelpers;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import uk.ac.wmin.cpc.submission.jsdl.helpers.DCITools;
import uk.ac.wmin.cpc.submission.jsdl.helpers.JSDLExtractor;
import uk.ac.wmin.cpc.submission.frontend.servlets.LoggerServlet;
import org.shiwa.repository.submission.interfaces.ImplJSDL;
import org.shiwa.repository.submission.interfaces.Parameter;
import org.shiwa.repository.submission.interfaces.WorkflowEngineInstance;
import org.shiwa.repository.submission.service.DatabaseProblemException;
import org.shiwa.repository.submission.service.ForbiddenException;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.helpers.ServiceTools;
import uk.ac.wmin.cpc.submission.repository.RepositoryWSAccess;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLModificator {

    private static Logger logger = LoggerServlet.getMainLogger();
    private RepositoryWSAccess repository;
    private JSDLExtractor extractor;
    private JSDLItem newJSDL;

    public JSDLModificator(RepositoryWSAccess repository, JobDefinitionType jsdl)
            throws IllegalArgumentException, JAXBException, IllegalParameterException {
        ServiceTools.checkParam(repository, jsdl);
        this.repository = repository;
        this.newJSDL = new JSDLItem(jsdl);
        this.extractor = quickCheckJSDL(jsdl);
    }

    public JobDefinitionType generateNewJSDL()
            throws JAXBException, MalformedURLException, NotFoundException,
            IllegalArgumentException, DatabaseProblemException,
            ForbiddenException, IOException, IllegalParameterException {
        // getDataFromRepository()  -> get all data to exploit them             OK
        // modifyPOSIXApplication() -> add executable and arguments?            OK
        // modifyDataStaging()      -> add new inputs                           OK
        // modifyExtensionType()    -> should stay the same                     OK
        // modifySDLType()          -> according to the workflow engine         KO
        // modifyResources()        -> according to the workflow engine as well OK
        logger.info("New JSDL generation have been started");

        String implName = extractor.getImplementationName();
        String engineInstanceName = extractor.getWorkflowEngineInstance();

        ImplJSDL implementation = getFullImplementation(implName);
        logger.info("Implementation recovered");

        WorkflowEngineInstance engineInstance = getFullWorkflowEngineInstance(
                implementation.getWorkflowEngineName(),
                implementation.getWorkflowEngineVersion(), engineInstanceName);
        logger.info("Workflow Engine Instance recovered");

        JSDLHelpers.checkRepositoryData(implementation, engineInstance);
        logger.info("Engine compatible");

        logger.info("Configuration for resources and SDLType of JSDL");
        JSDLHelpers.configureMiddleware(engineInstance, newJSDL);

        logger.info("Configuration for application and missing data");
        modifyPOSIXApplicationAndDataStaging(implementation, engineInstance);

        JobDefinitionType generatedJSDL = newJSDL.getCompleteJSDL();
        if (logger.isDebugEnabled()) {
            logger.debug(DCITools.getJSDLXML(generatedJSDL));
        }

        return DCITools.readJSDLFromString(DCITools.getJSDLXML(generatedJSDL));
    }

    private ImplJSDL getFullImplementation(String implName)
            throws MalformedURLException, NotFoundException,
            DatabaseProblemException, ForbiddenException, IOException,
            IllegalParameterException {
        // good way could be to call the service or using the client interface
        // but possibility of conflicts when deployment
        logger.info("Getting " + implName + " from repository");
        return repository.getFullImplJSDL(implName);
    }

    private WorkflowEngineInstance getFullWorkflowEngineInstance(String engineName,
            String engineVersion, String instanceName)
            throws MalformedURLException, NotFoundException,
            DatabaseProblemException, ForbiddenException, IOException,
            IllegalParameterException {
        logger.info("Getting " + instanceName + " from repository");
        return repository.getFullWEIForJSDL(engineName, engineVersion, instanceName);
    }

    private void modifyPOSIXApplicationAndDataStaging(ImplJSDL implementation,
            WorkflowEngineInstance engineInstance) throws MalformedURLException,
            NotFoundException, IllegalArgumentException,
            IOException, DatabaseProblemException, ForbiddenException,
            IllegalParameterException {
        logger.info("Modification POSIXApplication JSDL");
        // checking params
        Parameter[] params = checkInputOutputs(implementation);

        // configuration of the executable and datastaging if needed
        JSDLHelpers.configureExecutable(implementation, engineInstance, newJSDL,
                extractor.getUserID());

        //configuration of inputs and outputs
        JSDLHelpers.treatParameters(implementation, params, newJSDL, extractor);

        // add workflow definition to argument
        JSDLHelpers.configureDefinition(engineInstance, implementation, newJSDL);

        // configuration of the max wall time
        JSDLHelpers.configureMaxWallTime(implementation, newJSDL);
    }

    private JSDLExtractor quickCheckJSDL(JobDefinitionType jsdl)
            throws IllegalArgumentException {
        try {
            String valueJSDL = DCITools.getJSDLXML(jsdl);

            if (logger.isDebugEnabled()) {
                logger.debug(valueJSDL);
            }

            if (valueJSDL != null) {
                JSDLExtractor jsdlExtractor = new JSDLExtractor(newJSDL);
                DCINameEnumeration dciName = jsdlExtractor.getMiddlewareName();

                if (dciName.equals(DCINameEnumeration.SHIWA)) {
                    logger.info("JSDL have been checked quickly: OK");
                    return jsdlExtractor;
                }
            }
        } catch (JAXBException ex) {
            throw new IllegalArgumentException("Error parsing JSDL file", ex);
        }

        throw new IllegalArgumentException("JSDL file or dciName wrong");
    }

    private Parameter[] checkInputOutputs(ImplJSDL implementation)
            throws MalformedURLException, NotFoundException, ForbiddenException,
            IllegalArgumentException, DatabaseProblemException, IOException,
            IllegalParameterException {
        if (logger.isDebugEnabled()) {
            logger.debug("Checking Inputs/Outputs data JSDL");
        }

        Parameter[] params = repository.getAllParameters(
                implementation.getAppName() + "#" + implementation.getImplVersion());
        List<ArgumentType> listArguments = newJSDL.getApplicationArguments();

        List<DataStagingType> inputsJSDL = newJSDL.getInputFiles();
        List<DataStagingType> outputsJSDL = newJSDL.getOutputFiles();

        if (params.length == listArguments.size()) {
            for (int i = 0; i < params.length; i++) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Check: " + params[i].getValue());
                }
                String[] lineParam = ((String) (params[i].getValue())).split(",");

                if (lineParam[2].equals("File")) {
                    switch (lineParam[4]) {
                        case "Input":
                            checkPorts(inputsJSDL,
                                    listArguments.get(i).getValue(), true);
                            break;
                        case "Output":
                            checkPorts(outputsJSDL,
                                    listArguments.get(i).getValue(), false);
                            break;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid number of parameters");
        }

        return params;
    }

    private void checkPorts(List<DataStagingType> portJSDL, String argumentValue,
            boolean input) throws IllegalArgumentException {
        boolean check = false;
        for (DataStagingType data : portJSDL) {
            if (argumentValue.equals(data.getFileName())) {
                if (logger.isDebugEnabled()) {
                    logger.debug(argumentValue + " checked OK");
                }
                check = true;
                break;
            }
        }

        if (!check) {
            String type = input ? "Input" : "Output";
            throw new IllegalArgumentException(
                    "Wrong configuration of a " + type + " port");
        }
    }
}
