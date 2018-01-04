package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.Proxl_XML_Peptide_UniqueId_Constants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_crosslink_looplink_in_conf.IsCrosslinkOrLooplinkMassInConf;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakSequenceUtils;
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
			ProxlInput proxlInputRoot ) throws ProxlGenXMLDataException {


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

			peptideList.add( getPeptide( peptide_1, null /* linkPositionsStrings */, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
			peptideList.add( getPeptide( peptide_2, null /* linkPositionsStrings */, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2 ) );

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


			peptideList.add( getPeptide( peptide_1, linkPositionsStrings_1, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
			peptideList.add( getPeptide( peptide_2, linkPositionsStrings_2, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__2 ) );

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

			peptideList.add( getPeptide( peptide_1, linkPositionsStrings, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );

		} else {

			//  unlinked

			proxlInputReportedPeptide.setType( LinkType.UNLINKED );

			peptideList.add( getPeptide( peptide_1, null /* linkPositionsStrings */, scanNumber, Proxl_XML_Peptide_UniqueId_Constants.PEPTIDE_UNIQUE_ID__1 ) );
		}


		return proxlInputReportedPeptide;
	}


	/**
	 * @param peptideSequence
	 * @param linkPositionsStrings
	 * @return
	 * @throws ProxlGenXMLDataException
	 */
	private Peptide getPeptide( String peptideSequence, String[] linkPositionsStrings, int scanNumber, String peptideUniqueId ) throws ProxlGenXMLDataException {


		String peptideSequenceNoMods = 
				KojakSequenceUtils.getInstance()
				.getPeptideWithDynamicModificationsRemoved( peptideSequence );

		Map<Integer,Collection<BigDecimal>> dynamicModLocationsAndMasses =
				KojakSequenceUtils.getInstance()
				.getDynamicModsForOneSequence( peptideSequence );


		Peptide proxlInputPeptide = new Peptide();

		proxlInputPeptide.setSequence( peptideSequenceNoMods );
		
		proxlInputPeptide.setUniqueId( peptideUniqueId );

		if ( dynamicModLocationsAndMasses != null && ( ! dynamicModLocationsAndMasses.isEmpty() ) ) {

			Modifications modifications = new Modifications();
			proxlInputPeptide.setModifications( modifications );
			
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
