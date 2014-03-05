/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.impl;

import java.io.IOException;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault;
import org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.shiwa.repository.submission.service.DatabaseProblemException;
import org.shiwa.repository.submission.service.ForbiddenException;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.exceptions.ExceptionsManager;
import uk.ac.wmin.cpc.submission.jsdl.helpers.DCISubmitter;
import uk.ac.wmin.cpc.submission.jsdl.JSDLModificator;
import uk.ac.wmin.cpc.submission.exceptions.ExecutionException;
import uk.ac.wmin.cpc.submission.exceptions.FileManagementException;
import uk.ac.wmin.cpc.submission.exceptions.WrongJSDLException;
import uk.ac.wmin.cpc.submission.frontend.interfaces.WSExecutionService;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.jsdl.helpers.DCITools;
import uk.ac.wmin.cpc.submission.repository.RepositoryWSAccess;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(endpointInterface = "uk.ac.wmin.cpc.submission.frontend."
        + "interfaces.WSExecutionService", serviceName = "WSExecutionService",
        portName = "WSExecutionService")
public class WSExecutionServiceImpl implements WSExecutionService {

    private static Logger logger = LogManager.getLogger(WSExecutionServiceImpl.class);

    @Override
    public JobDefinitionType modifyJSDLFile(String urlRepository,
            JobDefinitionType jsdl)
            throws WrongJSDLException, IllegalParameterException,
            RepositoryCommunicationException, FileManagementException {
        if (logger.isDebugEnabled()) {
            logger.debug("START: modifyJSDLFile");
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        try {
            RepositoryWSAccess repository = new RepositoryWSAccess(urlRepository);
            JSDLModificator creator = new JSDLModificator(repository, jsdl);
            return creator.generateNewJSDL();
        } catch (IllegalArgumentException | JAXBException | NotFoundException |
                DatabaseProblemException | ForbiddenException | IOException ex) {
            ExceptionsManager.managerExceptionsExecutionService(ex, logger);
        }

        return null;
    }

    @Override
    public String submitToDciBridge(String dciBridgeLocation, JobDefinitionType jsdl)
            throws IllegalParameterException, ExecutionException {
        try {
            DCITools.getJSDLXML(jsdl);
        } catch (JAXBException ex) {
            throw new IllegalParameterException("Incorrect JSDL received", ex);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("START: submitToDciBridge");
            logger.debug("DCI Bridge URL: "
                    + dciBridgeLocation == null || dciBridgeLocation.isEmpty()
                    ? "default" : dciBridgeLocation);
        }

        DCISubmitter dciSubmitter =
                dciBridgeLocation == null || dciBridgeLocation.isEmpty()
                ? new DCISubmitter() : new DCISubmitter(dciBridgeLocation);
        try {
            String id = dciSubmitter.submitJobDefinition(jsdl);
            logger.info("(" + id + ") has been submitted");

            return id;
        } catch (NotAuthorizedFault ex) {
            throw new ExecutionException("Not authorized to execute", ex);
        } catch (NotAcceptingNewActivitiesFault ex) {
            throw new ExecutionException("Not accepting any new activities", ex);
        } catch (InvalidRequestMessageFault ex) {
            throw new ExecutionException("Problem detected in the JSDL", ex);
        } catch (UnsupportedFeatureFault ex) {
            throw new ExecutionException("Incorrect option in the JSDL", ex);
        } catch (Exception ex) {
            throw new ExecutionException("Execution cannot be performed", ex);
        }
    }
}
