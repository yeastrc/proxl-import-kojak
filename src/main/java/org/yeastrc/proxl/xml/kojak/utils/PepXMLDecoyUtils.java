package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;

public class PepXMLDecoyUtils {
    /**
     * Return true if any of the supplied names contain one of the decoy strings
     * @param decoyString
     * @param name
     * @return
     */
    public static boolean isDecoyName( String decoyString, String name ) {

        if( name.contains( decoyString ) )
            return true;

        return false;
    }

    /**
     * Check whether the searchHit is a decoy. For crosslinks, this is a decoy only if both linked peptides only
     * match to decoy proteins. For unlinked and looplinks, this is a decoy only if all associated protein names
     * are decoy names.
     *
     * @param decoyString
     * @param searchHit
     * @return
     * @throws Exception
     */
    public static boolean isDecoy( String decoyString, MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit searchHit ) throws Exception {

        // testing crosslinks (is more involved)
        if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {

            // if either of the linked peptides are decoy hits, the xlink is a decoy
            if( isDecoy( decoyString, searchHit.getXlink().getLinkedPeptide().get( 0 ) ) ||
                isDecoy( decoyString, searchHit.getXlink().getLinkedPeptide().get( 1 ) ) ) {

                    return true;
            }

            return false;
        }

        if( searchHit.getProtein() == null ) {
            throw new Exception( "Got null for protein on search hit?" );
        }

        // if we got here, the type is either unlinked or looplinks, the test is the same
        if( !isDecoyName( decoyString, searchHit.getProtein() ) ) {
            return false;
        }


        // if any of the alternative proteins listed are not decoy proteins, this is not a decoy
        if( searchHit.getAlternativeProtein() != null && searchHit.getAlternativeProtein().size() > 0 ) {
            for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {
                if( !isDecoyName( decoyString, ap.getProtein() ) ) {
                    return false;
                }
            }
        }

        return true;	// if we get here, all names for all associated proteins contained the decoy identifier string
    }

    /**
     * Check whether the supplied linked peptide is a decoy.
     *
     * @param decoyString
     * @param linkedPeptide
     * @return
     * @throws Exception
     */
    private static boolean isDecoy( String decoyString, MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide linkedPeptide ) throws Exception {

        if( !isDecoyName( decoyString, linkedPeptide.getProtein() ) ) {
            return false;
        }

        if( linkedPeptide.getAlternativeProtein() != null && linkedPeptide.getAlternativeProtein().size() > 0 ) {
            for( AltProteinDataType ap : linkedPeptide.getAlternativeProtein() ) {
                if( !isDecoyName( decoyString, ap.getProtein() ) ) {
                    return false;
                }
            }
        }

        return true;
    }

}
