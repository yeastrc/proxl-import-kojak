package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
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
	public static PsmIdSplitObject splitKojakPsmId( 
			
			String psmIdString, 
			List<String> scanFilename_MainPart_For_Crux_Format_List ) throws Exception {

		//  Example psm_id
//	    <psm p:psm_id="T-32578">
//	    <psm p:psm_id="T-32578-2">  for second PSM for same scan number
		
//		<psm p:psm_id="T-XLpeplib_Beveridge_QEx-HFX_DSS_R3-26808-103.98">   for Crux with san filename:  May be 

		//  	psm_id="T-<ScanId>"

		//  or
		
		//  	psm_id="T-<ScanId>-<match counter>"

		//  or
		
		//		<psm p:psm_id="T-<Scan Filename Main Part>-<ScanId>-<Retention Time>">   for Crux with san filename:  May be 

		if ( psmIdString == null ) {
			
			String msg = "SplitKojakPsmId: psmIdString is null";
			throw new IllegalArgumentException(msg);
		}

		if ( ! psmIdString.startsWith( PercKojakPsmIdConstants.PREFIX ) ) {
			
			String msg = "SplitKojakPsmId: psmIdString does NOT start with '" + PercKojakPsmIdConstants.PREFIX + "'"
					+", psmIdString: " + psmIdString;
			throw new IllegalArgumentException(msg);
		}
		
		if ( scanFilename_MainPart_For_Crux_Format_List != null ) {

			//////////

			//		<psm p:psm_id="T-<Scan Filename Main Part>-<ScanId>-<Retention Time>">   for Crux with scan filename:  May be
			
			for ( String scanFilename_MainPart_For_Crux_Format : scanFilename_MainPart_For_Crux_Format_List ) {

				String psmIdString_AfterPrefix = psmIdString.substring(PercKojakPsmIdConstants.PREFIX.length() );  //  after "T-" at beginning
				
				if ( psmIdString_AfterPrefix.startsWith( scanFilename_MainPart_For_Crux_Format ) ) {
					
					if ( psmIdString_AfterPrefix.charAt( scanFilename_MainPart_For_Crux_Format.length() ) != '-' ) {
						
						//  Missing '-' after scan filename
						
						continue; // EARLY CONTINUE
					}
					
					String string_After_ScanFilename = psmIdString_AfterPrefix.substring(scanFilename_MainPart_For_Crux_Format.length() + 1); // + 1 to skip '-'

					int last_Dash_Index = string_After_ScanFilename.indexOf( "-" ); // dash after scan number

					String scanNumberString = string_After_ScanFilename.substring(0, last_Dash_Index);

					int scanNumber = 0;

					try {

						scanNumber = Integer.parseInt( scanNumberString );
					} catch ( Exception e ) {

						throw new Exception( "Cannot parse scanNumber '" + scanNumberString + "', psmIdString = '" + psmIdString + "'");
					}

					PsmIdSplitObject psmIdSplitObject = new PsmIdSplitObject();

					psmIdSplitObject.setPsmIdString( psmIdString );
					psmIdSplitObject.setScanNumberString( scanNumberString );
					psmIdSplitObject.setScanNumber( scanNumber );
					psmIdSplitObject.setScanFilename_MainPart_String(scanFilename_MainPart_For_Crux_Format);

					///
					
					return psmIdSplitObject;  //  EARLY RETURN
				}
			}
			
			throw new ProxlGenXMLDataException( "--crux specified and Percolator 'psm_id' string does NOT contain any scan filenames extracted from kojak output filenames.  filenames: " 
					+ StringUtils.join( scanFilename_MainPart_For_Crux_Format_List, ", " ) );
		}

		/////////////


		//  Original code

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

		PsmIdSplitObject psmIdSplitObject = new PsmIdSplitObject();

		psmIdSplitObject.setPsmIdString( psmIdString );
		psmIdSplitObject.setScanNumberString( scanNumberString );
		psmIdSplitObject.setScanNumber( scanNumber );


		return psmIdSplitObject;

	}
	
}
