/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl;

import dci.extension.ExtensionType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType;
import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.CandidateHostsType;
import org.ggf.schemas.jsdl._2005._11.jsdl.CreationFlagEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDescriptionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobIdentificationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemType;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeType;
import org.ggf.schemas.jsdl._2005._11.jsdl.ResourcesType;
import org.ggf.schemas.jsdl._2005._11.jsdl.SourceTargetType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.w3c.dom.Element;
import uk.ac.wmin.cpc.submission.jsdl.helpers.DCITools;
import uri.mbschedulingdescriptionlanguage.ConstraintsType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLItem {

    private JobIdentificationType jobIdentification;
    private ApplicationType application;
    private POSIXApplicationType posixApplication;
    private ResourcesType resources;
    private List<DataStagingType> listData;
    private ExtensionType extension;
    private SDLType metabroker;

    public JSDLItem(JobDefinitionType jsdl)
            throws IllegalArgumentException, JAXBException {
        this.jobIdentification = jsdl.getJobDescription().getJobIdentification();
        this.application = jsdl.getJobDescription().getApplication();
        this.posixApplication = returnPOSIXApplicationType(application.getAny().get(0));
        this.application.getAny().clear();
        this.resources = jsdl.getJobDescription().getResources();
        this.listData = jsdl.getJobDescription().getDataStaging();
        this.extension = returnExtensionType(jsdl.getAny());
        this.metabroker = returnSDLType(jsdl.getAny());
    }

    public void setApplicationExecutable(String executableName) {
        FileNameType posixExecutable = new FileNameType();
        posixExecutable.setValue(executableName);
        posixApplication.setExecutable(posixExecutable);
    }

    public void setApplicationArgument(String argument) {
        ArgumentType prefixArgument = new ArgumentType();
        prefixArgument.setValue(argument);
        posixApplication.getArgument().add(prefixArgument);
    }

    public List<ArgumentType> getApplicationArguments() {
        return posixApplication.getArgument();
    }

    public void addNewInputFile(String name, CreationFlagEnumeration creationFlag,
            boolean deletionFlag, String inputURL) {
        DataStagingType dataInput = new DataStagingType();
        dataInput.setFileName(name);
        dataInput.setCreationFlag(creationFlag);
        dataInput.setDeleteOnTermination(deletionFlag);

        SourceTargetType dataInputURL = new SourceTargetType();
        dataInputURL.setURI(inputURL);
        dataInput.setSource(dataInputURL);

        listData.add(dataInput);
    }

    public void addNewOutputFile(String name, String fileSystemName,
            CreationFlagEnumeration creationFlag, boolean deletionFlag,
            String outputURL) {
        DataStagingType dataOutput = new DataStagingType();
        dataOutput.setFileName(name);
        dataOutput.setFilesystemName(fileSystemName);
        dataOutput.setCreationFlag(creationFlag);
        dataOutput.setDeleteOnTermination(deletionFlag);

        SourceTargetType dataOutputURL = new SourceTargetType();
        dataOutputURL.setURI(outputURL);
        dataOutput.setTarget(dataOutputURL);

        listData.add(dataOutput);
    }

    public List<DataStagingType> getInputFiles() {
        List<DataStagingType> listInputs = new ArrayList<>();

        for (DataStagingType data : listData) {
            if (data.getSource() != null) {
                listInputs.add(data);
            }
        }

        return listInputs;
    }

    public List<DataStagingType> getOutputFiles() {
        List<DataStagingType> listOutputs = new ArrayList<>();

        for (DataStagingType data : listData) {
            if (data.getTarget() != null) {
                listOutputs.add(data);
            }
        }

        return listOutputs;
    }

    public void createSDLType(String dciType, String gridVO, String gridProxy)
            throws IllegalArgumentException {
        metabroker = new SDLType();
        metabroker.setConstraints(new ConstraintsType());
        metabroker.getConstraints().getMiddleware().add(
                DCITools.mbsdlMiddleware(dciType, gridVO, gridProxy));
    }

    public void createResource(String hostName,
            OperatingSystemTypeEnumeration operatingSystem) {
        CandidateHostsType candidateHostsType = new CandidateHostsType();
        candidateHostsType.getHostName().add(hostName);

        OperatingSystemType operatingSystemType = new OperatingSystemType();
        OperatingSystemTypeType operatingSystemTypeType = new OperatingSystemTypeType();
        operatingSystemTypeType.setOperatingSystemName(operatingSystem);
        operatingSystemType.setOperatingSystemType(operatingSystemTypeType);

        resources = new ResourcesType();
        resources.setOperatingSystem(operatingSystemType);
        resources.setCandidateHosts(candidateHostsType);
    }

    public CreateActivityType getExecutableJSDL()
            throws JAXBException {
        JobDefinitionType jsdl = getCompleteJSDL();

        CreateActivityType activityJsdl = new CreateActivityType();
        activityJsdl.setActivityDocument(new ActivityDocumentType());
        activityJsdl.getActivityDocument().setJobDefinition(jsdl);

        return activityJsdl;
    }

    public JobDefinitionType getCompleteJSDL() {
        JobDefinitionType jsdl = new JobDefinitionType();

        jsdl.setJobDescription(new JobDescriptionType());
        jsdl.getJobDescription().setJobIdentification(jobIdentification);
        jsdl.getJobDescription().setApplication(application);
        jsdl.getJobDescription().setResources(resources);

        for (DataStagingType data : listData) {
            jsdl.getJobDescription().getDataStaging().add(data);
        }

        jsdl.getJobDescription().getApplication().getAny().
                add(new JAXBElement(new QName("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix",
                "POSIXApplication_Type"), POSIXApplicationType.class, posixApplication));
        jsdl.getAny().add(new JAXBElement(
                new QName("uri:MBSchedulingDescriptionLanguage", "SDL_Type"),
                metabroker.getClass(), metabroker));
        jsdl.getAny().add(new JAXBElement(
                new QName("extension.dci", "extension_type"),
                extension.getClass(), extension));

        return jsdl;
    }

    private POSIXApplicationType returnPOSIXApplicationType(Object object)
            throws JAXBException {
        posixApplication = new POSIXApplicationType();
        Element element = (Element) object;

        if (element != null && element.getNamespaceURI().
                equals("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix")) {
            return DCITools.extractClass(element, posixApplication);
        }

        throw new JAXBException("POSIXApplication not found");
    }

    private ExtensionType returnExtensionType(List<Object> anyObjects)
            throws JAXBException {
        extension = new ExtensionType();

        for (Object object : anyObjects) {
            Element element = (Element) object;

            if (element != null && element.getNamespaceURI().equals("extension.dci")) {
                return DCITools.extractClass(element, extension);
            }
        }

        throw new JAXBException("Extension not found");
    }

    private SDLType returnSDLType(List<Object> anyObjects)
            throws JAXBException {
        metabroker = new SDLType();

        for (Object object : anyObjects) {
            Element element = (Element) object;

            if (element != null && element.getNamespaceURI().
                    equals("uri:MBSchedulingDescriptionLanguage")) {
                return DCITools.extractClass(element, metabroker);
            }
        }

        throw new JAXBException("Extension not found");
    }

    public JobIdentificationType getJobIdentification() {
        return jobIdentification;
    }

    public ResourcesType getResources() {
        return resources;
    }

    public SDLType getMetabroker() {
        return metabroker;
    }

    public POSIXApplicationType getPOSIXApplication() {
        return posixApplication;
    }
}
