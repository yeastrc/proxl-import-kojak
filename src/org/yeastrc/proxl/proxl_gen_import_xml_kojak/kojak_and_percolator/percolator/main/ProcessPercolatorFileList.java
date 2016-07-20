package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptides;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsmIds;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.command_line_options_container.CommandLineOptionsContainer;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakConfFileReaderResult;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakProteinNonDecoy;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PercolatorFileAndUnmarshalledObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.psm_processing.PsmMatchingAndCollection;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.psm_processing.PsmMatchingPSMDataHolder;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;



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
	 * @param kojakConfFileReaderResult
	 * @param psmMatchingAndCollection
	 * @param percolatorVersion
	 * @param skipPopulatingMatchedProteins
	 * @return
	 * @throws Exception
	 */
	public Set<String> processPercolatorFileList( 

			List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList,

			ProxlInput proxlInputRoot,
			KojakConfFileReaderResult kojakConfFileReaderResult,
			PsmMatchingAndCollection psmMatchingAndCollection,
			String percolatorVersion,
			boolean skipPopulatingMatchedProteins ) throws Exception {


		PopulateSearchProgramEntriesForPercolator.getInstance().populateSearchProgramEntriesForPercolator( proxlInputRoot, percolatorVersion );

		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );

		List<ReportedPeptide> proxlInputReportedPeptideList = reportedPeptides.getReportedPeptide();

		//  Set of reported peptide strings to ensure no duplicates across the percolator files
		Set<String> reportedPeptideStrings = new HashSet<>();
		
		//  Set of protein name strings from Kojak data, null if not returning any
		Set<String> proteinNameStrings = null;
		
		if ( ! skipPopulatingMatchedProteins ) {
			proteinNameStrings = new HashSet<>();
		}

		for ( PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject : percolatorFileAndUnmarshalledObjectList ) {

			IPercolatorOutput percolatorOutput = percolatorFileAndUnmarshalledObject.getPercOutputRootObject();

			processSinglePercolatorFileData( 
					percolatorOutput, 
					kojakConfFileReaderResult,
					proxlInputReportedPeptideList, 
					psmMatchingAndCollection, 
					reportedPeptideStrings,
					proteinNameStrings );
		}

		return proteinNameStrings;
	}

	

	/**
	 * @param percolatorOutput
	 * @param kojakConfFileReaderResult
	 * @param proxlInputReportedPeptideList
	 * @param psmMatchingAndCollection
	 * @param reportedPeptideStrings
	 * @param proteinNameStrings
	 * @throws ProxlGenXMLDataException
	 */
	private void processSinglePercolatorFileData( 

			IPercolatorOutput percolatorOutput, 
			KojakConfFileReaderResult kojakConfFileReaderResult,
			List<ReportedPeptide> proxlInputReportedPeptideList,
			PsmMatchingAndCollection psmMatchingAndCollection,
			Set<String> reportedPeptideStrings,
			Set<String> proteinNameStrings ) throws ProxlGenXMLDataException {

		IPeptides percolatorPeptides = percolatorOutput.getPeptides();

		List<? extends IPeptide> percolatorPeptideList = percolatorPeptides.getPeptide();

		for ( IPeptide percolatorPeptide : percolatorPeptideList ) {

			String percolatorPeptideString = percolatorPeptide.getPeptideId();

			if ( ! reportedPeptideStrings.add(percolatorPeptideString) ) {

				String msg = "Duplicate Reported Peptide String: " + percolatorPeptideString;
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}

			ReportedPeptide reportedPeptide = 
					getProxlInputReportedPeptide( percolatorPeptide, kojakConfFileReaderResult, psmMatchingAndCollection, proteinNameStrings );

			proxlInputReportedPeptideList.add( reportedPeptide );
		}

	}


	/**
	 * @param percolatorPeptide
	 * @param psmMatchingAndCollection
	 * @param proteinNameStrings
	 * @return
	 * @throws ProxlGenXMLDataException
	 */
	private ReportedPeptide getProxlInputReportedPeptide( 
			IPeptide percolatorPeptide, 
			KojakConfFileReaderResult kojakConfFileReaderResult,
			PsmMatchingAndCollection psmMatchingAndCollection,
			Set<String> proteinNameStrings ) throws ProxlGenXMLDataException {


		//  Get proxlInputReportedPeptide Populated With:
		//
		//    - Reported Peptide String
		//    - Link Type
		//    - Parsed Peptides and their Dynamic Modifications

		ReportedPeptide proxlInputReportedPeptide = 
				ParsePercolatorReportedPeptideIntoProxlInputReportedPeptide.getInstance()
				.parsePercolatorReportedPeptideIntoProxlInputReportedPeptide( percolatorPeptide );


		//  Add Reported Peptide Level Annotations to Proxl Input Reported Peptide

		PopulateProxlInputReportedPeptideFromPercolator.getInstance().populateProxlInputReportedPeptide( proxlInputReportedPeptide, percolatorPeptide );

		//  Add PSMs and their Annotations to Proxl Input Reported Peptide

		populatePsmsOnProxlInputReportedPeptide( 
				kojakConfFileReaderResult, proxlInputReportedPeptide, percolatorPeptide, proteinNameStrings, psmMatchingAndCollection );

		return proxlInputReportedPeptide;
	}



	/**
	 * @param reportedPeptide
	 * @param percolatorPeptide
	 * @param proteinNameStrings
	 * @param psmMatchingAndCollection
	 * @throws ProxlGenXMLDataException
	 */
	private void populatePsmsOnProxlInputReportedPeptide( 

			KojakConfFileReaderResult kojakConfFileReaderResult,
			
			ReportedPeptide reportedPeptide, 
			IPeptide percolatorPeptide, 
			Set<String> proteinNameStrings,
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

			IPsm percolatorPsmData = psmMatchingPSMDataHolder.getPercolatorPsmData();
			
			KojakPsmDataObject kojakPsmDataObject = psmMatchingPSMDataHolder.getKojakPsmDataObject();
			
			

			if ( kojakPsmDataObject == null ) {
				
				if ( psmMatchingPSMDataHolder.isKojakMatchesMultiplePercolatorPsms() ) {
					
					//  No matching Kojak record since it matched some other Percolator record 
					
					if ( CommandLineOptionsContainer.isForceDropKojakDuplicateRecordsOptOnCommandLine() ) {

						String msg = "psmMatchingPSMDataHolder.getKojakPsmDataObject() == null and psmMatchingPSMDataHolder.isKojakMatchesMultiplePercolatorPsms() is true and CommandLineOptionsContainer.isForceDropKojakDuplicateRecordsOptOnCommandLine() is true So Skipping to next record.  "
								+ "  percolatorPsmId: '" + percolatorPsmId + "', " 
								+ "percolatorPeptide.getPeptideId(): '" + percolatorPeptide.getPeptideId() + "'.";

						log.warn( msg );

						continue;
						
					} else {

						String msg = "psmMatchingPSMDataHolder.getKojakPsmDataObject() == null.  "
								+ "psmMatchingPSMDataHolder.isKojakMatchesMultiplePercolatorPsms() is true and CommandLineOptionsContainer.isForceDropKojakDuplicateRecordsOptOnCommandLine() is false so YES thowing Exception.  "
								+ "  percolatorPsmId: '" + percolatorPsmId + "', " 
								+ "percolatorPeptide.getPeptideId(): '" + percolatorPeptide.getPeptideId() + "'.";

						log.error( msg );

						throw new ProxlGenXMLDataException( msg );
					}

				} else {

					String msg = "psmMatchingPSMDataHolder.getKojakPsmDataObject() == null."
							+ "  percolatorPsmId: '" + percolatorPsmId + "', " 
							+ "percolatorPeptide.getPeptideId(): '" + percolatorPeptide.getPeptideId() + "'.";

					log.error( msg );

					throw new ProxlGenXMLDataException( msg );
				}
			}
			

			if ( percolatorPsmData == null ) {
				
				//   This should never happen

				String msg = "psmMatchingPSMDataHolder.getPercolatorPsmData() == null So Skipping to next record."
						+ "  percolatorPsmId: '" + percolatorPsmId + "', " 
						+ "percolatorPeptide.getPeptideId(): '" + percolatorPeptide.getPeptideId() + "'.";
				
				log.error( msg );
				
				throw new ProxlGenXMLDataException( msg );
			}

			
			Psm proxlInputPsm = PopulateProxlInputPsmFromKojakAndPercolator.getInstance().populateProxlInputPsm( kojakPsmDataObject, percolatorPsmData );
					

			proxlImportPsmList.add( proxlInputPsm );
			
			
			if ( proteinNameStrings != null ) {

				//   Get non-decoy protein name strings and add to proteinNameSet

				KojakProteinNonDecoy kojakProteinNonDecoy = KojakProteinNonDecoy.getInstance();

				kojakProteinNonDecoy.setDecoyIdentificationStrings( kojakConfFileReaderResult.getDecoyIdentificationStringFromConfFileList() );

				if ( StringUtils.isNotEmpty( kojakPsmDataObject.getProtein_1() ) ) {

					Set<String> proteinNameSet = 
							kojakProteinNonDecoy.getProteinNameList_nonDecoy( kojakPsmDataObject.getProtein_1() );

					proteinNameStrings.addAll( proteinNameSet );
				}

				if ( StringUtils.isNotEmpty( kojakPsmDataObject.getProtein_2() ) ) {

					Set<String> proteinNameSet = 
							kojakProteinNonDecoy.getProteinNameList_nonDecoy( kojakPsmDataObject.getProtein_2() );

					proteinNameStrings.addAll( proteinNameSet );
				}
			}
		}
	}

}
