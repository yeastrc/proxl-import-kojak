package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl_import.api.xml_dto.DefaultVisibleAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.VisiblePsmPerPeptideAnnotations;



public class AddKojakOnlyDefaultVisibleAnnotations {

//	private static final Logger log = Logger.getLogger( XXXXXXXXXXXX.class );
	

	/**
	 * private constructor
	 */
	private AddKojakOnlyDefaultVisibleAnnotations() { }

	public static AddKojakOnlyDefaultVisibleAnnotations getInstance() {
		return new AddKojakOnlyDefaultVisibleAnnotations();
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

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_E_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}
			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_LOW_E_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}


			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_DSCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PEP_DIFF );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}
			

		}
		

		{
			VisiblePsmPerPeptideAnnotations visiblePsmPerPeptideAnnotations = new VisiblePsmPerPeptideAnnotations();
			defaultVisibleAnnotations.setVisiblePsmPerPeptideAnnotations( visiblePsmPerPeptideAnnotations );

			List<SearchAnnotation> visiblePsmPerPeptideAnnotationsSearchAnnotationList = visiblePsmPerPeptideAnnotations.getSearchAnnotation();

			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmPerPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_E_VALUE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}
			{
				SearchAnnotation searchAnnotation = new SearchAnnotation();
				visiblePsmPerPeptideAnnotationsSearchAnnotationList.add( searchAnnotation );

				searchAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_SCORE );
				searchAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
			}
		}
	}
	

}
