package org.yeastrc.proxl.xml.kojak.annotations;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypes {

	// Kojak scores
	public static final String KOJAK_ANNOTATION_TYPE_SCORE = "Score";
	public static final String KOJAK_ANNOTATION_TYPE_DELTA_SCORE = "delta Score";
	public static final String KOJAK_ANNOTATION_TYPE_PPM_ERROR = "PPM Error";
	public static final String KOJAK_ANNOTATION_TYPE_EVALUE = "evalue";
	public static final String KOJAK_ANNOTATION_TYPE_ION_MATCH = "Ion Match";
	public static final String KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH = "Consec. Ion Match";


	/**
	 * @return
	 */
	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes(String programName, int RUNTYPE ) {
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
				type.setName( KOJAK_ANNOTATION_TYPE_DELTA_SCORE );
				type.setDescription( "Difference in score between this and the next best match" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_PPM_ERROR );
				type.setDescription( "PPM error of parent ion given this PSM id" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilter( false );

				types.add( type );
			}
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( KOJAK_ANNOTATION_TYPE_EVALUE );
				type.setDescription( "Computed expect value for this PSM match" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilter( false );

				if( RUNTYPE == ConverterConstants.RUN_TYPE_KOJAK_ONLY ) {
					type.setDefaultFilterValue( new BigDecimal( "0.01" ) );
				}

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
