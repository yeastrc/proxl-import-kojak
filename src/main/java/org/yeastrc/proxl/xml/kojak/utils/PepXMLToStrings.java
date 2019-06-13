package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;

public class PepXMLToStrings {

    public static String searchHitToString(SearchHit searchHit ) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(SearchHit.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // To format XML
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        //If we DO NOT have JAXB annotated class
        JAXBElement<SearchHit> jaxbElement =
                new JAXBElement<>( new QName("", "employee"),
                        SearchHit.class,
                        searchHit);

        //Print XML String to Console
        StringWriter sw = new StringWriter();

        jaxbMarshaller.marshal(jaxbElement, sw);

        return sw.toString();
    }
}
