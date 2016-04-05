package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFiles;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;
import org.yeastrc.proxl_import.api.xml_dto.StaticModifications;

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
			
			
			KojakConfFileReaderResult kojakConfFileReaderResult =
					kojakConfFileReader.readKojakConfFile( kojakConfFile, proxlInputRoot );

			ConfigurationFiles configurationFiles = new ConfigurationFiles();
			proxlInputRoot.setConfigurationFiles( configurationFiles );
			List<ConfigurationFile> configurationFileList = configurationFiles.getConfigurationFile();
			configurationFileList.add( kojakConfFileReaderResult.getConfigurationFile() );

			//  Add Static modifications if list not empty
			
			if ( kojakConfFileReaderResult.getStaticModificationListForThisFile() != null
					&& ( ! kojakConfFileReaderResult.getStaticModificationListForThisFile().isEmpty() ) ) {

				StaticModifications staticModifications = new StaticModifications();
				List<StaticModification> staticModificationList = staticModifications.getStaticModification();

				staticModificationList.addAll( kojakConfFileReaderResult.getStaticModificationListForThisFile() );

				proxlInputRoot.setStaticModifications( staticModifications );
			}
			
			
			IsModificationAMonolink.getInstance().setMonolinkModificationMassesFromProxlInputObject( proxlInputRoot );
			
		} catch ( Exception e ) {
			
			String msg = "Error processing Kojak Conf file: " + kojakConfFile.getAbsolutePath();
			log.error( msg );
			throw e;
		
		} finally {
			
			
			
		}
		
		
		
	}
}
