package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;

/**
 * Get FilterablePsmAnnotationType objects
 *
 */
public class GetKojakFilterableAnnTypeObjects {

//	private static final Logger log = Logger.getLogger( GetKojakFilterableObjects.class );
	
	// private constructor
	private GetKojakFilterableAnnTypeObjects() { }
	
	public static GetKojakFilterableAnnTypeObjects getInstance() {
		return new GetKojakFilterableAnnTypeObjects();
	}
	public static final String SCORE_HEADER_LABEL = "Score";
	public static final String DSCORE_HEADER_LABEL = "dScore";
	public static final String PEPDIFF_HEADER_LABEL = "Pep. Diff.";

	
	public FilterablePsmAnnotationType getScoreAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		
		filterablePsmAnnotationType.setDefaultFilter( false );

		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_SCORE );

		filterablePsmAnnotationType.setDescription( "Kojak Score" );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		
		return filterablePsmAnnotationType;
	}
	

	public FilterablePsmAnnotationType getDScoreAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();

		filterablePsmAnnotationType.setDefaultFilter( false );

		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_DSCORE );

		filterablePsmAnnotationType.setDescription( "Difference between score and next-highest scoring PSM." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		
		return filterablePsmAnnotationType;
	}
	

	public FilterablePsmAnnotationType getPepDiffAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();

		filterablePsmAnnotationType.setDefaultFilter( false );

		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PEP_DIFF );

		filterablePsmAnnotationType.setDescription( "For cross-links, the score of the lower scoring peptide." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		
		return filterablePsmAnnotationType;
	}

}
