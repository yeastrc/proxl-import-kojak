package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class KojakPeptideBuilder {
    private String sequence;
    private Map<Integer, Collection<BigDecimal>> modifications;
    private BigDecimal nTerminalMod;
    private BigDecimal cTerminalMod;
    private Integer position1;
    private Integer position2;

    public KojakPeptideBuilder setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }

    public KojakPeptideBuilder setModifications(Map<Integer, Collection<BigDecimal>> modifications) {
        this.modifications = modifications;
        return this;
    }

    public KojakPeptideBuilder setnTerminalMod(BigDecimal nTerminalMod) {
        this.nTerminalMod = nTerminalMod;
        return this;
    }

    public KojakPeptideBuilder setcTerminalMod(BigDecimal cTerminalMod) {
        this.cTerminalMod = cTerminalMod;
        return this;
    }

    public KojakPeptideBuilder setPosition1(Integer position1) {
        this.position1 = position1;
        return this;
    }

    public KojakPeptideBuilder setPosition2(Integer position2) {
        this.position2 = position2;
        return this;
    }

    public KojakPeptide createKojakPeptide() {
        return new KojakPeptide(sequence, modifications, nTerminalMod, cTerminalMod, position1, position2);
    }
}