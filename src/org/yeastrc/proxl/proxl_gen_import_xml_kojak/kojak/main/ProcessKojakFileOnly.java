package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SwapPerPeptideScoresBetweenPeptides;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_crosslink_looplink_in_conf.IsCrosslinkOrLooplinkMassInConf;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.IsAllProtein_1or2_Decoy;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakConfFileReaderResult;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakFileGetContents;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakFileReader;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakProteinNonDecoy;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.PopulateOnlyKojakAnnotationTypesInSearchProgram;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakFileGetContents.KojakFileGetContentsResult;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.enums.KojakGenImportInternalLinkTypeEnum;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.LinkTypeAndReportedPeptideString;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;

/**
 * 
 *
 */
public class ProcessKojakFileOnly {

	private static final Logger log = Logger.getLogger( ProcessKojakFileOnly.class );
	
	// private constructor
	private ProcessKojakFileOnly() { }
	
	public static ProcessKojakFileOnly getInstance() {
		return new ProcessKojakFileOnly();
	}


	/**
	 * @param kojakOutputFile
	 * @param proxlInputRoot
	 * @param proteinNameStrings
	 * @param kojakConfFileReaderResult
	 * @throws Exception
	 */
	public void processKojakFile( 

			File kojakOutputFile,
			ProxlInput proxlInputRoot,
			Set<String> proteinNameStrings,
			KojakConfFileReaderResult kojakConfFileReaderResult ) throws Exception {
		
		Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile = kojakConfFileReaderResult.getIsotopes_SpecifiedIn_KojakConfFile();

		SearchProgramInfo searchProgramInfo = proxlInputRoot.getSearchProgramInfo();
		
		SearchPrograms searchPrograms = searchProgramInfo.getSearchPrograms();
		List<SearchProgram> searchProgramList = searchPrograms.getSearchProgram();
		
		SearchProgram searchProgram = new SearchProgram();
		searchProgramList.add( searchProgram );
		
		searchProgram.setName( SearchProgramNameKojakImporterConstants.KOJAK );
		searchProgram.setDisplayName( SearchProgramNameKojakImporterConstants.KOJAK );
		searchProgram.setDescription( null );
		
		try {
			KojakFileGetContentsResult kojakFileGetContentsResult =
					KojakFileGetContents.getInstance().kojakFileGetContents( kojakOutputFile, isotope_Labels_SpecifiedIn_KojakConfFile );
			
			KojakFileReader kojakFileReader = kojakFileGetContentsResult.getKojakFileReader();
			List<KojakPsmDataObject> kojakPsmDataObjectList = kojakFileGetContentsResult.getKojakPsmDataObjectList();
			
			PopulateOnlyKojakAnnotationTypesInSearchProgram.getInstance()
			.populateKojakAnnotationTypesInSearchProgram( 
					searchProgram, kojakFileReader, PopulateOnlyKojakAnnotationTypesInSearchProgram.SetKojakDefaultCutoffs.YES );
			
			searchProgram.setVersion( kojakFileReader.getProgramVersion() );
			
			AddKojakOnlyAnnotationSortOrder.getInstance().addAnnotationSortOrder( searchProgramInfo );
			
			AddKojakOnlyDefaultVisibleAnnotations.getInstance().addDefaultVisibleAnnotations( searchProgramInfo );
			
			
			Map<String, ReportedPeptide> reportedPeptidesKeyedOnReportedPeptideString = new HashMap<>();
			
			//  Process the data lines:

			for ( KojakPsmDataObject kojakPsmDataObject : kojakPsmDataObjectList ) {

				if ( log.isInfoEnabled() ) {

					System.out.println( "Processing Kojak record for scan number: " + kojakPsmDataObject.getScanNumber() );
				}

				if ( IsAllProtein_1or2_Decoy.getInstance().isAllProtein_1or2_Decoy( kojakPsmDataObject, kojakConfFileReaderResult) ) {
					
					if ( log.isInfoEnabled() ) {

						System.out.println( "All proteins for Protein #1 or Protein #2 are decoys so skipping this Kojak record."
								+ "  scan number: " + kojakPsmDataObject.getScanNumber() );
					}		
					
					//   All proteins for Protein #1 or Protein #2 are decoys so skipping this Kojak record.
					
					continue;  //   EARLY CONTINUE to next record
				}
				
				LinkTypeAndReportedPeptideString linkTypeAndReportedPeptideString = 
						GetLinkTypeAndReportedPeptideString.getInstance().getLinkTypeAndReportedPeptideString( kojakPsmDataObject );
				
				if ( linkTypeAndReportedPeptideString == null ) {
					
					//  Kojak did not make an identification so this record is skipped.
					
					continue;  //  EARY CONTINUE
				}
				
				
				if ( linkTypeAndReportedPeptideString.getKojakGenImportInternalLinkTypeEnum() 
						== KojakGenImportInternalLinkTypeEnum.CROSSLINK ) {
					
					
					if ( ! IsCrosslinkOrLooplinkMassInConf.getInstance()
							.isCrosslinkOrLooplinkMassInConf( kojakPsmDataObject.getLinkerMass(), proxlInputRoot ) ) {
						
						String msg = "Crosslink link mass not found in Kojak conf file.  Scan number: " +
								kojakPsmDataObject.getScanNumber() + ", linker mass on PSM: " + kojakPsmDataObject.getLinkerMass();
						log.error(msg);
						throw new ProxlGenXMLDataException(msg);
					}
					
				} else if ( linkTypeAndReportedPeptideString.getKojakGenImportInternalLinkTypeEnum() 
							== KojakGenImportInternalLinkTypeEnum.LOOPLINK ) {

					if ( ! IsCrosslinkOrLooplinkMassInConf.getInstance()
							.isCrosslinkOrLooplinkMassInConf( kojakPsmDataObject.getLinkerMass(), proxlInputRoot ) ) {
						
						String msg = "Looplink link mass not found in Kojak conf file.  Scan number: " +
								kojakPsmDataObject.getScanNumber() + ", linker mass on PSM: " + kojakPsmDataObject.getLinkerMass();
						log.error(msg);
						throw new ProxlGenXMLDataException(msg);
					}
				}
				
				
				String reportedPeptideString = linkTypeAndReportedPeptideString.getReportedPeptideString();
				
				ReportedPeptide reportedPeptide = 
						reportedPeptidesKeyedOnReportedPeptideString.get( reportedPeptideString );
				
				if ( reportedPeptide == null ) {
					
					//  No ReportedPeptide object for this reportedPeptideString 
					//  so create it and store in the List and in the Map
					
					reportedPeptide = 
							PopulateProxlInputReportedPeptideFromKojakOnly.getInstance()
							.populateProxlInputReportedPeptide(
									kojakPsmDataObject, linkTypeAndReportedPeptideString, isotope_Labels_SpecifiedIn_KojakConfFile, proxlInputRoot );
					
					ReportedPeptides reportedPeptides = proxlInputRoot.getReportedPeptides();
					
					if ( reportedPeptides == null ) {
						
						reportedPeptides = new ReportedPeptides();
						proxlInputRoot.setReportedPeptides( reportedPeptides );
					}
					
					List<ReportedPeptide> reportedPeptideList = reportedPeptides.getReportedPeptide();
					
					reportedPeptideList.add( reportedPeptide );
					
					reportedPeptidesKeyedOnReportedPeptideString.put( reportedPeptideString, reportedPeptide );
				}
				
				int numPeptidesOnReportedPeptide = reportedPeptide.getPeptides().getPeptide().size();
				
				Psms psms = reportedPeptide.getPsms();
				if ( psms == null ) {
					psms = new Psms();
					reportedPeptide.setPsms( psms );
				}
				List<Psm> psmList = psms.getPsm();
				
				Psm proxlInputPsm = 
						Proxl_Psm__PsmPerPeptide_CreateWithKojakAnnotations.getInstance()
						.createProxlInputPsm( 
								kojakPsmDataObject, 
								numPeptidesOnReportedPeptide, 
								SwapPerPeptideScoresBetweenPeptides.NO );
				
				psmList.add( proxlInputPsm );
				

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
			
		} catch ( Exception e ) {
			
			String msg = "Error processing Kojak file: " + kojakOutputFile.getAbsolutePath();
			log.error( msg );
			throw e;
		
		} 
		
	}
	

}
