package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.IsotopeLabelValuesConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;

public class Kojak_GetDynamicModsForOneSequence {

	private static final Logger log = Logger.getLogger( Kojak_GetDynamicModsForOneSequence.class );
	
	private static final Kojak_GetDynamicModsForOneSequence singletonInstance = new Kojak_GetDynamicModsForOneSequence();
	
	//  Private constructor
	private Kojak_GetDynamicModsForOneSequence() {}
	
	/**
	 * @return
	 */
	public static Kojak_GetDynamicModsForOneSequence getSingletonInstance() {
		return singletonInstance;
	}
	
	public static class Kojak_GetDynamicModsForOneSequence_Result {
		
		public Map<Integer,Collection<BigDecimal>> dynamicModsForPositions_KeyPosition;
		
		public Collection<BigDecimal> n_Terminal_Mods;
		public Collection<BigDecimal> c_Terminal_Mods;
	}
	
	/**
	 * Given a Kojak sequence which optionally contains dynamic mod information in the form of:
	 * P[16.02]EPT[23.12]IDE  
	 * Return a Map where the key is the position in
	 * that peptide (starts at 1), and the value is a collection of Doubles, which are the dynamic mods
	 * found at that position (Kojak supports more than modificaton at a position)
	 * 
	 * @param peptideSequenceParam
	 * @return
	 * @throws ProxlGenXMLDataException
	 */
		
	public Kojak_GetDynamicModsForOneSequence_Result  getDynamicModsForOneSequence( final String peptideSequenceParam, Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile ) throws ProxlGenXMLDataException {

		String nakedPeptideSequence = KojakSequenceUtils.getSingletonInstance().getPeptideWithDynamicModificationsRemoved( peptideSequenceParam, isotope_Labels_SpecifiedIn_KojakConfFile );
		
		if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {
			nakedPeptideSequence = nakedPeptideSequence.replace( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR, "" );
		}
		
		//  Just remove 'n' and 'c' here.  Their positions are validated in getPeptideWithDynamicModificationsRemoved
//		String pseq = pseqParam.replace( "n", "" ).replace( "c", "" );
		
		String peptideSequence = peptideSequenceParam;
		
		if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {
			peptideSequence = peptideSequence.replace( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR, "" );
		}
				
		if ( peptideSequence.startsWith( "[" ) ) {
			//  There was a mod right after the 'n' so the first residue letter needs to be moved to the first position of the sequence string.
			int firstLetterIndex = 0;
			while ( firstLetterIndex < peptideSequence.length() && ( ! Character.isLetter( peptideSequence.charAt(firstLetterIndex) ) ) ) {
				firstLetterIndex++;
			}
			if ( firstLetterIndex == peptideSequence.length() ) {
				String msg = "Peptide sequence contains no letters: " + peptideSequenceParam;
				System.err.println( msg );
				throw new ProxlGenXMLDataException(msg);
			}
			//  Move first letter to start of string
			char firstLetter = peptideSequence.charAt( firstLetterIndex );
			peptideSequence = firstLetter 
					//  String before first letter
					+ peptideSequence.substring( 0, firstLetterIndex )
					//  String after first letter
					+ peptideSequence.substring( firstLetterIndex + 1 );
		}

		// if it contains no dynamic mods, no processing is necessary
		if( ! KojakSequenceUtils.getSingletonInstance().containsDynamicMods( peptideSequence ) ) {

			Kojak_GetDynamicModsForOneSequence_Result result = new Kojak_GetDynamicModsForOneSequence_Result();
			
			return result;   //  EARLY EXIT from METHOD
		}

		Map<Integer,Collection<BigDecimal>> dynamicModsForPositions_KeyPosition = new HashMap<>();
		Collection<BigDecimal> n_Terminal_Mods = new ArrayList<>();
		Collection<BigDecimal> c_Terminal_Mods = new ArrayList<>();
		
		StringBuilder residuesOnly_ExtractedWhileProcessingProvidedSequenceSB = new StringBuilder( 1000 );	// string with no modification information
		StringBuilder massStringSB = new StringBuilder( 1000 );					// the string we're building that contains a mass (e.g. 24.12)

		boolean in_n_terminus = false;  // last non-mod read is 'n' for n terminus
		boolean in_c_terminus = false;  // last non-mod read is 'c' for c terminus
		
		boolean readingMass = false;		// whether or not we're currently reading mass information
		
		int position = 0;					// current position in the peptide sequence, not counting mod notations
		
		for ( int indexOfSequence = 0; indexOfSequence < peptideSequence.length(); indexOfSequence++ ) {
			
		    char characterAtIndex = peptideSequence.charAt(indexOfSequence);        

		    if( characterAtIndex == ']' ) {
		    	if ( ! readingMass )
		    		throw new ProxlGenXMLDataException( "Got a ']' but am not reading a dynamid mod mass in: " + peptideSequence );
		    	
		    	// BigDecimal version of our mass
		    	String massString = massStringSB.toString();
		    	BigDecimal massBD = new BigDecimal( massString );
		    	
		    	if ( in_n_terminus ) {
		    		//  Save 'n' terminal mod
		    		n_Terminal_Mods.add( massBD );
		    		
		    	} else if ( in_c_terminus ) {
		    		//  Save 'c' terminal mod
		    		c_Terminal_Mods.add( massBD );
		    		
		    	} else {

		    		// add this mass at the current position to the data structure
		    		if( !dynamicModsForPositions_KeyPosition.containsKey( position ) ) {
		    			dynamicModsForPositions_KeyPosition.put( position, new ArrayList<BigDecimal>() );
		    		}
		    		dynamicModsForPositions_KeyPosition.get( position).add( massBD );
		    	}
		    	
		    	massStringSB.setLength( 0 );  // Clear it
		    	readingMass = false;
		    	continue; //  EARLY CONTINUE
		    }
		    
		    if( characterAtIndex == '[' ) {
		    	if( readingMass )
		    		throw new ProxlGenXMLDataException( "Got a '[' before a ']' while reading dynamic modd mass in: " + peptideSequence );
		    	
		    	massStringSB.setLength( 0 );  // Clear it
		    	readingMass = true;
		    	continue; //  EARLY CONTINUE
		    }
		    
		    if( readingMass ) {
		    	massStringSB.append( characterAtIndex );
		    	continue; //  EARLY CONTINUE
		    }
		    

			in_n_terminus = false;  // reset to false
			in_c_terminus = false;  // reset to false
			
		    if ( characterAtIndex == 'n' ) {
		    	//  'n' for n terminus
		    	in_n_terminus = true;
		    	continue; //  EARLY CONTINUE
		    }
		    if ( characterAtIndex == 'c' ) {
		    	//  'c' for c terminus
		    	in_c_terminus = true;
		    	continue; //  EARLY CONTINUE
		    }

		    
		    // increment our position in the sequence string, only if we're not reading mass information
		    position++;
		    residuesOnly_ExtractedWhileProcessingProvidedSequenceSB.append( characterAtIndex );
		}
		
		String residuesOnly_ExtractedWhileProcessingProvidedSequence = residuesOnly_ExtractedWhileProcessingProvidedSequenceSB.toString();
				
		// sanity check
		if( ! residuesOnly_ExtractedWhileProcessingProvidedSequence.equals( nakedPeptideSequence ) )
			throw new ProxlGenXMLDataException( "Ending sequence not equal to naked sequence.  seq: '" 
					+ residuesOnly_ExtractedWhileProcessingProvidedSequence + "' and nakedSeq: '" + nakedPeptideSequence + "'" );
		
		// make some mods were found...
		if( peptideSequence.equals( residuesOnly_ExtractedWhileProcessingProvidedSequence ) )
			throw new ProxlGenXMLDataException( "Error processing sequence for dynamic mods. Ending sequence same as starting sequence: " + peptideSequence );
		
		Kojak_GetDynamicModsForOneSequence_Result result = new Kojak_GetDynamicModsForOneSequence_Result();
		
		result.dynamicModsForPositions_KeyPosition =  dynamicModsForPositions_KeyPosition;
		result.n_Terminal_Mods = n_Terminal_Mods;
		result.c_Terminal_Mods = c_Terminal_Mods;
		
		return result;
	}
}
