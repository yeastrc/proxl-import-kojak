package org.yeastrc.proxl.xml.kojak.objects;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KojakReportedPeptide {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KojakReportedPeptide that = (KojakReportedPeptide) o;
        return type == that.type &&
                kojakPeptides.equals(that.kojakPeptides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, kojakPeptides);
    }

    /**
     * Create a new KojakReportedPeptide. The order of peptide1 and peptide2 doesn't matter, hashCode
     * evaluation will correctly match identical reported peptides regardless of order.
     *
     * @param type
     * @param peptide1
     * @param peptide2
     */
    public KojakReportedPeptide(int type, KojakPeptide peptide1, KojakPeptide peptide2) {

        this.type = type;

        if( type == ConverterConstants.LINK_TYPE_CROSSLINK ) {

            final int i = peptide1.toString().compareTo(peptide2.toString());
            this.kojakPeptides = new ArrayList<>(2);

            if( i <= 0 ) {
                this.kojakPeptides.add( peptide1 );
                this.kojakPeptides.add( peptide2 );

            } else {

                this.kojakPeptides.add(peptide2);
                this.kojakPeptides.add(peptide1);
            }

        } else {

            this.kojakPeptides = new ArrayList<>( 1 );
            this.kojakPeptides.add( peptide1 );

        }

        this.kojakPeptides = Collections.unmodifiableList( this.kojakPeptides );
    }



    public String toString() {

        if( this.getType() == ConverterConstants.LINK_TYPE_UNLINKED || this.getType() == ConverterConstants.LINK_TYPE_LOOPLINK ) {
            return this.getKojakPeptides().get( 0 ).toString();

        } else if( this.getType() == ConverterConstants.LINK_TYPE_CROSSLINK ) {
            return this.getKojakPeptides().get(0).toString() + "-" + this.getKojakPeptides().get(1).toString();
        }

        return "Error: unknown peptide type";
    }

    public int getType() {
        return type;
    }

    public List<KojakPeptide> getKojakPeptides() {
        return kojakPeptides;
    }

    private int type;
    private List<KojakPeptide> kojakPeptides;
}
