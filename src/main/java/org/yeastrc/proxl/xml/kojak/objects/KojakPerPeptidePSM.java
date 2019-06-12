package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;

public class KojakPerPeptidePSM {

    private BigDecimal kojakScore;
    private int rank;
    private BigDecimal evalue;
    private int ionMatch;
    private int consecutiveIonMatch;
    private KojakPeptide linkedPeptide;

    public KojakPerPeptidePSM(BigDecimal kojakScore, int rank, BigDecimal evalue, int ionMatch, int consecutiveIonMatch, KojakPeptide linkedPeptide) {
        this.kojakScore = kojakScore;
        this.rank = rank;
        this.evalue = evalue;
        this.ionMatch = ionMatch;
        this.consecutiveIonMatch = consecutiveIonMatch;
        this.linkedPeptide = linkedPeptide;
    }

    public void setKojakScore(BigDecimal kojakScore) {
        this.kojakScore = kojakScore;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setEvalue(BigDecimal evalue) {
        this.evalue = evalue;
    }

    public void setIonMatch(int ionMatch) {
        this.ionMatch = ionMatch;
    }

    public void setConsecutiveIonMatch(int consecutiveIonMatch) {
        this.consecutiveIonMatch = consecutiveIonMatch;
    }

    public void setLinkedPeptide(KojakPeptide linkedPeptide) {
        this.linkedPeptide = linkedPeptide;
    }
}
