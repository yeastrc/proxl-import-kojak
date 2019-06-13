package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Collection;
import java.util.Map;

public class KojakResults {

    public KojakResults(String kojakVersion, Map<KojakReportedPeptide, Collection<KojakPSMResult>> kojakResults) {
        this.kojakVersion = kojakVersion;
        this.kojakResults = kojakResults;
    }

    public String getKojakVersion() {
        return kojakVersion;
    }

    public Map<KojakReportedPeptide, Collection<KojakPSMResult>> getKojakResults() {
        return kojakResults;
    }

    private String kojakVersion;
    private Map<KojakReportedPeptide, Collection<KojakPSMResult>> kojakResults;
}
