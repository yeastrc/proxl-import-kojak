package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.util.List;

import org.apache.commons.lang3.StringUtils;




/**
 * 
 *
 */
public class IsAllProtein_1or2_Decoy {


	/**
	 * private constructor
	 */
	private IsAllProtein_1or2_Decoy(){}
	
	public static IsAllProtein_1or2_Decoy getInstance(  )  { 
		
		return new IsAllProtein_1or2_Decoy();
	}
		
	
	/**
	 * @param kojakPsmDataObject
	 * @param proxlInputRoot
	 * @return
	 */
	public boolean isAllProtein_1or2_Decoy( KojakPsmDataObject kojakPsmDataObject, KojakConfFileReaderResult kojakConfFileReaderResult ) {
		
		List<String> decoyIdentificationStringFromConfFileList = kojakConfFileReaderResult.getDecoyIdentificationStringFromConfFileList();

		String[] proteins = null;
		
		if ( StringUtils.isNotEmpty( kojakPsmDataObject.getProtein_2() ) ) {
			
			proteins = new String[ 2 ];
			proteins[ 1 ] = kojakPsmDataObject.getProtein_2();
		
		} else {
			proteins = new String[ 1 ];
		}
		
		proteins[ 0 ] = kojakPsmDataObject.getProtein_1();
		
		
		boolean allDecoysForEitherProtein = false;
		
		for ( String protein1or2 : proteins ) {
		
			String[] protein1or2Split = protein1or2.split( ">" );  //  Split on ">" since when Kojak reports the Protein it reports the whole header, including the ">"

			boolean allDecoysForProtein = true;
			
			for ( String proteinSingle : protein1or2Split ) {
				
				if ( "".equals( proteinSingle ) ) {
					
					//  skip empty string that is created by splitting on ">" when first character in string is ">"
					
					continue;
				}
				
				for ( String decoyIdentificationString : decoyIdentificationStringFromConfFileList ) {
				
					if ( ! proteinSingle.contains( decoyIdentificationString ) ) {

						//  Not decoy found

						allDecoysForProtein = false;
						break;

					}
				}
			}
		
			if ( allDecoysForProtein ) {
				
				allDecoysForEitherProtein = true;
				
				break;
			}
		}
		
		return allDecoysForEitherProtein;
		
		
	}

}
