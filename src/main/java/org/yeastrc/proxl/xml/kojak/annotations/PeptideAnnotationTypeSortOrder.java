package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;

import java.util.ArrayList;
import java.util.List;

public class PeptideAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPeptideAnnotationTypeSortOrder() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();
		
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_QVALUE );
			annotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_PERCOLATOR );
			annotations.add( annotation );
		}
		
		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_PEP );
			annotation.setSearchProgram( ConverterConstants.PROGRAM_NAME_PERCOLATOR );
			annotations.add( annotation );
		}
		
		return annotations;
	}
}