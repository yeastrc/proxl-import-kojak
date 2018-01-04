package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;



public class KojakSequenceUtils {

//	private static final Logger log = Logger.getLogger( KojakSequenceUtils.class );
	
	//  private constructor
	private KojakSequenceUtils() {}


	public static KojakSequenceUtils getInstance() {
		
		return new KojakSequenceUtils();
	}
	
	
	/**
	 * Attempts to get the "naked" peptide string of a peptide string that
	 * has been marked with dynamic modification masses (e.g. "PEPT[21.23]IDE"
	 * would return "PEPTIDE")
	 * @param pseq
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public String getPeptideWithDynamicModificationsRemoved( final String pseqParam ) throws ProxlGenXMLDataException {
		String pseq = pseqParam.replaceAll( "\\[[^\\[]+\\]",  "" );
		//  Remove 'n' and start and 'c' at end
		if ( pseq.startsWith( "n" ) ) {
			pseq = pseq.substring( 1 );
		}
		if ( pseq.endsWith( "c" ) ) {
			pseq = pseq.substring( 0, pseq.length() - 1 );
		}
		if ( pseq.contains( "n" ) ) {
			String msg = "peptide sequence contains 'n' in location other than at end.  peptide sequence: " + pseqParam;
			System.err.println( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		if ( pseq.contains( "c" ) ) {
			String msg = "peptide sequence contains 'c' in location other than at end.  peptide sequence: " + pseqParam;
			System.err.println( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		return pseq;
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
		
	public Map<Integer,Collection<BigDecimal>>  getDynamicModsForOneSequence( final String pseqParam ) throws ProxlGenXMLDataException {

		String nakedSeq = getPeptideWithDynamicModificationsRemoved( pseqParam );
		
		//  Just remove 'n' and 'c' here.  Their positions are validated in getPeptideWithDynamicModificationsRemoved
		String pseq = pseqParam.replace( "n", "" ).replace( "c", "" );
				
		if ( pseq.startsWith( "[" ) ) {
			//  There was a mod right after the 'n' so the first residue letter needs to be moved to the first position of the sequence string.
			int firstLetterIndex = 0;
			while ( firstLetterIndex < pseq.length() && ( ! Character.isLetter( pseq.charAt(firstLetterIndex) ) ) ) {
				firstLetterIndex++;
			}
			if ( firstLetterIndex == pseq.length() ) {
				String msg = "Peptide sequence contains no letters: " + pseqParam;
				System.err.println( msg );
				throw new ProxlGenXMLDataException(msg);
			}
			//  Move first letter to start of string
			char firstLetter = pseq.charAt( firstLetterIndex );
			pseq = firstLetter 
					//  String before first letter
					+ pseq.substring( 0, firstLetterIndex )
					//  String after first letter
					+ pseq.substring( firstLetterIndex + 1 );
		}

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
