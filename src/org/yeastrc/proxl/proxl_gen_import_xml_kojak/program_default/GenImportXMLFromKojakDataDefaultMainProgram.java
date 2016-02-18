package org.yeastrc.proxl.proxl_gen_import_xml_kojak.program_default;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.core_entry_point.GenImportXMLFromKojakDataCoreEntryPoint;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.PrintHelpOnlyException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;

/**
 * 
 *
 */
public class GenImportXMLFromKojakDataDefaultMainProgram {

	private static final Logger log = Logger.getLogger( GenImportXMLFromKojakDataDefaultMainProgram.class );


	private static final int PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED = 0;
	

	private static final String PROTEIN_NAME_DECOY_PREFIX_CMD_LINE_PARAM_STRING  = "protein_name_decoy_prefix";
	
	public static void main(String[] args) throws Exception {
	

		boolean successfulImport = false;
		
		int programExitCode = PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED;
		
		
		try {

			
			
			
			CmdLineParser cmdLineParser = new CmdLineParser();
	        
			CmdLineParser.Option linkerOpt = cmdLineParser.addStringOption( 'l', "linker" );	
			CmdLineParser.Option fastaOpt = cmdLineParser.addStringOption( 'f', "fasta" );	
			CmdLineParser.Option nameOpt = cmdLineParser.addStringOption( 'n', "name" );	
			CmdLineParser.Option kojakFileWithPathCommandLineOpt = cmdLineParser.addStringOption( 'k', "kojak_file_with_path" );
			CmdLineParser.Option kojakConfFileWithPathCommandLineOpt = cmdLineParser.addStringOption( 'c', "kojak_conf_file_with_path" );
			CmdLineParser.Option monolinkMassesCommandLineOpt = cmdLineParser.addStringOption( 'm', "monolink_masses" );
			
			CmdLineParser.Option proteinNameDecoyPrefixCommandLineOpt = cmdLineParser.addStringOption( 'Z', PROTEIN_NAME_DECOY_PREFIX_CMD_LINE_PARAM_STRING );
			

			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 
			
			
	        // parse command line options
	        try { cmdLineParser.parse(args); }
	        catch (IllegalOptionValueException e) {
	            System.err.println(e.getMessage());
	            
	            
				programExitCode = 1;
	            throw new PrintHelpOnlyException();
//	            System.exit( 0 );
	        }
	        catch (UnknownOptionException e) {
	            System.err.println(e.getMessage());
	            
				programExitCode = 1;
	            throw new PrintHelpOnlyException();
//	            System.exit( 0 );
	        }
	        
	        Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
	        if(help) {
	        	
				programExitCode = 1;
	            throw new PrintHelpOnlyException();
	        }
	        
	        String linkerNameString = (String)cmdLineParser.getOptionValue( linkerOpt );
	        String fastaFilename = (String)cmdLineParser.getOptionValue( fastaOpt );

	        String kojakFileWithPath = (String)cmdLineParser.getOptionValue( kojakFileWithPathCommandLineOpt );
	        String kojakConfFileWithPathCommandLine = (String)cmdLineParser.getOptionValue( kojakConfFileWithPathCommandLineOpt );
	        String monolinkMassesCommandLine = (String)cmdLineParser.getOptionValue( monolinkMassesCommandLineOpt );
	        
	        String searchName = (String)cmdLineParser.getOptionValue( nameOpt );
	        
	        String proteinNameDecoyPrefix = (String)cmdLineParser.getOptionValue( proteinNameDecoyPrefixCommandLineOpt );
	        
	    
	        
			String[] remainingArgs = cmdLineParser.getRemainingArgs();
			
			if( remainingArgs.length < 1 ) {
				System.err.println( "Got unexpected number of arguments.\n" );
				
				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}
				        
			
	        if( linkerNameString == null || linkerNameString.equals( "" ) ) {
	        	System.err.println( "Must specify a linker using -l\n" );
	        	
				programExitCode = 1;
				throw new PrintHelpOnlyException();
	        }
	        
	        if( fastaFilename == null || fastaFilename.equals( "" ) ) {
	        	System.err.println( "Must specify a fasta name using -f\n" );
	        	
				programExitCode = 1;
				throw new PrintHelpOnlyException();
	        }

	        
	        if( StringUtils.isEmpty( kojakFileWithPath ) ) {
	        	System.err.println( "Must specify a kojak output file name with path using -k\n" );
	        	
				programExitCode = 1;
				throw new PrintHelpOnlyException();
	        }
	        
	        

	        if( StringUtils.isEmpty( kojakConfFileWithPathCommandLine ) ) {
	        	System.err.println( "Must specify a Kojak Conf file name with path using -s\n" );
	        	
				programExitCode = 1;
				throw new PrintHelpOnlyException();
	        }
	        

	        if( StringUtils.isEmpty( monolinkMassesCommandLine ) ) {
	        	System.err.println( "Must specify Monolink Masses using -m\n" );
	        	
				programExitCode = 1;
				throw new PrintHelpOnlyException();
	        }
	        
	        

	        String[] percolatorFileArray = remainingArgs;
	        
	        
	        
			List<File> percolatorFileList = new ArrayList<>( percolatorFileArray.length );
						
			for ( String percolatorFileString : percolatorFileArray ) {
				
				File percolatorFile = new File( percolatorFileString );

				if( ! percolatorFile.exists() ) {
					System.err.println( "Could not find percolator file: " + percolatorFile );
					
					programExitCode = 1;
					throw new PrintHelpOnlyException();
//					System.exit( 1 );
				}
				
				percolatorFileList.add( percolatorFile );
			}
			
	        
	        System.out.println( "Performing Proxl import for parameters:" );
	        System.out.println( "linker: " + linkerNameString );
	        System.out.println( "Kojak output filename with path: " + kojakFileWithPath );
	        System.out.println( "Kojak Conf filename with path: " + kojakConfFileWithPathCommandLine );
	        System.out.println( "Monolink Masses: " + monolinkMassesCommandLine );
	        
	        System.out.println( "fasta filename: " + fastaFilename );

	        System.out.println( "search name: " + searchName );

	        System.out.println( "protein name decoy prefix: " + proteinNameDecoyPrefix );

	        
	        System.out.println( "Percolator files on command line:" );

			for ( String percolatorFileString : percolatorFileArray ) {
				
				System.out.println( percolatorFileString );
			}
			
			System.out.println( " " );
	        
	        System.out.println( "Percolator files full path:" );

			for ( File percolatorFile : percolatorFileList ) {
				
				System.out.println( percolatorFile.getAbsolutePath() );
			}

			
			System.out.println( " " );
	        
			System.out.println( " " );
			
			


//			
//			try {
//				//  throws Exception if fasta filename is not found
//				FASTADatabaseLookup.getInstance().lookupDatabase( fastaFilename );
//			} catch( Exception e ) {
//				System.err.println( "Could not find a parsed FASTA file named: " + fastaFilename );
//				System.err.println( "Error: " + e.getMessage() );
//				System.err.println( "Is the name correct? Has it been parsed?" );
//				printHelp();
//				
//				throw e;
//				
//				//System.exit( 0 );
//			}
//			


			
	        File kojakConfFile = new File( kojakConfFileWithPathCommandLine );
	        
	        if ( ! kojakConfFile.exists() ) {

				String msg = "ERROR: Kojak Conf does not exist (file: " + kojakConfFile.getAbsolutePath() + ") .";
				log.error( msg );
				System.err.println( msg );

				throw new Exception( msg );
	        }
	        

	        File kojakOutputFile = new File( kojakFileWithPath );
	        
	        if ( ! kojakOutputFile.exists() ) {

				String msg = "ERROR: Kojak output file does not exist (file: " + kojakOutputFile.getAbsolutePath() + ") .";
				log.error( msg );
				System.err.println( msg );

				throw new Exception( msg );
	        }

	        Set<BigDecimal> monolinkModificationMasses = new HashSet<>();
	        

	        String[] monolinkMassesCommandLineSplit = monolinkMassesCommandLine.split( ";" );

	        for ( String monolinkMassString : monolinkMassesCommandLineSplit ) {

	        	try {
	        		
	        		BigDecimal monolinkMass = new BigDecimal( monolinkMassString );
	        		
	        		monolinkModificationMasses.add( monolinkMass );
	        		
	        	} catch ( Exception e ) {
	        		
	        		String msg = "Failed to parse monolink mass on command line: " + monolinkMassString;
	        		log.error( msg );
	        		throw new ProxlGenXMLDataException( msg );
	        	}
	        }

			File outputFile = new File( "proxlImportTestFile.xml" );
			
	        
			//////////////////////////////////////
			
			//////////   Do the import
			
			GenImportXMLFromKojakDataCoreEntryPoint.getInstance().doGenFile( 

					fastaFilename, 
					linkerNameString, 
					searchName, 
					proteinNameDecoyPrefix,
					
					monolinkModificationMasses,
					false /* forceDropKojakDuplicateRecordsOptOnCommandLine */,
					
					
					percolatorFileList, 
					kojakOutputFile, 
					kojakConfFile,
					outputFile
					 );

			
			
			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );

	        System.out.println( "Completed Proxl Gen XML for Import for parameters:" );
	        System.out.println( "linker: " + linkerNameString );
	        System.out.println( "Kojak output filename with path: " + kojakFileWithPath );
	        System.out.println( "Kojak Conf filename with path: " + kojakConfFileWithPathCommandLine );
	        System.out.println( "Monolink Masses: " + monolinkMassesCommandLine );
	        System.out.println( "fasta filename: " + fastaFilename );
	        System.out.println( "search name: " + searchName );

	        
	        System.out.println( "Percolator files on command line:" );


			for ( String percolatorFileString : percolatorFileArray ) {
				
				System.out.println( percolatorFileString );
			}
			
			System.out.println( " " );
	        
	        System.out.println( "Percolator files full path:" );

			for ( File percolatorFile : percolatorFileList ) {
				
				System.out.println( percolatorFile.getAbsolutePath() );
			}

			
			System.out.println( " " );

			System.out.println( "--------------------------------------" );
	        
			System.out.println( " " );
			
			
			successfulImport = true;
			
		} catch ( PrintHelpOnlyException e ) {
			
			//  land here when only need to print the help
			
            printHelp();

			
		} catch ( Exception e ) {
			
			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );
			
			e.printStackTrace( System.out );
			e.printStackTrace( System.err );
			
			
			throw e;
			
		} finally {
			
			

		}
	    
	    if ( successfulImport ) {

	    	System.out.println( "" );
	    	System.out.println( "--------------------------------------" );
	    	System.out.println( "" );
	    	System.out.println( "Done Generating Proxl Import XML data." );

	    	System.out.println( "" );
	    	System.out.println( "--------------------------------------" );
	    	System.out.println( "" );
	    }

		
		if ( programExitCode != PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED ) {
			
			System.exit( programExitCode );
		}
	}


	
	private static void printHelp() throws Exception {
		
		String line = "Usage: <run jar script> -l linker -f fasta_name.fasta "
				+ " -k kojak_file_with_path -c kojak_conf_filename "
				+ " -m <monolink masses, ';' delimited> "
				+ "  [ -n search_name ] "

				+ " [ --" + PROTEIN_NAME_DECOY_PREFIX_CMD_LINE_PARAM_STRING + "=protein_name_decoy_prefix ] "
				+ " /path/to/percolator_file.xml "
				+ "[ more percolator files ]";
				
		
		System.err.println( line );
		
		System.err.println( "E.g.:  java -jar <name of main jar>  -l dss -f yeast.fasta -k kojak_output_file -c Kojak.conf -n \"Such and such name\" /path/to/percolator.xml " );
		System.err.println( "" );
		System.err.println( "<run jar script> is the appropriate script for your language to run the main jar with the other jars on the java class path" );
		System.err.println( "" );


		System.err.println( "" );
		System.err.println( "The -k is required.");
		System.err.println( "--kojak_file_with_path can be used instead of -k.");
		System.err.println( "The path is required, either absolute or relative to the execution of the importer" );

		System.err.println( "" );
		System.err.println( "The -c is required.");
		System.err.println( "--kojak_conf_filename can be used instead of -c.");
		System.err.println( "The -c is the kojak conf file with path is used to get the Kojak configuration." );
		System.err.println( "If the value for the -c parameter has spaces in it, the value must be enclosed in quotes (\")" );
		System.err.println( "" );

		System.err.println( "" );
		System.err.println( "The -m is required.");
		System.err.println( "--monolink_masses can be used instead of -m.");
		System.err.println( "The -m is the Monolink masses used to determine which Dynamic Modifications are Monolinks." );
		System.err.println( "The number of signicant digits must match the number output in the dynamic modifications embedded in the peptide sequence." );
		System.err.println( "Examine your output file for dynamic modifications embedded in the peptide sequences.  They are denoted with '[' and ']'." );
		System.err.println( "For Kojak, this is 2 digits to the right of the decimal place." );
		System.err.println( "" );
		
		System.err.println( "The -n is optional.");
		System.err.println( "--name can be used instead of -n.");
		System.err.println( "The -n is the search name displayed as the label for the search on the website" );
		System.err.println( "If the -n is not provided, a default will be used.  Currently that default is 'Search: <search id>'" );
		System.err.println( "If the value for the -n parameter has spaces in it, the value must be enclosed in quotes (\")" );
		System.err.println( "The search name can be modified on the website later as needed" );
		
		
		

		System.err.println( "" );
		System.err.println( "The --" + PROTEIN_NAME_DECOY_PREFIX_CMD_LINE_PARAM_STRING + " is optional.");
		System.err.println( "If --" + PROTEIN_NAME_DECOY_PREFIX_CMD_LINE_PARAM_STRING 
				+ " is not provided, no decoy proteins will be excluded." );
		
//
//			System.err.println( "" );
//			System.err.println( "linkers available for the '-l' option:" );
//			System.err.println( "" );
//			System.err.println( "abbr\tmonolink linker masses (the mass must match exactly for the variable mod to be identified as a monolink");
//			System.err.println( "" );
//
//			List<LinkerDTO>  linkerList = LinkerDAO.getInstance().getAllLinkerDTO();
//
//			for ( LinkerDTO linkerDTO : linkerList ) {
//
//				System.err.println( linkerDTO.getAbbr() );
//
//				List<LinkerMonolinkMassDTO>  linkerMonolinkMassList = LinkerMonolinkMassDAO.getInstance().getLinkerMonolinkMassDTOForLinkerId( linkerDTO.getId() );
//
//				for ( LinkerMonolinkMassDTO linkerMonolinkMassDTO : linkerMonolinkMassList ) {
//
//					System.err.println( "\t" + linkerMonolinkMassDTO.getMass() );
//				}
//
//
//			}
		
	}


}
