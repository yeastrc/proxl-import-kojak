package org.yeastrc.proxl.proxl_gen_import_xml_kojak.program_default;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.core_entry_point.GenImportXMLFromKojakDataCoreEntryPoint;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.core_entry_point.GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint;

/**
 * 
 *
 */
public class GenImportXMLFromKojakDataDefaultMainProgram {

//	private static final Logger log = Logger.getLogger( GenImportXMLFromKojakDataDefaultMainProgram.class );


	private static final int PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED = 0;
	
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;
	
	private static final int PROGRAM_EXIT_CODE_HELP = 1;
	
	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	

//		boolean successfulGenImportXMLFile = false;
		
		int programExitCode = PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED;
		

		

        
        String outputFilename = null;
        
        String fastaFileWithPathFileFromCmdLineString = null;
        File fastaFileWithPathFileFromCmdLine = null;

        String kojakFileWithPath = null;
        String kojakConfFileWithPathCommandLine = null;
        
        
        Boolean noPercolatorCmdLine = (Boolean) null;
        
        Boolean skipPopulatingMatchedProteins = false;
        

        String searchName = null;
        
        String proteinNameDecoyPrefix = null;
        
        List<String> percolatorFileStringsList = new ArrayList<>( );
        List<String> linkerNamesStringsList = new ArrayList<>(  );
        List<String> monolinkMassessStringsList = new ArrayList<>( );
        

        Set<BigDecimal> monolinkModificationMasses = null;
        
		List<File> percolatorFileList = new ArrayList<>( percolatorFileStringsList.size() );

		File outputFile = null;
		
		
        File kojakConfFile = null;
        

		File kojakOutputFile = null;
		
		try {
			
			if ( args.length == 0 ) {
				
	            printHelp();
	            
	            System.exit( PROGRAM_EXIT_CODE_HELP );
			}

			
			
			
			CmdLineParser cmdLineParser = new CmdLineParser();
	        
			CmdLineParser.Option outputFilenameOpt = cmdLineParser.addStringOption( 'o', "output-file" );
			CmdLineParser.Option kojakFileWithPathCommandLineOpt = cmdLineParser.addStringOption( 'k', "kojak-data-file" );
			CmdLineParser.Option kojakConfFileWithPathCommandLineOpt = cmdLineParser.addStringOption( 'c', "kojak-conf-file" );
			CmdLineParser.Option linkerOpt = cmdLineParser.addStringOption( 'l', "linker" );
			
			CmdLineParser.Option fastaFileOpt = cmdLineParser.addStringOption( 'f', "fasta-file" );

			CmdLineParser.Option noPercolatorOpt = cmdLineParser.addBooleanOption('N', "no-percolator"); 

			CmdLineParser.Option percolatorFileOpt = cmdLineParser.addStringOption( 'x', "percolator-xml" );
			
			CmdLineParser.Option monolinkMassCommandLineOpt = cmdLineParser.addStringOption( 'm', "monolink-mass" );
			
			
			CmdLineParser.Option nameOpt = cmdLineParser.addStringOption( 'n', "name" );	
			
			//  'Z' is not mentioned to the user
			CmdLineParser.Option proteinNameDecoyPrefixCommandLineOpt = cmdLineParser.addStringOption( 'Z', "decoy-prefix" );

			//  'Z' is not mentioned to the user
			CmdLineParser.Option skipPopulatingMatchedProteinsCommandLineOpt = cmdLineParser.addStringOption( 'Z', "skip-populating-matched-proteins" );
			
			

			CmdLineParser.Option verboseOpt = cmdLineParser.addBooleanOption('V', "verbose"); 
			CmdLineParser.Option debugOpt = cmdLineParser.addBooleanOption('D', "debug"); 

			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 
			
			
	        // parse command line options
	        try { cmdLineParser.parse(args); }
	        catch (IllegalOptionValueException e) {
	            System.err.println(e.getMessage());

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        catch (UnknownOptionException e) {
	            System.err.println(e.getMessage());

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        
	        Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
	        if(help) {
	        	
	            printHelp();
	            
	            System.exit( PROGRAM_EXIT_CODE_HELP );
	        }
	        

			Boolean verbose = (Boolean) cmdLineParser.getOptionValue(verboseOpt, Boolean.FALSE);
			
			Boolean debugValue = (Boolean) cmdLineParser.getOptionValue(debugOpt, Boolean.FALSE);

			if ( verbose != null &&  verbose ) {

				LogManager.getRootLogger().setLevel(Level.INFO);
			}
			
			if ( debugValue != null &&  debugValue ) {

				LogManager.getRootLogger().setLevel(Level.DEBUG);
			}
			

			skipPopulatingMatchedProteins = (Boolean) cmdLineParser.getOptionValue(skipPopulatingMatchedProteinsCommandLineOpt, Boolean.FALSE);
			
			
			
			
	        
	        outputFilename = (String)cmdLineParser.getOptionValue( outputFilenameOpt );
	        
	        
	        fastaFileWithPathFileFromCmdLineString = (String)cmdLineParser.getOptionValue( fastaFileOpt );
	         
	        
			@SuppressWarnings("rawtypes")
			Vector linkerNameStringsVector = cmdLineParser.getOptionValues( linkerOpt );

	        kojakFileWithPath = (String)cmdLineParser.getOptionValue( kojakFileWithPathCommandLineOpt );
	        kojakConfFileWithPathCommandLine = (String)cmdLineParser.getOptionValue( kojakConfFileWithPathCommandLineOpt );
	        
	        
	        noPercolatorCmdLine = (Boolean) cmdLineParser.getOptionValue( noPercolatorOpt, Boolean.FALSE);

	        @SuppressWarnings("rawtypes")
	        Vector percolatorFileStringsVector = cmdLineParser.getOptionValues( percolatorFileOpt );

	        
			@SuppressWarnings("rawtypes")
			Vector monolinkMassesCommandLineVector = cmdLineParser.getOptionValues( monolinkMassCommandLineOpt );
	        
	        searchName = (String)cmdLineParser.getOptionValue( nameOpt );
	        
	        proteinNameDecoyPrefix = (String)cmdLineParser.getOptionValue( proteinNameDecoyPrefixCommandLineOpt );
	        
	    
	        
			String[] remainingArgs = cmdLineParser.getRemainingArgs();
			
			if( remainingArgs.length > 0 ) {

				System.out.println( "Unexpected command line parameters:");
				
				for ( String remainingArg : remainingArgs ) {
				
					System.out.println( remainingArg );
				}
				
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			
			
			/////////////   Validate the parameters
			
				      
	        if( outputFilename == null || outputFilename.equals( "" ) ) {
	        	System.err.println( "Must specify an output file using -o or --output_file=" );
	        	System.err.println( "" );

				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        
	        
	        if( linkerNameStringsVector == null || ( linkerNameStringsVector.isEmpty() ) ) {
	        
	        	System.err.println( "Must specify at least one linker using -l" );
	        	System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }

	        
	        
	        if( noPercolatorCmdLine != null && noPercolatorCmdLine ) {

	        	if ( percolatorFileStringsVector != null && ( ! percolatorFileStringsVector.isEmpty() ) ) {

		        	System.err.println( "If specify 'no percolator file', cannot also specify a percolator file.");
		        	System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	
	        } else {

	        	if ( percolatorFileStringsVector == null || percolatorFileStringsVector.isEmpty() ) {
	        		

		        	System.err.println( "If not specify a percolator file, must specify 'no percolator file'.");
		        	System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	

		        for ( Object percolatorFileStringObject : percolatorFileStringsVector ) {
		        	
		        	if ( ! (  percolatorFileStringObject instanceof String ) ) {

			        	System.err.println( "Internal ERROR:  percolatorFileStringObject is not a String object." );
			        	System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
		        	}
		        	
		        	String percolatorFileString = (String) percolatorFileStringObject;

		        	if( percolatorFileString == null || percolatorFileString.equals( "" ) ) {

		        		System.err.println( "Internal ERROR:  percolatorFileStringObject is empty or null." );
		        		System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
		        	}
		        	
		        	percolatorFileStringsList.add( percolatorFileString );
		        }

	        	
	        }

	        
	        for ( Object linkerNameStringObject : linkerNameStringsVector ) {
	        	
	        	if ( ! (  linkerNameStringObject instanceof String ) ) {

		        	System.err.println( "Internal ERROR:  linkerNameStringObject is not a String object\n" );
	        		System.err.println( "" );
		        	System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	
	        	String linkerNameString = (String) linkerNameStringObject;

	        	if( linkerNameString == null || linkerNameString.equals( "" ) ) {

	        		System.err.println( "Internal ERROR:  linkerNameString is empty or null." );
	        		System.err.println( "" );
		        	System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	
	        	linkerNamesStringsList.add( linkerNameString );
	        }
	        

	        
	        for ( Object monolinkMassesStringObject : monolinkMassesCommandLineVector ) {
	        	
	        	if ( ! (  monolinkMassesStringObject instanceof String ) ) {

		        	System.err.println( "Internal ERROR: monolinkMassesStringObject is not a String object\n" );
	        		System.err.println( "" );
		        	System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	
	        	String monolinkMassesString = (String) monolinkMassesStringObject;

	        	if( monolinkMassesString == null || monolinkMassesString.equals( "" ) ) {

	        		System.err.println( "Internal ERROR:  monolinkMassesStringObject is empty or null." );
	        		System.err.println( "" );
		        	System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        	}
	        	
	        	monolinkMassessStringsList.add( monolinkMassesString );
	        }
	        
	        
	        

	        
	        if( StringUtils.isEmpty( kojakFileWithPath ) ) {
	        	System.err.println( "Must specify a kojak output file name with path using -k\n" );
        		System.err.println( "" );
	        	System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        
	        

	        if( StringUtils.isEmpty( kojakConfFileWithPathCommandLine ) ) {
	        	System.err.println( "Must specify a Kojak Conf file name with path using -s\n" );
        		System.err.println( "" );
	        	System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        

			
			if( ! percolatorFileStringsList.isEmpty() ) {

				for ( String percolatorFileString : percolatorFileStringsList ) {

					File percolatorFile = new File( percolatorFileString );

					if( ! percolatorFile.exists() ) {
						System.err.println( "Could not find percolator file: " + percolatorFile );
		        		System.err.println( "" );
			        	System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
					}

					percolatorFileList.add( percolatorFile );
				}
			}
			

			if ( fastaFileWithPathFileFromCmdLineString != null ) {

				fastaFileWithPathFileFromCmdLine = new File( fastaFileWithPathFileFromCmdLineString );

				if( ! fastaFileWithPathFileFromCmdLine.exists() ) {
					System.err.println( "Could not find fasta file: " + fastaFileWithPathFileFromCmdLine );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );

					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
				}
			}

			/////////////////////////////////
			
			
			//    List input params to sysout
	        
	        System.out.println( "Performing Proxl Gen import XML file for parameters:" );
	        
	        System.out.println( "output filename: " + outputFilename );
	        
	        
	        
	        System.out.print( "linker"  );
	        
	        if ( linkerNamesStringsList.size() > 1 ) {

	        	System.out.print( "s"  );
	        }

	        System.out.print( ": "  );
	        
	        boolean firstLinkerBefore = true;

	        for ( String linkerNameString : linkerNamesStringsList ) {
	        	
	        	if ( firstLinkerBefore ) {
	        		
	        		firstLinkerBefore = false;
	        	} else {
	        		
	        		System.out.print( ", " ) ;
	        	}
	        	
	        	System.out.print( linkerNameString );
	        }
	        
	        System.out.println( "" );
	        
	        
	        
	        
	        System.out.println( "Kojak output filename with path: " + kojakFileWithPath );
	        System.out.println( "Kojak Conf filename with path: " + kojakConfFileWithPathCommandLine );

	        
	        if ( StringUtils.isNotEmpty( fastaFileWithPathFileFromCmdLineString ) ) {

	        	System.out.println( "fasta file (with path): " + fastaFileWithPathFileFromCmdLineString );
	        }

	        if ( StringUtils.isNotEmpty( searchName ) ) {

	        	System.out.println( "search name: " + searchName );
	        }

	        if ( StringUtils.isNotEmpty( proteinNameDecoyPrefix ) ) {
	        
	        	System.out.println( "protein name decoy prefix: " + proteinNameDecoyPrefix );
	        }

	        
	        

	        if( ! monolinkMassessStringsList.isEmpty() ) {
	        
	        	System.out.println( "Monolink Masses on command line:" );

	        	for ( String monolinkMassessString : monolinkMassessStringsList ) {

	        		System.out.println( monolinkMassessString );
	        	}
	        }
	        

	        if( ! percolatorFileStringsList.isEmpty() ) {
	        
	        	System.out.println( "Percolator files:" );

	        	for ( String percolatorFileString : percolatorFileStringsList ) {

	        		System.out.println( percolatorFileString );
	        	}

//	        	System.out.println( " " );
//
//	        	System.out.println( "Percolator files full path:" );
//
//	        	for ( File percolatorFile : percolatorFileList ) {
//
//	        		System.out.println( percolatorFile.getCanonicalPath() );
//	        	}
	        }
			
			System.out.println( " " );
	        
			System.out.println( " " );
			

			outputFile = new File( outputFilename );
			
			
	        kojakConfFile = new File( kojakConfFileWithPathCommandLine );
	        
	        if ( ! kojakConfFile.exists() ) {

				String msg = "ERROR: Kojak Conf does not exist (file: " + kojakConfFile.getAbsolutePath() + ") .";
				System.err.println( msg );
        		System.err.println( "" );
	        	System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }
	        

	        kojakOutputFile = new File( kojakFileWithPath );
	        
	        if ( ! kojakOutputFile.exists() ) {

				String msg = "ERROR: Kojak output file does not exist (file: " + kojakOutputFile.getAbsolutePath() + ") .";
				System.err.println( msg );
        		System.err.println( "" );
	        	System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        }


	        if ( ! monolinkMassessStringsList.isEmpty() ) {
        
	        	monolinkModificationMasses = new HashSet<>();

	        	for ( String monolinkMassString : monolinkMassessStringsList ) {

	        		try {

	        			BigDecimal monolinkMass = new BigDecimal( monolinkMassString );

	        			monolinkModificationMasses.add( monolinkMass );

	        		} catch ( Exception e ) {

	        			String msg = "Failed to parse monolink mass on command line: " + monolinkMassString;
	        			System.err.println( msg );
		        		System.err.println( "" );
			        	System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
	        		}
	        	}
	        }

		} catch ( Exception e ) {

			System.err.println( "Error processing command line parameters." ); 
			e.printStackTrace();
			throw e;
		}

			
		try {
				
	        
			//////////////////////////////////////
			
			//////////   Do Generate the import Proxl XML file
	        

	        if( ! percolatorFileStringsList.isEmpty() ) {

	        	GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint.getInstance().doGenFile( 

	        			fastaFileWithPathFileFromCmdLine, 
	        			linkerNamesStringsList, 
	        			searchName, 
	        			proteinNameDecoyPrefix,

	        			monolinkModificationMasses,
	        			
	        			skipPopulatingMatchedProteins,
	        			
	        			false /* forceDropKojakDuplicateRecordsOptOnCommandLine */,


	        			percolatorFileList, 
	        			kojakOutputFile, 
	        			kojakConfFile,
	        			outputFile
	        			);

	        } else {
	        	
	        	GenImportXMLFromKojakDataCoreEntryPoint.getInstance().doGenFile( 
	        			
	        			fastaFileWithPathFileFromCmdLine, 
	        			linkerNamesStringsList, 
	        			searchName, 
	        			proteinNameDecoyPrefix, 
	        			
	        			monolinkModificationMasses, 
	        			
	        			skipPopulatingMatchedProteins,
	        			
	        			false /* forceDropKojakDuplicateRecordsOptOnCommandLine */,
	        			
	        			kojakOutputFile, 
	        			kojakConfFile, 
	        			outputFile ) ;
	        	
	        }
			
			
			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );

	        System.out.println( "Completed Proxl Gen XML for Import for parameters:" );
	        System.out.println( "output filename: " + outputFilename );

	        
	        System.out.print( "linker"  );
	        
	        if ( linkerNamesStringsList.size() > 1 ) {

	        	System.out.print( "s"  );
	        }

	        System.out.print( ": "  );
	        
	        boolean firstLinkerAfter = true;

	        for ( String linkerNameString : linkerNamesStringsList ) {
	        	
	        	if ( firstLinkerAfter ) {
	        		
	        		firstLinkerAfter = false;
	        	} else {
	        		
	        		System.out.print( ", " ) ;
	        	}
	        	
	        	System.out.print( linkerNameString );
	        }
	        
	        System.out.println( "" );
	        
	        
	        
	        
	        System.out.println( "Kojak output filename with path: " + kojakFileWithPath );
	        System.out.println( "Kojak Conf filename with path: " + kojakConfFileWithPathCommandLine );
	        
	        
	        if ( StringUtils.isNotEmpty( fastaFileWithPathFileFromCmdLineString ) ) {

	        	System.out.println( "fasta file (with path): " + fastaFileWithPathFileFromCmdLineString );
	        }

	        if ( StringUtils.isNotEmpty( searchName ) ) {

	        	System.out.println( "search name: " + searchName );
	        }

	        if ( StringUtils.isNotEmpty( proteinNameDecoyPrefix ) ) {
	        
	        	System.out.println( "protein name decoy prefix: " + proteinNameDecoyPrefix );
	        }

	        
	        

	        if( ! monolinkMassessStringsList.isEmpty() ) {
	        
	        	System.out.println( "Monolink Masses on command line:" );

	        	for ( String monolinkMassessString : monolinkMassessStringsList ) {

	        		System.out.println( monolinkMassessString );
	        	}
	        }
	        

	        if( ! percolatorFileStringsList.isEmpty() ) {
	        
	        	System.out.println( "Percolator files:" );

	        	for ( String percolatorFileString : percolatorFileStringsList ) {

	        		System.out.println( percolatorFileString );
	        	}

//	        	System.out.println( " " );
//
//	        	System.out.println( "Percolator files full path:" );
//
//	        	for ( File percolatorFile : percolatorFileList ) {
//
//	        		System.out.println( percolatorFile.getCanonicalPath() );
//	        	}
	        }
			
			
			System.out.println( " " );

			System.out.println( "--------------------------------------" );
	        
			System.out.println( " " );
			
			
//			successfulGenImportXMLFile = true;
			
			
		} catch ( Exception e ) {
			
			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );
			
			e.printStackTrace( System.out );
			e.printStackTrace( System.err );
			
			
			throw e;
			
		} finally {
			
			

		}
	    
//	    if ( successfulGenImportXMLFile ) {
//
//	    	System.out.println( "" );
//	    	System.out.println( "--------------------------------------" );
//	    	System.out.println( "" );
//	    	System.out.println( "Done Generating Proxl Import XML data." );
//
//	    	System.out.println( "" );
//	    	System.out.println( "--------------------------------------" );
//	    	System.out.println( "" );
//	    }

		
		if ( programExitCode != PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED ) {
			
			System.exit( programExitCode );
		}
	}


	
	private static void printHelp() throws Exception {
		
		try( BufferedReader br = new BufferedReader( new InputStreamReader( GenImportXMLFromKojakDataDefaultMainProgram.class.getResourceAsStream( "/help_output_proxl_gen_import_xml_Kojak_Percolator.txt" ) ) ) ) {
			
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
			
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
		
		
	}


}
