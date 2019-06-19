package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.Objects;

public class KojakDynamicMod {

    @Override
    public String toString() {
        return "KojakDynamicMod{" +
                "massDiff=" + massDiff +
                ", isMonolink=" + isMonolink +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KojakDynamicMod that = (KojakDynamicMod) o;
        return isMonolink == that.isMonolink &&
                massDiff.equals(that.massDiff);
    }

    @Override
    public int hashCode() {
        return Objects.hash(massDiff, isMonolink);
    }

    public KojakDynamicMod(BigDecimal massDiff, boolean isMonolink) {
        this.massDiff = massDiff;
        this.isMonolink = isMonolink;
    }

    public BigDecimal getMassDiff() {
        return massDiff;
    }

    public boolean isMonolink() {
        return isMonolink;
    }

    private BigDecimal massDiff;
    private boolean isMonolink;

}
