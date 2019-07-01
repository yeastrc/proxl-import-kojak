package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Map;

public class PercolatorPeptideData {

	private PercolatorPeptideScores percolatorPeptideScores;
	private Map<String, Map<Integer, PercolatorPSM>> percolatorPSMs;

	public PercolatorPeptideData(PercolatorPeptideScores percolatorPeptideScores, Map<String, Map<Integer, PercolatorPSM>> percolatorPSMs) {
		this.percolatorPeptideScores = percolatorPeptideScores;
		this.percolatorPSMs = percolatorPSMs;
	}

	public PercolatorPeptideScores getPercolatorPeptideScores() {
		return percolatorPeptideScores;
	}

	public Map<String, Map<Integer, PercolatorPSM>> getPercolatorPSMs() {
		return percolatorPSMs;
	}
}
