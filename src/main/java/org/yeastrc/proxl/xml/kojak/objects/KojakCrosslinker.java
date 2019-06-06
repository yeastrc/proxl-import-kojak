package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;

public class KojakCrosslinker {

    private String name;
    private BigDecimal massMod;
    private LinkableEnds linkableEnds;

    public KojakCrosslinker(String name, BigDecimal massMod, LinkableEnds linkableEnds) {
        this.name = name;
        this.massMod = massMod;
        this.linkableEnds = linkableEnds;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMassMod() {
        return massMod;
    }

    public LinkableEnds getLinkableEnds() {
        return linkableEnds;
    }
}
