package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Collection;

public class LinkableEnd {

    private Collection<String> residues;
    private boolean nTerminal;
    private boolean cTerminal;

    public LinkableEnd(Collection<String> residues, boolean nTerminal, boolean cTerminal) {
        this.residues = residues;
        this.nTerminal = nTerminal;
        this.cTerminal = cTerminal;
    }

    public Collection<String> getResidues() {
        return residues;
    }

    public boolean isnTerminal() {
        return nTerminal;
    }

    public boolean iscTerminal() {
        return cTerminal;
    }
}
