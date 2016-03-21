package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;



import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFiles;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;
import org.yeastrc.proxl_import.api.xml_dto.StaticModifications;

/**
 * Process the Kojak Conf file, and returning the input file
 *
 */
public class KojakConfFileReader {
	
	private static final Logger log = Logger.getLogger(KojakConfFileReader.class);
	
	
	
	//  Keys in the Conf file
	
	/**
	 * Input file to Kojak
	 */
	private static final String MS_DATA_FILE_CONFIG_KEY = "MS_data_file";
	
	public static final String FIXED_MODIFICATION_CONFIG_KEY = "fixed_modification";
	

	/**
	 * private constructor
	 */
	private KojakConfFileReader(){}
	
	public static KojakConfFileReader getInstance(  ) throws Exception {
		
		KojakConfFileReader kojakConfFileProcessor = new KojakConfFileReader();
		return kojakConfFileProcessor;
	}

	/**
	 * @param kojakConfFile
	 * @param proxlInputRoot
	 * @throws Exception
	 */
	public void readKojakConfFile( File kojakConfFile, ProxlInput proxlInputRoot ) throws Exception{
	
		StaticModifications staticModifications = new StaticModifications();
		
		//  This is called at end of processing if staticModificationList is not empty:
		// 		proxlInputRoot.setStaticModifications( staticModifications );

		List<StaticModification> staticModificationList = staticModifications.getStaticModification();
		
		
		ConfigurationFiles configurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( configurationFiles );
		
		List<ConfigurationFile> configurationFileList = configurationFiles.getConfigurationFile();
		
		ConfigurationFile configurationFile = new ConfigurationFile();
		configurationFileList.add( configurationFile );
		
		configurationFile.setFileName( kojakConfFile.getName() );
		configurationFile.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
		
		
//		proxlInputRoot.setLinkers(  );
		

		
		System.out.println( "Reading Kojak conf file using filename provided on command line: " + kojakConfFile.getAbsolutePath() );
		
				
		
		long kojakConfFileSize = kojakConfFile.length();
		
		if ( kojakConfFileSize > Integer.MAX_VALUE ) {
			
			String msg = "Kojak Conf file is larger than Integer.MAX_VALUE so unable to read into byte[] (file: " + kojakConfFile.getAbsolutePath() + ") .";
			log.error( msg );
			throw new Exception( msg );
		}
		

		//  Get contents of Kojak conf file as bytes
		
		
		
		byte[] kojakConfFileBytes = new byte[ (int) kojakConfFileSize ];
		
		FileInputStream kojakConfFileFileInputStream = null;
		
		try {
			
			kojakConfFileFileInputStream = new FileInputStream( kojakConfFile );
			
			
			int bytesRead = kojakConfFileFileInputStream.read( kojakConfFileBytes );
			
			if ( bytesRead != kojakConfFileSize ) {
			
				String msg = "ERROR: bytesRead != kojakConfFileSize: Kojak Conf file: " + kojakConfFile.getAbsolutePath();

				log.error( msg );

				throw new Exception(msg);
			}
			
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: Reading into byte[]: Kojak Conf file: " + kojakConfFile.getAbsolutePath();
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			if ( kojakConfFileFileInputStream != null ) {
				kojakConfFileFileInputStream.close();
			}
		}
		
		
		configurationFile.setFileContent( kojakConfFileBytes );
		
		
		
		
		BufferedReader reader = null;
		
		try {
			
			reader = new BufferedReader( new FileReader( kojakConfFile ) ); 

			String line = null;
			
			while ( ( line = reader.readLine() ) != null ) {
				
				ParsedLine lineParsed = parseLine( line );
				
				if ( lineParsed == null ) {
					
					continue;  // skip to next line
				}
				
				
				if ( MS_DATA_FILE_CONFIG_KEY.equals( lineParsed.key ) ) {

//					kojakInputFilenamePossiblyWithPath = lineParsed.value;

				} else if ( FIXED_MODIFICATION_CONFIG_KEY.equals( lineParsed.key ) ) {
					
					StaticModification staticModification = parseStaticMod( lineParsed.value, line );
					
					staticModificationList.add( staticModification );
				
//				} else if ( KOJAK_OUTPUT_FILENAME_CONFIG_KEY.equals( lineParsed.getKojakConfFileKey() ) ) {
//					
//					kojakOutputFilename = lineParsed.getValue();
//					
//				} else if ( KOJAK_OUTPUT_FILENAME_FOR_PERCOLATOR_CONFIG_KEY.equals( lineParsed.getKojakConfFileKey() ) ) {
//					
//					kojakOutputFilenameForPercolator = lineParsed.getValue();
				}
			}
			
		} catch ( Exception e ) {
			
			String msg = "Kojak Conf file: " + kojakConfFile.getAbsolutePath();
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			if ( reader != null ) {
				reader.close();
			}
		}
		

//		if ( kojakInputFilenamePossiblyWithPath == null ) {
//			
//			String msg = "Kojak Conf file does not contain input filename key '" + MS_DATA_FILE_CONFIG_KEY 
//					+ "' ( Kojak Conf file: " + kojakConfFile.getAbsolutePath() + ") .";
//			log.error( msg );
//			throw new Exception( msg );
//		}
//		
//		if ( kojakInputFilenamePossiblyWithPath.isEmpty() ) {
//			
//			String msg = "Kojak Conf file does not contain a value for input filename key '" + MS_DATA_FILE_CONFIG_KEY 
//					+ "' ( Kojak Conf file: " + kojakConfFile.getAbsolutePath() + ") .";
//			log.error( msg );
//			throw new Exception( msg );
//		}
		
		
		
		
		//  Throws exception if errors
		validateStaticMods( staticModificationList, kojakConfFile );
		
		
		//  Add Static modifications if list not empty
		
		if ( ! staticModificationList.isEmpty() ) {
		
			proxlInputRoot.setStaticModifications( staticModifications );
		}
		
	}
	

	/**
	 * Validate same residue is not in the list more than once.
	 * @param kojakConfStaticMods
	 * @throws Exception 
	 */
	private void validateStaticMods( List<StaticModification> staticModificationList, File kojakConfFile ) throws Exception {
		
		Set<String> aminoAcidSet = new HashSet<>();
		
		
		for ( StaticModification staticModification : staticModificationList ) {
			
			String aminoAcid = staticModification.getAminoAcid();
			
			if ( aminoAcidSet.contains( aminoAcid )) {
				
				
				String msg = "ERROR: Duplicate aminoAcid entries for config key '" 
						+ FIXED_MODIFICATION_CONFIG_KEY + "' ,Kojak Conf file: " + kojakConfFile.getAbsolutePath();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			aminoAcidSet.add( aminoAcid );
		}
		
	}
	
	
	/**
	 * @param line
	 * @return
	 */
	private ParsedLine parseLine( String line ) {
		
		//  Remove comment part of line, starts at "#" in any position on the line
		
		int commentCharPos = line.indexOf( '#' );
		
		if ( commentCharPos != -1 ) {
			line = line.substring(0, commentCharPos);
		}
		
		line = line.trim();
		
		if ( line.length() == 0 ) {
			
			// Nothing left on the line, so return null 
			return null;
		}
		
		//  Split into key and value
		
		int equalsSignPos = line.indexOf('=');

		if ( equalsSignPos == -1 || equalsSignPos == 0 ) {
			
			//  Not valid format as missing "=" separator or "=" is first char so skip
			return null;
		}
		
		String key = line.substring(0,equalsSignPos);
		
		String value = "";
		
		if ( equalsSignPos < line.length() - 1 ) {
			
			value = line.substring(equalsSignPos + 1, line.length());
		}
		
		key = key.trim();
		value = value.trim();
		
		ParsedLine lineParsed = new ParsedLine();
		
		lineParsed.key = key;
		lineParsed.value = value;
		
		return lineParsed;
	}
	
	
	
	/**
	 * @param lineParsedValue
	 * @param line
	 * @return
	 * @throws Exception 
	 */
	private StaticModification parseStaticMod( String lineParsedValue, String line ) throws Exception {
		
		
		String[] lineParsedValueSplit = lineParsedValue.split( "\\s+" ); // split on white space.
		
		if ( lineParsedValueSplit.length != 2 ) {
			
			String msg = "Config key '" + FIXED_MODIFICATION_CONFIG_KEY + "' must have 2 values, a residue and a mass, line: " + line;
			
			log.error( msg );
			
			throw new Exception(msg);
		}
		
		String aminoAcid = lineParsedValueSplit[ 0 ].trim();
		String massString = lineParsedValueSplit[ 1 ].trim();
		
		
		BigDecimal mass = null;
		
		try {
			
			mass = new BigDecimal( massString );
			
		} catch ( Exception e ) {
			
			
			String msg = "Config key '" + FIXED_MODIFICATION_CONFIG_KEY + "' mass is not parsable to BigDecimal.  mass: " 
					+ massString + ", line: " + line;
			
			log.error( msg, e );
			
			throw new Exception(msg, e);
			
		}
		
		
		
		StaticModification staticModification = new StaticModification();
		
		staticModification.setAminoAcid( aminoAcid );
		staticModification.setMassChange( mass );
		
		return staticModification;
	}
	
	
	private class ParsedLine {
		
		String key;
		String value;
		
	}
	
	
}
