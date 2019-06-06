package org.yeastrc.proxl.xml.kojak.objects;

import java.io.File;

public class ConversionParameters {

    public ConversionParameters(KojakAnalysis kojakAnalysis, File proxlXMLOutFile) {
        this.kojakAnalysis = kojakAnalysis;
        this.proxlXMLOutFile = proxlXMLOutFile;
    }

    public KojakAnalysis getKojakAnalysis() {
        return kojakAnalysis;
    }

    public File getProxlXMLOutFile() {
        return proxlXMLOutFile;
    }

    private KojakAnalysis kojakAnalysis;
    private File proxlXMLOutFile;
}
