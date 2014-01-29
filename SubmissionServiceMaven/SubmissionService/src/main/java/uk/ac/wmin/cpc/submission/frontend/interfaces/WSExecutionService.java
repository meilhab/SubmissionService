/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(name = "WSExecutionService")
public interface WSExecutionService {

    @WebMethod(operationName = "modifyJSDLFile")
    public JobDefinitionType modifyJSDLFile(
            @WebParam(name = "urlRepository") String urlRepository,
            @WebParam(name = "jsdl") JobDefinitionType jsdl)
            throws IllegalParameterException, WrongJSDLException,
            RepositoryCommunicationException, FileManagementException;

    @WebMethod(operationName = "submitToDciBridge")
    public String submitToDciBridge(
            @WebParam(name = "dciBridgeLocation") String dciBridgeLocation,
            @WebParam(name = "jsdl") JobDefinitionType jsdl)
            throws IllegalParameterException, ExecutionException;
}
