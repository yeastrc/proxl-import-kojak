package org.yeastrc.proxl.xml.kojak.objects;

import org.yeastrc.proxl.xml.kojak.reader.KojakConfReader;

import java.io.File;
import java.io.IOException;

public class KojakAnalysis {

    public KojakAnalysis(File[] pepXMLFiles, File[] kojakConfFiles, File fastaFile) throws IOException {
        this.pepXMLFiles = pepXMLFiles;
        this.kojakConfFiles = kojakConfFiles;
        this.fastaFile = fastaFile;
        this.kojakConfReader = new KojakConfReader( kojakConfFiles[ 0 ] );
    }

    public File[] getPepXMLFiles() {
        return pepXMLFiles;
    }

    public File[] getKojakConfFiles() {
        return kojakConfFiles;
    }

    public File getFastaFile() {
        return fastaFile;
    }

    public KojakConfReader getKojakConfReader() {
        return kojakConfReader;
    }

    private File[] pepXMLFiles;
    private File[] kojakConfFiles;
    private File fastaFile;
    private KojakConfReader kojakConfReader;

}
