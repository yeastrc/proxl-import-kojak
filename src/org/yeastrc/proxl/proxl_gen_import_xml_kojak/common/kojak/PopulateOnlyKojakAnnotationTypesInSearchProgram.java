package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakFileContentsConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.constants.KojakOnlyDefaultFilterValueConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmPerPeptideAnnotationTypes;

public class PopulateOnlyKojakAnnotationTypesInSearchProgram {

	private static final Logger log = Logger.getLogger( PopulateOnlyKojakAnnotationTypesInSearchProgram.class );
	
	public enum SetKojakDefaultCutoffs { YES, NO }
	
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
	 * @param setKojakDefaultCutoffs - Should default cutoffs be set on Kojak Filterable annotation types
	 * @throws ProxlGenXMLDataException
	 */
	public void populateKojakAnnotationTypesInSearchProgram( 
			SearchProgram searchProgram, 
			KojakFileReader kojakFileReader, 
			SetKojakDefaultCutoffs setKojakDefaultCutoffs 
			) throws ProxlGenXMLDataException {
		

		searchProgram.setVersion( kojakFileReader.getProgramVersion() );

		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

		/////////////  Filterable
		
		GetKojakFilterableAnnTypeObjects getKojakFilterableAnnTypeObjects = GetKojakFilterableAnnTypeObjects.getInstance();
		
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
		psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
		
		List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();
				
		Set<String> filteredAnnotationNamesFromColumnHeaders =
				kojakFileReader.getFilteredAnnotationNamesFromColumnHeaders();
		
		//  Process filteredAnnotationNamesFromColumnHeaders twice
		//    1)  Process for E_VALUE_HEADER_LABEL
		//    2)  Process for Other Than E_VALUE_HEADER_LABEL
		

		boolean foundEvalue = false;
		
		//  E-value
		
		for ( String name : filteredAnnotationNamesFromColumnHeaders ) {

			if ( KojakFileContentsConstants.E_VALUE_HEADER_LABEL.equals( name ) ) {
				
				foundEvalue = true;
				FilterablePsmAnnotationType filterablePsmAnnotationType = getKojakFilterableAnnTypeObjects.getEValueAnnTypeObject();
				
				if ( setKojakDefaultCutoffs == SetKojakDefaultCutoffs.YES ) {
					filterablePsmAnnotationType.setDefaultFilter( true );
					filterablePsmAnnotationType.setDefaultFilterValue( KojakOnlyDefaultFilterValueConstants.KOJAK_E_VALUE_PSM_VALUE_DEFAULT );
				}
				
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
				break;
			}
		}

		//  Other than E-value
		
		for ( String name : filteredAnnotationNamesFromColumnHeaders ) {
		
			if ( KojakFileContentsConstants.E_VALUE_HEADER_LABEL.equals( name ) ) {
				//  Processed in first loop

			} else if ( KojakFileContentsConstants.SCORE_HEADER_LABEL.equals( name ) ) {
				
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getScoreAnnTypeObject();
				
				if ( ! foundEvalue ) { //  No E-value so Score is default cutoff
					if ( setKojakDefaultCutoffs == SetKojakDefaultCutoffs.YES ) {
						filterablePsmAnnotationType.setDefaultFilter( true );
						filterablePsmAnnotationType.setDefaultFilterValue( KojakOnlyDefaultFilterValueConstants.KOJAK_SCORE_PSM_VALUE_DEFAULT );
					}
				}
				
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
			} else if ( KojakFileContentsConstants.DSCORE_HEADER_LABEL.equals( name ) ) {

				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getDScoreAnnTypeObject();
				
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
			} else if ( KojakFileContentsConstants.PEPDIFF_HEADER_LABEL.equals( name ) ) {

				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getPepDiffAnnTypeObject();
				
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
				
			} else {
				
				String msg = "Kojak file processing.  Unexpected Filterable annotation name: " + name;
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}
		}
		
		//   Filterable at PSM and PSM Per Peptide

		//  Kojak E-value at PSM Per Peptide

		if ( kojakFileReader.headerHasPeptide_1_e_value() && kojakFileReader.headerHasPeptide_2_e_value() ) {
			
			//  Process Per-peptide E-value
			
			//  At PSM level, add High E-value and Low E-value Ann Types
			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getPerPeptideHighEValueeAnnTypeObject();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
			}
			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getPerPeptideLowEValueAnnTypeObject();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
			}

			//  At Psm Per Peptide Level, Add Per Peptide E-value
			
			addPsmPerPeptide_E_Value_FilterableAnnotationTypes( searchProgram, getKojakFilterableAnnTypeObjects );
		}
		
		//  Kojak Score at PSM Per Peptide:
		
		if ( kojakFileReader.headerHasPeptide_1_score() && kojakFileReader.headerHasPeptide_2_score() ) {
			
			//  Process Per-peptide score
			
			//  At PSM level, add High Score and Low Score Ann Types
			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getPerPeptideHighScoreAnnTypeObject();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
			}
			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = 
						getKojakFilterableAnnTypeObjects.getPerPeptideLowScoreAnnTypeObject();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );
			}

			//  At Psm Per Peptide Level, Add Per Peptide Score
			
			addPsmPerPeptide_KojakScore_FilterableAnnotationTypes( searchProgram, getKojakFilterableAnnTypeObjects );
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
	

	/**
	 * @param searchProgram
	 * @param getKojakFilterableAnnTypeObjects
	 */
	private void addPsmPerPeptide_E_Value_FilterableAnnotationTypes( SearchProgram searchProgram, GetKojakFilterableAnnTypeObjects getKojakFilterableAnnTypeObjects ) {

		//  At Psm Per Peptide Level, Add Per Peptide E-value
		
		PsmPerPeptideAnnotationTypes psmPerPeptideAnnotationTypes = searchProgram.getPsmPerPeptideAnnotationTypes();
		if ( psmPerPeptideAnnotationTypes == null ) {
			psmPerPeptideAnnotationTypes = new PsmPerPeptideAnnotationTypes();
			searchProgram.setPsmPerPeptideAnnotationTypes( psmPerPeptideAnnotationTypes );
		}
		
		/////////////  Filterable
		
		FilterablePsmPerPeptideAnnotationTypes filterablePsmPerPeptideAnnotationTypes = psmPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationTypes();
		if ( filterablePsmPerPeptideAnnotationTypes == null ) {
			filterablePsmPerPeptideAnnotationTypes = new FilterablePsmPerPeptideAnnotationTypes();
			psmPerPeptideAnnotationTypes.setFilterablePsmPerPeptideAnnotationTypes( filterablePsmPerPeptideAnnotationTypes );
		}
		
		List<FilterablePsmPerPeptideAnnotationType> filterablePsmPerPeptideAnnotationTypeList =
				filterablePsmPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationType();
				
		FilterablePsmPerPeptideAnnotationType filterablePsmPerPeptideAnnotationType = 
				getKojakFilterableAnnTypeObjects.getPsmPerPeptideEValueAnnTypeObject();

		filterablePsmPerPeptideAnnotationTypeList.add( filterablePsmPerPeptideAnnotationType );
	}
	
	
	/**
	 * @param searchProgram
	 * @param getKojakFilterableAnnTypeObjects
	 */
	private void addPsmPerPeptide_KojakScore_FilterableAnnotationTypes( SearchProgram searchProgram, GetKojakFilterableAnnTypeObjects getKojakFilterableAnnTypeObjects ) {

		//  At Psm Per Peptide Level, Add Per Peptide Score
		
		PsmPerPeptideAnnotationTypes psmPerPeptideAnnotationTypes = searchProgram.getPsmPerPeptideAnnotationTypes();
		if ( psmPerPeptideAnnotationTypes == null ) {
			psmPerPeptideAnnotationTypes = new PsmPerPeptideAnnotationTypes();
			searchProgram.setPsmPerPeptideAnnotationTypes( psmPerPeptideAnnotationTypes );
		}

		/////////////  Filterable
		
		FilterablePsmPerPeptideAnnotationTypes filterablePsmPerPeptideAnnotationTypes = psmPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationTypes();
		if ( filterablePsmPerPeptideAnnotationTypes == null ) {
			filterablePsmPerPeptideAnnotationTypes = new FilterablePsmPerPeptideAnnotationTypes();
			psmPerPeptideAnnotationTypes.setFilterablePsmPerPeptideAnnotationTypes( filterablePsmPerPeptideAnnotationTypes );
		}
				
		List<FilterablePsmPerPeptideAnnotationType> filterablePsmPerPeptideAnnotationTypeList =
				filterablePsmPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationType();
				
		FilterablePsmPerPeptideAnnotationType filterablePsmPerPeptideAnnotationType = 
				getKojakFilterableAnnTypeObjects.getPsmPerPeptideScoreAnnTypeObject();

		filterablePsmPerPeptideAnnotationTypeList.add( filterablePsmPerPeptideAnnotationType );
	}

}
