package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.psm_processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.command_line_options_container.CommandLineOptionsContainer;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PsmIdSplitObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils.SplitKojakPsmId;

/**
 *	Handles all core PSM matching.
 *
 *  Matches Percolator PSM to Kojak PSM.
 *  Provides retrieval by Percolator psm_id
 *   
 */
public class PsmMatchingAndCollection {

	private static final Logger log = Logger.getLogger(PsmMatchingAndCollection.class);

	//  private constructor
	private PsmMatchingAndCollection() {}
	
	
	/**
	 *   Singleton instance
	 */
	private static PsmMatchingAndCollection instance = new PsmMatchingAndCollection();
	
	public static PsmMatchingAndCollection getInstance() {
		
		return instance;
	}
	
	/**
	 * Map key is Percolator psm_id
	 */
	private Map<String, List<PsmMatchingPSMDataHolder>> psmDataByPercolator_psm_id_Map = new HashMap<>();
	
	/**
	 * Map key is scan number.  The value is a list since there can be more than one PSM per scan number.
	 */
	private Map<Integer, List<PsmMatchingPSMDataHolder>> psmDataByScanNumber_Map = new HashMap<>();

	
	/**
	 * @param percolatorPsmId
	 * @param percolatorPeptideSequence
	 * @return
	 */
	public PsmMatchingPSMDataHolder getPsmMatchingPSMDataHolderForPercolatorPsmIdAndPercolatorPeptideSequence(
			
			String percolatorPsmId,
			String percolatorPeptideSequence
			) {
		
		List<PsmMatchingPSMDataHolder> psmMatchingPSMDataHolderList = psmDataByPercolator_psm_id_Map.get( percolatorPsmId );
		
		if ( psmMatchingPSMDataHolderList == null ) {
			
			log.error("No psmMatchingPSMDataHolderList for percolatorPsmId: " + percolatorPsmId );
			
			return null;
		}
		
		PsmMatchingPSMDataHolder psmMatchingPSMDataHolder = null;
		
		for ( PsmMatchingPSMDataHolder psmMatchingPSMDataHolderInList : psmMatchingPSMDataHolderList ) {
		
			if ( psmMatchingPSMDataHolderInList.getPercolatorPsmData().getPeptideSeq().getSeq().equals( percolatorPeptideSequence ) ) {
				
				psmMatchingPSMDataHolder = psmMatchingPSMDataHolderInList;
				break;
			}
		}

		if ( psmMatchingPSMDataHolder == null ) {
			
			log.error("No psmMatchingPSMDataHolder for percolatorPsmId: '" + percolatorPsmId
					+ "', percolatorPeptideSequence: '" + percolatorPeptideSequence + "'" );
			
			return null;
		}
		
		
		return psmMatchingPSMDataHolder;
	}
	
	
	/**
	 * Add Percolator PSM
	 * 
	 * @param psm
	 * @throws ProxlGenXMLDataException
	 * @throws Exception 
	 */
	public void addPercolatorPsm( IPsm psm ) throws ProxlGenXMLDataException, Exception {
		

		
		String psmIdString = psm.getPsmId();
		
		String psmReportedPeptideSequence = psm.getPeptideSeq().getSeq();
		
		PsmIdSplitObject psmIdSplitObject = SplitKojakPsmId.splitKojakPsmId( psmIdString );
		
		PsmMatchingPSMDataHolder internalPSMDataHolder = new PsmMatchingPSMDataHolder();
		
		internalPSMDataHolder.setPercolatorPsmData( psm );
		internalPSMDataHolder.setPsmIdSplitObject( psmIdSplitObject );
		
		List<PsmMatchingPSMDataHolder> psmDataByPercolator_psm_id_List = psmDataByPercolator_psm_id_Map.get( psmIdString );
		
		if ( psmDataByPercolator_psm_id_List == null ) {

			psmDataByPercolator_psm_id_List = new ArrayList<>();
			psmDataByPercolator_psm_id_List.add( internalPSMDataHolder );
			psmDataByPercolator_psm_id_Map.put( psmIdString, psmDataByPercolator_psm_id_List);

		} else {
			
			//  Confirm no record already in list with same reported peptide sequence 
			for ( PsmMatchingPSMDataHolder internalPSMDataHolderItem : psmDataByPercolator_psm_id_List ) {
				
				String internalPSMDataHolderItem_PeptideSequence =
						internalPSMDataHolderItem.getPercolatorPsmData().getPeptideSeq().getSeq();
				
				if ( internalPSMDataHolderItem_PeptideSequence.equals( psmReportedPeptideSequence ) ) {
					
					String msg = "Already have Percolator PSM record with psm_id: " + psmIdString
							+ " and sequence: " + psmReportedPeptideSequence;
					
					log.error( msg );
					
					throw new ProxlGenXMLDataException(msg);
				}
			}
			
			psmDataByPercolator_psm_id_List.add( internalPSMDataHolder );
		}
		
		int scanNumber = psmIdSplitObject.getScanNumber();
		
		List<PsmMatchingPSMDataHolder> psmDataByScanNumberList = psmDataByScanNumber_Map.get( scanNumber );
		
		if ( psmDataByScanNumberList == null ) {
			
			psmDataByScanNumberList = new ArrayList<>();
			psmDataByScanNumber_Map.put( scanNumber, psmDataByScanNumberList );
		}
		
		psmDataByScanNumberList.add( internalPSMDataHolder );
		
		

	}
	
	
	
	/**
	 * Match the Kojak PSM data to the Percolator PSM data 
	 * and store Kojak PSM data with the Percolator PSM data
	 */
	public void addKojakPsmData( 
			
			KojakPsmDataObject kojakPsmDataObject

			) throws Exception {
		
		int scanNumber = kojakPsmDataObject.getScanNumber();
		String peptide_1 = kojakPsmDataObject.getPeptide_1();
		String peptide_2 = kojakPsmDataObject.getPeptide_2();
		String link_1 = kojakPsmDataObject.getLink_1();
		String link_2 = kojakPsmDataObject.getLink_2();

		
		
		if ( "-".equals( peptide_1 )  ) {

			if ( log.isInfoEnabled() ) {

				String msg = "\t\tINFO: Kojak record not processed.  Peptide 1 contains '" + peptide_1 + "' so Kojak did NOT make an identification. "

					+ ", scanNumber: " + scanNumber;

				System.out.println( msg );
			}

			return;  //  EARLY EXIT
		}
		
		
		String comparisonString1 = null;
		String comparisonString2 = null;
		String comparisonString3 = null;
		String comparisonString4 = null;
		
		
		if ( ( "-1".equals( link_1 ) ) && ( "-1".equals( link_2 ) ) && ( ! "-".equals( peptide_1 ) ) && ( ! "-".equals( peptide_2 ) ) ) {
		
			//		dimer
			
			comparisonString1 = peptide_1 + "+" + peptide_2 ;
			comparisonString2 = peptide_2 + "+" + peptide_1 ;

		} else if ( ! "-".equals( peptide_2 ) && ( ! "-1".equals( link_1 ) ) && ( ! "-1".equals( link_2 ) )  ) {
		
			//  cross link
			
			comparisonString1 = peptide_1 + "(" + link_1 + ")--" + peptide_2 + "(" + link_2 + ")";
			comparisonString2 = peptide_2 + "(" + link_2 + ")--" + peptide_1 + "(" + link_1 + ")";
		
		} else if ( ( ! "-".equals( link_2 ) ) && ( ! "-1".equals( link_2 ) ) && ( "-".equals( peptide_2 ) ) ) {
			
			//  loop link

			comparisonString1 = peptide_1 + "(" + link_1 + "," + link_2 + ")-LOOP";
			comparisonString2 = peptide_1 + "(" + link_2 + "," + link_1 + ")-LOOP";

			comparisonString3 = peptide_1 + "(" + link_1 + "," + link_2 + ")-Loop";
			comparisonString4 = peptide_1 + "(" + link_2 + "," + link_1 + ")-Loop";

			
		} else {
			
			//  unlinked
			
			comparisonString1 = peptide_1;
			
			
		}
		

		
		List<PsmMatchingPSMDataHolder> psmDataByScanNumberList = psmDataByScanNumber_Map.get( scanNumber );
				
		if ( psmDataByScanNumberList == null ) {

//			if ( log.isInfoEnabled() ) {
//
//			String msg = "\t\tINFO: Kojak record not processed.  Scan number in Kojak Data not found in Percolator data. "
//			
//					+ ", scanNumber: " + scanNumber
//					+ ", comparisonString1: " + comparisonString1
//					+ ", comparisonString2: " + comparisonString2
//					+ ", comparisonString3: " + comparisonString3
//					+ ", comparisonString4: " + comparisonString4;
//			
//			System.out.println( msg );
//			}
			
//			String msg = "ERROR: getForKojakData(...): scan number not found. filename: " + filename + ", scanNumber: " + scanNumber;
//			
//			log.error( msg );
//			System.out.println( msg );
//			System.err.println( msg );
//			
//			throw new Exception( msg );
			
			return;  //  EARLY EXIT
		}
		
		
		
		boolean foundMoreThanOnePsmHolderObject = false;
		
		PsmMatchingPSMDataHolder matchingInternalPSMDataHolder = null;
		
		for ( PsmMatchingPSMDataHolder psmDataByScanNumberObject : psmDataByScanNumberList ) {
			
			IPsm psmFromList = psmDataByScanNumberObject.getPercolatorPsmData();
			
			String psmSequenceFromList = psmFromList.getPeptideSeq().getSeq();
			
			if ( psmSequenceFromList.equals( comparisonString1 ) 
					|| psmSequenceFromList.equals( comparisonString2 ) 
					|| psmSequenceFromList.equals( comparisonString3 ) 
					|| psmSequenceFromList.equals( comparisonString4 ) 
				) {
				
				if ( matchingInternalPSMDataHolder != null ) {
					
					//  if not null then while processing contents of percPsmToKojakMatchingItemList,
					//      already found a match for the comparison strings
					

					matchingInternalPSMDataHolder.setKojakMatchesMultiplePercolatorPsms( true ); //  flag previously found as Kojak matching multiple Percolator PSMs.
					psmDataByScanNumberObject.setKojakMatchesMultiplePercolatorPsms( true ); //  flag currently found as Kojak matching multiple Percolator PSMs.
					
					foundMoreThanOnePsmHolderObject = true;  // flag

					
					String msg = "\t\t ERROR:  DROPPED RECORD:  Kojak PSMs will be dropped : getForKojakData(...): Processing Kojak Data, searching Percolator data, already found a psm match." 

							+ ", scanNumber: " + scanNumber 
							+ ", peptide1: " + peptide_1
							+ ", peptide2: " + peptide_2
							+ ", link1: " + link_1
							+ ", link2: " + link_2
							+ ", comparisonString1: " + comparisonString1
							+ ", comparisonString2: " + comparisonString2
							+ ", comparisonString3: " + comparisonString3
							+ ", comparisonString4: " + comparisonString4;
							
					if ( ! CommandLineOptionsContainer.isForceDropKojakDuplicateRecordsOptOnCommandLine() ) {

						log.error( msg );
						System.out.println( msg );
						System.err.println( msg );

						//  If not ForceDropKojakDuplicateRecordsOptOnCommandLine, this is an error to stop the importer
						
						throw new Exception( msg );
					}

					log.warn( msg );
					System.out.println( msg );
					
					
				} else {
				
					matchingInternalPSMDataHolder = psmDataByScanNumberObject;
					
					psmDataByScanNumberObject.setKojakPsmDataObject( kojakPsmDataObject );
				}
				
			}
			
				
		}
		
		if ( foundMoreThanOnePsmHolderObject ) {
			
			//  Ambiguous match so change to not match
			
			matchingInternalPSMDataHolder = null;
			
		} else {
		
			if ( matchingInternalPSMDataHolder == null ) {
				
				//  No Match found, report it

				if ( log.isInfoEnabled() ) {

					String msg = "\t\t INFO: getForKojakData(...): Processing Kojak Data, searching Percolator data, psm not found. scanNumber: " + scanNumber 
							+ ", peptide1: " + peptide_1
							+ ", peptide2: " + peptide_2
							+ ", link1: " + link_1
							+ ", link2: " + link_2
							+ ", comparisonString1: " + comparisonString1
							+ ", comparisonString2: " + comparisonString2
							+ ", comparisonString3: " + comparisonString3
							+ ", comparisonString4: " + comparisonString4;


					System.out.println( msg );

					String msg2 = "\t\t\t Perc PSM Sequences for "
						+ "scanNumber: " + scanNumber;

					System.out.println( msg2 );

					for ( PsmMatchingPSMDataHolder psmDataByScanNumberObject : psmDataByScanNumberList ) {

						IPsm psmFromList = psmDataByScanNumberObject.getPercolatorPsmData();

						String psmSequenceFromList = psmFromList.getPeptideSeq().getSeq();

						System.out.println( "\t\t\t Sequence: " + psmSequenceFromList  );
					}

				}

				//			String msg = "ERROR: getForKojakData(...): psm not found. filename: " + filename + ", scanNumber: " + scanNumber 
				//					+ ", peptide1: " + peptide1
				//					+ ", peptide2: " + peptide2
				//					+ ", link1: " + link1
				//					+ ", link2: " + link2;
				//			
				//			log.error( msg );
				//			System.out.println( msg );
				//			System.err.println( msg );

				//			throw new Exception( msg );
			}
			
		}
		
		if ( matchingInternalPSMDataHolder != null ) {
			
			
		}
	}
	

	

	
	/**
	 * @return false if a Percolator PSM record is found where Kojak data was not added
	 */
	public boolean doAllPercPSMsHaveKojakRecords() {
		
		
		boolean foundMissing = false;
		

		for ( Map.Entry<String, List<PsmMatchingPSMDataHolder>> mapEntry : psmDataByPercolator_psm_id_Map.entrySet() ) {
			
			List<PsmMatchingPSMDataHolder> psmDataByPercolator_psm_id_List = mapEntry.getValue();

			for ( PsmMatchingPSMDataHolder psmDataByScanNumberObject : psmDataByPercolator_psm_id_List ) {

				if ( psmDataByScanNumberObject.isKojakMatchesMultiplePercolatorPsms() ) {



				} else {


					if ( psmDataByScanNumberObject.getKojakPsmDataObject() == null ) {

						foundMissing = true;

						if ( log.isInfoEnabled() ) {

							String msg = "ERROR: PercPSM record found without matching Kojak record.";

							msg += ", Scan number: " + psmDataByScanNumberObject.getPsmIdSplitObject().getScanNumber()
									+ ", psm_id: " + psmDataByScanNumberObject.getPercolatorPsmData().getPsmId()
									+ ", psm sequence: " 
									+ psmDataByScanNumberObject.getPercolatorPsmData().getPeptideSeq().getSeq();

							log.error( msg );
//							System.out.println( msg );
//							System.err.println( msg );
						}
						
						break;

					}
				}
			}
			
		}
		
		
		if ( foundMissing ) {
			
			return false;
		}
		
		return true;
	}
		
	
}
