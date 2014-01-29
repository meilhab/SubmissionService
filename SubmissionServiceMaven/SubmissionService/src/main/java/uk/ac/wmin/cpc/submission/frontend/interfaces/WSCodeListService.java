/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.frontend.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(name = "WSCodeListService")
public interface WSCodeListService {

    @WebMethod(operationName = "getLCIDs")
    public ImplShort[] getLCIDs(
            @WebParam(name = "userAccess") UserAccessConfig userAccess,
            @WebParam(name = "urlRepository") String urlRepository)
            throws IllegalParameterException, RepositoryCommunicationException;

    @WebMethod(operationName = "getExecutorSites")
    public String[] getExecutorSites(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "lcid") String lcid)
            throws IllegalParameterException, RepositoryCommunicationException;

    @WebMethod(operationName = "getLCParameters")
    public Parameter[] getLCParameters(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "lcid") String lcid)
            throws IllegalParameterException, RepositoryCommunicationException;

    @WebMethod(operationName = "getExecutorSiteConfiguration")
    public BeInstance getExecutorSiteConfiguration(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "implName") String implName,
            @WebParam(name = "siteName") String siteName)
            throws IllegalParameterException, RepositoryCommunicationException;
}
