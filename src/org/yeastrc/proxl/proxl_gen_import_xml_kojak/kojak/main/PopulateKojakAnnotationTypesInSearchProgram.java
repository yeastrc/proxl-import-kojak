package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.constants.KojakFileContentsConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;

/**
 * 
 *
 */
public class PopulateKojakAnnotationTypesInSearchProgram {

	private static final Logger log = Logger.getLogger( PopulateKojakAnnotationTypesInSearchProgram.class );
	
	// private constructor
	private PopulateKojakAnnotationTypesInSearchProgram() { }
	
	public static PopulateKojakAnnotationTypesInSearchProgram getInstance() {
		return new PopulateKojakAnnotationTypesInSearchProgram();
	}


	/**
	 * Add data from the Kojak header to the searchProgram object
	 * 
	 * @param searchProgram
	 * @param kojakFileReader
	 * @throws ProxlGenXMLDataException
	 */
	public void populateKojakAnnotationTypesInSearchProgram( SearchProgram searchProgram, KojakFileReader kojakFileReader ) throws ProxlGenXMLDataException {
		

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
