package uk.ac.wmin.cpc.submission.jsdl;

import dci.extension.ExtensionType;
import java.math.BigInteger;
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
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.LimitsType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.w3c.dom.Element;
import uk.ac.wmin.cpc.submission.jsdl.helpers.DCITools;
import uri.mbschedulingdescriptionlanguage.ConstraintsType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 * This class is the representation of a JSDL file, with all sections that it
 * integrates. Its purpose is to modify each section according to the needs and
 * return an assembled class.
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLItem {

    /**
     * Section JobIdentificationType of the JSDL: Job name and id.
     */
    private JobIdentificationType jobIdentification;
    /**
     * Section ApplicationType of the JSDL: application name and
     * POSIXApplicationType.
     */
    private ApplicationType application;
    /**
     * Section POSIXApplicationType of the JSDL: executable, output, error,
     * arguments, etc.
     */
    private POSIXApplicationType posixApplication;
    /**
     * Section ResourcesType of the JSDL: candidate hosts and operating system.
     */
    private ResourcesType resources;
    /**
     * Section DataStagingType of the JSDL: input and outputs.
     */
    private List<DataStagingType> listData;
    /**
     * Section ExtensionType of the JSDL: workflow information (wfi) service and
     * proxy service.
     */
    private ExtensionType extension;
    /**
     * Section SDLType of the JSDL: middleware information.
     */
    private SDLType metabroker;

    public JSDLItem(JobDefinitionType jsdl)
            throws IllegalArgumentException, JAXBException {
        this.jobIdentification = jsdl.getJobDescription().getJobIdentification();
        this.application = jsdl.getJobDescription().getApplication();
        this.posixApplication = returnPOSIXApplicationType(application.getAny());
        this.application.getAny().clear();
        this.resources = jsdl.getJobDescription().getResources();
        this.listData = jsdl.getJobDescription().getDataStaging();
        this.extension = returnExtensionType(jsdl.getAny());
        this.metabroker = returnSDLType(jsdl.getAny());
    }

    /**
     * Set an executable name to the POSIXApplicationType section.
     *
     * @param executableName name of the executable
     */
    public void setApplicationExecutable(String executableName) {
        FileNameType posixExecutable = new FileNameType();
        posixExecutable.setValue(executableName);
        posixApplication.setExecutable(posixExecutable);
    }

    /**
     * Add an argument to the POSIXApplicationType section.
     *
     * @param argument argument to add.
     */
    public void setApplicationArgument(String argument) {
        ArgumentType prefixArgument = new ArgumentType();
        prefixArgument.setValue(argument);
        posixApplication.getArgument().add(prefixArgument);
    }

    /**
     * Get all arguments added to the POSIXApplicationType section.
     *
     * @return list of arguments
     */
    public List<ArgumentType> getApplicationArguments() {
        return posixApplication.getArgument();
    }

    /**
     * Add a new input file to the JSDL.
     *
     * @param name name of the input file
     * @param creationFlag behaviour when created (default: overwrite)
     * @param deletionFlag behaviour when the workflow has finished its
     * execution (default: true)
     * @param inputURL URL where the input file can be found
     */
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

    /**
     * Add a new output file to the JSDL.
     *
     * @param name name of the output file
     * @param fileSystemName name of the file that is supposed to contain this
     * output file (default: see Configuration.FILE_SYSTEM_NAME)
     * @param creationFlag behaviour when created (default: overwrite)
     * @param deletionFlag behaviour when the workflow has finished its
     * execution (default: true)
     * @param outputURL URL where the output file has to be uploaded
     */
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

    /**
     * Get the list of all input files.
     *
     * @return input files list
     */
    public List<DataStagingType> getInputFiles() {
        List<DataStagingType> listInputs = new ArrayList<>();

        for (DataStagingType data : listData) {
            if (data.getSource() != null) {
                listInputs.add(data);
            }
        }

        return listInputs;
    }

    /**
     * Get the list of all output files.
     *
     * @return output files list
     */
    public List<DataStagingType> getOutputFiles() {
        List<DataStagingType> listOutputs = new ArrayList<>();

        for (DataStagingType data : listData) {
            if (data.getTarget() != null) {
                listOutputs.add(data);
            }
        }

        return listOutputs;
    }

    /**
     * Create a new SDLType section containing data about the middleware.
     *
     * @param dciType type of middleware
     * @param gridVO resource associated to this middleware
     * @param gridProxy leave it blank by default
     * @throws IllegalArgumentException
     */
    public void createSDLType(String dciType, String gridVO, String gridProxy)
            throws IllegalArgumentException {
        metabroker = new SDLType();
        metabroker.setConstraints(new ConstraintsType());
        metabroker.getConstraints().getMiddleware().add(
                DCITools.mbsdlMiddleware(dciType, gridVO, gridProxy));
    }

    // TODO: use this function for MPI integration 
//    public void addMPIType() throws IllegalArgumentException {
//        if (metabroker == null || metabroker.getConstraints() == null) {
//            throw new IllegalArgumentException("metabroker not initialized");
//        }
//
//        metabroker.getConstraints().getJobType().add(JobTypeEnumeration.MPI);
//    }
    /**
     * Create a new ResourceType section containing data about candidate hosts
     * and operating system.
     *
     * @param hostName hostname to add to the section
     * @param operatingSystem operating system for the JSDL
     */
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

    // TODO: use this function for MPI integration
//    public void addCPUsNumber(int numberCPUs) throws IllegalArgumentException {
//        if (numberCPUs <= 0 || resources == null) {
//            throw new IllegalArgumentException("CPUs number incorrect or "
//                    + "resource not initialized");
//        }
//
//        BoundaryType boundedValue = new BoundaryType();
//        boundedValue.setValue(numberCPUs);
//        RangeValueType valueCPU = new RangeValueType();
//        valueCPU.setUpperBoundedRange(boundedValue);
//        resources.setTotalCPUCount(valueCPU);
//    }
    /**
     * Add a walltime value for an execution. Note that this is not supported by
     * the DCI Bridge yet.
     *
     * @param value value of the walltime > 0
     * @throws IllegalArgumentException
     */
    public void addMaxWallTimeValue(int value) throws IllegalArgumentException {
        if (posixApplication == null || value <= 0) {
            throw new IllegalArgumentException("MaxWallTime incorrect or "
                    + "application not initialized");
        }

        LimitsType limit = new LimitsType();
        limit.setValue(BigInteger.valueOf(value));
        posixApplication.setWallTimeLimit(limit);
    }

    /**
     * Get a JSDL ready to be executed.
     *
     * @return activity ready for execution representing the JSDL
     * @throws JAXBException
     */
    public CreateActivityType getExecutableJSDL()
            throws JAXBException {
        JobDefinitionType jsdl = getCompleteJSDL();

        CreateActivityType activityJsdl = new CreateActivityType();
        activityJsdl.setActivityDocument(new ActivityDocumentType());
        activityJsdl.getActivityDocument().setJobDefinition(jsdl);

        return activityJsdl;
    }

    /**
     * Get a complete JSDL file with all modified information actualised.
     *
     * @return the JSDL updated
     */
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

    /**
     * Get the POSIXApplicationType section from a JSDL.
     *
     * @param anyObjects object corresponding to the ApplicationType.getAny()
     * @return POSIXApplicationType section or exception thrown.
     * @throws JAXBException
     */
    private POSIXApplicationType returnPOSIXApplicationType(List<Object> anyObjects)
            throws JAXBException {
        posixApplication = new POSIXApplicationType();

        for (Object object : anyObjects) {
            Element element = (Element) object;
            
            if (element != null && element.getNamespaceURI().
                    equals("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix")) {
                return DCITools.extractClass(element, posixApplication);
            }
        }

        throw new JAXBException("POSIXApplication not found");
    }

    /**
     * Get the ExtensionType section from a JSDL.
     *
     * @param anyObjects object corresponding to the jsdl.getAny()
     * @return ExtensionType section or exception thrown.
     * @throws JAXBException
     */
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

    /**
     * Get the SDLType section from a JSDL.
     *
     * @param anyObjects object corresponding to the jsdl.getAny()
     * @return SDLType section or exception thrown.
     * @throws JAXBException
     */
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
