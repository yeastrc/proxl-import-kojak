package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.IsotopeLabelValuesConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.Proxl_XML_Peptide_UniqueId_Constants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLInternalException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakSequenceUtils;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Kojak_GetDynamicModsForOneSequence;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Kojak_GetDynamicModsForOneSequence.Kojak_GetDynamicModsForOneSequence_Result;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel;


/**
 * 
 *
 */
public class ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide {

	private static final Logger log = Logger.getLogger( ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide.class );
	
	//  private constructor
	private ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide() {}


	public static ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide getInstance() {
		
		return new ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide();
	}
	
	

	/**
	 * Parse PercolatorReportedPeptide Into ProxlInputReportedPeptide
	 * 
	 * In ProxlInputReportedPeptide, set:
	 * 
	 *    1)  ReportedPeptideString
	 *    2)  Type - Link Type
	 *    3)  Peptides - The Parsed Peptides and their children objects for Dynamic Modifications
	 * 
	 * @param percolatorPeptide
	 * @return ReportedPeptide proxlInputReportedPeptide
	 * @throws ProxlGenXMLDataException 
	 * @throws ProxlGenXMLInternalException 
	 */
	public ReportedPeptide parsePercolatorReportedPeptideIntoProxlInputReportedPeptide( 
			IPeptide percolatorPeptide, 
			Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile ) throws ProxlGenXMLDataException, ProxlGenXMLInternalException {
			
		ReportedPeptide proxlInputReportedPeptide = new ReportedPeptide();

		String reportedPeptideSequence = percolatorPeptide.getPeptideId();
		
		proxlInputReportedPeptide.setReportedPeptideString( reportedPeptideSequence );

		LinkType linkType = getLinkType( reportedPeptideSequence );
		
		proxlInputReportedPeptide.setType( linkType );

		Peptides proxlInputPeptides = getProxlInputPeptides( reportedPeptideSequence, linkType, isotopes_SpecifiedIn_KojakConfFile );
		
		proxlInputReportedPeptide.setPeptides( proxlInputPeptides );
	
		return proxlInputReportedPeptide;
	}
	
	
	

	/**
	 * Get the type of link represented in the string as defined by these rules:
	 * 
	 * 	unlinked: will look like a single peptide, e.g. LAADTGKGGQR
	 *  monolinked: peptide-Mono, e.g. EVYSLEKCYR-Mono
	 *  looplinked: peptide-Loop, e.g AKIVQKSSGLNMENLANHEHLLSPVR-Loop
	 *  dimer: FADQEGLTSSVGEYNENTIQQLLLPK+FADQEGLTSSVGEYNENTIQQLLLPK
	 *  crosslinked: peptide--peptide, e.g. AKLCQLDPVLYEK--NMNAILFDELSKER
	 * 
	 * @param reportedPeptideSequence
	 * @return
	 * @throws Exception 
	 */
	public static LinkType getLinkType( String reportedPeptideSequence ) {
		
		
		if( reportedPeptideSequence.contains( "--" ) )
			return LinkType.CROSSLINK;
		
		if( reportedPeptideSequence.contains( "-Loop" ) )
			return LinkType.LOOPLINK;
		
		if( reportedPeptideSequence.contains( "-LOOP" ) )
			return LinkType.LOOPLINK;
		
		
		// Comment out since Dimer is just unlinked type
//		if( reportedPeptideSequence.contains( "+" ) )
//			return LinkType.;

		
		return LinkType.UNLINKED;
	}
	
	

	/**
	 * @param reportedPeptideSequence
	 * @param linkType
	 * @return
	 * @throws ProxlGenXMLDataException 
	 * @throws ProxlGenXMLInternalException 
	 */
	private Peptides getProxlInputPeptides( 
			String reportedPeptideSequence, 
			LinkType linkType,
			Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile ) throws ProxlGenXMLDataException, ProxlGenXMLInternalException {
	
		Peptides proxlInputPeptides = new Peptides();
	
		List<Peptide> proxlInputPeptideList = proxlInputPeptides.getPeptide();
		
		
		ParseKojakReportedPeptideSequenceResult parseKojakReportedPeptideSequenceResult =
				getSequencesFromPercolatorReportedPeptideSequence( reportedPeptideSequence, linkType, isotope_Labels_SpecifiedIn_KojakConfFile );
		
		String peptideUniqueId = null;
		
		int listItemCounter = 0;
		
		for ( ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem : parseKojakReportedPeptideSequenceResult.parseKojakReportedPeptideSequenceResultItemList ) {

			listItemCounter++;
			
			if ( listItemCounter == 1 ) {
				peptideUniqueId = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1;
			} else if ( listItemCounter == 2 ) {
				peptideUniqueId = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2;
			} else {
				String msg = "More than 2 entries in parseKojakReportedPeptideSequenceResult.parseKojakReportedPeptideSequenceResultItemList";
				log.error( msg );
				throw new ProxlGenXMLDataException( msg );
			}
			
			String peptideSequenceWithMods = parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods;

			String peptideIsotopeLabelString = parseKojakReportedPeptideSequenceResultItem.isotope_Label_For_ProxlXML_File;


			
			String peptideSequenceForProxlXML_PeptideObject = 
					KojakSequenceUtils.getSingletonInstance()
					.getPeptideWithDynamicModificationsRemoved( peptideSequenceWithMods, isotope_Labels_SpecifiedIn_KojakConfFile );

			if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {
				if ( peptideSequenceForProxlXML_PeptideObject.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) {
					
					if ( peptideIsotopeLabelString == null ) {
						String msg = "peptideIsotopeLabelString == null but found '"
								+ IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR
								+ "' on end of peptide sequence: " + peptideSequenceForProxlXML_PeptideObject
								+ ", reportedPeptideSequence: " + reportedPeptideSequence;
						log.error( msg );
						throw new ProxlGenXMLInternalException( msg );
					}
					
					peptideSequenceForProxlXML_PeptideObject = 
							peptideSequenceForProxlXML_PeptideObject.substring( 
									0,
									peptideSequenceForProxlXML_PeptideObject.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
				}
			}

			Kojak_GetDynamicModsForOneSequence_Result kojak_GetDynamicModsForOneSequence_Result =
					Kojak_GetDynamicModsForOneSequence.getSingletonInstance()
					.getDynamicModsForOneSequence( peptideSequenceWithMods, isotope_Labels_SpecifiedIn_KojakConfFile );
			
			List<Integer> linkPositions = parseKojakReportedPeptideSequenceResultItem.linkPositions;
			
			
			Modifications modifications = null;
			
			//  Process Modifications at Positions

			if ( kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition != null && ( ! kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition.isEmpty() ) ){

				if ( modifications == null ) {
					modifications = new Modifications();
				}

				List<Modification> modificationList = modifications.getModification();

				Map<Integer,Collection<BigDecimal>> dynamicModLocationsAndMasses = kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition;

				for ( Map.Entry<Integer,Collection<BigDecimal>> dynamicModLocationsAndMassesEntry : dynamicModLocationsAndMasses.entrySet() ) {

					int position = dynamicModLocationsAndMassesEntry.getKey();

					for ( BigDecimal modificationMass : dynamicModLocationsAndMassesEntry.getValue() ) {

						boolean isModificationAMonolink = 
								IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );

						Modification modification = new Modification();
						modificationList.add( modification );

						modification.setPosition( BigInteger.valueOf( position ) );
						modification.setMass( modificationMass );
						modification.setIsMonolink( isModificationAMonolink );
					}
				}
			}
			
			//  Process Modifications at 'n' terminus
			
			if ( kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods != null && ( ! kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods.isEmpty() ) ){
				for ( BigDecimal modificationMass : kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods ) {

					boolean isModificationAMonolink = 
							IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );
					

					if ( modifications == null ) {
						modifications = new Modifications();
					}

					List<Modification> modificationList = modifications.getModification();

					Modification modification = new Modification();
					modificationList.add( modification );

					modification.setMass( modificationMass );
					modification.setIsMonolink( isModificationAMonolink );
					modification.setIsNTerminal( true );
					modification.setPosition( BigInteger.ONE ); // 1 based position
				}
			}

			//  Process Modifications at 'c' terminus
			
			if ( kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods != null && ( ! kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods.isEmpty() ) ){
				
				BigInteger peptideLengthBI = BigInteger.valueOf( peptideSequenceForProxlXML_PeptideObject.length() );
				
				for ( BigDecimal modificationMass : kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods ) {

					boolean isModificationAMonolink = 
							IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );

					if ( modifications == null ) {
						modifications = new Modifications();
					}

					List<Modification> modificationList = modifications.getModification();

					Modification modification = new Modification();
					modificationList.add( modification );

					modification.setMass( modificationMass );
					modification.setIsMonolink( isModificationAMonolink );
					modification.setIsCTerminal( true );
					modification.setPosition( peptideLengthBI ); // 1 based position
				}
			}
			
			LinkedPositions linkedPositions = new LinkedPositions();
			List<LinkedPosition> linkedPositionList = linkedPositions.getLinkedPosition();

			for ( int linkPosition : linkPositions ) {
				
				LinkedPosition linkedPosition = new LinkedPosition();
				linkedPositionList.add( linkedPosition );
				
				linkedPosition.setPosition( BigInteger.valueOf( linkPosition ) );
			}


			Peptide proxlInputPeptide = new Peptide();
		
			proxlInputPeptide.setSequence( peptideSequenceForProxlXML_PeptideObject );
			proxlInputPeptide.setModifications( modifications );
			proxlInputPeptide.setLinkedPositions( linkedPositions );

			if ( peptideIsotopeLabelString != null ) {

				PeptideIsotopeLabel peptideIsotopeLabel = new PeptideIsotopeLabel();
				peptideIsotopeLabel.setLabel( peptideIsotopeLabelString );
				
				PeptideIsotopeLabels peptideIsotopeLabels = new PeptideIsotopeLabels();
				proxlInputPeptide.setPeptideIsotopeLabels( peptideIsotopeLabels );
				peptideIsotopeLabels.setPeptideIsotopeLabel( peptideIsotopeLabel );
			}
			
			proxlInputPeptide.setUniqueId(peptideUniqueId  );
			
			proxlInputPeptideList.add( proxlInputPeptide );
		}
		
		return proxlInputPeptides;
	}
	
	
	private class ParseKojakReportedPeptideSequenceResult {
		
		private List<ParseKojakReportedPeptideSequenceResultItem> parseKojakReportedPeptideSequenceResultItemList;
	}
	

	private class ParseKojakReportedPeptideSequenceResultItem {
		
		private String peptideSequenceWithMods;
		private List<Integer> linkPositions = new ArrayList<>();
		
		//  Isotope Label Info
		private String isotope_Label_For_ProxlXML_File;
		private String isotope_Label_Suffix_From_Kojak_File; // TODO  Not Currently Used
	}
	
	/**
	 * This returns sequences that are stripped of other info like dynamic modifications/variable modifications
	 * 
	 * 
	 * Get the actual peptide sequences present in the Kojak peptide sequence
	 * according to these rules:
	 * 
	 * 	unlinked: will look like a single peptide, e.g. LAADTGKGGQR
	 *  looplinked: peptide-Loop, e.g AKIVQKSSGLNMENLANHEHLLSPVR-Loop
	 *  dimer: FADQEGLTSSVGEYNENTIQQLLLPK+FADQEGLTSSVGEYNENTIQQLLLPK
	 *  crosslinked: peptide--peptide, e.g. AKLCQLDPVLYEK(5)--NMNAILFDELSKER(9)
	 * 
	 * @param reportedPeptideSequence
	 * @param linkType
	 * @return Map where Key is each sequence, including dynamic modifications, and the List is the link position(s)
	 */
	private ParseKojakReportedPeptideSequenceResult getSequencesFromPercolatorReportedPeptideSequence( 
			String reportedPeptideSequence, 
			LinkType linkType,
			Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile ) throws ProxlGenXMLDataException {
		
		ParseKojakReportedPeptideSequenceResult parseKojakReportedPeptideSequenceResult = new ParseKojakReportedPeptideSequenceResult();
		List<ParseKojakReportedPeptideSequenceResultItem> parseKojakReportedPeptideSequenceResultItemList = new ArrayList<>();
		parseKojakReportedPeptideSequenceResult.parseKojakReportedPeptideSequenceResultItemList = parseKojakReportedPeptideSequenceResultItemList;
		
		
		
		//  Patterns to extract Link Positions from Crosslink and Looplink
		Pattern regexPatternGetLinkPositionForCrosslinkEachPeptide = Pattern.compile( "^(\\w+)\\((\\d+)\\)$" );
		Pattern regexPatternGetLinkPositionsForLooplinkPeptide = Pattern.compile( "^(\\w+)\\((\\d+),(\\d+)\\)$" );


		
		String reportedPeptideSequenceForProcessing = reportedPeptideSequence;
		
		if ( LinkType.UNLINKED.equals( linkType ) ) {
			
			//  Can be single peptide or Dimer:
			
			if( reportedPeptideSequenceForProcessing.contains( "+" ) ) {
				
				//  Dimer - Split on "+"
				String[] reportedPeptideSequenceSplit = reportedPeptideSequenceForProcessing.split( "\\+" );
				
				for( String peptideSequence : reportedPeptideSequenceSplit ) {
				
					ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
					parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

					if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {

						String peptideSequenceNoMods = 
								KojakSequenceUtils.getSingletonInstance()
								.getPeptideWithDynamicModificationsRemoved( peptideSequence, isotope_Labels_SpecifiedIn_KojakConfFile );

						if ( peptideSequenceNoMods.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) { 

							// -15N label suffix is immediately before the '(' of the link position so remove it
							peptideSequence = peptideSequence.replace( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR, "" );

							parseKojakReportedPeptideSequenceResultItem.isotope_Label_For_ProxlXML_File = 
									IsotopeLabelValuesConstants.ISOTOPE_LABEL_FOR_PROXL_XML_FILE_15N;

							parseKojakReportedPeptideSequenceResultItem.isotope_Label_Suffix_From_Kojak_File = 
									IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR;
						}
					}
					
					parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = peptideSequence;
				}
					
			} else {
				
				// single peptide 
				
				ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
				parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

				if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {

					String peptideSequenceNoMods = 
							KojakSequenceUtils.getSingletonInstance()
							.getPeptideWithDynamicModificationsRemoved( reportedPeptideSequenceForProcessing, isotope_Labels_SpecifiedIn_KojakConfFile );

					if ( peptideSequenceNoMods.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) { 

						// -15N label suffix is immediately before the '(' of the link position so remove it
						reportedPeptideSequenceForProcessing = reportedPeptideSequenceForProcessing.replace( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR, "" );

						parseKojakReportedPeptideSequenceResultItem.isotope_Label_For_ProxlXML_File = 
								IsotopeLabelValuesConstants.ISOTOPE_LABEL_FOR_PROXL_XML_FILE_15N;

						parseKojakReportedPeptideSequenceResultItem.isotope_Label_Suffix_From_Kojak_File = 
								IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR;
					}
				}
				
				parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = reportedPeptideSequenceForProcessing;
			}
			
		} else if ( LinkType.CROSSLINK.equals( linkType ) ) {
			
			//  Split Reported Peptide String on "--" then process each substring
			
			String[] crosslinkedPeptideSequencesWithModsAndLinkPosition =
					reportedPeptideSequenceForProcessing.split( "--" );
			
			for( String eachCrosslinkedPeptideSequenceWithModsAndLinkPosition : crosslinkedPeptideSequencesWithModsAndLinkPosition ) {

				ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
				parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

				
				String peptideSequenceNoMods = 
						KojakSequenceUtils.getSingletonInstance()
						.getPeptideWithDynamicModificationsRemoved( eachCrosslinkedPeptideSequenceWithModsAndLinkPosition, isotope_Labels_SpecifiedIn_KojakConfFile );

				if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {

					int isotope_15N_SuffixPosition = peptideSequenceNoMods.indexOf( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR );
	
					if ( isotope_15N_SuffixPosition != -1 ) {
						
						//  Remove Isotope Label, after peptide sequence, before link position: '-15N' in  TVKDQVLELENNSDVQSLKLR-15N(19)
						int leftParenPos = peptideSequenceNoMods.indexOf( '(' );
						if ( leftParenPos == -1 ) {
							String msg = "Could not get position of crosslinked peptide |" 
									+ eachCrosslinkedPeptideSequenceWithModsAndLinkPosition
									+ "|, missing '(' to start link position. "
									+ " crosslinked peptide with modifications removed: "
									+ peptideSequenceNoMods
									+ "  Reported Peptide String in Percolator file: " + reportedPeptideSequence;
							log.error( msg );
							throw new ProxlGenXMLDataException(  );
						}
						
						if ( isotope_15N_SuffixPosition + IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() 
								== leftParenPos ) {
							
							// -15N label suffix is immediately before the '(' of the link position so remove it
							peptideSequenceNoMods = 
									peptideSequenceNoMods.substring(0, isotope_15N_SuffixPosition ) 
									+ peptideSequenceNoMods.substring( leftParenPos );

							parseKojakReportedPeptideSequenceResultItem.isotope_Label_For_ProxlXML_File = 
									IsotopeLabelValuesConstants.ISOTOPE_LABEL_FOR_PROXL_XML_FILE_15N;
							
							parseKojakReportedPeptideSequenceResultItem.isotope_Label_Suffix_From_Kojak_File = 
									IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR;

						}
					}
				}
				
				Matcher m = regexPatternGetLinkPositionForCrosslinkEachPeptide.matcher( peptideSequenceNoMods );
				if( ! m.matches() ) {
					String msg = "Could not get position of crosslinked peptide |" 
							+ eachCrosslinkedPeptideSequenceWithModsAndLinkPosition
							+ "|. "
							+ " crosslinked peptide with modifications removed: "
							+ peptideSequenceNoMods
							+ "  Reported Peptide String in Percolator file: " + reportedPeptideSequence;
					log.error( msg );
					throw new ProxlGenXMLDataException(  );
				}
				
				String eachCrosslinkedPeptideSequenceWithMods = getPeptideSequenceWithPositonsRemoved( eachCrosslinkedPeptideSequenceWithModsAndLinkPosition );
				String linkPositionString = m.group( 2 );
				
				int linkPositionInt = 0;
				
				try {
				 
					linkPositionInt = Integer.parseInt( linkPositionString );
				
				} catch ( Exception e ) {
					
					String msg = "Failed to parse Link Position '" + linkPositionString + "' in Kojak Reported Peptide: " + reportedPeptideSequence;
					log.error( msg );
					throw new ProxlGenXMLDataException( msg );
				}
				

				parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = eachCrosslinkedPeptideSequenceWithMods;
				parseKojakReportedPeptideSequenceResultItem.linkPositions.add( linkPositionInt );
				
			}

			
		} else if ( LinkType.LOOPLINK.equals( linkType ) ) {

			ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
			parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

			String looplink_ReportedPeptideSequence = reportedPeptideSequenceForProcessing;

			if( looplink_ReportedPeptideSequence.endsWith( "-Loop" ) ) {
				looplink_ReportedPeptideSequence = looplink_ReportedPeptideSequence.replace( "-Loop", "" );
			}
			
			else if( looplink_ReportedPeptideSequence.endsWith( "-LOOP" ) ) {
				looplink_ReportedPeptideSequence = looplink_ReportedPeptideSequence.replace( "-LOOP", "" );
			}
			

			String peptideSequenceNoMods = 
					KojakSequenceUtils.getSingletonInstance()
					.getPeptideWithDynamicModificationsRemoved( looplink_ReportedPeptideSequence, isotope_Labels_SpecifiedIn_KojakConfFile );

			if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {

				int isotope_15N_SuffixPosition = peptideSequenceNoMods.indexOf( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR );

				if ( isotope_15N_SuffixPosition != -1 ) {
					
					//  Remove Isotope Label, after peptide sequence, before link position: '-15N' in  TVKDQVLELENNSDVQSLKLR-15N(19)
					int leftParenPos = peptideSequenceNoMods.indexOf( '(' );
					if ( leftParenPos == -1 ) {
						String msg = "Could not get position of looplink peptide |" 
								+ looplink_ReportedPeptideSequence
								+ "|, missing '(' to start link position. "
								+ " looplinked peptide with modifications removed: "
								+ peptideSequenceNoMods
								+ "  Reported Peptide String in Percolator file: " + reportedPeptideSequence;
						log.error( msg );
						throw new ProxlGenXMLDataException(  );
					}
					
					if ( isotope_15N_SuffixPosition + IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() 
							== leftParenPos ) {
						
						// -15N label suffix is immediately before the '(' of the link position so remove it
						peptideSequenceNoMods = 
								peptideSequenceNoMods.substring(0, isotope_15N_SuffixPosition ) 
								+ peptideSequenceNoMods.substring( leftParenPos );

						parseKojakReportedPeptideSequenceResultItem.isotope_Label_For_ProxlXML_File = 
								IsotopeLabelValuesConstants.ISOTOPE_LABEL_FOR_PROXL_XML_FILE_15N;
						
						parseKojakReportedPeptideSequenceResultItem.isotope_Label_Suffix_From_Kojak_File = 
								IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR;

					}
				}
			}
			
			Matcher m = regexPatternGetLinkPositionsForLooplinkPeptide.matcher( peptideSequenceNoMods );
			if( !m.matches() ) {
				throw new ProxlGenXMLDataException( "Could not get position of looplink peptide (" + looplink_ReportedPeptideSequence + ") from " + reportedPeptideSequence );
			}
			
			String peptideSequenceWithMods = getPeptideSequenceWithPositonsRemoved( looplink_ReportedPeptideSequence );

			String linkPosition_1_String = m.group( 2 );
			String linkPosition_2_String = m.group( 3 );
			

			int linkPosition_1_Int = 0;
			
			try {
			 
				linkPosition_1_Int = Integer.parseInt( linkPosition_1_String );
			
			} catch ( Exception e ) {
				
				String msg = "Failed to parse Link Position '" + linkPosition_1_String + "' in Kojak Reported Peptide: " + reportedPeptideSequence;
				log.error( msg );
				throw new ProxlGenXMLDataException( msg );
			}
			

			int linkPosition_2_Int = 0;
			
			try {
			 
				linkPosition_2_Int = Integer.parseInt( linkPosition_2_String );
			
			} catch ( Exception e ) {
				
				String msg = "Failed to parse Link Position '" + linkPosition_2_String + "' in Kojak Reported Peptide: " + reportedPeptideSequence;
				log.error( msg );
				throw new ProxlGenXMLDataException( msg );
			}
			

			parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = peptideSequenceWithMods;
			
			parseKojakReportedPeptideSequenceResultItem.linkPositions.add( linkPosition_1_Int );
			parseKojakReportedPeptideSequenceResultItem.linkPositions.add( linkPosition_2_Int );

			Collections.sort( parseKojakReportedPeptideSequenceResultItem.linkPositions );
			
			
		} else {
			
			String msg = "Unknown Link Type: " + linkType;
			log.error( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		
		return parseKojakReportedPeptideSequenceResult;
	}
		
	/**
	 * Remove Position(s) from peptide sequence
	 * 
	 * @param peptideSequenceWithModsAndPositions
	 * @return
	 */
	private String getPeptideSequenceWithPositonsRemoved( String peptideSequenceWithModsAndPositions ) {
		
		String peptideSequenceWithMods = peptideSequenceWithModsAndPositions.replaceAll( "\\([^\\(]+\\)",  "" );
		
		return peptideSequenceWithMods;
	}



}
