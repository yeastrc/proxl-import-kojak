package org.yeastrc.proxl.xml.kojak.reader;

import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;
import net.systemsbiology.regis_web.pepxml.NameValueType;
import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl.xml.kojak.constants.KojakConstants;
import org.yeastrc.proxl.xml.kojak.objects.*;
import org.yeastrc.proxl.xml.kojak.utils.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class KojakResultsParser {

	private static final KojakResultsParser _INSTANCE = new KojakResultsParser();
	public static KojakResultsParser getInstance() { return _INSTANCE; }
	private KojakResultsParser() { }

	public Map<KojakReportedPeptide, Collection<KojakPSMResult>> getResultsFromAnalysis(KojakAnalysis kojakAnalysis) throws Exception {

		Map<KojakReportedPeptide, Collection<KojakPSMResult>> results = new HashMap<>();

		for (File pepXMLFile : kojakAnalysis.getPepXMLFiles()) {
			results.putAll(getResultsForPepXMLFile(pepXMLFile, kojakAnalysis));
		}

		return results;
	}

	private Map<KojakReportedPeptide, Collection<KojakPSMResult>> getResultsForPepXMLFile( File pepXMLFile, KojakAnalysis kojakAnalysis) throws Exception {

		Map<KojakReportedPeptide, Collection<KojakPSMResult>> results = new HashMap<>();

		MsmsPipelineAnalysis analysis = getJaxbRootForPepXMLFile( pepXMLFile );

		for( MsmsRunSummary runSummary : analysis.getMsmsRunSummary() ) {

			Collection<PepXMLModDefinition> dynamicMods = PepXMLModificationUtils.getDynamicModsForSearch( runSummary );
			dynamicMods.addAll( PepXMLModificationUtils.getNTerminalDynamicModsForSearch( runSummary ) );
			dynamicMods.addAll( PepXMLModificationUtils.getCTerminalDynamicModsForSearch( runSummary ) );

			Collection<PepXMLModDefinition> staticMods = PepXMLModificationUtils.getStaticModsForSearch( runSummary );

			String decoyFilter = PepXMLRunSummaryUtils.getDecoyFilter( runSummary );
			String spectralFile = PepXMLRunSummaryUtils.getMSDataFile( runSummary );

			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {

						try {
							// only one interprophet result will appear for a search hit, and we are only
							// interested in search hits with an interprophet result.

							// skip this if it's a decoy
							if (PepXMLDecoyUtils.isDecoy(decoyFilter, searchHit))
								continue;

							// get our result
							KojakPSMResult result = getResult(runSummary, spectrumQuery, searchHit, spectralFile);

							// get our reported peptide
							KojakReportedPeptide reportedPeptide = getReportedPeptide(searchHit, dynamicMods, staticMods);

							if (!results.containsKey(reportedPeptide))
								results.put(reportedPeptide, new ArrayList<>());

							results.get(reportedPeptide).add(result);
						} catch( Throwable t ) {

							System.err.println( "Got error processing search hit" );
							System.err.println( "Error message: " + t.getMessage() );
							System.err.println( "SearchHit (XML):" );
							System.err.println(PepXMLToStrings.searchHitToString( searchHit ) + "\n" );

							throw t;

						}
					}
				}
			}
		}

		return results;
	}

	private MsmsPipelineAnalysis getJaxbRootForPepXMLFile( File pepXMLFile ) throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis)jaxbUnmarshaller.unmarshal(pepXMLFile);

		return msAnalysis;
	}

	/**
	 * Get the IProphetReportedPeptide for the given SearchHit
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods ) throws Exception {

		int type = PepXMLUtils.getHitType( searchHit );

		if( type == ConverterConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit, dynamicMods, staticMods );

		if( type == ConverterConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit, dynamicMods, staticMods );

		return getUnlinkedReportedPeptide( searchHit, dynamicMods, staticMods );

	}

	/**
	 * Get the IProphetReportedPeptide for a crosslink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getCrosslinkReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods ) throws Exception {

		List<KojakPeptide> kojakPeptides = new ArrayList<>( 2 );

		for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {

			if( kojakPeptides.size() > 2 ) {
				throw new Exception( "Got more than two linked peptides." );
			}

			kojakPeptides.add( getPeptideFromLinkedPeptide( linkedPeptide, dynamicMods, staticMods ) );
		}

		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_CROSSLINK, kojakPeptides.get( 0 ), kojakPeptides.get( 1 ) );
	}

	/**
	 * Get the IProphetReportedPeptide for a looplink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getLooplinkReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods  ) throws Exception {

		KojakPeptide kojakPeptide = getPeptideFromSearchHit( searchHit, dynamicMods, staticMods );
		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_LOOPLINK, kojakPeptide, null );
	}

	/**
	 * Get the IProphetReportedPeptide for an unlinked result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getUnlinkedReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods ) throws Exception {

		KojakPeptide kojakPeptide = getPeptideFromSearchHit( searchHit, dynamicMods, staticMods );
		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_UNLINKED, kojakPeptide, null );
	}



	/**
	 * Get the KojakPeptide from the searchHit. Includes the peptide sequence and any mods.
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakPeptide getPeptideFromSearchHit(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods ) throws Exception {

		KojakPeptideBuilder peptideBuilder = new KojakPeptideBuilder();

		String peptideSequence = searchHit.getPeptide();
		peptideBuilder.setSequence( peptideSequence );

		ModInfoDataType modInfo = searchHit.getModificationInfo();
		peptideBuilder.setModifications( PepXMLModificationUtils.getDynamicModMapForModInfoDataType( modInfo, peptideSequence, dynamicMods, staticMods ) );
		peptideBuilder.setnTerminalMod( PepXMLModificationUtils.getNTerminalModMassDiff( modInfo, dynamicMods ) );
		peptideBuilder.setcTerminalMod( PepXMLModificationUtils.getCTerminalModMassDiff( modInfo, dynamicMods ) );

		if( PepXMLUtils.getHitType( searchHit ) == ConverterConstants.LINK_TYPE_LOOPLINK ) {
			List<Integer> linkedPositions = getLinkedPositionsForLooplinkSearchHit( searchHit );
			peptideBuilder.setPosition1( linkedPositions.get( 0 ) );
			peptideBuilder.setPosition2( linkedPositions.get( 1 ) );
		}

		return peptideBuilder.createKojakPeptide();
	}

	private List<Integer> getLinkedPositionsForLooplinkSearchHit( SearchHit searchHit ) throws Exception {

		List<Integer> linkedPositions = new ArrayList<>( 2 );

		Xlink xlink = searchHit.getXlink();
		for( NameValueType nvt : xlink.getXlinkScore() ) {
			if( nvt.getName().equals( "link" ) ) {
				linkedPositions.add( Integer.valueOf( nvt.getValueAttribute() ) );
			}
		}

		if( linkedPositions.size() != 2 ) {
			throw new Exception( "Did not get two positions for looplink in pepXML file." );
		}

		return linkedPositions;
	}

	/**
	 * Get the KojakPeptide for the LinkedPeptide (mods, etc)
	 * @param linkedPeptide
	 * @param dynamicMods
	 * @param staticMods
	 * @return
	 * @throws Exception
	 */
	private KojakPeptide getPeptideFromLinkedPeptide(LinkedPeptide linkedPeptide, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods ) throws Exception {


		KojakPeptideBuilder peptideBuilder = new KojakPeptideBuilder();

		String peptideSequence = linkedPeptide.getPeptide();
		peptideBuilder.setSequence( peptideSequence );

		peptideBuilder.setPosition1( getLinkedPositionForLinkedPeptide( linkedPeptide ) );

		ModInfoDataType modInfo = linkedPeptide.getModificationInfo();
		peptideBuilder.setModifications( PepXMLModificationUtils.getDynamicModMapForModInfoDataType( modInfo, peptideSequence, dynamicMods, staticMods ) );
		peptideBuilder.setnTerminalMod( PepXMLModificationUtils.getNTerminalModMassDiff( modInfo, dynamicMods ) );
		peptideBuilder.setcTerminalMod( PepXMLModificationUtils.getCTerminalModMassDiff( modInfo, dynamicMods ) );

		return peptideBuilder.createKojakPeptide();
	}

	private int getLinkedPositionForLinkedPeptide( LinkedPeptide linkedPeptide ) throws Exception {

		for( NameValueType nvt : linkedPeptide.getXlinkScore() ) {
			if( nvt.getName().equals( "link" ) ) {
				return Integer.valueOf( nvt.getValueAttribute() );
			}
		}

		throw new Exception( "Could not find linked position for linked peptide." );
	}

	/**
	 * Get the PSM result for the given spectrum query and search hit.
	 *
	 * @param spectrumQuery
	 * @param searchHit
	 * @return
	 * @throws Exception If any of the expected scores are not found
	 */
	private KojakPSMResult getResult(MsmsRunSummary runSummary, SpectrumQuery spectrumQuery, SearchHit searchHit, String spectralFile ) throws Exception {

		KojakPSMResultBuilder psmBuilder = new KojakPSMResultBuilder();


		psmBuilder.setScanFile( new File(spectralFile).getName() );
		psmBuilder.setScanNumber( (int)spectrumQuery.getStartScan() );
		psmBuilder.setCharge( spectrumQuery.getAssumedCharge().intValueExact() );

		// if this is a crosslink or looplink, get the mass of the linker
		int type = PepXMLUtils.getHitType( searchHit );
		if( type == ConverterConstants.LINK_TYPE_CROSSLINK || type == ConverterConstants.LINK_TYPE_LOOPLINK ) {
			Xlink xl = searchHit.getXlink();
			psmBuilder.setLinkerMass( xl.getMass() );
		}

		// get the kojak scores
		for( NameValueType score : searchHit.getSearchScore() ) {
			if( score.getName().equals( KojakConstants.NAME_KOJAK_SCORE ) ) {
				psmBuilder.setKojakScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_DELTA_SCORE ) ) {
				psmBuilder.setDeltaScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_PPM_ERROR ) ) {
				psmBuilder.setPpmError( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_EVALUE ) ) {
				psmBuilder.setEvalue( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_ION_MATCH ) ) {
				psmBuilder.setIonMatch( new BigInteger( score.getValueAttribute() ).intValueExact() );
			}

			else if( score.getName().equals( KojakConstants.NAME_CONSECUTIVE_ION_MATCH ) ) {
				psmBuilder.setConsecutiveIonMatch( new BigInteger( score.getValueAttribute() ).intValueExact() );
			}
		}

		KojakPSMResult psmResult = psmBuilder.createKojakPSMResult();

		if( psmResult.getDeltaScore() == null )
			throw new Exception( "Missing delta score for result: " + spectrumQuery.getSpectrum() );

		if( psmResult.getPpmError() == null )
			throw new Exception( "Missing PPM error for result: " + spectrumQuery.getSpectrum() );

		if( psmResult.getKojakScore() == null )
			throw new Exception( "Missing kojak score for result: " + spectrumQuery.getSpectrum() );

		if( psmResult.getEvalue() == null )
			throw new Exception( "Missing delta score for result: " + spectrumQuery.getSpectrum() );

		return psmResult;

	}

}
