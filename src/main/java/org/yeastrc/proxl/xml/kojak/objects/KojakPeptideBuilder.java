package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class KojakPeptideBuilder {
    private String sequence;
    private Map<Integer, Collection<KojakDynamicMod>> modifications;
    private KojakDynamicMod nTerminalMod;
    private KojakDynamicMod cTerminalMod;
    private Integer position1;
    private Integer position2;
    private String n15Label;

    public KojakPeptideBuilder setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }

    public KojakPeptideBuilder setModifications(Map<Integer, Collection<KojakDynamicMod>> modifications) {
        this.modifications = modifications;
        return this;
    }

    public KojakPeptideBuilder setnTerminalMod(KojakDynamicMod nTerminalMod) {
        this.nTerminalMod = nTerminalMod;
        return this;
    }

    public KojakPeptideBuilder setcTerminalMod(KojakDynamicMod cTerminalMod) {
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

    public KojakPeptideBuilder setN15Label(String n15Label) {
        this.n15Label = n15Label;
        return this;
    }

    public KojakPeptide createKojakPeptide() {
        return new KojakPeptide(sequence, modifications, nTerminalMod, cTerminalMod, position1, position2, n15Label);
    }
}