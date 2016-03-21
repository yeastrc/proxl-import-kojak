package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl_import.api.xml_dto.AnnotationSortOrder;
import org.yeastrc.proxl_import.api.xml_dto.PsmAnnotationSortOrder;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;


public class AddKojakOnlyAnnotationSortOrder {

//	private static final Logger log = Logger.getLogger( AddKojakOnlyAnnotationSortOrder.class );
	

	/**
	 * private constructor
	 */
	private AddKojakOnlyAnnotationSortOrder() { }

	public static AddKojakOnlyAnnotationSortOrder getInstance() {
		return new AddKojakOnlyAnnotationSortOrder();
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

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				psmAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_DSCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				psmAnnotationSortOrderSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PEP_DIFF );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}

		}
		
		
	}

}
