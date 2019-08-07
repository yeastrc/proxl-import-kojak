package org.yeastrc.proxl.xml.kojak.utils;

import org.yeastrc.proxl.xml.kojak.objects.KojakReportedPeptide;
import org.yeastrc.proxl.xml.kojak.objects.KojakResults;

public class KojakParsingUtils {

    /**
     *
     * @param reportedPeptide
     * @param kojakResults
     * @return
     */
    public static KojakReportedPeptide getKojakReportedPeptideForString(String reportedPeptide, KojakResults kojakResults ) {

        for( KojakReportedPeptide kojakPeptide : kojakResults.getKojakResults().keySet()) {
            if( kojakPeptide.toString().equals( reportedPeptide ) ) {
                return kojakPeptide;
            }
        }

        return null;
    }

}
