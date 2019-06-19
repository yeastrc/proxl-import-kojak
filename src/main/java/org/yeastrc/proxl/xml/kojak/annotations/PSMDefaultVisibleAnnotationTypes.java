package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

public class PSMDefaultVisibleAnnotationTypes {

	/**
	 * Get the default visibile annotation types for iProphet data
	 * @return
	 */
	public static List<SearchAnnotation> getDefaultVisibleAnnotationTypes( int RUN_TYPE) {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

			{
				SearchAnnotation annotation = new SearchAnnotation();
				annotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_EVALUE);
				annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
				annotations.add(annotation);
			}

			if( RUN_TYPE == ConverterConstants.RUN_TYPE_KOJAK_ONLY ) {
				{
					SearchAnnotation annotation = new SearchAnnotation();
					annotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_SCORE);
					annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
					annotations.add(annotation);
				}
				{
					SearchAnnotation annotation = new SearchAnnotation();
					annotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_DELTA_SCORE);
					annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
					annotations.add(annotation);
				}
			}

		return annotations;
	}
	
}
