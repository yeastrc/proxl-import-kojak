package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Collection;

public class KojakPSMResultBuilder {
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

    public KojakPSMResultBuilder setScanFile(String scanFile) {
        this.scanFile = scanFile;
        return this;
    }

    public KojakPSMResultBuilder setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
        return this;
    }

    public KojakPSMResultBuilder setCharge(int charge) {
        this.charge = charge;
        return this;
    }

    public KojakPSMResultBuilder setKojakScore(BigDecimal kojakScore) {
        this.kojakScore = kojakScore;
        return this;
    }

    public KojakPSMResultBuilder setDeltaScore(BigDecimal deltaScore) {
        this.deltaScore = deltaScore;
        return this;
    }

    public KojakPSMResultBuilder setEvalue(BigDecimal evalue) {
        this.evalue = evalue;
        return this;
    }

    public KojakPSMResultBuilder setPpmError(BigDecimal ppmError) {
        this.ppmError = ppmError;
        return this;
    }

    public KojakPSMResultBuilder setLinkerMass(BigDecimal linkerMass) {
        this.linkerMass = linkerMass;
        return this;
    }

    public KojakPSMResultBuilder setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
        return this;
    }

    public KojakPSMResultBuilder setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
        this.precursorNeutralMass = precursorNeutralMass;
        return this;
    }

    public KojakPSMResultBuilder setIonMatch(int ionMatch) {
        this.ionMatch = ionMatch;
        return this;
    }

    public KojakPSMResultBuilder setConsecutiveIonMatch(int consecutiveIonMatch) {
        this.consecutiveIonMatch = consecutiveIonMatch;
        return this;
    }

    public KojakPSMResultBuilder setPerPeptidePSMResults(Collection<KojakPerPeptidePSM> perPeptidePSMResults) {
        this.perPeptidePSMResults = perPeptidePSMResults;
        return this;
    }

    public KojakPSMResult createKojakPSMResult() {
        return new KojakPSMResult(scanFile, scanNumber, charge, kojakScore, deltaScore, evalue, ppmError, linkerMass, retentionTime, precursorNeutralMass, ionMatch, consecutiveIonMatch, perPeptidePSMResults);
    }
}