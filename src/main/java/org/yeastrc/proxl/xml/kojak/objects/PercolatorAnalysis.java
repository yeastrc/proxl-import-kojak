package org.yeastrc.proxl.xml.kojak.objects;

import java.io.File;

public class PercolatorAnalysis {

    public PercolatorAnalysis(File[] outXMLFiles) {
        this.outXMLFiles = outXMLFiles;
    }

    public File[] getOutXMLFiles() {
        return outXMLFiles;
    }

    private File[] outXMLFiles;

}
