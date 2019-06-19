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
                Objects.equals(dynamicModifications, that.dynamicModifications) &&
                Objects.equals(nTerminalMod, that.nTerminalMod) &&
                Objects.equals(cTerminalMod, that.cTerminalMod) &&
                Objects.equals(linkedPositions, that.linkedPositions) &&
                Objects.equals(n15Label, that.n15Label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, dynamicModifications, nTerminalMod, cTerminalMod, linkedPositions, n15Label);
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

            if( this.getDynamicModifications() != null ) {
                List<String> modsAtPosition = new ArrayList<String>();

                if( this.getDynamicModifications().get( i ) != null ) {
                    for( KojakDynamicMod mod : this.getDynamicModifications().get( i ) ) {
                        modsAtPosition.add( mod.getMassDiff().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() );
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
            str = "n[" + this.getnTerminalMod().getMassDiff().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +"]" + str;
        }

        if( this.getcTerminalMod() != null ) {
            str = str + "c[" + this.getcTerminalMod().getMassDiff().setScale( 2, BigDecimal.ROUND_HALF_UP ).toString() +"]";
        }

        if( this.getN15Label() != null ) {
            str = str + "-" + this.getN15Label();
        }

        if( this.getLinkedPositions() != null ) {
            str += "(";

            String delim = "";
            for (Integer i : this.getLinkedPositions()) {
                str += delim + i;
                delim = ",";
            }

            str += ")";

        }

        return str;
    }


    public String getSequence() {
        return sequence;
    }

    public Map<Integer, Collection<KojakDynamicMod>> getDynamicModifications() {
        return dynamicModifications;
    }

    public KojakDynamicMod getnTerminalMod() {
        return nTerminalMod;
    }

    public KojakDynamicMod getcTerminalMod() {
        return cTerminalMod;
    }

    public List<Integer> getLinkedPositions() {
        return linkedPositions;
    }

    public String getN15Label() {
        return n15Label;
    }

    private String sequence;
    private Map<Integer, Collection<KojakDynamicMod>> dynamicModifications;
    private KojakDynamicMod nTerminalMod;
    private KojakDynamicMod cTerminalMod;
    private List<Integer> linkedPositions;
    private String n15Label;

    public KojakPeptide(String sequence, Map<Integer, Collection<KojakDynamicMod>> dynamicModifications, KojakDynamicMod nTerminalMod, KojakDynamicMod cTerminalMod, Integer position1, Integer position2, String n15Label ) {
        this.sequence = sequence;
        this.dynamicModifications = dynamicModifications;
        this.nTerminalMod = nTerminalMod;
        this.cTerminalMod = cTerminalMod;
        this.n15Label = n15Label;

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
