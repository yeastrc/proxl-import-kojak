package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Collection;
import java.util.Map;

public class KojakResults {

    /**
     * The results of one or more Kojak run
     * @param kojakVersion
     * @param kojakResults A map keyed on he reportedpeptide string, pointing to a map keyed on pepXML file name
     *                     (e.g. foo.pep.xml) that points to a collection of PSMs from that kojak run
     */
    public KojakResults(String kojakVersion, Map<KojakReportedPeptide, Map<String, Map<Integer, KojakPSMResult>>> kojakResults) {
        this.kojakVersion = kojakVersion;
        this.kojakResults = kojakResults;
    }

    public String getKojakVersion() {
        return kojakVersion;
    }

    public  Map<KojakReportedPeptide, Map<String, Map<Integer, KojakPSMResult>>> getKojakResults() {
        return kojakResults;
    }

    private String kojakVersion;
    private Map<KojakReportedPeptide, Map<String, Map<Integer, KojakPSMResult>>> kojakResults;
}
