package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Objects;

public class PepXMLModDefinition {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PepXMLModDefinition that = (PepXMLModDefinition) o;
        return massDiff.equals(that.massDiff) &&
                totalMass.equals(that.totalMass) &&
                residue.equals(that.residue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(massDiff, totalMass, residue);
    }

    private BigDecimal massDiff;
    private BigDecimal totalMass;
    private String residue;

    public BigDecimal getMassDiff() {
        return massDiff;
    }

    public BigDecimal getTotalMass() {
        return totalMass;
    }

    public String getResidue() {
        return residue;
    }

    public PepXMLModDefinition(BigDecimal massDiff, BigDecimal totalMass, String residue) {
        this.massDiff = massDiff;
        this.totalMass = totalMass;
        this.residue = residue;
    }

    @Override
    public String toString() {
        return "PepXMLModDefinition{" +
                "massDiff=" + massDiff +
                ", totalMass=" + totalMass +
                ", residue='" + residue + '\'' +
                '}';
    }
}

