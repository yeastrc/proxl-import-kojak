package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;

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
	 * @param percolatorPeptide
	 * @param psmMatchingAndCollection
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public Psm populateProxlInputPsm( KojakPsmDataObject kojakPsmDataObject, IPsm percolatorPsmData ) throws ProxlGenXMLDataException {

		Psm proxlInputPsm = new Psm();

		proxlInputPsm.setPrecursorCharge( BigInteger.valueOf( kojakPsmDataObject.getCharge() ) );

		proxlInputPsm.setLinkerMass( kojakPsmDataObject.getLinkerMass() );

		//  Leave null since only have one scan file.  Whatever scan file is imported will be used.
		//		proxlInputPsm.setScanFileName(  );

		proxlInputPsm.setScanNumber( BigInteger.valueOf( kojakPsmDataObject.getScanNumber() ) );


		///////////////////////////////////////

		/////////////    Filterable Annotations

		{

			//  Percolator annotations:

			FilterablePsmAnnotations filterablePsmAnnotations = new FilterablePsmAnnotations();
			proxlInputPsm.setFilterablePsmAnnotations( filterablePsmAnnotations );

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

		}


		///////////////////////////////////////

		/////////////    Descriptive Annotations

		{
			DescriptivePsmAnnotations descriptivePsmAnnotations = new DescriptivePsmAnnotations();
			proxlInputPsm.setDescriptivePsmAnnotations( descriptivePsmAnnotations );

			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
					descriptivePsmAnnotations.getDescriptivePsmAnnotation();


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

			//  Scan Number

			{
				DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
				descriptivePsmAnnotationList.add( descriptivePsmAnnotation );

				descriptivePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
				descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				if ( percolatorPsmData.getCalcMass() != null ) {
					descriptivePsmAnnotation.setValue( Integer.toString( kojakPsmDataObject.getScanNumber() ) );
				}
			}
			
			
			



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

		//////////////////////////////

		return proxlInputPsm;
	}

}
