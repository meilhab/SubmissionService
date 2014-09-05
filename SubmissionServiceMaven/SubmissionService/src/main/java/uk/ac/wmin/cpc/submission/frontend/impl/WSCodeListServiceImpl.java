/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.impl;

import javax.jws.WebService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import uk.ac.wmin.cpc.submission.exceptions.ExceptionsManager;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.interfaces.WSCodeListService;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.ExecutorSite;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;
import uk.ac.wmin.cpc.submission.jsdl.helpers.MiddlewareExtractor;
import uk.ac.wmin.cpc.submission.repository.RepositoryWSAccess;

/**
 * Implementation of the WSCodeList Service. This web service requests data from
 * the SHIWA Repository or a test SHIWA Repository concerning implementation and
 * workflow engine implementations.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(endpointInterface = "uk.ac.wmin.cpc.submission.frontend."
        + "interfaces.WSCodeListService", serviceName = "WSCodeListService",
        portName = "WSCodeListService")
public class WSCodeListServiceImpl implements WSCodeListService {

    private static final Logger logger = LogManager.getLogger(WSCodeListServiceImpl.class);

    @Override
    public ImplShort[] getLCIDs(UserAccessConfig userAccess, String urlRepository)
            throws IllegalParameterException, RepositoryCommunicationException {
        logger.info("START: getLCIDs");

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        try {
            RepositoryWSAccess repository = new RepositoryWSAccess(urlRepository);
            ImplShort[] responses =
                    repository.getAllPublicValidatedImplementations(userAccess);
            return responses;
        } catch (Exception ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public String[] getExecutorSites(String urlRepository, String lcid)
            throws IllegalParameterException, RepositoryCommunicationException {
        logger.info("START: getExecutorSites for: " + lcid);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        try {
            RepositoryWSAccess repository = new RepositoryWSAccess(urlRepository);
            String[] responses = repository.getAllWorkflowEngineInstances(lcid);
            return responses;
        } catch (Exception ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public Parameter[] getLCParameters(String urlRepository, String lcid)
            throws IllegalParameterException, RepositoryCommunicationException {
        logger.info("START: getLCParameters for: " + lcid);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        try {
            RepositoryWSAccess repository = new RepositoryWSAccess(urlRepository);
            Parameter[] responses = repository.getAllParameters(lcid);
            return responses;
        } catch (Exception ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public ExecutorSite getExecutorSiteConfiguration(String urlRepository, String implName,
            String siteName)
            throws IllegalParameterException, RepositoryCommunicationException {
        logger.info("START: getExecutorSiteConfiguration for: " + implName);
        logger.info("Using: " + siteName);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        try {
            RepositoryWSAccess repository = new RepositoryWSAccess(urlRepository);
            BeInstance backend = repository.getBackendInstance(implName, siteName);

            ExecutorSite executorSite = new ExecutorSite();
            executorSite.setType(MiddlewareExtractor.getDCIName(backend).value());
            executorSite.setResource(MiddlewareExtractor.getResource(backend));

            if (logger.isDebugEnabled()) {
                logger.debug("Middleware (" + executorSite.getType() + ")");
                logger.debug("Resource (" + executorSite.getResource() + ")");
            }

            return executorSite;
        } catch (Exception ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }
}
