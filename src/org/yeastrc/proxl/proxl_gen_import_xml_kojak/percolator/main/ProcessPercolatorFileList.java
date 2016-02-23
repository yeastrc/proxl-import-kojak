package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptides;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsmIds;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.DefaultFilterValueConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.objects.PercolatorFileAndUnmarshalledObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.psm_processing.PsmMatchingAndCollection;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.psm_processing.PsmMatchingPSMDataHolder;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.ReportedPeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;



/**
 * 
 *
 */
public class ProcessPercolatorFileList {

	private static final Logger log = Logger.getLogger( ProcessPercolatorFileList.class );

	//  private constructor
	private ProcessPercolatorFileList() {}


	public static ProcessPercolatorFileList getInstance() {

		return new ProcessPercolatorFileList();
	}


	/**
	 * @param percolatorFileAndUnmarshalledObjectList
	 * @param proxlInputRoot
	 * @param psmMatchingAndCollection
	 * @throws Exception
	 */
	public void processPercolatorFileList( 

			List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList,

			ProxlInput proxlInputRoot,
			PsmMatchingAndCollection psmMatchingAndCollection,
			String percolatorVersion ) throws Exception {


		setSearchProgramValuesFromPercolatorFile( proxlInputRoot, percolatorVersion );

		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );

		List<ReportedPeptide> proxlInputReportedPeptideList = reportedPeptides.getReportedPeptide();

		//  Set of reported peptide strings to ensure no duplicates across the percolator files
		Set<String> reportedPeptideStrings = new HashSet<>();

		for ( PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject : percolatorFileAndUnmarshalledObjectList ) {

			IPercolatorOutput percolatorOutput = percolatorFileAndUnmarshalledObject.getPercOutputRootObject();

			processSinglePercolatorFileData( percolatorOutput, proxlInputReportedPeptideList, psmMatchingAndCollection, reportedPeptideStrings );
		}

	}

	/**
	 * @param percolatorOutput
	 * @param proxlInputReportedPeptideList
	 * @param psmMatchingAndCollection
	 * @param reportedPeptideStrings
	 * @throws ProxlGenXMLDataException 
	 */
	private void processSinglePercolatorFileData( 

			IPercolatorOutput percolatorOutput, 
			List<ReportedPeptide> proxlInputReportedPeptideList,
			PsmMatchingAndCollection psmMatchingAndCollection,
			Set<String> reportedPeptideStrings ) throws ProxlGenXMLDataException {

		IPeptides percolatorPeptides = percolatorOutput.getPeptides();

		List<? extends IPeptide> percolatorPeptideList = percolatorPeptides.getPeptide();

		for ( IPeptide percolatorPeptide : percolatorPeptideList ) {

			String percolatorPeptideString = percolatorPeptide.getPeptideId();

			if ( ! reportedPeptideStrings.add(percolatorPeptideString) ) {

				String msg = "Duplicate Reported Peptide String: " + percolatorPeptideString;
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}

			ReportedPeptide reportedPeptide = getProxlInputReportedPeptide( percolatorPeptide, psmMatchingAndCollection );

			proxlInputReportedPeptideList.add( reportedPeptide );
		}

	}


	/**
	 * @param percolatorPeptide
	 * @param psmMatchingAndCollection
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	private ReportedPeptide getProxlInputReportedPeptide( IPeptide percolatorPeptide, PsmMatchingAndCollection psmMatchingAndCollection ) throws ProxlGenXMLDataException {


		//  Get proxlInputReportedPeptide Populated With:
		//
		//    - Reported Peptide String
		//    - Link Type
		//    - Parsed Peptides and their Dynamic Modifications

		ReportedPeptide proxlInputReportedPeptide = 
				ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide.getInstance()
				.parsePercolatorReportedPeptideIntoProxlInputReportedPeptide( percolatorPeptide );


		//  Add Reported Peptide Level Annotations to Proxl Input Reported Peptide

		populateProxlInputReportedPeptide( proxlInputReportedPeptide, percolatorPeptide );

		//  Add PSMs and their Annotations to Proxl Input Reported Peptide

		populatePsmsOnProxlInputReportedPeptide( proxlInputReportedPeptide, percolatorPeptide, psmMatchingAndCollection );

		return proxlInputReportedPeptide;
	}


	/**
	 * @param proxlInputReportedPeptide
	 * @param percolatorPeptide
	 * @throws ProxlGenXMLDataException
	 */
	private void populateProxlInputReportedPeptide( ReportedPeptide proxlInputReportedPeptide, IPeptide percolatorPeptide ) throws ProxlGenXMLDataException {

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

		{
			DescriptiveReportedPeptideAnnotations descriptiveReportedPeptideAnnotations = new DescriptiveReportedPeptideAnnotations();
			reportedPeptideAnnotations.setDescriptiveReportedPeptideAnnotations( descriptiveReportedPeptideAnnotations );

			List<DescriptiveReportedPeptideAnnotation> descriptiveReportedPeptideAnnotationList =
					descriptiveReportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotation();


			//////////////

			//  Calc Mass

			DescriptiveReportedPeptideAnnotation descriptiveReportedPeptideAnnotation = new DescriptiveReportedPeptideAnnotation();
			descriptiveReportedPeptideAnnotationList.add( descriptiveReportedPeptideAnnotation );

			descriptiveReportedPeptideAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
			descriptiveReportedPeptideAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

			if ( percolatorPeptide.getCalcMass() != null ) {
				descriptiveReportedPeptideAnnotation.setValue( Double.toString( percolatorPeptide.getCalcMass() ) );
			}
		}
	}





	/**
	 * @param percolatorPeptide
	 * @param psmMatchingAndCollection
	 * @throws ProxlGenXMLDataException 
	 */
	private void populatePsmsOnProxlInputReportedPeptide( 

			ReportedPeptide reportedPeptide, 
			IPeptide percolatorPeptide, 
			PsmMatchingAndCollection psmMatchingAndCollection ) throws ProxlGenXMLDataException {

		Psms proxlImportPsms = new Psms();
		reportedPeptide.setPsms( proxlImportPsms );

		List<Psm> proxlImportPsmList = proxlImportPsms.getPsm();


		IPsmIds percolatorPsmIds = percolatorPeptide.getPsmIds();
		List<String> percolatorPsmIdList = percolatorPsmIds.getPsmId();

		for ( String percolatorPsmId : percolatorPsmIdList ) {

			PsmMatchingPSMDataHolder psmMatchingPSMDataHolder =
					psmMatchingAndCollection.getPsmMatchingPSMDataHolderForPercolatorPsmIdAndPercolatorPeptideSequence( percolatorPsmId, percolatorPeptide.getPeptideId() );

			if ( psmMatchingPSMDataHolder == null ) {

				String msg = "Failed in populating Psms for Peptide."
						+ " No psmMatchingPSMDataHolder for percolatorPsmId: '" + percolatorPsmId
						+ "', percolatorPeptideSequence: '" + percolatorPeptide.getPeptideId() + "'";
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}

			Psm proxlInputPsm = populateProxlInputPsm( psmMatchingPSMDataHolder );

			proxlImportPsmList.add( proxlInputPsm );
		}
	}



	/**
	 * Populate a single 
	 * 
	 * @param percolatorPeptide
	 * @param psmMatchingAndCollection
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	private Psm populateProxlInputPsm( PsmMatchingPSMDataHolder psmMatchingPSMDataHolder ) throws ProxlGenXMLDataException {

		Psm proxlInputPsm = new Psm();

		KojakPsmDataObject kojakPsmDataObject = psmMatchingPSMDataHolder.getKojakPsmDataObject();
		IPsm percolatorPsmData = psmMatchingPSMDataHolder.getPercolatorPsmData();

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

			{
				DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
				descriptivePsmAnnotationList.add( descriptivePsmAnnotation );

				descriptivePsmAnnotation.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
				descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );

				if ( percolatorPsmData.getCalcMass() != null ) {
					descriptivePsmAnnotation.setValue( Double.toString( percolatorPsmData.getCalcMass() ) );
				}
			}

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


	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Add SearchProgram data for Percolator
	 * 
	 * @param searchProgram
	 * @param kojakFileReader
	 * @throws ProxlGenXMLDataException
	 */
	private void setSearchProgramValuesFromPercolatorFile( ProxlInput proxlInputRoot, String percolatorVersion ) throws ProxlGenXMLDataException {

		SearchProgramInfo searchProgramInfo = proxlInputRoot.getSearchProgramInfo();
		SearchPrograms searchPrograms = searchProgramInfo.getSearchPrograms();

		List<SearchProgram> searchProgramList = searchPrograms.getSearchProgram();

		SearchProgram searchProgram = new SearchProgram();
		searchProgramList.add( searchProgram );


		searchProgram.setName( SearchProgramNameKojakImporterConstants.PERCOLATOR );
		searchProgram.setDisplayName( SearchProgramNameKojakImporterConstants.PERCOLATOR );


		searchProgram.setVersion( percolatorVersion );




		////////////   Reported Peptide data

		ReportedPeptideAnnotationTypes peptideAnnotationTypes = new ReportedPeptideAnnotationTypes();
		searchProgram.setReportedPeptideAnnotationTypes( peptideAnnotationTypes );

		/////////////  Filterable

		{
			FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes = new FilterablePeptideAnnotationTypes();
			peptideAnnotationTypes.setFilterablePeptideAnnotationTypes( filterablePeptideAnnotationTypes );

			List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
					filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();


			//////////////

			//  q-value - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( true );
				
				filterablePeptideAnnotationType.setDefaultFilterValue( DefaultFilterValueConstants.PERCOLATOR_PEPTIDE_Q_VALUE_DEFAULT );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_Q_VALUE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  PEP - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_PEP );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}
			
			//////////////

			//  p-value - Smallest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_P_VALUE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  SVM Score - Biggest first

			{
				FilterablePeptideAnnotationType filterablePeptideAnnotationType = new FilterablePeptideAnnotationType();
				filterablePeptideAnnotationTypeList.add( filterablePeptideAnnotationType );

				filterablePeptideAnnotationType.setDefaultFilter( false );

				filterablePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterablePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_SVM_SCORE );
				filterablePeptideAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
			}


		}


		/////////////  Descriptive

		/* Don't store this.
		{
			DescriptivePeptideAnnotationTypes descriptivePeptideAnnotationTypes = new DescriptivePeptideAnnotationTypes();
			peptideAnnotationTypes.setDescriptivePeptideAnnotationTypes( descriptivePeptideAnnotationTypes );

			List<DescriptivePeptideAnnotationType> descriptivePeptideAnnotationTypeList =
					descriptivePeptideAnnotationTypes.getDescriptivePeptideAnnotationType();

			{
				DescriptivePeptideAnnotationType descriptivePeptideAnnotationType = new DescriptivePeptideAnnotationType();
				descriptivePeptideAnnotationTypeList.add( descriptivePeptideAnnotationType );

				descriptivePeptideAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
				descriptivePeptideAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
			}
			
		}
		*/


		////////////////////////////

		////////////   PSM data

		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

		/////////////  Filterable
		{

			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );

			List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();


			//////////////

			//  q-value - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( true );
				
				filterablePsmAnnotationType.setDefaultFilterValue( DefaultFilterValueConstants.PERCOLATOR_PEPTIDE_Q_VALUE_DEFAULT );


				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_Q_VALUE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  PEP - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_PEP );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_PEP );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}
			
			//////////////

			//  p-value - Smallest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_P_VALUE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_P_VALUE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.BELOW );
			}

			//////////////

			//  SVM Score - Biggest first

			{
				FilterablePsmAnnotationType filterablePsmAnnotationType = new FilterablePsmAnnotationType();
				filterablePsmAnnotationTypeList.add( filterablePsmAnnotationType );

				filterablePsmAnnotationType.setDefaultFilter( false );

				filterablePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SVM_SCORE );

				filterablePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_DESCRIPTION_SVM_SCORE );
				filterablePsmAnnotationType.setFilterDirection( FilterDirectionType.ABOVE );
			}


		}

		/////////////  Descriptive

		{
			DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
			psmAnnotationTypes.setDescriptivePsmAnnotationTypes( descriptivePsmAnnotationTypes );

			List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
					descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();

			/* Let's not store these
			{
				DescriptivePsmAnnotationType descriptivePsmAnnotationType = new DescriptivePsmAnnotationType();
				descriptivePsmAnnotationTypeList.add( descriptivePsmAnnotationType );

				descriptivePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
				descriptivePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_CALC_MASS );
			}
		
			{
				DescriptivePsmAnnotationType descriptivePsmAnnotationType = new DescriptivePsmAnnotationType();
				descriptivePsmAnnotationTypeList.add( descriptivePsmAnnotationType );


				descriptivePsmAnnotationType.setName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
				descriptivePsmAnnotationType.setDescription( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_SCAN_NUMBER );
			}
			*/
		}
	}
}
