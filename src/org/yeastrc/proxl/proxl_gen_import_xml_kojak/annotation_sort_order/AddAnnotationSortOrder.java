package org.yeastrc.proxl.proxl_gen_import_xml_kojak.annotation_sort_order;

import java.util.List;




//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.AnnotationSortOrder;
import org.yeastrc.proxl_import.api.xml_dto.PsmAnnotationSortOrder;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptideAnnotationSortOrder;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;

/**
 * 
 *
 */
public class AddAnnotationSortOrder {
	
//	private static final Logger log = Logger.getLogger( AddAnnotationSortOrder.class );
	

	/**
	 * private constructor
	 */
	private AddAnnotationSortOrder() { }

	public static AddAnnotationSortOrder getInstance() {
		return new AddAnnotationSortOrder();
	}
	
	/**
	 * Add Annotation Sort Order for Reported Peptides and PSMs 
	 * @param searchProgramInfo
	 */
	public void addAnnotationSortOrder( SearchProgramInfo searchProgramInfo ) {
		
		AnnotationSortOrder annotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( annotationSortOrder );
		
		{
			PsmAnnotationSortOrder psmAnnotationSortOrder = new PsmAnnotationSortOrder();
			annotationSortOrder.setPsmAnnotationSortOrder( psmAnnotationSortOrder );

			List<SearchAnnotation> psmAnnotationSortOrderSearchAnnotationList = psmAnnotationSortOrder.getSearchAnnotation();

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				psmAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				psmAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				psmAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}
		}
		
		{
			ReportedPeptideAnnotationSortOrder reportedPeptideAnnotationSortOrder = new ReportedPeptideAnnotationSortOrder();
			annotationSortOrder.setReportedPeptideAnnotationSortOrder( reportedPeptideAnnotationSortOrder );
			
			List<SearchAnnotation> reportedPeptideAnnotationSortOrderSearchAnnotationList = reportedPeptideAnnotationSortOrder.getSearchAnnotation();
			

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				reportedPeptideAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				reportedPeptideAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				reportedPeptideAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			}
		}
		
	}
	
}
