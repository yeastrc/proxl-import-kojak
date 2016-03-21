package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercKojakPsmIdConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PsmIdSplitObject;


/**
 * Split the "psm_id" in the percolator file for Kojak data
 *
 */
public class SplitKojakPsmId {
	
	
	/**
	 * Split the "psm_id" in the percolator file for Kojak data
	 * 
	 * @param psmIdString
	 * @return
	 * @throws Exception
	 */
	public static PsmIdSplitObject splitKojakPsmId( String psmIdString ) throws Exception {

		//  Example psm_id
//	    <psm p:psm_id="T-32578">
//	    <psm p:psm_id="T-32578-2">  for second PSM for same scan number

		//  	psm_id="T-<ScanId>"

		//  or
		
		//  	psm_id="T-<ScanId>-<match counter>"
		
		

		PsmIdSplitObject psmIdSplitObject = new PsmIdSplitObject();
		
		if ( psmIdString == null ) {
			
			String msg = "SplitKojakPsmId: psmIdString is null";
			throw new IllegalArgumentException(msg);
		}
		
		if ( ! psmIdString.startsWith( PercKojakPsmIdConstants.PREFIX ) ) {
			
			String msg = "SplitKojakPsmId: psmIdString does NOT start with '" + PercKojakPsmIdConstants.PREFIX + "'"
					+", psmIdString: " + psmIdString;
			throw new IllegalArgumentException(msg);
		}


		int scanNumberEndIndex = psmIdString.length();

		//  Search for the "-" in the scan number
		int scanNumberCounterIndex = psmIdString.indexOf( '-' , PercKojakPsmIdConstants.PREFIX.length() );

		if ( scanNumberCounterIndex != -1 && scanNumberCounterIndex < scanNumberEndIndex ) {

			scanNumberEndIndex = scanNumberCounterIndex;
		}

		String scanNumberString = psmIdString.substring( PercKojakPsmIdConstants.PREFIX.length(), scanNumberEndIndex );

		int scanNumber = 0;

		try {

			scanNumber = Integer.parseInt( scanNumberString );
		} catch ( Exception e ) {

			throw new Exception( "Cannot parse scanNumber '" + scanNumberString + "', psmIdString = '" + psmIdString + "'");
		}

		psmIdSplitObject.setPsmIdString( psmIdString );
		psmIdSplitObject.setScanNumberString( scanNumberString );
		psmIdSplitObject.setScanNumber( scanNumber );
		

		return psmIdSplitObject;
	}
	
}
