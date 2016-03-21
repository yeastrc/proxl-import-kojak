package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptides;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsmIds;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
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
	 * @param psmMatchingAndCollection
	 * @throws Exception
	 */
	public void processPercolatorFileList( 

			List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList,

			ProxlInput proxlInputRoot,
			PsmMatchingAndCollection psmMatchingAndCollection,
			String percolatorVersion ) throws Exception {


		PopulateSearchProgramEntriesForPercolator.getInstance().populateSearchProgramEntriesForPercolator( proxlInputRoot, percolatorVersion );

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

		PopulateProxlInputReportedPeptideFromPercolator.getInstance().populateProxlInputReportedPeptide( proxlInputReportedPeptide, percolatorPeptide );

		//  Add PSMs and their Annotations to Proxl Input Reported Peptide

		populatePsmsOnProxlInputReportedPeptide( proxlInputReportedPeptide, percolatorPeptide, psmMatchingAndCollection );

		return proxlInputReportedPeptide;
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

			IPsm percolatorPsmData = psmMatchingPSMDataHolder.getPercolatorPsmData();
			
			KojakPsmDataObject kojakPsmDataObject = psmMatchingPSMDataHolder.getKojakPsmDataObject();
			
			Psm proxlInputPsm = PopulateProxlInputPsmFromKojakAndPercolator.getInstance().populateProxlInputPsm( kojakPsmDataObject, percolatorPsmData );
					

			proxlImportPsmList.add( proxlInputPsm );
		}
	}




}
