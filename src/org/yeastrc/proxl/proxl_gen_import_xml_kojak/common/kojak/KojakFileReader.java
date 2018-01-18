package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.IsotopeLabelValuesConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakFileContentsConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;


/**
 * Reader for Kojak File
 *
 */
public class KojakFileReader {

	private static final Logger log = Logger.getLogger(KojakFileReader.class);
	
	private static final String FIELD_HAS_NO_VALUE = "-";
	
			
	private static final int INDEX_INIT_VALUE = -2;
	
	private Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile;
	
	private File inputFile;
	
	private BufferedReader reader;
	
	private int kojakFileLineNumber = 0;
	
	private String programVersionLine;
	
	private String programVersion;

	private String headerLine;
	
	private int headerLineElementCount = -1;


	private Set<String> filteredAnnotationNamesFromColumnHeaders = new HashSet<>();
	private Set<String> descriptiveAnnotationNamesFromColumnHeaders = new HashSet<>();
	

	private String[] headerStrings;

	//  These are extracted for specific processing
	
	private int PROTEIN_1_HeaderIndex = INDEX_INIT_VALUE;
	private int PROTEIN_2_HeaderIndex = INDEX_INIT_VALUE;

	private int PROTEIN_1_SITE_HeaderIndex = INDEX_INIT_VALUE;
	private int PROTEIN_2_SITE_HeaderIndex = INDEX_INIT_VALUE;

	
	//  These are extracted for specific processing and storage
	
	private int SCAN_NUMBER_HeaderIndex = INDEX_INIT_VALUE;

	private int PEPTIDE_1_HeaderIndex = INDEX_INIT_VALUE;
	private int LINK_1_HeaderIndex = INDEX_INIT_VALUE;
	
	private int PEPTIDE_2_HeaderIndex = INDEX_INIT_VALUE;
	private int LINK_2_HeaderIndex = INDEX_INIT_VALUE;
	
	private int LINKER_MASS_HeaderIndex = INDEX_INIT_VALUE;
	
	private int CHARGE_HeaderIndex = INDEX_INIT_VALUE;

	//  These are extracted for specific processing and storage
	
	private int PEPTIDE_1_SCORE_HeaderIndex = INDEX_INIT_VALUE;
	private int PEPTIDE_2_SCORE_HeaderIndex = INDEX_INIT_VALUE;

	
	
	//   Filterable annotations
	
	
//	Score is Kojak's primary score assignment (bigger is better) 
//	dScore is the difference between that score and the next score (bigger is better).
	private int SCORE_HeaderIndex = INDEX_INIT_VALUE;
	private int DSCORE_HeaderIndex = INDEX_INIT_VALUE;
	private int PEPDIFF_HeaderIndex = INDEX_INIT_VALUE;

	//  All other are descriptive annotations

	
	
	
	
	/**
	 * private constructor
	 */
	private KojakFileReader(){}
	
	//  Make Package Private
	
	static KojakFileReader getInstance( File inputFile, Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile ) throws Exception {
		
		KojakFileReader kojakFileReader = new KojakFileReader();
		
		kojakFileReader.inputFile = inputFile;
		kojakFileReader.isotopes_SpecifiedIn_KojakConfFile = isotopes_SpecifiedIn_KojakConfFile;
		
		try {
			
			kojakFileReader.init();
			
		} catch ( Exception e ) {
			
			try {
				kojakFileReader.close();
			} catch ( Exception eClose ) {
				
			}
			
			throw e;
		}
		
		return kojakFileReader;
	}
	

	/**
	 * @param inputFile
	 * @return
	 * @throws Exception 
	 */
	static String getKojakVersionOnly( File inputFile ) throws Exception {

		String programVersion = null;
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		
		try {

			fileInputStream = new FileInputStream( inputFile );

			inputStreamReader = new InputStreamReader( fileInputStream, KojakFileContentsConstants.KOJAK_FILE_INPUT_CHARACTER_SET );

			reader = new BufferedReader( inputStreamReader );
			
			String headerLine = reader.readLine();

			if ( headerLine == null ) {
				String msg = "Kojak file header line does not exist.  Kojak file: " + inputFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}

			if ( StringUtils.isEmpty( headerLine ) ) {
				String msg = "Kojak file header line is empty.  Kojak file: " + inputFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}

			if ( headerLine.startsWith( KojakFileContentsConstants.PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK ) ) {

				String programVersionLine = headerLine;
				programVersion = programVersionLine.substring( KojakFileContentsConstants.PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK.length() );
			}
		} catch ( Exception e ) {
			throw e;
			
		} finally {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			if ( inputStreamReader != null ) {
				inputStreamReader.close();
			}
			if ( fileInputStream != null ) {
				fileInputStream.close();
			}
		}

		return programVersion;
	}
	
	/**
	 * @throws Exception 
	 */
	private void init() throws Exception {
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		
		try {

			fileInputStream = new FileInputStream( inputFile );

			inputStreamReader = new InputStreamReader( fileInputStream, KojakFileContentsConstants.KOJAK_FILE_INPUT_CHARACTER_SET );

			reader = new BufferedReader( inputStreamReader );
			
			headerLine = reader.readLine();
			kojakFileLineNumber++;

			if ( headerLine == null ) {
				String msg = "Kojak file header line does not exist.  Kojak file: " + inputFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}

			if ( StringUtils.isEmpty( headerLine ) ) {
				String msg = "Kojak file header line is empty.  Kojak file: " + inputFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}

			if ( headerLine.startsWith( KojakFileContentsConstants.PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK ) ) {

				programVersionLine = headerLine;
				programVersion = programVersionLine.substring( KojakFileContentsConstants.PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK.length() );

				//  Next line is actually the header line so read it

				headerLine = reader.readLine();
				kojakFileLineNumber++;
				
				if ( headerLine == null ) {
					String msg = "Kojak file header line does not exist.  Kojak file: " + inputFile.getAbsolutePath();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( StringUtils.isEmpty( headerLine ) ) {
					String msg = "Kojak file header line is empty.  Kojak file: " + inputFile.getAbsolutePath();
					log.error( msg );
					throw new Exception(msg);
				}
			} else {
				String msg = "Kojak file does not start with version header line.  Kojak file: " + inputFile.getAbsolutePath()
						+ ".  First line in file: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

			String[] headerLineSplit = headerLine.split( "\t" );

			headerLineElementCount = headerLineSplit.length;
			
			headerStrings = headerLineSplit;

			for ( int headerElementIndex = 0; headerElementIndex < headerLineSplit.length; headerElementIndex++ ) {

				String headerElement = headerLineSplit[ headerElementIndex ];

				//  This first block contains headers that will not be stored:
				
				if ( KojakFileContentsConstants.PROTEIN_1_HEADER_LABEL.equals( headerElement ) ) {

					PROTEIN_1_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.PROTEIN_2_HEADER_LABEL.equals( headerElement ) ) {

					PROTEIN_2_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.PROTEIN_1_SITE_HEADER_LABEL.equals( headerElement ) ) {

					PROTEIN_1_SITE_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.PROTEIN_2_SITE_HEADER_LABEL.equals( headerElement ) ) {

					PROTEIN_2_SITE_HeaderIndex = headerElementIndex;


					/////////////////////////////
					
					//   This block contains value that will be saved off for specific processing
				

				} else if ( KojakFileContentsConstants.PEPTIDE_1_SCORE_HEADER_LABEL.equals( headerElement ) ) {

					PEPTIDE_1_SCORE_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.PEPTIDE_2_SCORE_HEADER_LABEL.equals( headerElement ) ) {

					PEPTIDE_2_SCORE_HeaderIndex = headerElementIndex;

					
					
				} else if ( KojakFileContentsConstants.SCAN_NUMBER_HEADER_LABEL.equals( headerElement ) ) {

					SCAN_NUMBER_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.CHARGE_HEADER_LABEL.equals( headerElement ) ) {

					CHARGE_HeaderIndex = headerElementIndex;



				} else if ( KojakFileContentsConstants.PEPTIDE_1_HEADER_LABEL.equals( headerElement ) ) {

					PEPTIDE_1_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.LINK_1_HEADER_LABEL_Pre_1_6_1.equals( headerElement )
						|| KojakFileContentsConstants.LINK_1_HEADER_LABEL_Start_1_6_1.equals( headerElement ) ) {

					if ( LINK_1_HeaderIndex != INDEX_INIT_VALUE ) {
						String msg = "Link 1 position Header Index already set.  Prev Value: " + LINK_1_HeaderIndex
								+ ", Current headerElementIndex: " + headerElementIndex
								+ ", Link 1 position Header String is one of two options: '"
								+ KojakFileContentsConstants.LINK_1_HEADER_LABEL_Pre_1_6_1
								+ "' or '"
								+ KojakFileContentsConstants.LINK_1_HEADER_LABEL_Pre_1_6_1
								+ "'.";
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}
					LINK_1_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.PEPTIDE_2_HEADER_LABEL.equals( headerElement ) ) {

					PEPTIDE_2_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.LINK_2_HEADER_LABEL_Pre_1_6_1.equals( headerElement )
						|| KojakFileContentsConstants.LINK_2_HEADER_LABEL_Start_1_6_1.equals( headerElement ) ) {

					if ( LINK_2_HeaderIndex != INDEX_INIT_VALUE ) {
						String msg = "Link 2 position Header Index already set.  Prev Value: " + LINK_2_HeaderIndex
								+ ", Current headerElementIndex: " + headerElementIndex
								+ ", Link 2 position Header String is one of two options: '"
								+ KojakFileContentsConstants.LINK_2_HEADER_LABEL_Pre_1_6_1
								+ "' or '"
								+ KojakFileContentsConstants.LINK_2_HEADER_LABEL_Pre_1_6_1
								+ "'.";
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}
					LINK_2_HeaderIndex = headerElementIndex;

				} else if ( KojakFileContentsConstants.LINKER_MASS_HEADER_LABEL.equals( headerElement ) ) {

					LINKER_MASS_HeaderIndex = headerElementIndex;


					/////////////////////////////
					
					//   This block contains value that will be saved in Filtered Annotations

				} else if ( KojakFileContentsConstants.SCORE_HEADER_LABEL.equals( headerElement ) ) {

					SCORE_HeaderIndex = headerElementIndex;
					
					if ( ! filteredAnnotationNamesFromColumnHeaders.add( headerElement ) ) {
						
						String msg = "Column header value '" + headerElement + "' occurs more than once in "
								 + " Kojak file: " + inputFile.getAbsolutePath()
								 + ", headerLine: " + headerLine;
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}

				} else if ( KojakFileContentsConstants.DSCORE_HEADER_LABEL.equals( headerElement ) ) {

					DSCORE_HeaderIndex = headerElementIndex;

					if ( ! filteredAnnotationNamesFromColumnHeaders.add( headerElement ) ) {
						
						String msg = "Column header value '" + headerElement + "' occurs more than once in "
								 + " Kojak file: " + inputFile.getAbsolutePath()
								 + ", headerLine: " + headerLine;
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}

				} else if ( KojakFileContentsConstants.PEPDIFF_HEADER_LABEL.equals( headerElement ) ) {

					PEPDIFF_HeaderIndex = headerElementIndex;

					if ( ! filteredAnnotationNamesFromColumnHeaders.add( headerElement ) ) {
						
						String msg = "Column header value '" + headerElement + "' occurs more than once in "
								 + " Kojak file: " + inputFile.getAbsolutePath()
										+ ", headerLine: " + headerLine;
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}

				} else  {


					/////////////////////////////
					
					//   All other headers will be saved in Descriptive Annotations
				
					if ( ! descriptiveAnnotationNamesFromColumnHeaders.add( headerElement ) ) {
						
						String msg = "Column header value '" + headerElement + "' occurs more than once in "
								 + " Kojak file: " + inputFile.getAbsolutePath()
										+ ", headerLine: " + headerLine;
						log.error( msg );
						throw new ProxlGenXMLDataException( msg );
					}

				}		
			}

			
			if ( SCAN_NUMBER_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.SCAN_NUMBER_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

			if ( CHARGE_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.CHARGE_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

			if ( SCORE_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.SCORE_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( DSCORE_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.DSCORE_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			
			//  Remove requirement for file have 'Pep. Diff.' since not in file starting with 1.6.1 (or maybe sooner)
//			if ( PEPDIFF_HeaderIndex == INDEX_INIT_VALUE ) {
//
//				String msg = "Kojak file header line does not contain header label '" 
//						+ KojakFileContentsConstants.PEPDIFF_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
//						+ ", headerLine: " + headerLine;
//				log.error( msg );
//				throw new Exception(msg);
//			}
			if ( PEPTIDE_1_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.PEPTIDE_1_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( LINK_1_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label.  Must be one of two options: '" 
						+ KojakFileContentsConstants.LINK_1_HEADER_LABEL_Pre_1_6_1 
						+ "' or '"
						+ KojakFileContentsConstants.LINK_1_HEADER_LABEL_Start_1_6_1
						+ "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( PROTEIN_1_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.PROTEIN_1_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

			if ( PEPTIDE_2_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.PEPTIDE_2_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( LINK_2_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label.  Must be one of two options: '" 
						+ KojakFileContentsConstants.LINK_2_HEADER_LABEL_Pre_1_6_1 
						+ "' or '"
						+ KojakFileContentsConstants.LINK_2_HEADER_LABEL_Start_1_6_1
						+ "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( PROTEIN_2_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.PROTEIN_2_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

			if ( ( PEPTIDE_1_SCORE_HeaderIndex == INDEX_INIT_VALUE && PEPTIDE_2_SCORE_HeaderIndex != INDEX_INIT_VALUE ) 
					|| ( PEPTIDE_1_SCORE_HeaderIndex != INDEX_INIT_VALUE && PEPTIDE_2_SCORE_HeaderIndex == INDEX_INIT_VALUE ) ) {

				String msg = "Kojak file header line must either contain both header labels"
						+ " '" + KojakFileContentsConstants.PEPTIDE_1_SCORE_HEADER_LABEL + "' "
						+ " and '" + KojakFileContentsConstants.PEPTIDE_2_SCORE_HEADER_LABEL + "' "
						+ " or it must not contain either of them. "
						+ "  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( LINKER_MASS_HeaderIndex == INDEX_INIT_VALUE ) {

				String msg = "Kojak file header line does not contain header label '" 
						+ KojakFileContentsConstants.LINKER_MASS_HEADER_LABEL + "'.  Kojak file: " + inputFile.getAbsolutePath()
						+ ", headerLine: " + headerLine;
				log.error( msg );
				throw new Exception(msg);
			}

		} catch ( Exception e ) {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			if ( inputStreamReader != null ) {
				inputStreamReader.close();
			}
			if ( fileInputStream != null ) {
				fileInputStream.close();
			}
			throw e;
		}
	}
	
	
	/**
	 * @return the first line of the file if it is the program version
	 */
	public String getProgramVersionLine() {
		return programVersionLine;
	}
	

	/**
	 * @return the Kojak program version
	 */
	public String getProgramVersion() {
		return programVersion;
	}
	

	public boolean headerHasPeptide_1_score() {
		return PEPTIDE_1_SCORE_HeaderIndex != INDEX_INIT_VALUE;
	}

	public boolean headerHasPeptide_2_score() {
		return PEPTIDE_2_SCORE_HeaderIndex != INDEX_INIT_VALUE;
	}

	//  Make Package Private
	
	/**
	 * @throws IOException
	 */
	void close() throws IOException {
		if ( reader != null ) {
			try {
				reader.close();
			} finally {
				reader = null;
			}
		}
	}
	
	//  Make Package Private
	
	KojakPsmDataObject getNextKojakLine() throws Exception {

		if ( reader == null ) {
			return null;
		}
		
		String line = reader.readLine();
		kojakFileLineNumber++;
		
		if ( line == null ) {
			close();
			return null;
		}
		
		KojakPsmDataObject kojakFileParsedLine = parseKojakRecord( line );
		kojakFileParsedLine.setKojakFileLineNumber( kojakFileLineNumber );
		
		return kojakFileParsedLine;
	}
	
	
	/**
	 * @return
	 * @throws Exception 
	 */
	private KojakPsmDataObject parseKojakRecord( String line ) throws Exception {
		
		KojakPsmDataObject kojakPsmDataObject = new KojakPsmDataObject();
		
		Map<String, BigDecimal> filteredAnnotations = new HashMap<>();
		Map<String, String> descriptiveAnnotations = new HashMap<>();
		
		kojakPsmDataObject.setFilteredAnnotations( filteredAnnotations );
		kojakPsmDataObject.setDescriptiveAnnotations( descriptiveAnnotations );
		
		String[] lineSplit = line.split( "\t" );
		
		if ( lineSplit.length != headerLineElementCount ) {
			
			String msg = "Kojak file: " 
					+ ", number of line elements split on tab (" 
					+ lineSplit.length
					+ ")"
					+ " does not match the number of header elements split on tab ("
					+ headerLineElementCount
					+ ")"
					+ ", Kojak file: " + inputFile.getAbsolutePath()
					+ ", line: " + line;
			log.error( msg );
			throw new Exception(msg);
		}

		for ( int lineSplitIndex = 0; lineSplitIndex < lineSplit.length; lineSplitIndex++ ) {

			//  This first block contains headers that will not be stored:
			
			if ( lineSplitIndex == PROTEIN_1_HeaderIndex ) {
			
				String protein_1 = lineSplit[ lineSplitIndex ];
				
				kojakPsmDataObject.setProtein_1( protein_1 );

			} else if ( lineSplitIndex == PROTEIN_2_HeaderIndex ) {

				String protein_2 = lineSplit[ lineSplitIndex ];
				
				kojakPsmDataObject.setProtein_2( protein_2 );

			} else if ( lineSplitIndex == PROTEIN_1_SITE_HeaderIndex ) {

				String protein_1_site = lineSplit[ lineSplitIndex ];

				kojakPsmDataObject.setProtein_1_site( protein_1_site );

			} else if ( lineSplitIndex == PROTEIN_2_SITE_HeaderIndex ) {

				String protein_2_site = lineSplit[ lineSplitIndex ];

				kojakPsmDataObject.setProtein_2_site( protein_2_site );
				
				/////////////////////////////
				
				//   This block contains value that will be saved off for specific processing
			
				

			} else if ( lineSplitIndex == PEPTIDE_1_HeaderIndex ) {
			
				String peptide_1 = lineSplit[ lineSplitIndex ];
				
				if ( isotopes_SpecifiedIn_KojakConfFile != null
						&& isotopes_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null
						&& peptide_1.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) {
	
					kojakPsmDataObject.setPeptide_1_Isotope_Label_For_ProxlXML_File( IsotopeLabelValuesConstants.ISOTOPE_LABEL_15N );
					
					String peptide_1_Isotope_Label_Suffix_From_Kojak_File = 
							peptide_1.substring( peptide_1.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
					
					kojakPsmDataObject.setPeptide_1_Isotope_Label_Suffix_From_Kojak_File( peptide_1_Isotope_Label_Suffix_From_Kojak_File );

					peptide_1 = peptide_1.substring( 0, peptide_1.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
				}
				
				kojakPsmDataObject.setPeptide_1( peptide_1 );
				

			} else if ( lineSplitIndex == PEPTIDE_2_HeaderIndex ) {
			
				String peptide_2 = lineSplit[ lineSplitIndex ];

				if ( isotopes_SpecifiedIn_KojakConfFile != null
						&& isotopes_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null
						&& peptide_2.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) {
	
					kojakPsmDataObject.setPeptide_2_Isotope_Label_For_ProxlXML_File( IsotopeLabelValuesConstants.ISOTOPE_LABEL_15N );
					
					String peptide_2_Isotope_Label_Suffix_From_Kojak_File = 
							peptide_2.substring( peptide_2.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
					
					kojakPsmDataObject.setPeptide_2_Isotope_Label_Suffix_From_Kojak_File( peptide_2_Isotope_Label_Suffix_From_Kojak_File );

					peptide_2 = peptide_2.substring( 0, peptide_2.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
				}
				
				kojakPsmDataObject.setPeptide_2( peptide_2 );
				
				
			} else if ( lineSplitIndex == LINK_1_HeaderIndex ) {
			
				String linkPosition_1 = lineSplit[ lineSplitIndex ];

				kojakPsmDataObject.setLink_1( linkPosition_1 );
				
			} else if ( lineSplitIndex == LINK_2_HeaderIndex ) {
			
				String linkPosition_2 = lineSplit[ lineSplitIndex ];

				kojakPsmDataObject.setLink_2( linkPosition_2 );

				
				
			} else if ( lineSplitIndex == SCAN_NUMBER_HeaderIndex ) {
			
				String scanNumberString = lineSplit[ lineSplitIndex ];

				try {
					
					int scanNumber = Integer.parseInt( scanNumberString );
					kojakPsmDataObject.setScanNumber( scanNumber );
					
				} catch ( Exception ex ) {
					
					String msg = "Kojak file 'scan number'"
							+ " as identified by the header label '" + KojakFileContentsConstants.SCAN_NUMBER_HEADER_LABEL
							+ "' is not parsible as integer."
							+ " scanNumberString: '" + scanNumberString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new Exception(msg);
				}
				
			} else if ( lineSplitIndex == CHARGE_HeaderIndex ) {
				
				String chargeString = lineSplit[ lineSplitIndex ];

				try {
					
					int charge = Integer.parseInt( chargeString );
					kojakPsmDataObject.setCharge( charge );
					
				} catch ( Exception ex ) {
					
					String msg = "Kojak file 'charge'"
							+ " as identified by the header label '" + KojakFileContentsConstants.CHARGE_HEADER_LABEL
							+ "' is not parsible as integer."
							+ " chargeString: '" + chargeString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new Exception(msg);
				}



			} else if ( lineSplitIndex == PEPTIDE_1_SCORE_HeaderIndex ) {

				String peptide_1_scoreString = lineSplit[ lineSplitIndex ];

				try {
					BigDecimal peptide_1_score = new BigDecimal( peptide_1_scoreString );
					kojakPsmDataObject.setPeptide_1_score( peptide_1_score );

				} catch ( Exception e ) {
					String msg = "Kojak file 'Peptide 1 score'"
							+ " as identified by the header label '" + KojakFileContentsConstants.PEPTIDE_1_SCORE_HEADER_LABEL
							+ "' is not parsible as decimal."
							+ " peptide_1_scoreString: '" + peptide_1_scoreString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new ProxlGenXMLDataException(msg);
				}


			} else if ( lineSplitIndex == PEPTIDE_2_SCORE_HeaderIndex ) {

				String peptide_2_scoreString = lineSplit[ lineSplitIndex ];

				try {
					BigDecimal peptide_2_score = new BigDecimal( peptide_2_scoreString );
					kojakPsmDataObject.setPeptide_2_score( peptide_2_score );

				} catch ( Exception e ) {
					String msg = "Kojak file 'Peptide 2 score'"
							+ " as identified by the header label '" + KojakFileContentsConstants.PEPTIDE_2_SCORE_HEADER_LABEL
							+ "' is not parsible as decimal."
							+ " peptide_2_scoreString: '" + peptide_2_scoreString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new ProxlGenXMLDataException(msg);
				}


			} else if ( lineSplitIndex == LINKER_MASS_HeaderIndex ) {

				String linkerMassString = lineSplit[ lineSplitIndex ];
				
				if ( ! FIELD_HAS_NO_VALUE.equals( linkerMassString ) ) {
					
					//  Only try to parse if not "-"

					try {

						BigDecimal linkerMass = new BigDecimal( linkerMassString );
						kojakPsmDataObject.setLinkerMass( linkerMass );;

					} catch ( Exception ex ) {

						String msg = "Kojak file 'linker mass'"
								+ " as identified by the header label '" + KojakFileContentsConstants.LINKER_MASS_HEADER_LABEL
								+ "' is not parsible as decimal."
								+ " linkerMassString: '" + linkerMassString
								+ "', Kojak file: " + inputFile.getAbsolutePath()
								+ ", line: " + line;
						log.error( msg );
						throw new ProxlGenXMLDataException(msg);
					}

				}
				
				
				/////////////////////////////
				
				//   This block contains value that will be saved in Filtered Annotations
			

			} else if ( lineSplitIndex == SCORE_HeaderIndex ) {

				String scoreString = lineSplit[ lineSplitIndex ];

				try {
					
					BigDecimal score = new BigDecimal( scoreString );
					
					filteredAnnotations.put( KojakFileContentsConstants.SCORE_HEADER_LABEL, score );
					
					
				} catch ( Exception ex ) {
					
					String msg = "Kojak file 'score'"
							+ " as identified by the header label '" + KojakFileContentsConstants.SCORE_HEADER_LABEL
							+ "' is not parsible as decimal."
							+ " scoreString: '" + scoreString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new Exception(msg);
				}

			} else if ( lineSplitIndex == DSCORE_HeaderIndex ) {

				String dScoreString = lineSplit[ lineSplitIndex ];

				try {
					
					BigDecimal dScore = new BigDecimal( dScoreString );
					
					filteredAnnotations.put( KojakFileContentsConstants.DSCORE_HEADER_LABEL, dScore );
					
					
				} catch ( Exception ex ) {
					
					String msg = "Kojak file 'dscore'"
							+ " as identified by the header label '" + KojakFileContentsConstants.DSCORE_HEADER_LABEL
							+ "' is not parsible as decimal."
							+ " DScoreString: '" + dScoreString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new Exception(msg);
				}
			} else if ( lineSplitIndex == PEPDIFF_HeaderIndex ) {

				String pepdiffString = lineSplit[ lineSplitIndex ];
				try {
					BigDecimal dScore = new BigDecimal( pepdiffString );
					filteredAnnotations.put( KojakFileContentsConstants.PEPDIFF_HEADER_LABEL, dScore );
					
				} catch ( Exception ex ) {
					String msg = "Kojak file 'Pep. Diff.' as identified by the header label '" 
							+ KojakFileContentsConstants.PEPDIFF_HEADER_LABEL
							+ "' is not parsible as decimal."
							+ " PepDiffString: '" + pepdiffString
							+ "', Kojak file: " + inputFile.getAbsolutePath()
							+ ", line: " + line;
					log.error( msg );
					throw new Exception(msg);
				}	
			} else {
				
				/////////////////////////////
				
				//   All other headers will be saved in Descriptive Annotations
			
				
				String headerString = headerStrings[ lineSplitIndex ];
				
				String valueOnLine = lineSplit[ lineSplitIndex ];

				descriptiveAnnotations.put( headerString, valueOnLine );
				
			}
		}
		
		
		return kojakPsmDataObject;
	}
	
	
	

	public Set<String> getFilteredAnnotationNamesFromColumnHeaders() {
		return filteredAnnotationNamesFromColumnHeaders;
	}

	public Set<String> getDescriptiveAnnotationNamesFromColumnHeaders() {
		return descriptiveAnnotationNamesFromColumnHeaders;
	}

	
}
