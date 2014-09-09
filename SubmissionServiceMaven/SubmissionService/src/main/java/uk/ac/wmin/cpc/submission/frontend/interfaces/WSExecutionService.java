package uk.ac.wmin.cpc.submission.frontend.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import uk.ac.wmin.cpc.submission.exceptions.ExecutionException;
import uk.ac.wmin.cpc.submission.exceptions.FileManagementException;
import uk.ac.wmin.cpc.submission.exceptions.IllegalParameterException;
import uk.ac.wmin.cpc.submission.exceptions.RepositoryCommunicationException;
import uk.ac.wmin.cpc.submission.exceptions.WrongJSDLException;

/**
 * Interface containing the methods of the WSExecutionService.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(name = "WSExecutionService")
public interface WSExecutionService {

    /**
     * Modify a JSDL file provided by the DCI Bridge in order to integrate
     * the workflow engine management system from the SHIWA Repository
     * @param urlRepository repository to contact
     * @param jsdl JSDL file to modify
     * @return completed and DCI Bridge submission-enabled JSDL file
     * @throws IllegalParameterException
     * @throws WrongJSDLException
     * @throws RepositoryCommunicationException
     * @throws FileManagementException 
     */
    @WebMethod(operationName = "modifyJSDLFile")
    public JobDefinitionType modifyJSDLFile(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "jsdl") JobDefinitionType jsdl)
            throws IllegalParameterException, WrongJSDLException,
            RepositoryCommunicationException, FileManagementException;

    /**
     * Submit a JSDL file to a DCI Bridge and return the ID of the workflow
     * submitted
     * @param dciBridgeLocation URL of the DCI Bridge
     * @param jsdl JSDL file to submit
     * @return ID of the workflow submitted
     * @throws IllegalParameterException
     * @throws ExecutionException 
     */
    @WebMethod(operationName = "submitToDciBridge")
    public String submitToDciBridge(
            @WebParam(name = "dciBridgeLocation") String dciBridgeLocation,
            @WebParam(name = "jsdl") JobDefinitionType jsdl)
            throws IllegalParameterException, ExecutionException;
}
