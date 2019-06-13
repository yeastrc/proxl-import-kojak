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

    public BigDecimal getKojakScore() {
        return kojakScore;
    }

    public int getRank() {
        return rank;
    }

    public BigDecimal getEvalue() {
        return evalue;
    }

    public int getIonMatch() {
        return ionMatch;
    }

    public int getConsecutiveIonMatch() {
        return consecutiveIonMatch;
    }

    public KojakPeptide getLinkedPeptide() {
        return linkedPeptide;
    }
}
