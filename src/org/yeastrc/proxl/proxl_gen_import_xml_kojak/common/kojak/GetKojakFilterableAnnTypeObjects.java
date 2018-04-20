package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotationType;

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
	

	public FilterablePsmAnnotationType getEValueAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		filterablePsmAnnotationType.setDefaultFilter( false );
		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_E_VALUE );
		filterablePsmAnnotationType.setDescription( "e-value: The expect value, or the number of hits with this score or better one can expect to see by chance." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
		return filterablePsmAnnotationType;
	}
	
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
	
	/////////////////////////////////
	
	//  High and Low values on PSM Per Peptide brought up to the PSM level
	
	//   High and Low E-value

	public FilterablePsmAnnotationType getPerPeptideHighEValueeAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		filterablePsmAnnotationType.setDefaultFilter( false );
		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_WORST_E_VALUE );
		filterablePsmAnnotationType.setDescription( "For  cross-links, the higher of the two e-values for each peptide." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
		return filterablePsmAnnotationType;
	}

	public FilterablePsmAnnotationType getPerPeptideLowEValueAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		filterablePsmAnnotationType.setDefaultFilter( false );
		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_BEST_E_VALUE );
		filterablePsmAnnotationType.setDescription( "For  cross-links, the lower of the two e-values for each peptide." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
		return filterablePsmAnnotationType;
	}
	
	//   High and Low Score (Kojak Score)

	public FilterablePsmAnnotationType getPerPeptideHighScoreAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		filterablePsmAnnotationType.setDefaultFilter( false );
		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_BEST_SCORE );
		filterablePsmAnnotationType.setDescription( "For cross-links, the score of the higher scoring peptide." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		return filterablePsmAnnotationType;
	}

	public FilterablePsmAnnotationType getPerPeptideLowScoreAnnTypeObject() {

		FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
		filterablePsmAnnotationType.setDefaultFilter( false );
		filterablePsmAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_WORST_SCORE );
		filterablePsmAnnotationType.setDescription( "For cross-links, the score of the lower scoring peptide." );
		filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		return filterablePsmAnnotationType;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	//   PSM Per Peptide

	public FilterablePsmPerPeptideAnnotationType getPsmPerPeptideEValueAnnTypeObject() {

		FilterablePsmPerPeptideAnnotationType filterablePsmPerPeptideAnnotationType = new FilterablePsmPerPeptideAnnotationType();
//		filterablePsmPerPeptideAnnotationType.setDefaultFilter( false );
		filterablePsmPerPeptideAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_E_VALUE );
		filterablePsmPerPeptideAnnotationType.setDescription( "e-value: The expect value, or the number of hits with this score or better one can expect to see by chance." );
		filterablePsmPerPeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
		return filterablePsmPerPeptideAnnotationType;
	}
	
	//  (Kojak Score)
	
	public FilterablePsmPerPeptideAnnotationType getPsmPerPeptideScoreAnnTypeObject() {

		FilterablePsmPerPeptideAnnotationType filterablePsmPerPeptideAnnotationType = new FilterablePsmPerPeptideAnnotationType();
//		filterablePsmPerPeptideAnnotationType.setDefaultFilter( false );
		filterablePsmPerPeptideAnnotationType.setName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_SCORE );
		filterablePsmPerPeptideAnnotationType.setDescription( "For cross-links, the score of one the peptide." );
		filterablePsmPerPeptideAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
		return filterablePsmPerPeptideAnnotationType;
	}
	
}
