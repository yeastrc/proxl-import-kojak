package org.yeastrc.proxl.xml.kojak.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some utility methods for parsing scan variables from the reported scan information in plink results files.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class ScanParsingUtils {
	
	/**
	 * Get the name of the scan file from the reported scan. E.g. QEP2_2016_0121_RJ_68_205_comet.00965.00965.3
	 * would return QEP2_2016_0121_RJ_68_205_comet
	 * 
	 * @param reportedScan
	 * @return
	 * @throws Exception
	 */
	public static String getFilenameFromReportedScan( String reportedScan ) throws Exception {
		
		Pattern r = Pattern.compile( "^(.+)\\.\\d+\\.\\d+\\.\\d+$" );
		Matcher m = r.matcher( reportedScan );
		
		if( m.matches() ) {
			return m.group( 1 );
		} else {
			return null;
		}
		
	}
}
