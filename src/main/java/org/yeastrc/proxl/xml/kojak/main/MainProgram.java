package org.yeastrc.proxl.xml.kojak.main;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(name = "java -jar " + ConverterConstants.CONVERTER_JAR_NAME,
        mixinStandardHelpOptions = true,
        version = ConverterConstants.CONVERTER_JAR_NAME + " " + ConverterConstants.CONVERTER_VERSION,
        sortOptions = false,
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        description = "Convert the results of Kojak (optionally processed by Percolator) to Proxl XML\n\n" +
                "More info at: " + ConverterConstants.CONVERTER_REPO_URL,
        footer = {
                "",
                "@|bold,underline Examples|@:",
                "java -jar " + ConverterConstants.CONVERTER_JAR_NAME + " ^\n" +
                        "-p \"c:\\kojak run\\results.pep.xml\" ^\n" +
                        "-o \"c:\\out put\\output.proxl.xml\" ^\n" +
                        "-f c:\\fastas\\myFasta.fasta",
                "",
                "java -jar " + ConverterConstants.CONVERTER_JAR_NAME + " ^\n" +
                        "-p \"/my/data/results.pep.xml\" ^\n" +
                        "-o \"/my/output/output.proxl.xml\" ^\n" +
                        "-f /fastas/myFasta.fasta",
                ""
        }
)
public class MainProgram implements Runnable {

    @CommandLine.Option(names = { "-c", "--conf" }, required = true, description = "[Required] Specify the full path " +
            "to at least one Kojak conf file (e.g., Kojak.conf). You may specify more than one if using multiple " +
            "pepXML files; however, all config options (e.g., cross-linker or modifications) must be the same in" +
            " combined data.")
    private File[] kojakConfFiles;

    @CommandLine.Option(names = { "-p", "--pepxml" }, required = true, description = "[Required] Full path to at " +
            "least one Kojak pepXML file. If specifying multiple pepXML files, they will be considered one search " +
            "in proxl and all MUST use the same config options (e.g., cross-linker or modifications.")
    private File[] pepXMLFiles;

    @CommandLine.Option(names = { "-f", "--fasta" }, required = true, description = "[Required] Full path to FASTA file " +
            "used in the experiment. If uploading multiple pepXML files, they must have ALL been processed with this " +
            "FASTA file.")
    private String fastaFile;

    @CommandLine.Option(names = { "-o", "--out" }, required = true, description = "[Required] Full path to use for the " +
            "ProXL XML output file (e.g., ./kojakrun.proxl.xml).")
    private String outFile;

    public static void main( String[] args ) {
        CommandLine.run(new MainProgram(), args);
    }

    @Override
    public void run() {
        System.out.println( "foo" );
    }
}
