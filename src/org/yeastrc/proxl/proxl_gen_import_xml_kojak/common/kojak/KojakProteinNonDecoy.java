package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 *
 */
public class KojakProteinNonDecoy {


	/**
	 * private constructor
	 */
	private KojakProteinNonDecoy(){}
	
	public static KojakProteinNonDecoy getInstance(  )  { 
		
		return new KojakProteinNonDecoy();
	}
	
	private List<String> decoyIdentificationStrings;
	
	/**
	 * @param kojakProteinString
	 * @return
	 */
	public Set<String> getProteinNameList_nonDecoy( String kojakProteinStringParam ) {
		
		Set<String> proteinNameSet = new HashSet<>();
		
		if ( StringUtils.isEmpty( kojakProteinStringParam ) ) {
			
			return proteinNameSet;  //  EARLY EXIT
		}

		if ( "-".equals( kojakProteinStringParam ) ) {
			
			return proteinNameSet;  //  EARLY EXIT
		}


		String[] kojakProteinStringSplitGT = kojakProteinStringParam.split( ">" ); //  Split on ">" since when Kojak reports the Protein it reports the whole header, including the ">"

		for ( String proteinName : kojakProteinStringSplitGT ) {



			if ( StringUtils.isEmpty( proteinName ) ) {

				continue;  //  EARLY CONTINUE
			}

			if ( "-".equals( proteinName ) ) {

				continue;  //  EARLY CONTINUE
			}


			boolean isDecoy = false;

			for ( String decoyIdentificationString : decoyIdentificationStrings ) {

				if ( proteinName.contains( decoyIdentificationString ) ) {

					isDecoy = true;
					break;
				}
			}

			if ( isDecoy ) {

				//  decoy found, skip to next

				continue;  //  EARLY CONTINUE
			}

			if ( proteinName.startsWith( ">" ) ) {

				//  remove ">" which defines start of FASTA header

				proteinName = proteinName.substring( 1 );
			}

			if ( proteinName.endsWith( ";" ) ) {

				//  remove ";" which Kojak puts on end of FASTA header

				proteinName = proteinName.substring( 0, proteinName.length() );
			}


			int lastLeftParenPosition = proteinName.lastIndexOf( "(" );

			if ( lastLeftParenPosition != -1 ) {

				//  Protein name string has (###) which is probably the position in the protein
				//  Need to remove it to find the protein name string in the FASTA file

				proteinName = proteinName.substring( 0, lastLeftParenPosition );
			}

			if ( StringUtils.isEmpty( proteinName )) {

				int z = 0;
			}

			proteinNameSet.add( proteinName );
		}
	
		
		
		//  TODO  DROP Splitting on ","

//		String[] kojakProteinStringSplit = kojakProteinString.split( "," );
//		
//		for ( String proteinName : kojakProteinStringSplit ) {
//			
//			for ( String decoyIdentificationString : decoyIdentificationStrings ) {
//			
//				if ( ! proteinName.contains( decoyIdentificationString ) ) {
//
//					//  Not decoy found
//
//					proteinNameSet.add( proteinName );
//				}
//			}
//		}
		
		return proteinNameSet;
		
		
	}
	



	public List<String> getDecoyIdentificationStrings() {
		return decoyIdentificationStrings;
	}

	public void setDecoyIdentificationStrings(
			List<String> decoyIdentificationStrings) {
		this.decoyIdentificationStrings = decoyIdentificationStrings;
	}
		
	
}
