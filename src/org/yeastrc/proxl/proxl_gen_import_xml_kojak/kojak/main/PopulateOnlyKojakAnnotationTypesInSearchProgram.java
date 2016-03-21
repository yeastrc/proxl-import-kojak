package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakFileContentsConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.GetKojakFilterableAnnTypeObjects;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakFileReader;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.constants.KojakOnlyDefaultFilterValueConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;

public class PopulateOnlyKojakAnnotationTypesInSearchProgram {

	private static final Logger log = Logger.getLogger( PopulateOnlyKojakAnnotationTypesInSearchProgram.class );
	
	// private constructor
	private PopulateOnlyKojakAnnotationTypesInSearchProgram() { }
	
	public static PopulateOnlyKojakAnnotationTypesInSearchProgram getInstance() {
		return new PopulateOnlyKojakAnnotationTypesInSearchProgram();
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
				
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						GetKojakFilterableAnnTypeObjects.getInstance().getScoreAnnTypeObject();
				
				filterablePsmAnnotationType.setDefaultFilter( true );
				filterablePsmAnnotationType.setDefaultFilterValue( KojakOnlyDefaultFilterValueConstants.KOJAK_SCORE_PSM_VALUE_DEFAULT );

				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
			} else if ( KojakFileContentsConstants.DSCORE_HEADER_LABEL.equals( name ) ) {

				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						GetKojakFilterableAnnTypeObjects.getInstance().getDScoreAnnTypeObject();
				
//				filterablePsmAnnotationType.setDefaultFilter( true );
//				filterablePsmAnnotationType.setDefaultFilterValue(  );

				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
			} else if ( KojakFileContentsConstants.PEPDIFF_HEADER_LABEL.equals( name ) ) {

				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						GetKojakFilterableAnnTypeObjects.getInstance().getPepDiffAnnTypeObject();
				
//				filterablePsmAnnotationType.setDefaultFilter( true );
//				filterablePsmAnnotationType.setDefaultFilterValue(  );

				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
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
