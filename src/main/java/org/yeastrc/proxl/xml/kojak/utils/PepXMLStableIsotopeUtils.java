package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

public class PepXMLStableIsotopeUtils {

    public static boolean isLinkedPeptide15NLabeled(LinkedPeptide linkedPeptide, String n15Prefix ) {

        if( n15Prefix == null || n15Prefix.equals( "" ) ) {
            return false;
        }

        if( linkedPeptide.getProtein() != null && linkedPeptide.getProtein().startsWith( n15Prefix + "-" ) ) {
            return true;
        }

        return false;
    }

    public static boolean isSearchHit15NLabeled(SearchHit searchHit, String n15Prefix ) {

        if( n15Prefix == null || n15Prefix.equals( "" ) ) {
            return false;
        }

        if( searchHit.getProtein() != null && searchHit.getProtein().startsWith( n15Prefix + "-" ) ) {
            return true;
        }

        return false;
    }

}
