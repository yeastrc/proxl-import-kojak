package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.main;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;

/**
 * 
 *
 */
public class PopulateProxlInputReportedPeptide {

	private static final Logger log = Logger.getLogger( PopulateProxlInputReportedPeptide.class );

	//  private constructor
	private PopulateProxlInputReportedPeptide() {}


	public static PopulateProxlInputReportedPeptide getInstance() {

		return new PopulateProxlInputReportedPeptide();
	}




	/**
	 * @param proxlInputReportedPeptide
	 * @param percolatorPeptide
	 * @throws ProxlGenXMLDataException
	 */
	public void populateProxlInputReportedPeptide( ReportedPeptide proxlInputReportedPeptide, IPeptide percolatorPeptide ) throws ProxlGenXMLDataException {

		//  Add to proxlInputReportedPeptide:  
		//
		//  - Annotations, filterable and Descriptive

		ReportedPeptideAnnotations reportedPeptideAnnotations = new ReportedPeptideAnnotations();
		proxlInputReportedPeptide.setReportedPeptideAnnotations( reportedPeptideAnnotations );

		///////////////////////////////////////

		/////////////    Filterable Annotations

		{

			FilterableReportedPeptideAnnotations filterableReportedPeptideAnnotations = new FilterableReportedPeptideAnnotations();
			reportedPeptideAnnotations.setFilterableReportedPeptideAnnotations( filterableReportedPeptideAnnotations );

			List<FilterableReportedPeptideAnnotation> filterableReportedPeptideAnnotationList =
					filterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation();


			//////////////

			//  q-value

			{
				FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				filterableReportedPeptideAnnotationList.add( filterableReportedPeptideAnnotation );

				filterableReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterableReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				String annotationValueString = percolatorPeptide.getQValue();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterableReportedPeptideAnnotation.setValue( annotationValueDecimal );

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
				FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				filterableReportedPeptideAnnotationList.add( filterableReportedPeptideAnnotation );

				filterableReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterableReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );


				String annotationValueString = percolatorPeptide.getPValue();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterableReportedPeptideAnnotation.setValue( annotationValueDecimal );

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
				FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				filterableReportedPeptideAnnotationList.add( filterableReportedPeptideAnnotation );

				filterableReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterableReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				double annotationValueDouble = percolatorPeptide.getSvmScore();
				
//				String annotationValueString = Double.toString( annotationValueDouble );

				try {

					BigDecimal annotationValueDecimal = BigDecimal.valueOf( annotationValueDouble );

					filterableReportedPeptideAnnotation.setValue( annotationValueDecimal );

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
				FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				filterableReportedPeptideAnnotationList.add( filterableReportedPeptideAnnotation );

				filterableReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterableReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				String annotationValueString = percolatorPeptide.getPep();

				if ( StringUtils.isNotEmpty( annotationValueString ) ) {
					try {

						BigDecimal annotationValueDecimal = new BigDecimal( annotationValueString );

						filterableReportedPeptideAnnotation.setValue( annotationValueDecimal );

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

//		{
//			DescriptiveReportedPeptideAnnotations descriptiveReportedPeptideAnnotations = new DescriptiveReportedPeptideAnnotations();
//			reportedPeptideAnnotations.setDescriptiveReportedPeptideAnnotations( descriptiveReportedPeptideAnnotations );
//
//			List<DescriptiveReportedPeptideAnnotation> descriptiveReportedPeptideAnnotationList =
//					descriptiveReportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotation();
//
//
		
			//  Removed
		
		
//			//////////////
//
//			//  Calc Mass
//
//			DescriptiveReportedPeptideAnnotation descriptiveReportedPeptideAnnotation = new DescriptiveReportedPeptideAnnotation();
//			descriptiveReportedPeptideAnnotationList.add( descriptiveReportedPeptideAnnotation );
//
//			descriptiveReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
//			descriptiveReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
//
//			if ( percolatorPeptide.getCalcMass() != null ) {
//				descriptiveReportedPeptideAnnotation.setValue( Double.toString( percolatorPeptide.getCalcMass() ) );
//			}
//		}
	}



}
