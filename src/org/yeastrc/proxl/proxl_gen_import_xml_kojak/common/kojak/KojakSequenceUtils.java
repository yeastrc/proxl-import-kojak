package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.IsotopeLabelValuesConstants;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;



/**
 * Moved method getDynamicModsForOneSequence to class Kojak_GetDynamicModsForOneSequence
 *
 */
public class KojakSequenceUtils {

//	private static final Logger log = Logger.getLogger( KojakSequenceUtils.class );
	
	//  private constructor
	private KojakSequenceUtils() {}
	
	private static final KojakSequenceUtils singletonInstance = new KojakSequenceUtils();


	public static KojakSequenceUtils getSingletonInstance() {
		
		return singletonInstance;
	}
	
	
	/**
	 * Attempts to get the "naked" peptide string of a peptide string that
	 * has been marked with dynamic modification masses (e.g. "PEPT[21.23]IDE"
	 * would return "PEPTIDE")
	 * @param pseq
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public String getPeptideWithDynamicModificationsRemoved( final String pseqParam, Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile ) throws ProxlGenXMLDataException {
		String pseq = pseqParam.replaceAll( "\\[[^\\[]+\\]",  "" );
		//  Remove 'n' and start and 'c' at end
		if ( pseq.startsWith( "n" ) ) {
			pseq = pseq.substring( 1 );
		}
		if ( pseq.endsWith( "c" ) ) {
			pseq = pseq.substring( 0, pseq.length() - 1 );
		}
		
		if ( pseq.contains( "c" ) ) {
			// 'c' not at end of string.  
			//  Strip off other text that would be appended to the end of the string to find the 'c' at the end of the sequence
			String mainSequence = pseq;
			String stringAfterSequence = "";
			int leftParenPos = pseq.indexOf( '(' );
			if ( leftParenPos != -1 ) {
				stringAfterSequence = mainSequence.substring( leftParenPos ) + stringAfterSequence; // add the link position
				mainSequence = mainSequence.substring( 0, leftParenPos ); // Reduce to string before link position
			}
			if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {
				if ( mainSequence.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) {
					stringAfterSequence = IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR + stringAfterSequence; // add 15N Isotope Label Suffix
					mainSequence = mainSequence.substring( 0, mainSequence.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() ); // Reduce to string before 15N Isotope Label Suffix
				}
			}
			
			if ( mainSequence.endsWith( "c" ) ) {
				mainSequence = pseq.substring( 0, mainSequence.length() - 1 );
			}
			pseq = mainSequence + stringAfterSequence;
		}
		
		if ( pseq.contains( "n" ) ) {
			String msg = "peptide sequence contains 'n' in location other than at end.  peptide sequence: " + pseqParam;
			System.err.println( msg );
			throw new ProxlGenXMLDataException(msg);
		}
		if ( pseq.contains( "c" ) ) {
			String msg = "peptide sequence contains 'c' in location other than at end of sequence.  peptide sequence: " + pseqParam;
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
	


	
	
}
