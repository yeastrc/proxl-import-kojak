package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SwapPerPeptideScoresBetweenPeptides;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;

/**
 * 
 *
 */
public class PopulateProxlInputPsmFromKojakAndPercolator {

	private static final Logger log = Logger.getLogger( PopulateProxlInputPsmFromKojakAndPercolator.class );

	//  private constructor
	private PopulateProxlInputPsmFromKojakAndPercolator() {}
	public static PopulateProxlInputPsmFromKojakAndPercolator getInstance() {
		return new PopulateProxlInputPsmFromKojakAndPercolator();
	}

	/**
	 * Populate a single PSM in the output object structure for Proxl XML file
	 * 
	 * @param kojakPsmDataObject
	 * @param percolatorPsmData
	 * @param numPeptidesOnReportedPeptide
	 * @param swapPerPeptideScoresBetweenPeptides
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public Psm populateProxlInputPsm( 
			KojakPsmDataObject kojakPsmDataObject, 
			IPsm percolatorPsmData,
			int numPeptidesOnReportedPeptide,
			SwapPerPeptideScoresBetweenPeptides swapPerPeptideScoresBetweenPeptides ) throws ProxlGenXMLDataException {

		//  Create Psm with Kojak data
		Psm proxlInputPsm = 
				Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations.getInstance()
				.createProxlInputPsm(kojakPsmDataObject, numPeptidesOnReportedPeptide, swapPerPeptideScoresBetweenPeptides );

		///////////////////////////////////////

		/////////////    Filterable Annotations

		{

			//  Percolator annotations:

			FilterablePsmAnnotations filterablePsmAnnotations = proxlInputPsm.getFilterablePsmAnnotations();
			if ( filterablePsmAnnotations == null ) {
				filterablePsmAnnotations = new FilterablePsmAnnotations();
				proxlInputPsm.setFilterablePsmAnnotations( filterablePsmAnnotations );
			}
			List<FilterablePsmAnnotation> filterablePsmAnnotationList =
					filterablePsmAnnotations.getFilterablePsmAnnotation();

			//		FilterablePsmAnnotation filterablePsmAnnotation = null;


			//////////////

			//  q-value

			{
				FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
				filterablePsmAnnotationList.add( filterablePsmAnnotation );

				filterablePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				String annotationValueString = percolatorPsmData.getQValue();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterablePsmAnnotation.setValue( annotationValueDecimal );

					} catch ( Exception e ) {

						String msg = "Failed to parse Percolator Peptide value '" + PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE 
								+ "' as decimal value.  Value as String: " + annotationValueString;
						log.error( msg );
						throw new ProxlGenXMLDataException(msg);
					}
				}
			}

			//////////////

			//  p-value

			{
				FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
				filterablePsmAnnotationList.add( filterablePsmAnnotation );

				filterablePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );


				String annotationValueString = percolatorPsmData.getPValue();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterablePsmAnnotation.setValue( annotationValueDecimal );

					} catch ( Exception e ) {

						String msg = "Failed to parse Percolator Peptide value '" + PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE 
								+ "' as decimal value.  Value as String: " + annotationValueString;
						log.error( msg );
						throw new ProxlGenXMLDataException(msg);
					}
				}
			}
			//////////////

			//  SVM Score

			{
				FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
				filterablePsmAnnotationList.add( filterablePsmAnnotation );

				filterablePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				double annotationValueDouble = percolatorPsmData.getSvmScore();
				
//				String annotationValueString = Double.toString( annotationValueDouble );

				try {

					BigDecimal annotationValueDecimal = BigDecimal.valueOf( annotationValueDouble );

					filterablePsmAnnotation.setValue( annotationValueDecimal );

				} catch ( Exception e ) {

					String msg = "Failed to parse Percolator Peptide value '" + PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE 
							+ "' as decimal value.  Value as Double: " + annotationValueDouble;
					log.error( msg );
					throw new ProxlGenXMLDataException(msg);
				}
			
			}

			//////////////

			//  PEP

			{
				FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
				filterablePsmAnnotationList.add( filterablePsmAnnotation );

				filterablePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				String annotationValueString = percolatorPsmData.getPep();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterablePsmAnnotation.setValue( annotationValueDecimal );

					} catch ( Exception e ) {

						String msg = "Failed to parse Percolator Peptide value '" + PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP 
								+ "' as decimal value.  Value as String: " + annotationValueString;
						log.error( msg );
						throw new ProxlGenXMLDataException(msg);
					}
				}
			}

		}


		///////////////////////////////////////

		/////////////    Descriptive Annotations

		{
//			DescriptivePsmAnnotations descriptivePsmAnnotations = proxlInputPsm.getDescriptivePsmAnnotations();
//			if ( descriptivePsmAnnotations == null ) {
//				descriptivePsmAnnotations = new DescriptivePsmAnnotations();
//				proxlInputPsm.setDescriptivePsmAnnotations( descriptivePsmAnnotations );
//			}
//			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
//					descriptivePsmAnnotations.getDescriptivePsmAnnotation();


			//////////////

			//  Calc Mass

			//  Removed
			
//			{
//				DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
//				descriptivePsmAnnotationList.add( descriptivePsmAnnotation );
//
//				descriptivePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
//				descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
//
//				if ( percolatorPsmData.getCalcMass() != null ) {
//					descriptivePsmAnnotation.setValue( Double.toString( percolatorPsmData.getCalcMass() ) );
//				}
//			}

			//////////////

			//  Remove since scan number added to regular psm processing in core proxl
			
//			//  Scan Number
//
//			{
//				DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
//				descriptivePsmAnnotationList.add( descriptivePsmAnnotation );
//
//				descriptivePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
//				descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
//
//				if ( percolatorPsmData.getCalcMass() != null ) {
//					descriptivePsmAnnotation.setValue( Integer.toString( kojakPsmDataObject.getScanNumber() ) );
//				}
//			}
		}

		//////////////////////////////

		return proxlInputPsm;
	}

}
