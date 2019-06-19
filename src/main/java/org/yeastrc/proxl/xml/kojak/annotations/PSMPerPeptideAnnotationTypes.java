package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.*;

import java.util.ArrayList;
import java.util.List;

public class PSMPerPeptideAnnotationTypes {

	// Kojak scores
	public static final String KOJAK_ANNOTATION_TYPE_SCORE = "Score";
	public static final String KOJAK_ANNOTATION_TYPE_RANK = "Rank";
	public static final String KOJAK_ANNOTATION_TYPE_EVALUE = "evalue";
	public static final String KOJAK_ANNOTATION_TYPE_ION_MATCH = "Ion Match";
	public static final String KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH = "Consec. Ion Match";


	/**
	 * @return
	 */
	public static List<FilterablePsmPerPeptideAnnotationType> getFilterablePsmPerPeptideAnnotationTypes(String programName) {
		List<FilterablePsmPerPeptideAnnotationType> types = new ArrayList<>();

		if( programName.equals(ConverterConstants.PROGRAM_NAME_KOJAK) ) {
			{
				FilterablePsmPerPeptideAnnotationType type = new FilterablePsmPerPeptideAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_SCORE );
				type.setDescription( "Kojak score" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}
			{
				FilterablePsmPerPeptideAnnotationType type = new FilterablePsmPerPeptideAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_RANK );
				type.setDescription( "Rank of this peptide when evaluating cross-links" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}
			{
				FilterablePsmPerPeptideAnnotationType type = new FilterablePsmPerPeptideAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_EVALUE );
				type.setDescription( "Computed expect value for this PSM match" );
				type.setFilterDirection( FilterDirectionType.BELOW );

				types.add( type );
			}
			{
				FilterablePsmPerPeptideAnnotationType type = new FilterablePsmPerPeptideAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_ION_MATCH );
				type.setDescription( "Number of fragment ions matched" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}
			{
				FilterablePsmPerPeptideAnnotationType type = new FilterablePsmPerPeptideAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH );
				type.setDescription( "Number of consecutive fragment ions matched" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}
		}
		
		return types;
	}
	
	/**
	 * Get the list of descriptive (non-filterable) PSM annotation types in StavroX data
	 * @return
	 */
	public static List<DescriptivePsmPerPeptideAnnotationType> getDescriptivePsmAnnotationTypes(String programName ) {
		List<DescriptivePsmPerPeptideAnnotationType> types = new ArrayList<>();

		
		return types;		
	}
	
}
