package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.Proxl_XML_Peptide_UniqueId_Constants;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SwapPerPeptideScoresBetweenPeptides;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;

public class Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations {

//	private static final Logger log = Logger.getLogger( PopulateProxlInputPsmFromKojakOnly.class );

	//  private constructor
	private Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations() {}
	public static Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations getInstance() {
		return new Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations();
	}

	/**
	 * Populate a single PSM in the output object structure for Proxl XML file
	 * @param swapPerPeptideScoresBetweenPeptides - Swap the order of the per peptide scores since the order of the peptides were swapped
	 * @param percolatorPeptide
	 * 
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public Psm createProxlInputPsm( KojakPsmDataObject kojakPsmDataObject, int numPeptidesOnReportedPeptide, SwapPerPeptideScoresBetweenPeptides swapPerPeptideScoresBetweenPeptides ) throws ProxlGenXMLDataException {

		Psm proxlInputPsm = new Psm();

		proxlInputPsm.setPrecursorCharge( BigInteger.valueOf( kojakPsmDataObject.getCharge() ) );

		proxlInputPsm.setLinkerMass( kojakPsmDataObject.getLinkerMass() );

		//  Leave null since only have one scan file.  Whatever scan file is imported will be used.
		//		proxlInputPsm.setScanFileName(  );

		proxlInputPsm.setScanNumber( BigInteger.valueOf( kojakPsmDataObject.getScanNumber() ) );


		///////////////////////////////////////

		/////////////    Filterable Annotations

		{
			FilterablePsmAnnotations filterablePsmAnnotations = new FilterablePsmAnnotations();
			proxlInputPsm.setFilterablePsmAnnotations( filterablePsmAnnotations );

			List<FilterablePsmAnnotation> filterablePsmAnnotationList =
					filterablePsmAnnotations.getFilterablePsmAnnotation();


			////////////   Kojak annotations

			{
				Map<String, BigDecimal> kojakFilteredAnnotations = kojakPsmDataObject.getFilteredAnnotations();

				for ( Map.Entry<String, BigDecimal> entry : kojakFilteredAnnotations.entrySet() ) {

					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );

					filterablePsmAnnotation.setAnnotationName( entry.getKey() );

					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );

					filterablePsmAnnotation.setValue( entry.getValue() );
				}
			}
			
			//   Compute and save High and Low Per Peptide E-value and save at PSM level

			if ( kojakPsmDataObject.getPeptide_1_e_value() != null ) {
				
				//  At PSM level, add Per Peptide High and Low Score entries.
				BigDecimal perPeptide_High_E_Value = null;
				BigDecimal perPeptide_Low_E_Value = null;
				
				if ( numPeptidesOnReportedPeptide == 1 || kojakPsmDataObject.getPeptide_2_e_value() == null ) {
					perPeptide_High_E_Value = kojakPsmDataObject.getPeptide_1_e_value(); 
					perPeptide_Low_E_Value = kojakPsmDataObject.getPeptide_1_e_value();
				} else if ( kojakPsmDataObject.getPeptide_1_e_value().equals( kojakPsmDataObject.getPeptide_2_e_value() ) ) {
					perPeptide_High_E_Value = kojakPsmDataObject.getPeptide_1_e_value(); 
					perPeptide_Low_E_Value = kojakPsmDataObject.getPeptide_1_e_value();
				} else if ( kojakPsmDataObject.getPeptide_1_e_value().compareTo( kojakPsmDataObject.getPeptide_2_e_value() ) > 0 ) {
					perPeptide_High_E_Value = kojakPsmDataObject.getPeptide_1_e_value(); 
					perPeptide_Low_E_Value = kojakPsmDataObject.getPeptide_2_e_value();
				} else {
					perPeptide_High_E_Value = kojakPsmDataObject.getPeptide_2_e_value(); 
					perPeptide_Low_E_Value = kojakPsmDataObject.getPeptide_1_e_value();
				}

				{
					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );
					filterablePsmAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_WORST_E_VALUE );
					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
					filterablePsmAnnotation.setValue( perPeptide_High_E_Value );
				}
				{
					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );
					filterablePsmAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_BEST_E_VALUE );
					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
					filterablePsmAnnotation.setValue( perPeptide_Low_E_Value );
				}
			}
			
			//   Compute and save High and Low Per Peptide Score and save at PSM level

			if ( kojakPsmDataObject.getPeptide_1_score() != null ) {
				
				//  At PSM level, add Per Peptide High and Low Score entries.
				BigDecimal perPeptide_HighScore = null;
				BigDecimal perPeptide_LowScore = null;
				
				if ( numPeptidesOnReportedPeptide == 1 || kojakPsmDataObject.getPeptide_2_score() == null ) {
					perPeptide_HighScore = kojakPsmDataObject.getPeptide_1_score(); 
					perPeptide_LowScore = kojakPsmDataObject.getPeptide_1_score();
				} else if ( kojakPsmDataObject.getPeptide_1_score().equals( kojakPsmDataObject.getPeptide_2_score() ) ) {
					perPeptide_HighScore = kojakPsmDataObject.getPeptide_1_score(); 
					perPeptide_LowScore = kojakPsmDataObject.getPeptide_1_score();
				} else if ( kojakPsmDataObject.getPeptide_1_score().compareTo( kojakPsmDataObject.getPeptide_2_score() ) > 0 ) {
					perPeptide_HighScore = kojakPsmDataObject.getPeptide_1_score(); 
					perPeptide_LowScore = kojakPsmDataObject.getPeptide_2_score();
				} else {
					perPeptide_HighScore = kojakPsmDataObject.getPeptide_2_score(); 
					perPeptide_LowScore = kojakPsmDataObject.getPeptide_1_score();
				}

				{
					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );
					filterablePsmAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_BEST_SCORE );
					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
					filterablePsmAnnotation.setValue( perPeptide_HighScore );
				}
				{
					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );
					filterablePsmAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PER_PEPTIDE_WORST_SCORE );
					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
					filterablePsmAnnotation.setValue( perPeptide_LowScore );
				}
			}
		}


		///////////////////////////////////////

		/////////////    Descriptive Annotations

		{
			DescriptivePsmAnnotations descriptivePsmAnnotations = new DescriptivePsmAnnotations();
			proxlInputPsm.setDescriptivePsmAnnotations( descriptivePsmAnnotations );

			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
					descriptivePsmAnnotations.getDescriptivePsmAnnotation();


			////////////   Kojak annotations

			{
				Map<String, String> kojakDescriptiveAnnotations = kojakPsmDataObject.getDescriptiveAnnotations();

				for ( Map.Entry<String, String> entry : kojakDescriptiveAnnotations.entrySet() ) {

					DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
					descriptivePsmAnnotationList.add( descriptivePsmAnnotation );

					descriptivePsmAnnotation.setAnnotationName( entry.getKey() );

					descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );

					descriptivePsmAnnotation.setValue( entry.getValue() );
				}
			}
		}
		
		//  PSM Per Peptide Annotations
		
		if ( kojakPsmDataObject.getPeptide_1_e_value() != null || kojakPsmDataObject.getPeptide_1_score() != null ) {
			
			String peptide_uniqueId_1 = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1;
			String peptide_uniqueId_2 = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2;
			
			if ( swapPerPeptideScoresBetweenPeptides == SwapPerPeptideScoresBetweenPeptides.YES ) {

				peptide_uniqueId_1 = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2;
				peptide_uniqueId_2 = Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1;
			}

			List<PerPeptideAnnotations> perPeptideAnnotationsList = proxlInputPsm.getPerPeptideAnnotations();

			addPsmPerPeptide_Score_EntryForPeptide( 
					kojakPsmDataObject.getPeptide_1_e_value(),
					kojakPsmDataObject.getPeptide_1_score(), 
					peptide_uniqueId_1, 
					perPeptideAnnotationsList );
			
			if ( numPeptidesOnReportedPeptide == 2 
					&& ( kojakPsmDataObject.getPeptide_2_e_value() != null || kojakPsmDataObject.getPeptide_2_score() != null ) ) {

				addPsmPerPeptide_Score_EntryForPeptide( 
						 kojakPsmDataObject.getPeptide_2_e_value(),
						kojakPsmDataObject.getPeptide_2_score(), 
						peptide_uniqueId_2, 
						perPeptideAnnotationsList );
			}
		}
		
		//////////////////////////////

		return proxlInputPsm;
	}

	/**
	 * Add PSM Per Peptide Kojak Score 
	 * 
	 * @param score
	 * @param uniqueId
	 * @param perPeptideAnnotationsList
	 */
	public void addPsmPerPeptide_Score_EntryForPeptide( BigDecimal e_value, BigDecimal score, String uniqueId, List<PerPeptideAnnotations> perPeptideAnnotationsList ) {
		{
			PerPeptideAnnotations perPeptideAnnotations = new PerPeptideAnnotations();
			perPeptideAnnotationsList.add( perPeptideAnnotations );
			
			PsmPeptide psmPeptide = new PsmPeptide();
			perPeptideAnnotations.setPsmPeptide( psmPeptide );
			psmPeptide.setUniqueId( uniqueId );
			
			FilterablePsmPerPeptideAnnotations filterablePsmPerPeptideAnnotations = new FilterablePsmPerPeptideAnnotations();
			psmPeptide.setFilterablePsmPerPeptideAnnotations( filterablePsmPerPeptideAnnotations );
			List<FilterablePsmPerPeptideAnnotation> filterablePsmPerPeptideAnnotationList = 
					filterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation();
			
			if ( e_value != null ) {
				FilterablePsmPerPeptideAnnotation filterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
				filterablePsmPerPeptideAnnotationList.add( filterablePsmPerPeptideAnnotation );
				filterablePsmPerPeptideAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_E_VALUE );
				filterablePsmPerPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
				filterablePsmPerPeptideAnnotation.setValue( e_value );
			}

			if ( score != null ) {
				FilterablePsmPerPeptideAnnotation filterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
				filterablePsmPerPeptideAnnotationList.add( filterablePsmPerPeptideAnnotation );
				filterablePsmPerPeptideAnnotation.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_PSM_PER_PEPTIDE_SCORE );
				filterablePsmPerPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
				filterablePsmPerPeptideAnnotation.setValue( score );
			}
		}
	}

}
