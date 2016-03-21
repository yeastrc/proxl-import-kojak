package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;




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
	public boolean isAllProtein_1or2_Decoy( KojakPsmDataObject kojakPsmDataObject, ProxlInput proxlInputRoot ) {
		
		DecoyLabels decoyLabels = proxlInputRoot.getDecoyLabels();
		List<DecoyLabel> decoyLabelList = decoyLabels.getDecoyLabel();

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
		
			String[] protein1or2Split = protein1or2.split( "," );

			boolean allDecoysForProtein = true;
			
			for ( String proteinSingle : protein1or2Split ) {
				
				for ( DecoyLabel decoyLabel : decoyLabelList ) {
				
					if ( ! proteinSingle.contains( decoyLabel.getPrefix() ) ) {

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
