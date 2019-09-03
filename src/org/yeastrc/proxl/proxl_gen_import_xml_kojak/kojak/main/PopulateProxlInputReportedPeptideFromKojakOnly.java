package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.IsotopeLabelValuesConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.Proxl_XML_Peptide_UniqueId_Constants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_crosslink_looplink_in_conf.IsCrosslinkOrLooplinkMassInConf;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakSequenceUtils;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Kojak_GetDynamicModsForOneSequence;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.Kojak_GetDynamicModsForOneSequence.Kojak_GetDynamicModsForOneSequence_Result;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.enums.KojakGenImportInternalLinkTypeEnum;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.LinkTypeAndReportedPeptideString;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel;



/**
 * 
 *
 */
public class PopulateProxlInputReportedPeptideFromKojakOnly {

	private static final Logger log = Logger.getLogger( PopulateProxlInputReportedPeptideFromKojakOnly.class );

	//  private constructor
	private PopulateProxlInputReportedPeptideFromKojakOnly() {}


	public static PopulateProxlInputReportedPeptideFromKojakOnly getInstance() {

		return new PopulateProxlInputReportedPeptideFromKojakOnly();
	}




	/**
	 * @param kojakPsmDataObject
	 * @throws ProxlGenXMLDataException
	 */
	public ReportedPeptide populateProxlInputReportedPeptide( 
			KojakPsmDataObject kojakPsmDataObject,
			LinkTypeAndReportedPeptideString linkTypeAndReportedPeptideString,
			Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile,
			ProxlInput proxlInputRoot ) throws ProxlGenXMLDataException {


		int scanNumber = kojakPsmDataObject.getScanNumber();
		String peptide_1 = kojakPsmDataObject.getPeptide_1();
		String peptide_2 = kojakPsmDataObject.getPeptide_2();
		String peptide_1_Isotope_LabelString = kojakPsmDataObject.getPeptide_1_Isotope_Label_For_ProxlXML_File();
		String peptide_2_Isotope_LabelString = kojakPsmDataObject.getPeptide_2_Isotope_Label_For_ProxlXML_File();
		String link_1 = kojakPsmDataObject.getLink_1();
		String link_2 = kojakPsmDataObject.getLink_2();


		if ( "-".equals( peptide_1 )  ) {

			if ( log.isInfoEnabled() ) {

				String msg = "\t\tINFO: Kojak record not processed.  Peptide 1 contains '" + peptide_1 + "' so Kojak did NOT make an identification. "

					+ ", scanNumber: " + scanNumber;

				System.out.println( msg );
			}

			return null;  //  EARLY EXIT
		}



		ReportedPeptide proxlInputReportedPeptide = new ReportedPeptide();

		proxlInputReportedPeptide.setReportedPeptideString( linkTypeAndReportedPeptideString.getReportedPeptideString() );

		Peptides peptides = new Peptides();
		proxlInputReportedPeptide.setPeptides( peptides );

		List<Peptide> peptideList = peptides.getPeptide();


		if ( linkTypeAndReportedPeptideString.getKojakGenImportInternalLinkTypeEnum() 
				== KojakGenImportInternalLinkTypeEnum.DIMER ) {

			//		dimer

			proxlInputReportedPeptide.setType( LinkType.UNLINKED );

			peptideList.add( 
					getPeptide( 
							peptide_1, 
							peptide_1_Isotope_LabelString, 
							null /* linkPositionsStrings */, 
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
			
			peptideList.add( 
					getPeptide( 
							peptide_2, 
							peptide_2_Isotope_LabelString, 
							null /* linkPositionsStrings */,
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2 ) );

		} else if ( linkTypeAndReportedPeptideString.getKojakGenImportInternalLinkTypeEnum() 
				== KojakGenImportInternalLinkTypeEnum.CROSSLINK ) {

			//  cross link
			
			if ( ! IsCrosslinkOrLooplinkMassInConf.getInstance()
					.isCrosslinkOrLooplinkMassInConf( kojakPsmDataObject.getLinkerMass(), proxlInputRoot ) ) {
				
				String msg = "Crosslink link mass not found in Kojak conf file.  Scan number: " +
						kojakPsmDataObject.getScanNumber() + ", linker mass on PSM: " + kojakPsmDataObject.getLinkerMass();
				log.error(msg);
				throw new ProxlGenXMLDataException(msg);
			}
			

			proxlInputReportedPeptide.setType( LinkType.CROSSLINK );

			String[] linkPositionsStrings_1 = { link_1 };
			String[] linkPositionsStrings_2 = { link_2 };


			peptideList.add( 
					getPeptide( 
							peptide_1, 
							peptide_1_Isotope_LabelString, 
							linkPositionsStrings_1, 
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
			
			peptideList.add( 
					getPeptide( 
							peptide_2, 
							peptide_2_Isotope_LabelString, 
							linkPositionsStrings_2, 
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2 ) );

		} else if ( linkTypeAndReportedPeptideString.getKojakGenImportInternalLinkTypeEnum() 
				== KojakGenImportInternalLinkTypeEnum.LOOPLINK ) {

			//  loop link
			

			if ( ! IsCrosslinkOrLooplinkMassInConf.getInstance()
					.isCrosslinkOrLooplinkMassInConf( kojakPsmDataObject.getLinkerMass(), proxlInputRoot ) ) {
				
				String msg = "Looplink link mass not found in Kojak conf file.  Scan number: " +
						kojakPsmDataObject.getScanNumber() + ", linker mass on PSM: " + kojakPsmDataObject.getLinkerMass();
				log.error(msg);
				throw new ProxlGenXMLDataException(msg);
			}
			

			proxlInputReportedPeptide.setType( LinkType.LOOPLINK );

			String[] linkPositionsStrings = { link_1, link_2 };

			peptideList.add( 
					getPeptide( 
							peptide_1, 
							peptide_1_Isotope_LabelString, 
							linkPositionsStrings, 
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );

		} else {

			//  unlinked

			proxlInputReportedPeptide.setType( LinkType.UNLINKED );

			peptideList.add( 
					getPeptide( 
							peptide_1, 
							peptide_1_Isotope_LabelString, 
							null /* linkPositionsStrings */,
							isotope_Labels_SpecifiedIn_KojakConfFile,
							scanNumber, 
							Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
		}


		return proxlInputReportedPeptide;
	}


	/**
	 * @param peptideSequence
	 * @param linkPositionsStrings
	 * @return
	 * @throws ProxlGenXMLDataException
	 */
	private Peptide getPeptide( 
			String peptideSequence, 
			String peptideIsotopeLabelString,
			String[] linkPositionsStrings,
			Isotope_Labels_SpecifiedIn_KojakConfFile isotope_Labels_SpecifiedIn_KojakConfFile,
			int scanNumber, 
			String peptideUniqueId) throws ProxlGenXMLDataException {


		String peptideSequenceForProxlXML_PeptideObject = 
				KojakSequenceUtils.getSingletonInstance()
				.getPeptideWithDynamicModificationsRemoved( peptideSequence, isotope_Labels_SpecifiedIn_KojakConfFile );
		
		if ( isotope_Labels_SpecifiedIn_KojakConfFile != null && isotope_Labels_SpecifiedIn_KojakConfFile.getIsotopeLabel_15N_filter_Value() != null ) {
			if ( peptideSequenceForProxlXML_PeptideObject.endsWith( IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR ) ) {
				peptideSequenceForProxlXML_PeptideObject = 
						peptideSequenceForProxlXML_PeptideObject.substring( 
								0,
								peptideSequenceForProxlXML_PeptideObject.length() - IsotopeLabelValuesConstants.ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR.length() );
			}
		}

		Kojak_GetDynamicModsForOneSequence_Result kojak_GetDynamicModsForOneSequence_Result =
				Kojak_GetDynamicModsForOneSequence.getSingletonInstance()
				.getDynamicModsForOneSequence( peptideSequence, isotope_Labels_SpecifiedIn_KojakConfFile );


		Peptide proxlInputPeptide = new Peptide();

		proxlInputPeptide.setSequence( peptideSequenceForProxlXML_PeptideObject );
		
		if ( peptideIsotopeLabelString != null ) {

			PeptideIsotopeLabel peptideIsotopeLabel = new PeptideIsotopeLabel();
			peptideIsotopeLabel.setLabel( peptideIsotopeLabelString );
			
			PeptideIsotopeLabels peptideIsotopeLabels = new PeptideIsotopeLabels();
			proxlInputPeptide.setPeptideIsotopeLabels( peptideIsotopeLabels );
			peptideIsotopeLabels.setPeptideIsotopeLabel( peptideIsotopeLabel );
		}
		
		proxlInputPeptide.setUniqueId( peptideUniqueId );

		if ( kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition != null && ( ! kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition.isEmpty() ) ){

			Map<Integer,Collection<BigDecimal>> dynamicModLocationsAndMasses = kojak_GetDynamicModsForOneSequence_Result.dynamicModsForPositions_KeyPosition;

			if ( dynamicModLocationsAndMasses != null && ( ! dynamicModLocationsAndMasses.isEmpty() ) ) {

				Modifications modifications = proxlInputPeptide.getModifications();
				if ( modifications == null ) {
					modifications = new Modifications();
					proxlInputPeptide.setModifications( modifications );
				}

				List<Modification> modificationList = modifications.getModification();

				for ( Map.Entry<Integer,Collection<BigDecimal>> dynamicModLocationsAndMassesEntry : dynamicModLocationsAndMasses.entrySet() ) {

					int position = dynamicModLocationsAndMassesEntry.getKey();

					for ( BigDecimal modificationMass : dynamicModLocationsAndMassesEntry.getValue() ) {

						boolean isModificationAMonolink = IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );

						Modification modification = new Modification();
						modificationList.add( modification );

						modification.setPosition( BigInteger.valueOf( position ) );
						modification.setMass( modificationMass );
						modification.setIsMonolink( isModificationAMonolink );
					}
				}

			}
		}

		//  Process Modifications at 'n' terminus
		
		if ( kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods != null && ( ! kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods.isEmpty() ) ){

			Modifications modifications = proxlInputPeptide.getModifications();
			if ( modifications == null ) {
				modifications = new Modifications();
				proxlInputPeptide.setModifications( modifications );
			}

			List<Modification> modificationList = modifications.getModification();

			for ( BigDecimal modificationMass : kojak_GetDynamicModsForOneSequence_Result.n_Terminal_Mods ) {

				boolean isModificationAMonolink = 
						IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );
				
				Modification modification = new Modification();
				modificationList.add( modification );

				modification.setMass( modificationMass );
				modification.setIsMonolink( isModificationAMonolink );
				modification.setIsNTerminal( true );
				modification.setPosition( BigInteger.ONE ); // 1 based position
			}
		}

		//  Process Modifications at 'c' terminus
		
		if ( kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods != null && ( ! kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods.isEmpty() ) ){
			
			BigInteger peptideLengthBI = BigInteger.valueOf( proxlInputPeptide.getSequence().length() );

			Modifications modifications = proxlInputPeptide.getModifications();
			if ( modifications == null ) {
				modifications = new Modifications();
				proxlInputPeptide.setModifications( modifications );
			}

			List<Modification> modificationList = modifications.getModification();

			for ( BigDecimal modificationMass : kojak_GetDynamicModsForOneSequence_Result.c_Terminal_Mods ) {

				boolean isModificationAMonolink = 
						IsModificationAMonolink.getInstance().isModificationAMonolink( modificationMass );
				
				Modification modification = new Modification();
				modificationList.add( modification );

				modification.setMass( modificationMass );
				modification.setIsMonolink( isModificationAMonolink );
				modification.setIsCTerminal( true );
				modification.setPosition( peptideLengthBI ); // 1 based position
			}
		}
		

		if ( linkPositionsStrings != null && linkPositionsStrings.length > 0 ) {

			LinkedPositions linkedPositions = new LinkedPositions();
			proxlInputPeptide.setLinkedPositions( linkedPositions );

			List<LinkedPosition> linkedPositionList = linkedPositions.getLinkedPosition();

			for ( String linkPositionString : linkPositionsStrings ) {

				try {

					BigInteger linkPosition = new BigInteger( linkPositionString );

					LinkedPosition linkedPosition = new LinkedPosition();
					linkedPositionList.add( linkedPosition );

					linkedPosition.setPosition( linkPosition );

				} catch ( Exception e ) {

					String msg = "Failed to parse link position '" + linkPositionString + "' for scan number: " + scanNumber;
					log.error( msg );
					throw new ProxlGenXMLDataException( msg, e );
				}
			}


		}
		
		return proxlInputPeptide;
	}

}
