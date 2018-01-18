package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;


import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.enums.KojakGenImportInternalLinkTypeEnum;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects.LinkTypeAndReportedPeptideString;

public class GetLinkTypeAndReportedPeptideString {


	private static final Logger log = Logger.getLogger( GetLinkTypeAndReportedPeptideString.class );

	//  private constructor
	private GetLinkTypeAndReportedPeptideString() {}


	public static GetLinkTypeAndReportedPeptideString getInstance() {

		return new GetLinkTypeAndReportedPeptideString();
	}

	/**
	 * @param kojakPsmDataObject
	 * @return null if no Kojak identification
	 */
	public LinkTypeAndReportedPeptideString getLinkTypeAndReportedPeptideString( KojakPsmDataObject kojakPsmDataObject ) {
		

		int scanNumber = kojakPsmDataObject.getScanNumber();
		String peptide_1 = kojakPsmDataObject.getPeptide_1();
		String peptide_2 = kojakPsmDataObject.getPeptide_2();
		String link_1 = kojakPsmDataObject.getLink_1();
		String link_2 = kojakPsmDataObject.getLink_2();
		
		//  Add Isotope label to end of peptide string if applicable
		if ( kojakPsmDataObject.getPeptide_1_Isotope_Label_Suffix_From_Kojak_File() != null ) {
			peptide_1 += kojakPsmDataObject.getPeptide_1_Isotope_Label_Suffix_From_Kojak_File();
		}
		if ( kojakPsmDataObject.getPeptide_2_Isotope_Label_Suffix_From_Kojak_File() != null ) {
			peptide_2 += kojakPsmDataObject.getPeptide_2_Isotope_Label_Suffix_From_Kojak_File();
		}


		if ( "-".equals( peptide_1 )  ) {

			if ( log.isInfoEnabled() ) {

				String msg = "\t\tINFO: Kojak record not processed.  Peptide 1 contains '" + peptide_1 + "' so Kojak did NOT make an identification. "

					+ ", scanNumber: " + scanNumber;

				System.out.println( msg );
			}
			
			return null;  //  EARLY EXIT
		}
		
		LinkTypeAndReportedPeptideString linkTypeAndReportedPeptideString = new LinkTypeAndReportedPeptideString();

		if ( ( "-1".equals( link_1 ) ) && ( "-1".equals( link_2 ) ) && ( ! "-".equals( peptide_1 ) ) && ( ! "-".equals( peptide_2 ) ) ) {

			//		dimer

			String reportedPeptideString = peptide_1 + "+" + peptide_2;
			
			linkTypeAndReportedPeptideString.setReportedPeptideString( reportedPeptideString );
			linkTypeAndReportedPeptideString.setKojakGenImportInternalLinkTypeEnum( KojakGenImportInternalLinkTypeEnum.DIMER );
			

		} else if ( ! "-".equals( peptide_2 ) && ( ! "-1".equals( link_1 ) ) && ( ! "-1".equals( link_2 ) ) 
				 && ( ! "".equals( link_1 ) ) && ( ! "".equals( link_2 ) ) ) {
		
			//  cross link
			String reportedPeptideString = peptide_1 + "(" + link_1 + ")--" + peptide_2 + "(" + link_2 + ")";
			
			linkTypeAndReportedPeptideString.setReportedPeptideString( reportedPeptideString );
			linkTypeAndReportedPeptideString.setKojakGenImportInternalLinkTypeEnum( KojakGenImportInternalLinkTypeEnum.CROSSLINK );
			

		} else if ( ( ! "-".equals( link_2 ) ) && ( ! "-1".equals( link_2 ) ) && ( "-".equals( peptide_2 ) ) ) {
			
			//  loop link
			
			String reportedPeptideString = peptide_1 + "(" + link_1 + "," + link_2 + ")-LOOP";
			
			linkTypeAndReportedPeptideString.setReportedPeptideString( reportedPeptideString );
			linkTypeAndReportedPeptideString.setKojakGenImportInternalLinkTypeEnum( KojakGenImportInternalLinkTypeEnum.LOOPLINK );
			
		} else {
			
			//  unlinked

			String reportedPeptideString = peptide_1;
			
			linkTypeAndReportedPeptideString.setReportedPeptideString( reportedPeptideString );
			linkTypeAndReportedPeptideString.setKojakGenImportInternalLinkTypeEnum( KojakGenImportInternalLinkTypeEnum.UNLINKED );
			
		}
		
		return linkTypeAndReportedPeptideString;
	}
}
