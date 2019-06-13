package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.NameValueType;


public class PepXMLRunSummaryUtils {

    public static String getMSDataFile(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) throws Exception {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );

        for(NameValueType nvt : searchSummary.getParameter()) {

            if( nvt.getName().equals( "MS_data_file" ) ) {
                return nvt.getValueAttribute();
            }
        }

        throw new Exception( "Unable to get the MS_data_file for the search in pepXML file." );
    }

    public static String getDecoyFilter(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) throws Exception {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );

        for(NameValueType nvt : searchSummary.getParameter()) {

            if( nvt.getName().equals( "decoy_filter" ) ) {

                if( nvt.getValueAttribute() != null && !nvt.getValueAttribute().equals( "" ) ) {
                    return nvt.getValueAttribute();
                }
            }
        }

        // what we return if no decoy_filter could be found
        return null;
    }

    public static String get15NPrefix(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) throws Exception {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );

        for(NameValueType nvt : searchSummary.getParameter()) {

            if( nvt.getName().equals( "15N_filter" ) ) {

                if( nvt.getValueAttribute() != null && !nvt.getValueAttribute().equals( "" ) ) {
                    return nvt.getValueAttribute();
                }
            }
        }

        // what we return if no decoy_filter could be found
        return null;
    }

}
