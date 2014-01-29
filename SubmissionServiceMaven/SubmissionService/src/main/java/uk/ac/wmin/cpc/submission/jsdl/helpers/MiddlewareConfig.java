/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeEnumeration;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.GLite;
import org.shiwa.repository.submission.interfaces.Gt2;
import org.shiwa.repository.submission.interfaces.Gt4;
import org.shiwa.repository.submission.interfaces.Local;
import uk.ac.wmin.cpc.submission.jsdl.JSDLItem;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class MiddlewareConfig {

    public static void modifySDLType(BeInstance middleware, JSDLItem jsdl)
            throws IllegalArgumentException {
        jsdl.createSDLType(middleware.getIdBackend().getBackendName(),
                middleware.getResource(), "");
    }

    public static void configurationResource(Gt2 middleware, JSDLItem jsdl) {
        String hostName = middleware.getSite() + "/" + middleware.getJobManager();
        jsdl.createResource(hostName,
                OperatingSystemTypeEnumeration.fromValue(middleware.getIdOS().getName()));
    }

    public static void configurationResource(Gt4 middleware, JSDLItem jsdl) {
        String hostName = middleware.getSite() + "/" + middleware.getJobManager();
        jsdl.createResource(hostName,
                OperatingSystemTypeEnumeration.fromValue(middleware.getIdOS().getName()));
    }

    public static void configurationResource(Local middleware, JSDLItem jsdl) {
        jsdl.createResource("",
                OperatingSystemTypeEnumeration.fromValue(middleware.getIdOS().getName()));
    }

    public static void configurationResource(GLite middleware, JSDLItem jsdl) {
        jsdl.createResource("",
                OperatingSystemTypeEnumeration.fromValue(middleware.getIdOS().getName()));
    }
}
