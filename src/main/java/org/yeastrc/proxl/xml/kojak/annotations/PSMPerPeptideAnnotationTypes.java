package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;

import java.math.BigDecimal;
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
	public static List<FilterablePsmAnnotationType> getFilterablePsmPerPeptideAnnotationTypes(String programName, int RUNTYPE ) {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		if( programName.equals(ConverterConstants.PROGRAM_NAME_KOJAK) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_SCORE );
				type.setDescription( "Kojak score" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_RANK );
				type.setDescription( "Rank of this peptide when evaluating cross-links" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_EVALUE );
				type.setDescription( "Computed expect value for this PSM match" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_ION_MATCH );
				type.setDescription( "Number of fragment ions matched" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH );
				type.setDescription( "Number of consecutive fragment ions matched" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );

				types.add( type );
			}
		}
		
		return types;
	}
	
	/**
	 * Get the list of descriptive (non-filterable) PSM annotation types in StavroX data
	 * @return
	 */
	public static List<DescriptivePsmAnnotationType> getDescriptivePsmAnnotationTypes(String programName ) {
		List<DescriptivePsmAnnotationType> types = new ArrayList<DescriptivePsmAnnotationType>();

		
		return types;		
	}
	
}
