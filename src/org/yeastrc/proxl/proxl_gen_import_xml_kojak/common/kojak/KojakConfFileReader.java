package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;



import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFiles;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMass;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMasses;
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
	
	private static final String DATABASE__FASTA_FILE = "database";
	
	/**
	 * Input file to Kojak
	 */
	private static final String MS_DATA_FILE_CONFIG_KEY = "MS_data_file";

	public static final String CROSS_LINK_CONFIG_KEY = "cross_link";
	
	public static final String MONO_LINK_CONFIG_KEY = "mono_link";
	
	public static final String FIXED_MODIFICATION_CONFIG_KEY = "fixed_modification";
	
	public static final String DECOY_FILTER_CONFIG_KEY = "decoy_filter";
	

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
		
		{
			//  First validate Linkers populated

			Linkers linkers = proxlInputRoot.getLinkers();
			
			if ( linkers == null ) {
				
				String msg = "Program ERROR: Linkers must be populated before adding crosslink masses";
				log.error( msg );
				throw new Exception( msg );
			}
			
			List<Linker> linkerList = linkers.getLinker();

			if ( linkerList == null || ( linkerList.isEmpty() ) ) {
				
				String msg = "Program ERROR: Linkers must be populated before adding crosslink masses";
				log.error( msg );
				throw new Exception( msg );
			}
	
		}
	
		StaticModifications staticModifications = new StaticModifications();
		List<StaticModification> staticModificationList = staticModifications.getStaticModification();

		ConfigurationFiles configurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( configurationFiles );
		
		List<ConfigurationFile> configurationFileList = configurationFiles.getConfigurationFile();
		
		ConfigurationFile configurationFile = new ConfigurationFile();
		configurationFileList.add( configurationFile );
		
		configurationFile.setFileName( kojakConfFile.getName() );
		configurationFile.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
		
		
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
				
				
				

				
				if ( DATABASE__FASTA_FILE.equals( lineParsed.key ) ) {

					processFastaFile( lineParsed, line, proxlInputRoot );
					
				
				} else if ( MS_DATA_FILE_CONFIG_KEY.equals( lineParsed.key ) ) {

//					kojakInputFilenamePossiblyWithPath = lineParsed.value;
					


				} else if ( CROSS_LINK_CONFIG_KEY.equals( lineParsed.key ) ) {
					
					parseCrosslinkMassConfig( lineParsed, line, proxlInputRoot );
					

				} else if ( MONO_LINK_CONFIG_KEY.equals( lineParsed.key ) ) {
										 
					parseMonolinkMassConfig( lineParsed, line, proxlInputRoot );

				} else if ( FIXED_MODIFICATION_CONFIG_KEY.equals( lineParsed.key ) ) {
					
					StaticModification staticModification = parseStaticMod( lineParsed.value, line );
					
					staticModificationList.add( staticModification );

				} else if ( DECOY_FILTER_CONFIG_KEY.equals( lineParsed.key ) ) {
					
					DecoyLabels decoyLabels = proxlInputRoot.getDecoyLabels();
							
					if ( decoyLabels == null ) {
						decoyLabels = new DecoyLabels();
						proxlInputRoot.setDecoyLabels( decoyLabels );
					}
					
					List<DecoyLabel> decoyLabelList = decoyLabels.getDecoyLabel();
					
					DecoyLabel decoyLabel = new DecoyLabel();
					decoyLabelList.add( decoyLabel );
					
					decoyLabel.setPrefix( lineParsed.value );
					
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
		validateCrosslinkMasses( proxlInputRoot, kojakConfFile );
		
		
		//  Throws exception if errors
		validateStaticMods( staticModificationList, kojakConfFile );
		
		
		//  Add Static modifications if list not empty
		
		if ( ! staticModificationList.isEmpty() ) {
		
			proxlInputRoot.setStaticModifications( staticModifications );
		}
		
	}
	

	/**
	 * Validate that every linker has at least one Crosslink Mass.
	 * @param kojakConfStaticMods
	 * @throws Exception 
	 */
	private void validateCrosslinkMasses( ProxlInput proxlInputRoot, File kojakConfFile ) throws Exception {

		Linkers linkers = proxlInputRoot.getLinkers();
		
		if ( linkers == null ) {
			
			String msg = "Linkers must be populated before adding crosslink masses";
			log.error( msg );
			throw new Exception( msg );
		}
		
		List<Linker> linkerList = linkers.getLinker();

		if ( linkerList == null || ( linkerList.isEmpty() ) ) {
			
			String msg = "Linkers must be populated before adding crosslink masses";
			log.error( msg );
			throw new Exception( msg );
		}

		for ( Linker linker : linkerList ) {
			

			String errorMsg = "ERROR: Linker does not have crosslink mass for config key '" 
					+ CROSS_LINK_CONFIG_KEY + "', Linker name: " + linker.getName()
					+ ", Kojak Conf file: " + kojakConfFile.getAbsolutePath();
		
			CrosslinkMasses crosslinkMasses = linker.getCrosslinkMasses();

			if ( crosslinkMasses == null ) {

				log.error( errorMsg );
				throw new ProxlGenXMLDataException( errorMsg );
			}

			List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();

			if ( crosslinkMassList == null || crosslinkMassList.isEmpty() ) {

				log.error( errorMsg );
				throw new ProxlGenXMLDataException( errorMsg );
			}
			
		}
		
	}
	
	

	/**
	 * Validate same residue is not in the list more than once.
	 * @param kojakConfStaticMods
	 * @throws ProxlGenXMLDataException 
	 */
	private void validateStaticMods( List<StaticModification> staticModificationList, File kojakConfFile ) throws ProxlGenXMLDataException {
		
		Set<String> aminoAcidSet = new HashSet<>();
		
		
		for ( StaticModification staticModification : staticModificationList ) {
			
			String aminoAcid = staticModification.getAminoAcid();
			
			if ( aminoAcidSet.contains( aminoAcid )) {
				
				
				String msg = "ERROR: Duplicate aminoAcid entries for config key '" 
						+ FIXED_MODIFICATION_CONFIG_KEY + "' ,Kojak Conf file: " + kojakConfFile.getAbsolutePath();
				
				log.error( msg );
				
				throw new ProxlGenXMLDataException(msg);
			}
			
			aminoAcidSet.add( aminoAcid );
		}
		
	}
	
	
	/**
	 * @param line
	 * @return
	 */
	private ParsedLine parseLine( String line ) {
		
 		String comment = "";
		
		//  Remove comment part of line, starts at "#" in any position on the line
		
		int commentCharPos = line.indexOf( '#' );
		
		if ( commentCharPos != -1 ) {
			
			comment = line.substring( commentCharPos + 1 );
			
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
		lineParsed.comment = comment;
		
		return lineParsed;
	}
	
	

	/**
	 * @param lineParsed
	 * @param line
	 * @param proxlInputRoot
	 */
	private void processFastaFile( ParsedLine lineParsed, String line, ProxlInput proxlInputRoot )  {
		
		if ( StringUtils.isEmpty( proxlInputRoot.getFastaFilename() ) ) {

			String lineParsedValue = lineParsed.value;

			File fastaFile = new File( lineParsedValue );

			String fastaFilename = fastaFile.getName();

			proxlInputRoot.setFastaFilename( fastaFilename );
		}
	}
	

	/**
	 * @param lineParsed
	 * @param line
	 * @param proxlInputRoot
	 * @throws Exception
	 */
	private void parseCrosslinkMassConfig( ParsedLine lineParsed, String line, ProxlInput proxlInputRoot ) throws Exception {
		

//		cross_link	=	1	1	138.0680742
		
		//  More than one linker so linker required in comments
		
//		cross_link = 1 2 -18.010595  #linker:edc

		String lineParsedValue = lineParsed.value;
		
		String[] lineParsedValueSplit = lineParsedValue.split( "\\s+" ); // split on white space.
		
		if ( lineParsedValueSplit.length != 3 ) {
			
			String msg = "Config key '" + CROSS_LINK_CONFIG_KEY + "' must have 3 values, 2 positions and a mass, line: " + line;
			
			log.error( msg );
			
			throw new ProxlGenXMLDataException(msg);
		}
		

		String massString = lineParsedValueSplit[ 2 ].trim();
		
		
		BigDecimal mass = null;
		
		try {
			
			mass = new BigDecimal( massString );
			
		} catch ( Exception e ) {
			
			
			String msg = "Config key '" + CROSS_LINK_CONFIG_KEY + "' mass is not parsable to BigDecimal.  mass: " 
					+ massString + ", line: " + line;
			
			log.error( msg, e );
			
			throw new ProxlGenXMLDataException(msg, e);
			
		}
		
		
		
		CrosslinkMass crosslinkMass = new CrosslinkMass();

		crosslinkMass.setMass( mass );
		

		Linkers linkers = proxlInputRoot.getLinkers();
		
		if ( linkers == null ) {
			
			String msg = "Linkers must be populated before adding crosslink masses";
			log.error( msg );
			throw new Exception( msg );
		}
		
		List<Linker> linkerList = linkers.getLinker();

		if ( linkerList == null || ( linkerList.isEmpty() ) ) {
			
			String msg = "Linkers must be populated before adding crosslink masses";
			log.error( msg );
			throw new Exception( msg );
		}

		Linker linker = null;
		
		if ( linkerList.size() > 1 ) {
			
			//  throws exception if no matching linker found for name or name not in comment
			linker = getLinkerForLinkerAbbr( lineParsed, line, linkerList );
			
		} else {
			
			linker = linkerList.get( 0 );
		}
		
		
		CrosslinkMasses crosslinkMasses = linker.getCrosslinkMasses();
		
		if ( crosslinkMasses == null ) {

			crosslinkMasses = new CrosslinkMasses();
			linker.setCrosslinkMasses( crosslinkMasses );
		}
		
		List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();

		crosslinkMassList.add( crosslinkMass );
	}
	
	

	/**
	 * @param lineParsedValue
	 * @param line
	 * @return
	 * @throws Exception 
	 */
	private void parseMonolinkMassConfig( ParsedLine lineParsed, String line, ProxlInput proxlInputRoot ) throws Exception {
		
//		mono_link	=	1	155.0946
//		mono_link	=	1	156.0786
		
		String lineParsedValue = lineParsed.value;

		String[] lineParsedValueSplit = lineParsedValue.split( "\\s+" ); // split on white space.
		
		if ( lineParsedValueSplit.length != 2 ) {
			
			String msg = "Config key '" + MONO_LINK_CONFIG_KEY + "' must have 2 values, a position and a mass, line: " + line;
			
			log.error( msg );
			
			throw new ProxlGenXMLDataException(msg);
		}
		
		String massString = lineParsedValueSplit[ 1 ].trim();
		
		
		BigDecimal mass = null;
		
		try {
			
			mass = new BigDecimal( massString );
			
		} catch ( Exception e ) {
			
			
			String msg = "Config key '" + MONO_LINK_CONFIG_KEY + "' mass is not parsable to BigDecimal.  mass: " 
					+ massString + ", line: " + line;
			
			log.error( msg, e );
			
			throw new ProxlGenXMLDataException(msg, e);
			
		}
		
		
		
		MonolinkMass monolinkMass = new MonolinkMass();

		monolinkMass.setMass( mass );


		Linkers linkers = proxlInputRoot.getLinkers();
		
		if ( linkers == null ) {
			
			String msg = "Linkers must be populated before adding monolink masses";
			log.error( msg );
			throw new Exception( msg );
		}
		
		List<Linker> linkerList = linkers.getLinker();

		if ( linkerList == null || ( linkerList.isEmpty() ) ) {
			
			String msg = "Linkers must be populated before adding monolink masses";
			log.error( msg );
			throw new Exception( msg );
		}

		Linker linker = null;
		
		if ( linkerList.size() > 1 ) {
			
			//  throws exception if no matching linker found for name or name not in comment
			linker = getLinkerForLinkerAbbr( lineParsed, line, linkerList );
			
		} else {
			
			linker = linkerList.get( 0 );
		}
		
		
		MonolinkMasses monolinkMasses = linker.getMonolinkMasses();
		
		if ( monolinkMasses == null ) {

			monolinkMasses = new MonolinkMasses();
			linker.setMonolinkMasses( monolinkMasses );
		}
		
		List<MonolinkMass> monolinkMassList = monolinkMasses.getMonolinkMass();

		monolinkMassList.add( monolinkMass );
	}
	

	/**
	 * @param lineParsed
	 * @param line
	 * @param linkerList
	 * @return
	 * @throws ProxlGenXMLDataException - if no matching linker found for name or name not in comment
	 */
	private Linker getLinkerForLinkerAbbr( ParsedLine lineParsed, String line, List<Linker> linkerList ) throws ProxlGenXMLDataException {
		
		//  First get linker name from comment

		//  More than one linker so linker required in comments
		
//		cross_link = 1 2 -18.010595  #linker:edc
		
		final String linkerInCommentPrefix = "linker:";
		
		final String errMsgBase = "For more than one linker, the linker name is required in the command.  "
				+ "Example: '#linker:edc' where the comment starts with 'linker:' and is followed by the "
				+ "linker name as entered on the command line, in this example is 'edc'.  "
				+ "The actual text would not include the quotes.";
		
		String comment = lineParsed.comment;
		
		if ( StringUtils.isEmpty( comment ) ) {
			
			String msg = errMsgBase;
			log.error( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		
		comment = comment.trim();
		
		if ( ! comment.startsWith( linkerInCommentPrefix ) ) {

			String msg = errMsgBase;
			log.error( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		
		String stringAfterLinkerPrefix = comment.substring( linkerInCommentPrefix.length() );
		
		String[] stringAfterLinkerPrefixSplit = stringAfterLinkerPrefix.split( "\\s+" ); // split on white space.

		String linkerAbbr = stringAfterLinkerPrefixSplit[ 0 ];
		
		Linker linkerForAbbr = null;
		
		for ( Linker item : linkerList ) {
			
			if ( item.getName().equals( linkerAbbr) )  {
				
				linkerForAbbr = item;
				break;
			}
		}
		
		if ( linkerForAbbr == null ) {
			
			String msg = "No linker name on command line for linker name found in Kojak conf file (in comment starting with '"
					+ linkerInCommentPrefix
					+ "'):  '"
					+ linkerAbbr
					+ "'.  " + errMsgBase;
			
			log.error( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		
		return linkerForAbbr;
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
		String comment;
		
	}
	
	
}
