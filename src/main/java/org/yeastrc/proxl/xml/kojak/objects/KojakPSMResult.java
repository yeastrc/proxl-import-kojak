package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Collection;

public class KojakPSMResult {

	private String scanFile;
	private int scanNumber;
	private int charge;
	private BigDecimal kojakScore;
	private BigDecimal deltaScore;
	private BigDecimal evalue;
	private BigDecimal ppmError;
	private BigDecimal linkerMass;
	private BigDecimal retentionTime;
	private BigDecimal precursorNeutralMass;
	private int ionMatch;
	private int consecutiveIonMatch;
	private Collection<KojakPerPeptidePSM> perPeptidePSMResults;

	public KojakPSMResult(String scanFile, int scanNumber, int charge, BigDecimal kojakScore, BigDecimal deltaScore, BigDecimal evalue, BigDecimal ppmError, BigDecimal linkerMass, BigDecimal retentionTime, BigDecimal precursorNeutralMass, int ionMatch, int consecutiveIonMatch, Collection<KojakPerPeptidePSM> perPeptidePSMResults) {
		this.scanFile = scanFile;
		this.scanNumber = scanNumber;
		this.charge = charge;
		this.kojakScore = kojakScore;
		this.deltaScore = deltaScore;
		this.evalue = evalue;
		this.ppmError = ppmError;
		this.linkerMass = linkerMass;
		this.retentionTime = retentionTime;
		this.precursorNeutralMass = precursorNeutralMass;
		this.ionMatch = ionMatch;
		this.consecutiveIonMatch = consecutiveIonMatch;
		this.perPeptidePSMResults = perPeptidePSMResults;
	}

	public String getScanFile() {
		return scanFile;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public int getCharge() {
		return charge;
	}

	public BigDecimal getKojakScore() {
		return kojakScore;
	}

	public BigDecimal getDeltaScore() {
		return deltaScore;
	}

	public BigDecimal getEvalue() {
		return evalue;
	}

	public BigDecimal getPpmError() {
		return ppmError;
	}

	public BigDecimal getLinkerMass() {
		return linkerMass;
	}

	public BigDecimal getRetentionTime() {
		return retentionTime;
	}

	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}

	public int getIonMatch() {
		return ionMatch;
	}

	public int getConsecutiveIonMatch() {
		return consecutiveIonMatch;
	}

	public Collection<KojakPerPeptidePSM> getPerPeptidePSMResults() {
		return perPeptidePSMResults;
	}
}
