/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.impl;

import java.io.IOException;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import org.shiwa.repository.submission.service.DatabaseProblemException;
import org.shiwa.repository.submission.service.ForbiddenException;
import org.shiwa.repository.submission.service.NotFoundException;
import org.shiwa.repository.submission.service.UnauthorizedException;
import uk.ac.wmin.cpc.submission.exceptions.ExceptionsManager;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.servlets.LoggerServlet;
import uk.ac.wmin.cpc.submission.frontend.helpers.ServiceTools;
import uk.ac.wmin.cpc.submission.frontend.interfaces.WSCodeListService;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;
import uk.ac.wmin.cpc.submission.repository.RepositoryWSAccess;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(endpointInterface = "uk.ac.wmin.cpc.submission.frontend."
        + "interfaces.WSCodeListService", serviceName = "WSCodeListService",
        portName = "WSCodeListService")
public class WSCodeListServiceImpl implements WSCodeListService {

    private static Logger logger = LoggerServlet.getLogger(1);
    private RepositoryWSAccess repository;

    @Override
    public ImplShort[] getLCIDs(UserAccessConfig userAccess, String urlRepository)
            throws IllegalParameterException, RepositoryCommunicationException {
        ServiceTools.checkParam(userAccess);
        logger.info("START: getLCIDs");

        if (logger.isDebugEnabled()) {
            logger.debug("Name: " + userAccess.getExtServiceId());
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        repository = (urlRepository == null || urlRepository.isEmpty()
                ? new RepositoryWSAccess() : new RepositoryWSAccess(urlRepository));

        try {
            ImplShort[] responses =
                    repository.getAllPublicValidatedImplementations(userAccess);
            return responses;
        } catch (UnauthorizedException | DatabaseProblemException |
                ForbiddenException | IOException ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public String[] getExecutorSites(String urlRepository, String lcid)
            throws IllegalParameterException, RepositoryCommunicationException {
        ServiceTools.checkParam(lcid);
        logger.info("START: getExecutorSites for: " + lcid);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        repository = (urlRepository == null || urlRepository.isEmpty()
                ? new RepositoryWSAccess() : new RepositoryWSAccess(urlRepository));

        try {
            String[] responses =
                    repository.getAllWorkflowEngineInstances(lcid);
            return responses;
        } catch (DatabaseProblemException | ForbiddenException | IOException ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public Parameter[] getLCParameters(String urlRepository, String lcid)
            throws IllegalParameterException, RepositoryCommunicationException {
        ServiceTools.checkParam(lcid);
        logger.info("START: getLCParameters for: " + lcid);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }

        repository = (urlRepository == null || urlRepository.isEmpty()
                ? new RepositoryWSAccess() : new RepositoryWSAccess(urlRepository));

        try {
            Parameter[] responses =
                    repository.getAllParameters(lcid);
            return responses;
        } catch (DatabaseProblemException | ForbiddenException | IOException ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }

    @Override
    public BeInstance getExecutorSiteConfiguration(String urlRepository, String implName,
            String siteName)
            throws IllegalParameterException, RepositoryCommunicationException {
        ServiceTools.checkParam(siteName);
        logger.info("START: getExecutorSiteConfiguration for: " + implName);
        logger.info("Using: " + siteName);

        if (logger.isDebugEnabled()) {
            logger.debug("RepoURL: " + (urlRepository == null || urlRepository.isEmpty()
                    ? "default" : urlRepository));
        }


        repository = (urlRepository == null || urlRepository.isEmpty()
                ? new RepositoryWSAccess() : new RepositoryWSAccess(urlRepository));

        try {
            return repository.getBackendInstance(implName, siteName);
        } catch (NotFoundException | DatabaseProblemException |
                ForbiddenException | IOException ex) {
            ExceptionsManager.manageExceptionsCodeListService(ex, logger);
        }

        return null;
    }
}
