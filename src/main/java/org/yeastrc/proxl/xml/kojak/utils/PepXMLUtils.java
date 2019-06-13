package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;

import java.util.Collection;

public class PepXMLUtils {

	public static final String XLINK_TYPE_LOOPLINK = "loop";
	public static final String XLINK_TYPE_CROSSLINK = "xl";
	public static final String XLINK_TYPE_UNLINKED = "na";
	
	/**
	 * Given a peptide sequence such as PIPTLDE, return all sequences that represent
	 * all possible combinations of leucine and isoleucine substitutions. E.g.:
	 * 
	 * PIPTLDE
	 * PLPTLDE
	 * PIPTIDE
	 * PLPTIDE
	 * 
	 * @param sequence
	 * @return
	 */
	public Collection<String> getAllLeucineIsoleucineTransormations( String sequence ) {
		
		return null;
	}
	
	/**
	 * Get the type of link represented by the search hit
	 * 
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	public static int getHitType( SearchHit searchHit ) throws Exception {
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_CROSSLINK ) ) {
			return ConverterConstants.LINK_TYPE_CROSSLINK;
		}
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_LOOPLINK ) ) {
			return ConverterConstants.LINK_TYPE_LOOPLINK;
		}
		
		if( searchHit.getXlinkType().equals( PepXMLUtils.XLINK_TYPE_UNLINKED ) ) {
			return ConverterConstants.LINK_TYPE_UNLINKED;
		}
		
		throw new Exception( "Unknown link type in pepxml: " + searchHit.getXlinkType() );
		
	}

	/**
	 * Attempt to get the comet version from the pepXML file. Returns "Unknown" if not found.
	 *
	 * @param msAnalysis
	 * @return
	 */
	public static String getKojakVersionFromXML(MsmsPipelineAnalysis msAnalysis ) {

		for( MsmsPipelineAnalysis.MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				if( 	searchSummary.getSearchEngine() != null &&
						searchSummary.getSearchEngine().value() != null &&
						searchSummary.getSearchEngine().value().equals( "Kojak" ) ) {

					String version = searchSummary.getSearchEngineVersion();

					if( version != null ) { return version; }

					return "Unknown";	// return Unknown if version can't be found
				}

			}
		}

		return "Unknown"; // return Unknown if version can't be found
	}


}
