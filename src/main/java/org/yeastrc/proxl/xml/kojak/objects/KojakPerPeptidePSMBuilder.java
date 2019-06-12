package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;

public class KojakPerPeptidePSMBuilder {
    private BigDecimal kojakScore;
    private int rank;
    private BigDecimal evalue;
    private int ionMatch;
    private int consecutiveIonMatch;
    private KojakPeptide linkedPeptide;

    public KojakPerPeptidePSMBuilder setKojakScore(BigDecimal kojakScore) {
        this.kojakScore = kojakScore;
        return this;
    }

    public KojakPerPeptidePSMBuilder setRank(int rank) {
        this.rank = rank;
        return this;
    }

    public KojakPerPeptidePSMBuilder setEvalue(BigDecimal evalue) {
        this.evalue = evalue;
        return this;
    }

    public KojakPerPeptidePSMBuilder setIonMatch(int ionMatch) {
        this.ionMatch = ionMatch;
        return this;
    }

    public KojakPerPeptidePSMBuilder setConsecutiveIonMatch(int consecutiveIonMatch) {
        this.consecutiveIonMatch = consecutiveIonMatch;
        return this;
    }

    public KojakPerPeptidePSMBuilder setLinkedPeptide(KojakPeptide linkedPeptide) {
        this.linkedPeptide = linkedPeptide;
        return this;
    }

    public KojakPerPeptidePSM createKojakPerPeptidePSM() {
        return new KojakPerPeptidePSM(kojakScore, rank, evalue, ionMatch, consecutiveIonMatch, linkedPeptide);
    }
}