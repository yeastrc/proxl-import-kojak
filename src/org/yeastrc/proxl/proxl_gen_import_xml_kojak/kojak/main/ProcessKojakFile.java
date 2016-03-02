package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.psm_processing.PsmMatchingAndCollection;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;

/**
 * 
 *
 */
public class ProcessKojakFile {

	private static final Logger log = Logger.getLogger( ProcessKojakFile.class );
	
	// private constructor
	private ProcessKojakFile() { }
	
	public static ProcessKojakFile getInstance() {
		return new ProcessKojakFile();
	}

	
	/**
	 * @param kojakOutputFile
	 * @param proxlInputRoot
	 * @param psmMatchingAndCollection
	 * @throws Exception 
	 */
	public void processKojakFile( 
			
			File kojakOutputFile,
			ProxlInput proxlInputRoot,
			PsmMatchingAndCollection psmMatchingAndCollection ) throws Exception {
		

		SearchProgramInfo searchProgramInfo = proxlInputRoot.getSearchProgramInfo();
		
		SearchPrograms searchPrograms = searchProgramInfo.getSearchPrograms();
		List<SearchProgram> searchProgramList = searchPrograms.getSearchProgram();
		
		SearchProgram searchProgram = new SearchProgram();
		searchProgramList.add( searchProgram );
		
		searchProgram.setName( SearchProgramNameKojakImporterConstants.KOJAK );
		searchProgram.setDisplayName( SearchProgramNameKojakImporterConstants.KOJAK );
		searchProgram.setDescription( null );
		
		
		KojakFileReader kojakFileReader = null;
		
		try {
			
			//  The reader reads the version line and the header lines in the getInstance(...) method
			
			kojakFileReader = KojakFileReader.getInstance( kojakOutputFile );
			
			PopulateKojakAnnotationTypesInSearchProgram.getInstance().populateKojakAnnotationTypesInSearchProgram( searchProgram, kojakFileReader );
			
			searchProgram.setVersion( kojakFileReader.getProgramVersion() );
			
			
			
			
			
			
			//  Process the data lines:

			while (true) {

				KojakPsmDataObject kojakPsmDataObject;

				try {
					kojakPsmDataObject = kojakFileReader.getNextKojakLine();

				} catch ( Exception e ) {

					String msg = "Error reading Kojak file (file: " + kojakOutputFile.getAbsolutePath() + ") .";

					log.error( msg, e );

					throw e;
				}

				if ( kojakPsmDataObject == null ) {

					break;  //  EARLY EXIT from LOOOP
				}
				
				System.out.println( "Processing Kojak record for scan number: " + kojakPsmDataObject.getScanNumber() );

				psmMatchingAndCollection.addKojakPsmData( kojakPsmDataObject );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Error processing Kojak file: " + kojakOutputFile.getAbsolutePath();
			log.error( msg );
			throw e;
		
		} finally {
			
			if ( kojakFileReader != null  ) {
				
				kojakFileReader.close();
			}
			
		}
		
		
		
	}
	
	

}
