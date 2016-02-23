package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.constants.KojakFileContentsConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.psm_processing.PsmMatchingAndCollection;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
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
			
			
			setSearchProgramValuesFromKojakFileReader( searchProgram, kojakFileReader );
			
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
	
	
	/**
	 * Add data from the Kojak header to the searchProgram object
	 * 
	 * @param searchProgram
	 * @param kojakFileReader
	 * @throws ProxlGenXMLDataException
	 */
	private void setSearchProgramValuesFromKojakFileReader( SearchProgram searchProgram, KojakFileReader kojakFileReader ) throws ProxlGenXMLDataException {
		

		searchProgram.setVersion( kojakFileReader.getProgramVersion() );

		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

		/////////////  Filterable
		
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
		psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
		
		List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();
				
		Set<String> filteredAnnotationNamesFromColumnHeaders =
				kojakFileReader.getFilteredAnnotationNamesFromColumnHeaders();
		
		for ( String name : filteredAnnotationNamesFromColumnHeaders ) {
			
			
			if ( KojakFileContentsConstants.SCORE_HEADER_LABEL.equals( name ) ) {
				
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( name );

				filterablePsmAnnotationType.setDescription( "Kojak Score" );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
				
				
				
			} else if ( KojakFileContentsConstants.DSCORE_HEADER_LABEL.equals( name ) ) {
				

				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( name );

				filterablePsmAnnotationType.setDescription( "Difference between score and next-highest scoring PSM." );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );

			} else if ( KojakFileContentsConstants.PEPDIFF_HEADER_LABEL.equals( name ) ) {
				

				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( name );

				filterablePsmAnnotationType.setDescription( "For cross-links, the score of the lower scoring peptide." );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
				
				
			} else {
				
				String msg = "Kojak file processing.  Unexpected Filterable annotation name: " + name;
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}
		}
		
		/////////////  Descriptive
		
		
		DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
		psmAnnotationTypes.setDescriptivePsmAnnotationTypes( descriptivePsmAnnotationTypes );
		
		List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
				descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();
		
		Set<String> descriptiveAnnotationNamesFromColumnHeaders =
				kojakFileReader.getDescriptiveAnnotationNamesFromColumnHeaders();

		for ( String name : descriptiveAnnotationNamesFromColumnHeaders ) {
			
			DescriptivePsmAnnotationType descriptivePsmAnnotationType = new DescriptivePsmAnnotationType();
			descriptivePsmAnnotationTypeList.add( descriptivePsmAnnotationType );
			
			descriptivePsmAnnotationType.setName( name );
			descriptivePsmAnnotationType.setDescription( name );
		}
	}

}
