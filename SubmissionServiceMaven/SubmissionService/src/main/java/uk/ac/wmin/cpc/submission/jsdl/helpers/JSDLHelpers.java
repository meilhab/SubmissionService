/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import java.io.IOException;
import org.ggf.schemas.jsdl._2005._11.jsdl.CreationFlagEnumeration;
import org.shiwa.repository.submission.interfaces.AbstractDeployment;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.GLite;
import org.shiwa.repository.submission.interfaces.Gt2;
import org.shiwa.repository.submission.interfaces.Gt4;
import org.shiwa.repository.submission.interfaces.ImplJSDL;
import org.shiwa.repository.submission.interfaces.Local;
import org.shiwa.repository.submission.interfaces.OnTheFly;
import org.shiwa.repository.submission.interfaces.PreDeploy;
import org.shiwa.repository.submission.interfaces.WorkflowEngineInstance;
import org.shiwa.repository.submission.service.NotFoundException;
import uk.ac.wmin.cpc.submission.storage.executables.ExecutablePreDeploy;
import uk.ac.wmin.cpc.submission.frontend.helpers.Configuration;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class JSDLHelpers {

    private static void modifyResourcesAndSDLType(
            WorkflowEngineInstance engineInstance, JSDLItem jsdl)
            throws NotFoundException, IllegalArgumentException {
        BeInstance middleware = engineInstance.getMiddlewareConfig();

        // TODO: add CPUs number here or after
        if (middleware instanceof Gt2) {
            MiddlewareConfig.configurationResource(
                    (Gt2) engineInstance.getMiddlewareConfig(), jsdl);
        } else if (middleware instanceof Gt4) {
            MiddlewareConfig.configurationResource(
                    (Gt4) engineInstance.getMiddlewareConfig(), jsdl);
        } else if (middleware instanceof Local) {
            MiddlewareConfig.configurationResource(
                    (Local) engineInstance.getMiddlewareConfig(), jsdl);
        } else if (middleware instanceof GLite) {
            MiddlewareConfig.configurationResource(
                    (GLite) engineInstance.getMiddlewareConfig(), jsdl);
        } else {
            throw new NotFoundException(
                    "No middleware configuration or not supported configuration "
                    + "detected", null);
        }
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

    public static String configureMiddleware(WorkflowEngineInstance instance,
            JSDLItem jsdl) throws NotFoundException, IllegalArgumentException {
        if (instance == null || instance.getMiddlewareConfig() == null
                || instance.getMiddlewareConfig().getIdBackend() == null
                || instance.getMiddlewareConfig().getIdBackend().getBackendName() == null) {
            throw new NotFoundException("Middleware not configured", null);
        }

        JSDLHelpers.modifyResourcesAndSDLType(instance, jsdl);

        return instance.getMiddlewareConfig().getIdBackend().getBackendName();
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
}
