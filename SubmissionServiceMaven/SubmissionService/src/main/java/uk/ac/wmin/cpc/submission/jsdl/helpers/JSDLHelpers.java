/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl._2005._11.jsdl.CreationFlagEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.shiwa.repository.submission.interfaces.AbstractDeployment;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.ExecutionNode;
import org.shiwa.repository.submission.interfaces.ImplJSDL;
import org.shiwa.repository.submission.interfaces.OnTheFly;
import org.shiwa.repository.submission.interfaces.Param;
import org.shiwa.repository.submission.interfaces.Parameter;
import org.shiwa.repository.submission.interfaces.PreDeploy;
import org.shiwa.repository.submission.interfaces.WorkflowEngineInstance;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.storage.executables.ExecutablePreDeploy;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.frontend.servlets.LoggerServlet;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLHelpers {

    private static Logger logger = LoggerServlet.getMainLogger();

    private static void modifyResourcesAndSDLType(
            WorkflowEngineInstance engineInstance, JSDLItem jsdl)
            throws NotFoundException, IllegalArgumentException {
        BeInstance middleware = engineInstance.getMiddlewareConfig();

        // TODO: add CPUs number here or after
        MiddlewareConfig.configurationResource(middleware, jsdl);
        // TODO: add job type MPI here
        MiddlewareConfig.modifySDLType(middleware, jsdl);
    }

    private static boolean checkImplementation(ImplJSDL implementation) {
        return implementation != null && implementation.getExecutionNode() != null
                && implementation.getWorkflowEngineName() != null
                && implementation.getWorkflowEngineVersion() != null;
    }

    private static boolean checkWorkflowEngineInstance(WorkflowEngineInstance instance) {
        return instance != null && instance.getDeploymentConfig() != null
                && instance.getMiddlewareConfig() != null
                && instance.getWorkflowEngineName() != null
                && instance.getWorkflowEngineVersion() != null;
    }

    public static void checkRepositoryData(ImplJSDL implementation,
            WorkflowEngineInstance engineInstance)
            throws IllegalArgumentException {
        if (!checkImplementation(implementation)
                || !checkWorkflowEngineInstance(engineInstance)) {
            throw new IllegalArgumentException("Implementation or "
                    + "workflow engine instance incorreclty configured");
        }

        if (!implementation.getWorkflowEngineName().
                equals(engineInstance.getWorkflowEngineName())
                || !implementation.getWorkflowEngineVersion().
                equals(engineInstance.getWorkflowEngineVersion())) {
            throw new IllegalArgumentException("Incompatibility between "
                    + "implementation needed workflow engine instance ("
                    + implementation.getWorkflowEngineName() + " "
                    + implementation.getWorkflowEngineVersion() + ") and "
                    + "workflow engine instance requested ("
                    + engineInstance.getWorkflowEngineName() + " "
                    + engineInstance.getWorkflowEngineVersion() + ")");
        }
    }

    public static void configureMiddleware(WorkflowEngineInstance instance,
            JSDLItem jsdl) throws NotFoundException, IllegalArgumentException {
        if (instance == null || instance.getMiddlewareConfig() == null
                || instance.getMiddlewareConfig().getName() == null
                || instance.getMiddlewareConfig().getBackend() == null) {
            throw new NotFoundException("Middleware not configured", null);
        }

        JSDLHelpers.modifyResourcesAndSDLType(instance, jsdl);
    }

    public static void configureExecutable(ImplJSDL implementation,
            WorkflowEngineInstance instance, JSDLItem jsdl, String userID)
            throws IOException {
        AbstractDeployment deployment = instance.getDeploymentConfig();

        if (deployment instanceof PreDeploy) {
            PreDeploy preDep = (PreDeploy) deployment;
            String executableName = Configuration.getExecutableName();

            ExecutablePreDeploy executable = new ExecutablePreDeploy(
                    userID, "Application", executableName);
            executable.createFile();
            executable.writeIntoFile("echo \"" + preDep.getShellPathEndPoint()
                    + " $@\"");
            executable.writeIntoFile("\n");
            executable.writeIntoFile(preDep.getShellPathEndPoint() + " $@");

            jsdl.setApplicationExecutable(executableName);
            jsdl.addNewInputFile(executableName,
                    CreationFlagEnumeration.OVERWRITE, true, executable.getURL());
        } else {
            OnTheFly onTheFly = (OnTheFly) deployment;
            jsdl.setApplicationExecutable(onTheFly.getShellName());
            jsdl.addNewInputFile(onTheFly.getShellName(),
                    CreationFlagEnumeration.OVERWRITE, true, onTheFly.getShellPath());
            jsdl.addNewInputFile(onTheFly.getZipName(),
                    CreationFlagEnumeration.OVERWRITE, true, onTheFly.getZipPath());
        }

        jsdl.addNewInputFile(implementation.getDefinitionFileName(),
                CreationFlagEnumeration.OVERWRITE, true,
                implementation.getDefinitionFilePath());
    }

    public static void treatParameters(ImplJSDL implementation,
            Parameter[] paramsNonFixed, JSDLItem jsdl, JSDLExtractor extractor)
            throws IllegalArgumentException {
        logger.info("Parameters are processing for " + implementation.getAppName());
        List<ArgumentType> nonFixedJSDL = jsdl.getApplicationArguments();

        ExecutionNode executionNode = implementation.getExecutionNode();
        List<Param> allInputs = executionNode.getListInputs();
        List<Param> allOutputs = executionNode.getListOutputs();
        List<Param> allParameters = new ArrayList<>();
        allParameters.addAll(allInputs);
        allParameters.addAll(allOutputs);

        List<String> argumentsInputs = new ArrayList<>();
        List<String> argumentsOutputs = new ArrayList<>();

        String portalURL = extractor.getStorageURL();

        for (Param parameter : allParameters) {
            if (parameter.isInput()) {
                treatInputs(parameter, argumentsInputs, paramsNonFixed, nonFixedJSDL, jsdl);
            } else {
                treatOutputs(parameter, argumentsOutputs, portalURL, jsdl);
            }
        }

        treatArguments(jsdl, argumentsInputs, argumentsOutputs);
    }

    private static void treatInputs(Param parameter, List<String> argumentsInputs,
            Parameter[] paramsNonFixed, List<ArgumentType> nonFixedJSDL, JSDLItem jsdl)
            throws IllegalArgumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("Input being processed: " + parameter.getTitle());
            logger.debug("Is file? " + parameter.isFile());
            logger.debug("Is fixed? " + parameter.isFixed());
            logger.debug("Is CmdLine? " + parameter.isCmdLine());
        }

        if (parameter.isFile()) {
            if (parameter.isFixed()) {
                // file fixed
                jsdl.addNewInputFile(parameter.getFileName(),
                        CreationFlagEnumeration.OVERWRITE, true,
                        parameter.getDefaultValue());
            } else {
                // file non-fixed
                for (DataStagingType data : jsdl.getInputFiles()) {
                    if (data.getFileName().equals(parameter.getFileName())) {
                        if (data.getSource().getURI().equals("Default")) {
                            data.getSource().setURI(parameter.getDefaultValue());
                        }
                        break;
                    }
                }
            }

            if (parameter.isCmdLine()) {
                // file cmdLine
                String cmdLine = parameter.getPrefixCmd() + " "
                        + parameter.getFileName();
                argumentsInputs.add(cmdLine.trim());
            }
        } else {
            if (parameter.isFixed() && parameter.isCmdLine()) {
                // non-file fixed
                argumentsInputs.add((parameter.getPrefixCmd() + " "
                        + parameter.getDefaultValue()).trim());
            } else if (!parameter.isFixed() && parameter.isCmdLine()) {
                // non-file non-fixed
                String cmdLine = parameter.getPrefixCmd() + " "
                        + getValueArgument(paramsNonFixed, nonFixedJSDL,
                        parameter);
                argumentsInputs.add(cmdLine.trim());
            }
        }
    }

    private static void treatOutputs(Param parameter, List<String> argumentsOutputs,
            String portalURL, JSDLItem jsdl) {
        if (logger.isDebugEnabled()) {
            logger.debug("Output being processed: " + parameter.getTitle());
            logger.debug("Is file? " + parameter.isFile());
            logger.debug("Is fixed? " + parameter.isFixed());
            logger.debug("Is CmdLine? " + parameter.isCmdLine());
        }

        if (parameter.isFile()) {
            if (parameter.isFixed()) {
                // file fixed
                jsdl.addNewOutputFile(parameter.getFileName(),
                        Configuration.getFileSystemName(),
                        CreationFlagEnumeration.OVERWRITE, true, portalURL);
            } // file non-fixed already done

            if (parameter.isCmdLine()) {
                // file cmdLine
                String cmdLine = parameter.getPrefixCmd() + " "
                        + parameter.getFileName();
                argumentsOutputs.add(cmdLine.trim());
            }
        } // not taking non file
    }

    private static String getValueArgument(Parameter[] paramsNFImpl,
            List<ArgumentType> paramsNFJSDL, Param param)
            throws IllegalArgumentException {
        if (paramsNFImpl.length != paramsNFJSDL.size()) {
            throw new IllegalArgumentException("Argument problem when "
                    + "modifying the JSDL");
        }

        for (int i = 0; i < paramsNFImpl.length; i++) {
            String[] dataNFImpl = ((String) paramsNFImpl[i].getValue()).split(",");
            if (dataNFImpl.length >= 7) {
                if (dataNFImpl[0].equals(param.getTitle())) {
                    return paramsNFJSDL.get(i).getValue();
                }
            }
        }

        throw new IllegalArgumentException("Argument not found, problem between"
                + " the repository data and the JSDL");
    }

    private static void treatArguments(JSDLItem jsdl,
            List<String> argumentsInputs, List<String> argumentsOutputs) {
        logger.info("New Arguments generated for POSIXApplication JSDL");
        jsdl.getApplicationArguments().clear();

        for (String argument : argumentsInputs) {
            if (logger.isDebugEnabled()) {
                logger.debug("(" + argument + ") -> added to list");
            }
            jsdl.setApplicationArgument(argument);
        }
        for (String argument : argumentsOutputs) {
            if (logger.isDebugEnabled()) {
                logger.debug("(" + argument + ") -> added to list");
            }
            jsdl.setApplicationArgument(argument);
        }
    }

    public static void configureDefinition(WorkflowEngineInstance engineInstance,
            ImplJSDL implementation, JSDLItem jsdl) {

        ArgumentType argumentDefinition = new ArgumentType();
        String cmdLine = engineInstance.getPrefixWorkflow()
                + " " + implementation.getDefinitionFileName();
        argumentDefinition.setValue(cmdLine.trim());
        jsdl.getApplicationArguments().add(0, argumentDefinition);
    }

    public static void configureMaxWallTime(ImplJSDL implJSDL, JSDLItem jsdl) {
        try {
            jsdl.addMaxWallTimeValue(implJSDL.getExecutionNode().getMaxWallTime());
        } catch (Exception ex) {
            logger.warn("MaxWallTime won't be added: " + ex.getMessage());
        }
    }
}
