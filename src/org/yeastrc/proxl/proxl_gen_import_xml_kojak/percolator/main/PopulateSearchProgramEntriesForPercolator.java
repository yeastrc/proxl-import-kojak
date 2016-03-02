package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.main;

import java.util.List;



//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.DefaultFilterValueConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.ReportedPeptideAnnotationTypes;

/**
 * 
 *
 */
public class PopulateSearchProgramEntriesForPercolator {


//	private static final Logger log = Logger.getLogger( PopulateSearchProgramEntriesForPercolator.class );

	//  private constructor
	private PopulateSearchProgramEntriesForPercolator() {}


	public static PopulateSearchProgramEntriesForPercolator getInstance() {

		return new PopulateSearchProgramEntriesForPercolator();
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Add SearchProgram data for Percolator
	 * 
	 * @param searchProgram
	 * @param kojakFileReader
	 * @throws ProxlGenXMLDataException
	 */
	public void populateSearchProgramEntriesForPercolator( ProxlInput proxlInputRoot, String percolatorVersion ) throws ProxlGenXMLDataException {

		SearchProgramInfo searchProgramInfo = proxlInputRoot.getSearchProgramInfo();
		SearchPrograms searchPrograms = searchProgramInfo.getSearchPrograms();

		List<SearchProgram> searchProgramList = searchPrograms.getSearchProgram();

		SearchProgram searchProgram = new SearchProgram();
		searchProgramList.add( searchProgram );


		searchProgram.setName( SearchProgramNameKojakImporterConstants.PERCOLATOR );
		searchProgram.setDisplayName( SearchProgramNameKojakImporterConstants.PERCOLATOR );


		searchProgram.setVersion( percolatorVersion );




		////////////   Reported Peptide data

		ReportedPeptideAnnotationTypes peptideAnnotationTypes = new ReportedPeptideAnnotationTypes();
		searchProgram.setReportedPeptideAnnotationTypes( peptideAnnotationTypes );

		/////////////  Filterable

		{
			FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes = new FilterablePeptideAnnotationTypes();
			peptideAnnotationTypes.setFilterablePeptideAnnotationTypes( filterablePeptideAnnotationTypes );

			List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
					filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();


			//////////////

			//  q-value - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( true );
				
				filterablePeptideAnnotationType.setDefaultFilterValue( DefaultFilterValueConstants.PERCOLATOR_PEPTIDE_Q_VALUE_DEFAULT );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_Q_VALUE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  PEP - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_PEP );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}
			
			//////////////

			//  p-value - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_P_VALUE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  SVM Score - Biggest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_SVM_SCORE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
			}


		}


		/////////////  Descriptive

		/* Don't store this.
		{
			DescriptivePeptideAnnotationTypes descriptivePeptideAnnotationTypes = new DescriptivePeptideAnnotationTypes();
			peptideAnnotationTypes.setDescriptivePeptideAnnotationTypes( descriptivePeptideAnnotationTypes );

			List<DescriptivePeptideAnnotationType> descriptivePeptideAnnotationTypeList =
					descriptivePeptideAnnotationTypes.getDescriptivePeptideAnnotationType();

			{
				DescriptivePeptideAnnotationType descriptivePeptideAnnotationType = new DescriptivePeptideAnnotationType();
				descriptivePeptideAnnotationTypeList.add( descriptivePeptideAnnotationType );

				descriptivePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
				descriptivePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
			}
			
		}
		*/


		////////////////////////////

		////////////   PSM data

		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

		/////////////  Filterable
		{

			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );

			List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();


			//////////////

			//  q-value - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( true );
				
				filterablePsmAnnotationType.setDefaultFilterValue( DefaultFilterValueConstants.PERCOLATOR_PEPTIDE_Q_VALUE_DEFAULT );


				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_Q_VALUE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  PEP - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_PEP );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}
			
			//////////////

			//  p-value - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_P_VALUE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  SVM Score - Biggest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_SVM_SCORE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
			}


		}

		/////////////  Descriptive

		{
			DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
			psmAnnotationTypes.setDescriptivePsmAnnotationTypes( descriptivePsmAnnotationTypes );

			List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
					descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();

			
			
//			// Let's not store these

//			{
//				DescriptivePsmAnnotationType descriptivePsmAnnotationType = new DescriptivePsmAnnotationType();
//				descriptivePsmAnnotationTypeList.add( descriptivePsmAnnotationType );
//
//				descriptivePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
//				descriptivePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
//			}
		
			{
				DescriptivePsmAnnotationType descriptivePsmAnnotationType = new DescriptivePsmAnnotationType();
				descriptivePsmAnnotationTypeList.add( descriptivePsmAnnotationType );


				descriptivePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
				descriptivePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
			}
		}
	}
}
