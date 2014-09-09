package uk.ac.wmin.cpc.submission.frontend.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.shiwa.repository.submission.interfaces.ImplShort;
import org.shiwa.repository.submission.interfaces.Parameter;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.ExecutorSite;
import uk.ac.wmin.cpc.submission.frontend.transferobjects.UserAccessConfig;

/**
 * Interface containing the methods of the WSCodeListService.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(name = "WSCodeListService")
public interface WSCodeListService {

    /**
     * Get the list of submittable implementations from the repository.
     * The name LCID is inherited from the GEMLCA interface.
     * @param userAccess portal and user credentials
     * @param urlRepository repository to contact
     * @return short information about the list of
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    @WebMethod(operationName = "getLCIDs")
    public ImplShort[] getLCIDs(
            @WebParam(name = "userAccess") UserAccessConfig userAccess,
            @WebParam(name = "urlRepository") String urlRepository)
            throws IllegalParameterException, RepositoryCommunicationException;

    /***
     * Get the names of workflow engine implementations where the provided 
     * implementation can be executed.
     * The name ExecutorSite is inherited from the GEMLCA interface.
     * @param urlRepository repository to contact
     * @param lcid implementation (name#version)
     * @return list containing the site names
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    @WebMethod(operationName = "getExecutorSites")
    public String[] getExecutorSites(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "lcid") String lcid)
            throws IllegalParameterException, RepositoryCommunicationException;

    /**
     * Get the list of non-fixed parameters for a provided implementation.
     * This returns the signature of the implementation (input/output).
     * The name LCParameter is inherited from the GEMLCA interface.
     * @param urlRepository repository to contact
     * @param lcid implementation (name#version)
     * @return
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    @WebMethod(operationName = "getLCParameters")
    public Parameter[] getLCParameters(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "lcid") String lcid)
            throws IllegalParameterException, RepositoryCommunicationException;

    /**
     * Get a resume of which middleware is associated to the provided 
     * implementation and workflow engine implementation.
     * From an implementation you can get the workflow engine, and from the 
     * workflow engine, with the workflow engine name, you can get to which
     * middleware it is configured.
     * @param urlRepository repository to contact
     * @param implName implementation (name#version)
     * @param siteName name of the workflow engine implementation
     * @return middleware associated
     * @throws IllegalParameterException
     * @throws RepositoryCommunicationException 
     */
    @WebMethod(operationName = "getExecutorSiteConfiguration")
    public ExecutorSite getExecutorSiteConfiguration(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "implName") String implName,
            @WebParam(name = "siteName") String siteName)
            throws IllegalParameterException, RepositoryCommunicationException;
}
