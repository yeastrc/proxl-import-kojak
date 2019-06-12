/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *
 * Copyright 2019 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.proxl.xml.kojak.main;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl.xml.kojak.objects.ConversionParameters;
import org.yeastrc.proxl.xml.kojak.objects.KojakAnalysis;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
    private File fastaFile;

    @CommandLine.Option(names = { "-o", "--out" }, required = true, description = "[Required] Full path to use for the " +
            "ProXL XML output file (e.g., ./kojakrun.proxl.xml).")
    private File outFile;



    @Override
    public void run() {

        try {
            ensureFilesExist( kojakConfFiles, "Kojak configuration file" );
            ensureFilesExist( pepXMLFiles, "PepXML file" );
            ensureFileExists( fastaFile, "FASTA file" );

            warnFileExists( outFile, "ProxlXML output file" );

            KojakAnalysis kojakAnalysis = new KojakAnalysis(
                    pepXMLFiles,
                    kojakConfFiles,
                    fastaFile
            );

            ConversionParameters conversionParamters = new ConversionParameters(
                    kojakAnalysis,
                    outFile
            );

            ConverterRunner.createInstance().runConversion( conversionParamters );

        } catch( Throwable t ) {
            System.err.println( t.getMessage() );
            System.exit( 1 );
        }

    }

    public static void main( String[] args ) {

        printRuntimeInfo();

        CommandLine.run(new MainProgram(), args);
    }

    private void ensureFilesExist(File[] files, String descriptiveName ) throws Exception {

        for( File file : files ) {
            ensureFileExists( file, descriptiveName );
        }
    }

    private void ensureFileExists(File file, String descriptiveName ) throws Exception {

        if( !file.exists() ) {
            throw new Exception( descriptiveName + " could not be found: " + file.getAbsolutePath() );
        }
    }

    private void warnFileExists( File file, String descriptiveName ) {

        System.err.println( "WARNING: " + descriptiveName + " already exists: " + file.getAbsolutePath() +
                " Will overwrite." );
    }

    /**
     * Print runtime info to STD ERR
     * @throws Exception
     */
    private static void printRuntimeInfo() {

        try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "run.txt" ) ) ) ) {

            String line;
            while ( ( line = br.readLine() ) != null ) {

                line = line.replace( "{{URL}}", ConverterConstants.CONVERTER_REPO_URL );
                line = line.replace( "{{VERSION}}", ConverterConstants.CONVERTER_VERSION );

                System.err.println( line );

            }

            System.err.println();

        } catch ( Exception e ) {
            System.err.println( "Error printing runtime information." );
        }
    }
}
