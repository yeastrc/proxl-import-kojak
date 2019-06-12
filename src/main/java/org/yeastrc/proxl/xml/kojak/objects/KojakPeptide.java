package org.yeastrc.proxl.xml.kojak.objects;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class KojakPeptide {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KojakPeptide that = (KojakPeptide) o;
        return sequence.equals(that.sequence) &&
                Objects.equals(modifications, that.modifications) &&
                Objects.equals(nTerminalMod, that.nTerminalMod) &&
                Objects.equals(cTerminalMod, that.cTerminalMod) &&
                Objects.equals(linkedPositions, that.linkedPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, modifications, nTerminalMod, cTerminalMod, linkedPositions);
    }

    /**
     * Get the string representation of this peptide that includes mods, in the form of:
     * n[16.04]PEP[12.29,15.99]TI[12.2932]DEc[111.22](22)
     */
    public String toString() {

        String str = "";

        for( int i = 1; i <= this.getSequence().length(); i++ ) {
            String r = String.valueOf( this.getSequence().charAt( i - 1 ) );
            str += r;

            if( this.getModifications() != null ) {
                List<String> modsAtPosition = new ArrayList<String>();

                if( this.getModifications().get( i ) != null ) {
                    for( BigDecimal mod : this.getModifications().get( i ) ) {
                        modsAtPosition.add( mod.setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() );
                    }

                    if( modsAtPosition.size() > 0 ) {

                        // sort these strings on double values
                        Collections.sort( modsAtPosition, new Comparator<String>() {
                            public int compare(String s1, String s2) {
                                return Double.valueOf( s1 ).compareTo( Double.valueOf( s2 ) );
                            }
                        });

                        String modsString = StringUtils.join( modsAtPosition, "," );
                        str += "[" + modsString + "]";
                    }
                }
            }
        }

        if( this.getnTerminalMod() != null ) {
            str = "n[" + this.getnTerminalMod().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +"]" + str;
        }

        if( this.getcTerminalMod() != null ) {
            str = str + "c[" + this.getcTerminalMod().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +"]";
        }

        if( this.getLinkedPositions() != null ) {
            str += "(" + String.join( ",", this.getLinkedPositions() + ")" );
        }

        return str;
    }


    public String getSequence() {
        return sequence;
    }

    public Map<Integer, Collection<BigDecimal>> getModifications() {
        return modifications;
    }

    public BigDecimal getnTerminalMod() {
        return nTerminalMod;
    }

    public BigDecimal getcTerminalMod() {
        return cTerminalMod;
    }

    public List<Integer> getLinkedPositions() {
        return linkedPositions;
    }

    private String sequence;
    private Map<Integer, Collection<BigDecimal>> modifications;
    private BigDecimal nTerminalMod;
    private BigDecimal cTerminalMod;
    private List<Integer> linkedPositions;

    public KojakPeptide(String sequence, Map<Integer, Collection<BigDecimal>> modifications, BigDecimal nTerminalMod, BigDecimal cTerminalMod, Integer position1, Integer position2 ) {
        this.sequence = sequence;
        this.modifications = modifications;
        this.nTerminalMod = nTerminalMod;
        this.cTerminalMod = cTerminalMod;

        if( position1 != null && position2 != null ) {
            this.linkedPositions = new ArrayList<>( 2 );

            if( position1 <= position2 ) {
                this.linkedPositions.add( position1 );
                this.linkedPositions.add( position2 );
            } else {
                this.linkedPositions.add( position2 );
                this.linkedPositions.add( position1 );
            }
        } else if( position1 != null ) {
            this.linkedPositions = new ArrayList<>( 1 );
            this.linkedPositions.add( position1 );
        }

        if( this.linkedPositions != null ) {
            this.linkedPositions = Collections.unmodifiableList( this.linkedPositions );
        }
    }
}
