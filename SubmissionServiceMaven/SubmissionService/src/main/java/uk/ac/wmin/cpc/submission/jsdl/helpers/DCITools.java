/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.jsdl.helpers;

import dci.extension.ExtensionType;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import jsdl.extension.cloud.CloudResourceType;
import jsdl.extension.cloud.CloudResourcesType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.LimitsType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
import org.shiwa.repository.submission.interfaces.BeInstance;
import org.shiwa.repository.submission.interfaces.GLite;
import org.shiwa.repository.submission.interfaces.Gt2;
import org.shiwa.repository.submission.interfaces.Gt4;
import org.shiwa.repository.submission.interfaces.Local;
import org.shiwa.repository.submission.interfaces.Pbs;
import org.w3c.dom.Element;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;
import uri.mbschedulingdescriptionlanguage.MiddlewareType;
import uri.mbschedulingdescriptionlanguage.MyProxyType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class DCITools {

    private static final Class[] classes = new Class[]{
        JobDefinitionType.class,
        UserNameType.class,
        GroupNameType.class,
        FileNameType.class,
        ArgumentType.class,
        LimitsType.class,
        POSIXApplicationType.class,
        ExtensionType.class,
        SDLType.class,
        CloudResourcesType.class,
        CloudResourceType.class};

    private static DCINameEnumeration getDCINameFromString(String dciName)
            throws IllegalArgumentException {
        if (dciName == null) {
            throw new IllegalArgumentException("Unknown dci name detected ("
                    + dciName + ")");
        }

        DCINameEnumeration name = DCINameEnumeration.valueOf(dciName.toLowerCase());
        if (name == null) {
            throw new IllegalArgumentException("Unknown dci name detected ("
                    + dciName + ")");
        }

        return name;
    }

    public static String getDCIName(BeInstance middleware) throws IllegalArgumentException {
        if (middleware == null) {
            throw new IllegalArgumentException("Unknown middleware detected");
        }

        if (middleware instanceof Gt2) {
            return DCINameEnumeration.GT_2.value();
        } else if (middleware instanceof Gt4) {
            return DCINameEnumeration.GT_4.value();
        } else if (middleware instanceof Local) {
            return DCINameEnumeration.LOCAL.value();
        } else if (middleware instanceof GLite) {
            return DCINameEnumeration.GLITE.value();
        } else if (middleware instanceof Pbs) {
            return DCINameEnumeration.PBS.value();
        } else {
            throw new IllegalArgumentException("Middleware not supported detected");
        }
    }

    public static MiddlewareType mbsdlMiddleware(String pType, String pVO,
            String pMyProxy) throws IllegalArgumentException {
        MiddlewareType middleware = new MiddlewareType();
        middleware.setDCIName(getDCINameFromString(pType));
        middleware.setManagedResource(pVO);

        MyProxyType myProxyServer = new MyProxyType();
        if (pMyProxy != null && !pMyProxy.isEmpty()) {
            myProxyServer.setServerName(pMyProxy);
        }
        middleware.setMyProxy(myProxyServer);

        return middleware;
    }

    public static String getJSDLXML(JobDefinitionType pValue) throws JAXBException {
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        JAXBContext jc = JAXBContext.newInstance(classes);
        Marshaller msh = jc.createMarshaller();
        msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        msh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schemas.ggf.org/jsdl/2005/11/jsdl");
        msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        JAXBElement<JobDefinitionType> jbx =
                wrap("http://schemas.ggf.org/jsdl/2005/11/jsdl", "JobDefinition_Type", pValue);
        msh.marshal(jbx, res);

        return new String(res.toByteArray());
    }

    private static <T> JAXBElement<T> wrap(String ns, String tag, T o) {
        QName qtag = new QName(ns, tag, "jsdl");
        Class clazz = o.getClass();
        @SuppressWarnings("unchecked")
        JAXBElement jbe = new JAXBElement(qtag, clazz, o);
        return jbe;
    }

    public static String getIDFromW3CEndPointReference(
            W3CEndpointReference endpointReference) {
        if (endpointReference == null) {
            return null;
        }

        int j0 = endpointReference.toString().indexOf("<job ") + 5;
        j0 = endpointReference.toString().indexOf(">", j0) + 1;

        int j1 = endpointReference.toString().indexOf("</job>");
        return endpointReference.toString().substring(j0, j1);
    }

    public static JobDefinitionType readJSDLFromString(String pValue)
            throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(classes);

        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<JobDefinitionType> obj = u.unmarshal(
                new StreamSource(new StringReader(pValue)), JobDefinitionType.class);
        JobDefinitionType jsdl = (JobDefinitionType) obj.getValue();

        return jsdl;
    }

    public static <T> T extractClass(Element element, T eClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(classes);
        Unmarshaller unMarshaller = context.createUnmarshaller();
        return (T) unMarshaller.unmarshal(element, eClass.getClass()).getValue();
    }
}
