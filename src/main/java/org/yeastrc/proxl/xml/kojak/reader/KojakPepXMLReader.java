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

public class KojakPepXMLReader {

	private static final KojakPepXMLReader _INSTANCE = new KojakPepXMLReader();
	public static KojakPepXMLReader getInstance() { return _INSTANCE; }
	private KojakPepXMLReader() { }

	public KojakResults getResultsFromAnalysis(KojakAnalysis kojakAnalysis) throws Exception {

		Map<KojakReportedPeptide, Collection<KojakPSMResult>> results = new HashMap<>();
		String kojakVersion = null;

		for (File pepXMLFile : kojakAnalysis.getPepXMLFiles()) {

			MsmsPipelineAnalysis analysis = getJaxbRootForPepXMLFile( pepXMLFile );
			if( kojakVersion == null ) {
				kojakVersion = PepXMLUtils.getKojakVersionFromXML( analysis );
			}

			results.putAll(getResultsForPepXMLFile(analysis, kojakAnalysis));
		}

		return new KojakResults( kojakVersion, results );
	}

	private Map<KojakReportedPeptide, Collection<KojakPSMResult>> getResultsForPepXMLFile( MsmsPipelineAnalysis analysis, KojakAnalysis kojakAnalysis) throws Exception {

		Map<KojakReportedPeptide, Collection<KojakPSMResult>> results = new HashMap<>();

		for( MsmsRunSummary runSummary : analysis.getMsmsRunSummary() ) {

			Collection<PepXMLModDefinition> dynamicMods = PepXMLModificationUtils.getDynamicModsForSearch( runSummary );
			dynamicMods.addAll( PepXMLModificationUtils.getNTerminalDynamicModsForSearch( runSummary ) );
			dynamicMods.addAll( PepXMLModificationUtils.getCTerminalDynamicModsForSearch( runSummary ) );

			Collection<PepXMLModDefinition> staticMods = PepXMLModificationUtils.getStaticModsForSearch( runSummary );

			String decoyFilter = PepXMLRunSummaryUtils.getDecoyFilter( runSummary );
			String spectralFile = PepXMLRunSummaryUtils.getMSDataFile( runSummary );
			String n15Prefix = PepXMLRunSummaryUtils.get15NPrefix( runSummary );

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
							KojakPSMResult result = getResult(spectrumQuery, searchHit, spectralFile, dynamicMods, staticMods, n15Prefix );

							// get our reported peptide
							KojakReportedPeptide reportedPeptide = getReportedPeptide(searchHit, dynamicMods, staticMods, n15Prefix);

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
	private KojakReportedPeptide getReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix ) throws Exception {

		int type = PepXMLUtils.getHitType( searchHit );

		if( type == ConverterConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit, dynamicMods, staticMods, n15Prefix );

		if( type == ConverterConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit, dynamicMods, staticMods, n15Prefix );

		return getUnlinkedReportedPeptide( searchHit, dynamicMods, staticMods, n15Prefix );

	}

	/**
	 * Get the IProphetReportedPeptide for a crosslink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getCrosslinkReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix ) throws Exception {

		List<KojakPeptide> kojakPeptides = new ArrayList<>( 2 );

		for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {

			if( kojakPeptides.size() > 2 ) {
				throw new Exception( "Got more than two linked peptides." );
			}

			kojakPeptides.add( getPeptideFromLinkedPeptide( linkedPeptide, dynamicMods, staticMods, n15Prefix ) );
		}

		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_CROSSLINK, kojakPeptides.get( 0 ), kojakPeptides.get( 1 ) );
	}

	/**
	 * Get the IProphetReportedPeptide for a looplink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getLooplinkReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix  ) throws Exception {

		KojakPeptide kojakPeptide = getPeptideFromSearchHit( searchHit, dynamicMods, staticMods, n15Prefix );
		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_LOOPLINK, kojakPeptide, null );
	}

	/**
	 * Get the IProphetReportedPeptide for an unlinked result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakReportedPeptide getUnlinkedReportedPeptide(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix ) throws Exception {

		KojakPeptide kojakPeptide = getPeptideFromSearchHit( searchHit, dynamicMods, staticMods, n15Prefix );
		return new KojakReportedPeptide( ConverterConstants.LINK_TYPE_UNLINKED, kojakPeptide, null );
	}



	/**
	 * Get the KojakPeptide from the searchHit. Includes the peptide sequence and any mods.
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakPeptide getPeptideFromSearchHit(SearchHit searchHit, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix ) throws Exception {

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

		if( n15Prefix != null && PepXMLStableIsotopeUtils.isSearchHit15NLabeled( searchHit, n15Prefix ) ) {
			peptideBuilder.setN15Label( n15Prefix );
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
	private KojakPeptide getPeptideFromLinkedPeptide(LinkedPeptide linkedPeptide, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods, String n15Prefix ) throws Exception {


		KojakPeptideBuilder peptideBuilder = new KojakPeptideBuilder();

		String peptideSequence = linkedPeptide.getPeptide();
		peptideBuilder.setSequence( peptideSequence );

		peptideBuilder.setPosition1( getLinkedPositionForLinkedPeptide( linkedPeptide ) );

		ModInfoDataType modInfo = linkedPeptide.getModificationInfo();
		peptideBuilder.setModifications( PepXMLModificationUtils.getDynamicModMapForModInfoDataType( modInfo, peptideSequence, dynamicMods, staticMods ) );
		peptideBuilder.setnTerminalMod( PepXMLModificationUtils.getNTerminalModMassDiff( modInfo, dynamicMods ) );
		peptideBuilder.setcTerminalMod( PepXMLModificationUtils.getCTerminalModMassDiff( modInfo, dynamicMods ) );

		if( n15Prefix != null && PepXMLStableIsotopeUtils.isLinkedPeptide15NLabeled( linkedPeptide, n15Prefix ) ) {
			peptideBuilder.setN15Label( n15Prefix );
		}

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
	private KojakPSMResult getResult(SpectrumQuery spectrumQuery,
									 SearchHit searchHit,
									 String spectralFile,
									 Collection<PepXMLModDefinition> dynamicMods,
									 Collection<PepXMLModDefinition> staticMods,
									 String n15Prefix ) throws Exception {

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
			if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_KOJAK_SCORE) ) {
				psmBuilder.setKojakScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_DELTA_SCORE) ) {
				psmBuilder.setDeltaScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_PPM_ERROR) ) {
				psmBuilder.setPpmError( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_EVALUE) ) {
				psmBuilder.setEvalue( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_ION_MATCH) ) {
				psmBuilder.setIonMatch( new BigInteger( score.getValueAttribute() ).intValueExact() );
			}

			else if( score.getName().equals( KojakConstants.PSM_SCORE_NAME_CONSECUTIVE_ION_MATCH) ) {
				psmBuilder.setConsecutiveIonMatch( new BigInteger( score.getValueAttribute() ).intValueExact() );
			}
		}

		// get the per-peptide PSM scores for cross-links
		if( type == ConverterConstants.LINK_TYPE_CROSSLINK  ) {
			Collection<KojakPerPeptidePSM> kojakPerPeptidePSMs = getKojakPerPeptidePSMs(
					searchHit,
					dynamicMods,
					staticMods,
					n15Prefix
			);

			psmBuilder.setPerPeptidePSMResults( kojakPerPeptidePSMs );
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


	private Collection<KojakPerPeptidePSM> getKojakPerPeptidePSMs( SearchHit searchHit,
																   Collection<PepXMLModDefinition> dynamicMods,
																   Collection<PepXMLModDefinition> staticMods,
																   String n15Prefix ) throws Exception {

		Collection<KojakPerPeptidePSM> kojakPerPeptidePSMs = new ArrayList<>( 2 );

		Xlink xlink = searchHit.getXlink();
		if( xlink == null ) {
			throw new Exception( "Did not get cross-link for searchHit when getting peptide PSM info..." );
		}

		for( LinkedPeptide linkedPeptide : xlink.getLinkedPeptide() ) {

			KojakPeptide kojakPeptide = getPeptideFromLinkedPeptide( linkedPeptide, dynamicMods, staticMods, n15Prefix  );

			KojakPerPeptidePSMBuilder builder = new KojakPerPeptidePSMBuilder();
			builder.setLinkedPeptide( kojakPeptide );

			for( NameValueType nvt : linkedPeptide.getXlinkScore() ) {

				if( nvt.getName().equals( KojakConstants.PSM_PER_PEPTIDE_SCORE_NAME_SCORE ) ) {
					builder.setKojakScore( new BigDecimal( nvt.getValueAttribute() ) );
				}

				if( nvt.getName().equals( KojakConstants.PSM_PER_PEPTIDE_SCORE_NAME_RANK ) ) {
					builder.setRank( Integer.valueOf( nvt.getValueAttribute() ) );
				}

				if( nvt.getName().equals( KojakConstants.PSM_PER_PEPTIDE_SCORE_NAME_EVALUE ) ) {
					builder.setEvalue( new BigDecimal( nvt.getValueAttribute() ) );
				}

				if( nvt.getName().equals( KojakConstants.PSM_PER_PEPTIDE_SCORE_NAME_ION_MATCH ) ) {
					builder.setIonMatch( Integer.valueOf( nvt.getValueAttribute() ) );
				}

				if( nvt.getName().equals( KojakConstants.PSM_PER_PEPTIDE_SCORE_NAME_CONSECUTIVE_ION_MATCH ) ) {
					builder.setConsecutiveIonMatch( Integer.valueOf( nvt.getValueAttribute() ) );
				}
			}

			KojakPerPeptidePSM perPeptidePSM = builder.createKojakPerPeptidePSM();

			if( perPeptidePSM.getKojakScore() == null )
				throw new Exception( "Missing score for per peptide PSM." );

			if( perPeptidePSM.getEvalue() == null )
				throw new Exception( "Missing evalue for per peptide PSM." );

			kojakPerPeptidePSMs.add( builder.createKojakPerPeptidePSM() );
		}

		if( kojakPerPeptidePSMs.size() !=2 ) {
			throw new Exception( "Did not get 2 linked peptides when parsing per peptide scores." );
		}

		return kojakPerPeptidePSMs;
	}

}
