package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;

/**
 * 
 *
 */
public class ProcessKojakConfFile {

	private static final Logger log = Logger.getLogger( ProcessKojakConfFile.class );
	
	// private constructor
	private ProcessKojakConfFile() { }
	
	public static ProcessKojakConfFile getInstance() {
		return new ProcessKojakConfFile();
	}

	
	/**
	 * @param kojakOutputFile
	 * @param proxlInputRoot
	 * @param psmMatchingAndCollection
	 * @throws Exception 
	 */
	public void processKojakConfFile( 
			
			File kojakConfFile,
			ProxlInput proxlInputRoot ) throws Exception {
		

		
		
		KojakConfFileReader kojakConfFileReader = null;
		
		try {
			
			//  The reader reads the version line and the header lines in the getInstance(...) method
			
			kojakConfFileReader = KojakConfFileReader.getInstance(  );
			
			
			kojakConfFileReader.readKojakConfFile( kojakConfFile, proxlInputRoot );

			
		} catch ( Exception e ) {
			
			String msg = "Error processing Kojak Conf file: " + kojakConfFile.getAbsolutePath();
			log.error( msg );
			throw e;
		
		} finally {
			
			
			
		}
		
		
		
	}
}
