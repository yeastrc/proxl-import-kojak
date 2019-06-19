package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * The default order by which to sort the results.
 * 
 * @author mriffle
 *
 */
public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder( int RUNTYPE ) {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		if( RUNTYPE == ConverterConstants.RUN_TYPE_KOJAK_ONLY) {

			if( RUNTYPE == ConverterConstants.RUN_TYPE_KOJAK_ONLY) {
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_EVALUE);
				annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
				annotations.add(annotation);
			}

			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_SCORE);
				annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
				annotations.add(annotation);
			}
		}
		
		return annotations;
	}
}
