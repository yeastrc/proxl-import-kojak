package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.default_visible_annotations;

import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;

//import org.apache.log4j.Logger;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.DefaultVisibleAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmPerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.VisibleReportedPeptideAnnotations;

/**
 * 
 *
 */
public class AddKojakAndPercolatorDefaultVisibleAnnotations {
	
//	private static final Logger log = Logger.getLogger( AddDefaultVisibleAnnotations.class );
	

	/**
	 * private constructor
	 */
	private AddKojakAndPercolatorDefaultVisibleAnnotations() { }

	public static AddKojakAndPercolatorDefaultVisibleAnnotations getInstance() {
		return new AddKojakAndPercolatorDefaultVisibleAnnotations();
	}
	
	/**
	 * Add Default Visible Annotations for Reported Peptides and PSMs 
	 * @param searchProgramInfo
	 */
	public void addDefaultVisibleAnnotations( SearchProgramInfo searchProgramInfo ) {
		
		DefaultVisibleAnnotations defaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( defaultVisibleAnnotations );
		
		{
			VisiblePsmAnnotations visiblePsmAnnotations = new VisiblePsmAnnotations();
			defaultVisibleAnnotations.setVisiblePsmAnnotations( visiblePsmAnnotations );

			List<SearchAnnotation> visiblePsmAnnotationsSearchAnnotationList = visiblePsmAnnotations.getSearchAnnotation();

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}
			

		}
		
		{
			VisibleReportedPeptideAnnotations visibleReportedPeptideAnnotations = new VisibleReportedPeptideAnnotations();
			defaultVisibleAnnotations.setVisibleReportedPeptideAnnotations( visibleReportedPeptideAnnotations );
			
			List<SearchAnnotation> visibleReportedPeptideAnnotationsSearchAnnotationList = visibleReportedPeptideAnnotations.getSearchAnnotation();
			

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visibleReportedPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visibleReportedPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visibleReportedPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}
			
		}
		{
			VisiblePsmPerPeptideAnnotations visiblePsmPerPeptideAnnotations = new VisiblePsmPerPeptideAnnotations();
			defaultVisibleAnnotations.setVisiblePsmPerPeptideAnnotations( visiblePsmPerPeptideAnnotations );

			List<SearchAnnotation> visiblePsmPerPeptideAnnotationsSearchAnnotationList = visiblePsmPerPeptideAnnotations.getSearchAnnotation();

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmPerPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}
		}
		
	}
	
}
