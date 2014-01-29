//package others;
//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//
//import dci.extension.ExtensionType;
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//import java.io.StringReader;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.namespace.QName;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.ws.wsaddressing.W3CEndpointReference;
//import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
//import uri.mbschedulingdescriptionlanguage.SDLType;
//
///**
// *
// * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
// */
//public class Tools {
//
//    private static Class[] classes = {
//        JobDefinitionType.class,
//        UserNameType.class,
//        GroupNameType.class,
//        FileNameType.class,
//        POSIXApplicationType.class,
//        SDLType.class,
//        ExtensionType.class
//    };
//
//    public static String getJSDLXML(JobDefinitionType pValue) throws Exception {
//        ByteArrayOutputStream res = new ByteArrayOutputStream();
//        JAXBContext jc = JAXBContext.newInstance(classes);
//        Marshaller msh = jc.createMarshaller();
//        msh.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
//        msh.setProperty("jaxb.schemaLocation",
//                "http://schemas.ggf.org/jsdl/2005/11/jsdl");
//        msh.setProperty("jaxb.encoding", "UTF-8");
//
//        JAXBElement jbx = wrap("http://schemas.ggf.org/jsdl/2005/11/jsdl",
//                "JobDefinition_Type", pValue);
//        msh.marshal(jbx, res);
//
//        return new String(res.toByteArray());
//    }
//
//    private static <T> JAXBElement<T> wrap(String ns, String tag, T o) {
//        QName qtag = new QName(ns, tag, "jsdl");
//        Class clazz = o.getClass();
//
//        JAXBElement jbe = new JAXBElement(qtag, clazz, o);
//        return jbe;
//    }
//
//    public static String getIDFromW3CEndPointReference(W3CEndpointReference endpointReference) {
//        if (endpointReference == null) {
//            return null;
//        }
//
//        int j0 = endpointReference.toString().indexOf("<job ") + 5;
//        j0 = endpointReference.toString().indexOf(">", j0) + 1;
//
//        int j1 = endpointReference.toString().indexOf("</job>");
//        return endpointReference.toString().substring(j0, j1);
//    }
//
//    public static JobDefinitionType readJSDLFromString(String pValue)
//            throws JAXBException, FileNotFoundException {
//        JAXBContext jc = JAXBContext.newInstance(classes);
//
//        Unmarshaller u = jc.createUnmarshaller();
//        JAXBElement obj = u.unmarshal(
//                new StreamSource(new StringReader(pValue)), JobDefinitionType.class);
//        JobDefinitionType jsdl = (JobDefinitionType) obj.getValue();
//
//        return jsdl;
//    }
//}
