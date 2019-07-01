package org.yeastrc.proxl.xml.kojak.objects;

import java.io.File;

public class ConversionParameters {
    public ConversionParameters(KojakAnalysis kojakAnalysis, PercolatorAnalysis percolatorAnalysis, File proxlXMLOutFile) {
        this.kojakAnalysis = kojakAnalysis;
        this.percolatorAnalysis = percolatorAnalysis;
        this.proxlXMLOutFile = proxlXMLOutFile;
    }

    public PercolatorAnalysis getPercolatorAnalysis() {
        return percolatorAnalysis;
    }

    public KojakAnalysis getKojakAnalysis() {
        return kojakAnalysis;
    }

    public File getProxlXMLOutFile() {
        return proxlXMLOutFile;
    }

    private KojakAnalysis kojakAnalysis;
    private PercolatorAnalysis percolatorAnalysis;
    private File proxlXMLOutFile;
}
