package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;


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
	 */
	public ReportedPeptide parsePercolatorReportedPeptideIntoProxlInputReportedPeptide( IPeptide percolatorPeptide ) throws ProxlGenXMLDataException {
			
		ReportedPeptide proxlInputReportedPeptide = new ReportedPeptide();

		String reportedPeptideSequence = percolatorPeptide.getPeptideId();
		
		proxlInputReportedPeptide.setReportedPeptideString( reportedPeptideSequence );

		LinkType linkType = getLinkType( reportedPeptideSequence );
		
		proxlInputReportedPeptide.setType( linkType );

		Peptides proxlInputPeptides = getProxlInputPeptides( reportedPeptideSequence, linkType );
		
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
	 */
	private Peptides getProxlInputPeptides( String reportedPeptideSequence, LinkType linkType ) throws ProxlGenXMLDataException {
	
		Peptides proxlInputPeptides = new Peptides();
	
		List<Peptide> proxlInputPeptideList = proxlInputPeptides.getPeptide();
		
		
		ParseKojakReportedPeptideSequenceResult parseKojakReportedPeptideSequenceResult =
				getSequencesFromKojakSequence( reportedPeptideSequence, linkType );
		
		for ( ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem : parseKojakReportedPeptideSequenceResult.parseKojakReportedPeptideSequenceResultItemList ) {
		
			String peptideSequenceWithMods = parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods;
			
			String peptideSequenceNoMods = getPeptideWithDynamicModificationsRemoved( peptideSequenceWithMods );
			
			Map<Integer,Collection<BigDecimal>> dynamicModLocationsAndMasses = 
					getDynamicModsForOneSequence( peptideSequenceWithMods );
			
			List<Integer> linkPositions = parseKojakReportedPeptideSequenceResultItem.linkPositions;
			
			
			Modifications modifications = new Modifications();
			List<Modification> modificationList = modifications .getModification();

			for ( Map.Entry<Integer,Collection<BigDecimal>> dynamicModLocationsAndMassesEntry : dynamicModLocationsAndMasses.entrySet() ) {
				
				int position = dynamicModLocationsAndMassesEntry.getKey();
				
				for ( BigDecimal modificationMass : dynamicModLocationsAndMassesEntry.getValue() ) {

					boolean isModificationAMonolink = IsModificationAMonolink.isModificationAMonolink( modificationMass );
					
					Modification modification = new Modification();
					modificationList.add( modification );

					modification.setPosition( BigInteger.valueOf( position ) );
					modification.setMass( modificationMass );
					modification.setIsMonolink( isModificationAMonolink );
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
			proxlInputPeptideList.add( proxlInputPeptide );
			
			proxlInputPeptide.setSequence( peptideSequenceNoMods );
			proxlInputPeptide.setModifications( modifications );
			proxlInputPeptide.setLinkedPositions( linkedPositions );

			
			
		}
		
		return proxlInputPeptides;
	}
	
	
	private class ParseKojakReportedPeptideSequenceResult {
		
		private List<ParseKojakReportedPeptideSequenceResultItem> parseKojakReportedPeptideSequenceResultItemList;
	}
	

	private class ParseKojakReportedPeptideSequenceResultItem {
		
		private String peptideSequenceWithMods;
		private List<Integer> linkPositions = new ArrayList<>();
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
	public ParseKojakReportedPeptideSequenceResult getSequencesFromKojakSequence( String reportedPeptideSequence, LinkType linkType ) throws ProxlGenXMLDataException {
		
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

					parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = peptideSequence;
				}
					
			} else {
				
				// single peptide 
				
				ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
				parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );
				
				parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = reportedPeptideSequenceForProcessing;
			}
			
		} else if ( LinkType.CROSSLINK.equals( linkType ) ) {
			
			//  Split Reported Peptide String on "--" then process each substring
			
			String[] crosslinkedPeptideSequencesWithModsAndLinkPosition =
					reportedPeptideSequenceForProcessing.split( "--" );
			
			for( String eachCrosslinkedPeptideSequenceWithModsAndLinkPosition : crosslinkedPeptideSequencesWithModsAndLinkPosition ) {

				String peptideSequenceNoMods = getPeptideWithDynamicModificationsRemoved( eachCrosslinkedPeptideSequenceWithModsAndLinkPosition );

				
				Matcher m = regexPatternGetLinkPositionForCrosslinkEachPeptide.matcher( peptideSequenceNoMods );
				if( ! m.matches() ) {
					throw new ProxlGenXMLDataException( "Could not get position of crosslinked peptide |" 
							+ eachCrosslinkedPeptideSequenceWithModsAndLinkPosition + "| from " + reportedPeptideSequence );
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
				

				ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
				parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

				parseKojakReportedPeptideSequenceResultItem.peptideSequenceWithMods = eachCrosslinkedPeptideSequenceWithMods;
				parseKojakReportedPeptideSequenceResultItem.linkPositions.add( linkPositionInt );

			}

			
		} else if ( LinkType.LOOPLINK.equals( linkType ) ) {
			
			String looplink_ReportedPeptideSequence = reportedPeptideSequenceForProcessing;

			if( looplink_ReportedPeptideSequence.endsWith( "-Loop" ) ) {
				looplink_ReportedPeptideSequence = looplink_ReportedPeptideSequence.replace( "-Loop", "" );
			}
			
			else if( looplink_ReportedPeptideSequence.endsWith( "-LOOP" ) ) {
				looplink_ReportedPeptideSequence = looplink_ReportedPeptideSequence.replace( "-LOOP", "" );
			}
			

			String peptideSequenceNoMods = getPeptideWithDynamicModificationsRemoved( looplink_ReportedPeptideSequence );

			
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
			

			ParseKojakReportedPeptideSequenceResultItem parseKojakReportedPeptideSequenceResultItem = new ParseKojakReportedPeptideSequenceResultItem();
			parseKojakReportedPeptideSequenceResultItemList.add( parseKojakReportedPeptideSequenceResultItem );

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


	
	/**
	 * Attempts to get the "naked" peptide string of a peptide string that
	 * has been marked with dynamic modification masses (e.g. "PEPT[21.23]IDE"
	 * would return "PEPTIDE")
	 * @param pseq
	 * @return
	 */
	public String getPeptideWithDynamicModificationsRemoved( String pseq ) {
		return pseq.replaceAll( "\\[[^\\[]+\\]",  "" );
	}
	
	/**
	 * Whether or not the supplied Kojak sequence contains any dynamic mod information in the
	 * form of PEPTID[12.33]E
	 * @param pseq
	 * @return true if dynamic mod information is present, false if not
	 */
	public boolean containsDynamicMods( String pseq ) {
		if( pseq.contains( "[" ) )
			return true;
		
		return false;
	}
	


	
	
	/**
	 * Given a Kojak sequence which optionally contains dynamic mod information in the form of:
	 * P[16.02]EPT[23.12]IDE  
	 * Return a Map where the key is the position in
	 * that peptide (starts at 1), and the value is a collection of Doubles, which are the dynamic mods
	 * found at that position (Kojak supports more than modificaton at a position)
	 * 
	 * @param sequence
	 * @return
	 * @throws ProxlGenXMLDataException
	 */
		
	public Map<Integer,Collection<BigDecimal>>  getDynamicModsForOneSequence( String pseq ) throws ProxlGenXMLDataException {

		String nakedSeq = getPeptideWithDynamicModificationsRemoved( pseq );
		

		Map<Integer,Collection<BigDecimal>> modLocations = new HashMap<Integer,Collection<BigDecimal>>();

		// if it contains no dynamic mods, no processing is necessary
		if( !containsDynamicMods( pseq ) ) {

			return modLocations;   //  EARLY EXIT from METHOD
		}

		
		String seq = "";					// string with no modification information
		String massString = null;					// the string we're building that contains a mass (e.g. 24.12)
		boolean readingMass = false;		// whether or not we're currently reading mass information
		int position = 0;					// current position in the peptide sequence, not counting mod notations
		
		for ( int i = 0; i < pseq.length(); i++ ) {
		    char c = pseq.charAt(i);        

		    if( c == ']' ) {
		    	if( !readingMass )
		    		throw new ProxlGenXMLDataException( "Got a ']' but am not reading a dynamid mod mass in: " + pseq );
		    	
		    	// BigDecimal version of our mass
		    	BigDecimal massBD = new BigDecimal( massString );
		    	
		    	// add this mass at the current position to the data structure
		    	if( !modLocations.containsKey( position ) )
		    		modLocations.put( position, new ArrayList<BigDecimal>() );
		    	
		    	modLocations.get( position).add( massBD );
		    	
		    	massString = null;
		    	readingMass = false;
		    	continue;
		    }
		    
		    if( c == '[' ) {
		    	if( readingMass )
		    		throw new ProxlGenXMLDataException( "Got a '[' before a ']' while reading dynamic modd mass in: " + pseq );
		    	
		    	massString = "";
		    	readingMass = true;
		    	continue;
		    }
		    
		    if( readingMass ) {
		    	massString += c;
		    	continue;
		    }
		    
		    
		    // increment our position in the sequence string, only if we're not reading mass information
		    position++;
		    seq += c;
		}
		
		// sanity check
		if( !seq.equals( nakedSeq ) )
			throw new ProxlGenXMLDataException( "Ending sequence not equal to naked sequence.  seq: '" 
					+ seq + "' and nakedSeq: '" + nakedSeq + "'" );
		
		// make some mods were found...
		if( pseq.equals( seq ) )
			throw new ProxlGenXMLDataException( "Error processing sequence for dynamic mods. Ending sequence same as starting sequence: " + pseq );
		
		return modLocations;
	}
	

}
