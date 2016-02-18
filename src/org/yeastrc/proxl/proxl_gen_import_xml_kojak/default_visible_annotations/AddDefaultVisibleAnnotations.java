package org.yeastrc.proxl.proxl_gen_import_xml_kojak.default_visible_annotations;

import java.util.List;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.DefaultVisibleAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.VisibleReportedPeptideAnnotations;

/**
 * 
 *
 */
public class AddDefaultVisibleAnnotations {
	
//	private static final Logger log = Logger.getLogger( AddDefaultVisibleAnnotations.class );
	

	/**
	 * private constructor
	 */
	private AddDefaultVisibleAnnotations() { }

	public static AddDefaultVisibleAnnotations getInstance() {
		return new AddDefaultVisibleAnnotations();
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

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );
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

			
		}
		
	}
	
}
