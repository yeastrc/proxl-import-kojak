package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Map;

public class PercolatorResults {

	public PercolatorResults(String percolatorVersion, Map<String, PercolatorPeptideData> reportedPeptideResults) {
		this.percolatorVersion = percolatorVersion;
		this.reportedPeptideResults = reportedPeptideResults;
	}

	public String getPercolatorVersion() {
		return percolatorVersion;
	}

	public Map<String, PercolatorPeptideData> getReportedPeptideResults() {
		return reportedPeptideResults;
	}

	private String percolatorVersion;
	private Map<String, PercolatorPeptideData> reportedPeptideResults;
	
}
