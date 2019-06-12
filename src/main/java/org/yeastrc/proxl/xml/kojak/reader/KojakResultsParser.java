package org.yeastrc.proxl.xml.kojak.reader;

import net.systemsbiology.regis_web.pepxml.*;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;

import java.math.BigDecimal;
import java.util.*;

public class KojakResultsParser {

	private static final KojakResultsParser _INSTANCE = new KojakResultsParser();
	public static KojakResultsParser getInstance() { return _INSTANCE; }
	private KojakResultsParser() { }

	/**
	 * Get the results of the analysis back in the form used by proxl:
	 * reported peptides are the keys, and all of the PSMs (and their scores)
	 * that reported that peptide are the values.
	 *
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	public Map<KojakReportedPeptide, Collection<IProphetResult>> getResultsFromAnalysis( IProphetAnalysis analysis ) throws Exception {

		Map<IProphetReportedPeptide, Collection<IProphetResult>> results = new HashMap<IProphetReportedPeptide, Collection<IProphetResult>>();

		for( MsmsRunSummary runSummary : analysis.getAnalysis().getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
							if( analysisResult.getAnalysis().equals( "interprophet" ) ) {

								// only one interprophet result will appear for a search hit, and we are only
								// interested in search hits with an interprophet result.

								// skip this if it's a decoy
								if( PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) )
									continue;

								// get our result
								IProphetResult result = getResult( runSummary, spectrumQuery, searchHit );

								// get our reported peptide
								IProphetReportedPeptide reportedPeptide = getReportedPeptide( searchHit, analysis );

								if( !results.containsKey( reportedPeptide ) )
									results.put( reportedPeptide, new ArrayList<IProphetResult>() );

								results.get( reportedPeptide ).add( result );

								/*
								 * Kojak reports leucine/isoleucine variations as individual peptide matches in its results
								 * file as tied as rank 1 hits to a spectrum. This is preferred by proxl, however, peptideprophet
								 * and iprophet only score a single rank 1 hit for a spectrum. If we only keep the peptide that
								 * iprophet scored, we may lose valuable information if the leucine->isoleucine variant of that
								 * peptide matched proteins of interest in the FASTA file.
								 *
								 * To address this, iterate over the other search hits for this search result, and keep all other
								 * rank 1 hits that are merely leucine/isoleucine substitutions of the scored rank 1 hit.
								 */
								Collection<IProphetReportedPeptide> otherReportedPeptides = getAllLeucineIsoleucineSubstitutions( reportedPeptide, searchResult, analysis );

								for( IProphetReportedPeptide otherReportedPeptide : otherReportedPeptides ) {
									if( !results.containsKey( otherReportedPeptide ) )
										results.put( otherReportedPeptide, new ArrayList<IProphetResult>() );

									results.get( otherReportedPeptide ).add( result );
								}

							}
						}
					}
				}
			}
		}

		/*
		 * Because it is impossible to know if a reported peptide only maps to decoys or not in peptideprophet results
		 * (since it also lists all proteins that match leucine/isoleucine substitutions as protein hits for a peptide)
		 * we need to confirm whether or not the reported peptides whose leucine/isoleucine substitutions matched
		 * proteins in the FASTA file exclusively match to decoys or not. If they do, remove them.
		 */

		Collection<IProphetReportedPeptide> reportedPeptidesToConfirm = new HashSet<>();
		reportedPeptidesToConfirm.addAll( results.keySet() );

		if( reportedPeptidesToConfirm.size() > 0 ) {

			// collection of all protein names we need to confirm
			Collection<String> proteinNames = new HashSet<>();

			// cache the relevant protein sequences
			Map<String, String> proteinSequences = new HashMap<>();

			for( IProphetReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {
				proteinNames.addAll( reportedPeptide.getPeptide1().getTargetProteins() );
				if( reportedPeptide.getPeptide2() != null )
					proteinNames.addAll( reportedPeptide.getPeptide2().getTargetProteins() );
			}

			// build the cache of protein sequences
			FASTAReader reader = null;
			try {
				reader = FASTAReader.getInstance( analysis.getFastaFile() );

		        FASTAEntry entry = reader.readNext();
		        while( entry != null ) {

		        	for( FASTAHeader header : entry.getHeaders() ) {

		        		for( String testString : proteinNames ) {
		        			if( header.getName().startsWith( testString ) ) {
		        				proteinSequences.put( header.getName(), entry.getSequence() );
		        			}
		        		}

		        	}
		            entry = reader.readNext();
		        }

			} finally {
				if( reader != null ){
					reader.close();
					reader = null;
				}
			}

			// now have cache of relevant protein names and sequences. iterate over the reportedPeptidesToConfirm and
			// remove associated proteins from peptides where that peptide is not actually found in that protein
			for( IProphetReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {

				for (Iterator<String> i = reportedPeptide.getPeptide1().getTargetProteins().iterator(); i.hasNext();) {
					String protein = i.next();
					boolean foundProtein = false;

					for( String cachedProteinName : proteinSequences.keySet() ) {
						if( cachedProteinName.startsWith( protein ) ) {
							if( proteinSequences.get( cachedProteinName ).toLowerCase().contains( reportedPeptide.getPeptide1().getSequence().toLowerCase() ) )
								foundProtein = true;
						}
					}

					if( !foundProtein )
						i.remove();

				}


				if( reportedPeptide.getType() == IProphetConstants.LINK_TYPE_CROSSLINK ) {

					for (Iterator<String> i = reportedPeptide.getPeptide2().getTargetProteins().iterator(); i.hasNext();) {
						String protein = i.next();
						boolean foundProtein = false;

						for( String cachedProteinName : proteinSequences.keySet() ) {
							if( cachedProteinName.startsWith( protein ) ) {
								if( proteinSequences.get( cachedProteinName ).toLowerCase().contains( reportedPeptide.getPeptide2().getSequence().toLowerCase() ) )
									foundProtein = true;
							}
						}

						if( !foundProtein )
							i.remove();

					}

				}

			}

			// now we can iterate over the reportedPeptidesToConfirm and remove any from our results where there are 0
			// targetProteins left for a peptide
			for( IProphetReportedPeptide reportedPeptide : reportedPeptidesToConfirm ) {

				if( reportedPeptide.getPeptide1().getTargetProteins().size() < 1 ) {
					System.out.println( "INFO: Removing " + reportedPeptide + " from results, does not match a target protein." );
					results.remove( reportedPeptide );
				}

				else if( reportedPeptide.getType() == IProphetConstants.LINK_TYPE_CROSSLINK && reportedPeptide.getPeptide2().getTargetProteins().size() < 1) {
					System.out.println( "INFO: Removing " + reportedPeptide + " from results, does not match a target protein." );
					results.remove( reportedPeptide );
				}

			}



		}

		return results;
	}

	/**
	 * For a given reported peptide, find all other reported peptides that are leucine<->isoleucine
	 * substituitions of that reported peptide, where those substutions match a  protein
	 * in the FASTA file. Since Kojak reports all such peptides as separate PSMs, this is done by
	 * iterating over the other rank 1 hits for a search result and getting those hits that are
	 * merely leucine<->isoleucine substitutions of the reported peptide. These are guaranteed to
	 * hit a protein in the FASTA file, otherwise Kojak would not have reported them.
	 *
	 * @param reportedPeptide
	 * @param searchResult
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<IProphetReportedPeptide>  getAllLeucineIsoleucineSubstitutions(IProphetReportedPeptide reportedPeptide, SearchResult searchResult, IProphetAnalysis analysis ) throws Exception {

		//System.out.println( "Calling getAllLeucineIsoleucineSubstitutions()" );

		Collection<IProphetReportedPeptide> reportedPeptides = new HashSet<IProphetReportedPeptide>();

		for( SearchHit otherSearchHit : searchResult.getSearchHit() ) {
			IProphetReportedPeptide otherReportedPeptide = getReportedPeptide( otherSearchHit, analysis );

			// if they're not the same type, there's no match
			if( reportedPeptide.getType() != otherReportedPeptide.getType() )
				continue;

			// don't return the same reported peptide that was passed in
			if( reportedPeptide.equals( otherReportedPeptide ) ) continue;

			// perform test by substitution all Is and Ls with =s and comparing for string equality
			String testSequence = reportedPeptide.toString();
			testSequence = testSequence.replaceAll( "I", "=" );
			testSequence = testSequence.replaceAll( "L", "=" );

			String otherSequence = otherReportedPeptide.toString();
			otherSequence = otherSequence.replaceAll( "I", "=" );
			otherSequence = otherSequence.replaceAll( "L", "=" );


			if( testSequence.equals( otherSequence ) ) {

				//System.out.println( "Adding " + otherReportedPeptide );

				reportedPeptides.add( otherReportedPeptide );
			} else {

				if( otherReportedPeptide.getType() == IProphetConstants.LINK_TYPE_CROSSLINK ) {

					// if we're testing a crosslink, be sure to test the other possible arrangement of peptides 1 and 2

					// switch peptides 1 and 2
					KojakPeptide tmpPeptide = otherReportedPeptide.getPeptide1();
					otherReportedPeptide.setPeptide1( otherReportedPeptide.getPeptide2() );
					otherReportedPeptide.setPeptide2( tmpPeptide );

					otherSequence = otherReportedPeptide.toString();
					otherSequence = otherSequence.replaceAll( "I", "=" );
					otherSequence = otherSequence.replaceAll( "L", "=" );

					if( testSequence.equals( otherSequence ) ) {

						// switch back
						otherReportedPeptide.setPeptide2( otherReportedPeptide.getPeptide1() );
						otherReportedPeptide.setPeptide1( tmpPeptide );

						reportedPeptides.add( otherReportedPeptide );
					}

				}

			}

		}


		return reportedPeptides;
	}

	/**
	 * For a given search hit, return a collection of strings that are target protein names
	 * reported by iProphet. Decoy names are filtered out.
	 *
	 * @param searchHit
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<String> getTargetProteinsForSearchHit(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {
		Collection<String> targetProteins = new HashSet<>();

		String protein = searchHit.getProtein();
		if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), protein ) )
			targetProteins.add( protein );

		if( searchHit.getAlternativeProtein() != null ) {
			for( AltProteinDataType altProtein  : searchHit.getAlternativeProtein() ) {
				if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), altProtein.getProtein() ) )
					targetProteins.add( altProtein.getProtein() );
			}
		}

		return targetProteins;
	}

	/**
	 * For a given search hit, return a collection of strings that are target protein names
	 * reported by iProphet. Decoy names are filtered out.
	 *
	 * @param linkedPeptide
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private Collection<String> getTargetProteinsForLinkedPeptide(LinkedPeptide linkedPeptide, IProphetAnalysis analysis ) throws Exception {
		Collection<String> targetProteins = new HashSet<>();

		String protein = linkedPeptide.getProtein();
		if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), protein ) )
			targetProteins.add( protein );

		if( linkedPeptide.getAlternativeProtein() != null ) {
			for( AltProteinDataType altProtein  : linkedPeptide.getAlternativeProtein() ) {
				if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(), altProtein.getProtein() ) )
					targetProteins.add( altProtein.getProtein() );
			}
		}

		return targetProteins;
	}


	/**
	 * Get the IProphetReportedPeptide for the given SearchHit
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getReportedPeptide(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {

		int type = PepXMLUtils.getHitType( searchHit );

		if( type == IProphetConstants.LINK_TYPE_CROSSLINK )
			return getCrosslinkReportedPeptide( searchHit, analysis );

		if( type == IProphetConstants.LINK_TYPE_LOOPLINK )
			return getLooplinkReportedPeptide( searchHit, analysis );

		return getUnlinkedReportedPeptide( searchHit, analysis );

	}

	/**
	 * Get the IProphetReportedPeptide for a crosslink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getCrosslinkReportedPeptide(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {

		//System.out.println( searchHit.getPeptide() );
		//System.out.println( "\t" + searchHit.getXlinkType() );

		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_CROSSLINK );

		for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {

			int peptideNumber = 0;
			if( reportedPeptide.getPeptide1() == null ) {
				peptideNumber = 1;
			} else if( reportedPeptide.getPeptide2() == null ) {
				peptideNumber = 2;
			} else {
				throw new Exception( "Got more than two linked peptides." );
			}


			//System.out.println( "\t\t" + linkedPeptide.getPeptide() );
			//System.out.println( "\t\tpeptide num: " + peptideNumber );

			KojakPeptide peptide = getPeptideFromLinkedPeptide( linkedPeptide, analysis );
			int position = 0;

			for( NameValueType nvt : linkedPeptide.getXlinkScore() ) {

				if( nvt.getName().equals( "link" ) ) {

					//System.out.println( "\t\t" + nvt.getValueAttribute() );

					if( position == 0 )
						position = Integer.valueOf( nvt.getValueAttribute() );
					else
						throw new Exception( "Got more than one linked position in peptide." );
				}
			}

			if( position == 0 )
				throw new Exception( "Could not find linked position in peptide." );


			if( peptideNumber == 1 ) {
				reportedPeptide.setPeptide1( peptide );
				reportedPeptide.setPosition1( position );
			} else {
				reportedPeptide.setPeptide2( peptide );
				reportedPeptide.setPosition2( position );
			}


		}


		// ensure peptides and positions are consistently ordered so that any two reported peptides containing the same
		// two peptides and linked positions are recognized as the same

		if( reportedPeptide.getPeptide1().toString().compareTo( reportedPeptide.getPeptide2().toString() ) > 0 ) {

			// swap them
			KojakPeptide tpep = reportedPeptide.getPeptide1();
			int tpos = reportedPeptide.getPosition1();

			reportedPeptide.setPeptide1( reportedPeptide.getPeptide2() );
			reportedPeptide.setPosition1( reportedPeptide.getPosition2() );

			reportedPeptide.setPeptide2( tpep );
			reportedPeptide.setPosition2( tpos );
		} else if( reportedPeptide.getPeptide1().toString().compareTo( reportedPeptide.getPeptide2().toString() ) == 0 ) {

			// peptides are the same, should we swap positions?
			if( reportedPeptide.getPosition1() > reportedPeptide.getPosition2() ) {
				int tpos = reportedPeptide.getPosition1();

				reportedPeptide.setPosition1( reportedPeptide.getPosition2() );
				reportedPeptide.setPosition2( tpos );
			}

		}

		return reportedPeptide;
	}

	/**
	 * Get the IProphetReportedPeptide for a looplink result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getLooplinkReportedPeptide(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {

		//System.out.println( searchHit.getPeptide() );
		//System.out.println( "\t" + searchHit.getXlinkType() );


		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();

		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_LOOPLINK );

		// add in the linked positions
		Xlink xl = searchHit.getXlink();

		for( NameValueType nvt : xl.getXlinkScore() ) {
			if( nvt.getName().equals( "link" ) ) {

				//System.out.println( "\t\t" + nvt.getValueAttribute() );

				if( reportedPeptide.getPosition1() == 0 )
					reportedPeptide.setPosition1( Integer.valueOf( nvt.getValueAttribute() ) );
				else if( reportedPeptide.getPosition2() == 0 )
					reportedPeptide.setPosition2( Integer.valueOf( nvt.getValueAttribute() ) );
				else
					throw new Exception( "Got more than 2 linked positions for looplink." );
			}
		}

		if( reportedPeptide.getPosition1() == 0 || reportedPeptide.getPosition2() == 0 )
			throw new Exception( "Did not get two positions for looplink." );

		if( reportedPeptide.getPosition1() > reportedPeptide.getPosition2() ) {
			int tpos = reportedPeptide.getPosition1();

			reportedPeptide.setPosition1( reportedPeptide.getPosition2() );
			reportedPeptide.setPosition2( tpos );
		}


		return reportedPeptide;
	}

	/**
	 * Get the IProphetReportedPeptide for an unlinked result
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private IProphetReportedPeptide getUnlinkedReportedPeptide(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {

		IProphetReportedPeptide reportedPeptide = new IProphetReportedPeptide();

		reportedPeptide.setPeptide1( getPeptideFromSearchHit( searchHit, analysis ) );
		reportedPeptide.setType( IProphetConstants.LINK_TYPE_UNLINKED );

		return reportedPeptide;
	}

	/**
	 * Get the KojakPeptide from the searchHit. Includes the peptide sequence and any mods.
	 *
	 * @param searchHit
	 * @return
	 * @throws Exception
	 */
	private KojakPeptide getPeptideFromSearchHit(SearchHit searchHit, IProphetAnalysis analysis ) throws Exception {

		KojakPeptide peptide = new KojakPeptide();

		peptide.setSequence( searchHit.getPeptide() );

		peptide.setTargetProteins( getTargetProteinsForSearchHit( searchHit, analysis ) );

		ModInfoDataType modInfo = searchHit.getModificationInfo();

		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();

			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {

				int position = mam.getPosition().intValue();
				String residue = peptide.getSequence().substring( position - 1, position );

				double massDifferenceDouble = mam.getMass() - KojakConstants.AA_MASS.get( residue );
				BigDecimal massDifference = BigDecimal.valueOf( massDifferenceDouble );
				massDifference = massDifference.setScale( 6, BigDecimal.ROUND_HALF_UP );

				// don't add static mods as mods
				if( ModUtils.isStaticMod(residue, massDifference, analysis.getKojakConfReader() ) )
					continue;

				if( !mods.containsKey( position ) )
					mods.put( position, new HashSet<BigDecimal>() );

				mods.get( position ).add( massDifference );
			}

			peptide.setModifications( mods );
		}

		return peptide;
	}

	/**
	 * Get the KojakPeptide from the searchHit. Includes the peptide sequence and any mods.
	 *
	 * @param linkedPeptide
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private KojakPeptide getPeptideFromLinkedPeptide(LinkedPeptide linkedPeptide, IProphetAnalysis analysis ) throws Exception {

		KojakPeptide peptide = new KojakPeptide();

		peptide.setSequence( linkedPeptide.getPeptide() );

		peptide.setTargetProteins( getTargetProteinsForLinkedPeptide( linkedPeptide, analysis ) );


		ModInfoDataType modInfo = linkedPeptide.getModificationInfo();

		if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {
			Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();

			for( ModAminoacidMass mam : modInfo.getModAminoacidMass() ) {

				int position = mam.getPosition().intValue();
				String residue = peptide.getSequence().substring( position - 1, position );

				double massDifferenceDouble = mam.getMass() - KojakConstants.AA_MASS.get( residue );
				BigDecimal massDifference = BigDecimal.valueOf( massDifferenceDouble );
				massDifference = massDifference.setScale( 6, BigDecimal.ROUND_HALF_UP );

				// don't add static mods as mods
				if( ModUtils.isStaticMod(residue, massDifference, analysis.getKojakConfReader() ) )
					continue;

				if( !mods.containsKey( position ) )
					mods.put( position, new HashSet<BigDecimal>() );

				mods.get( position ).add( massDifference );
			}

			peptide.setModifications( mods );
		}

		return peptide;
	}


	/**
	 * Get the PSM result for the given spectrum query and search hit.
	 *
	 * @param spectrumQuery
	 * @param searchHit
	 * @return
	 * @throws Exception If any of the expected scores are not found
	 */
	private IProphetResult getResult(MsmsRunSummary runSummary, SpectrumQuery spectrumQuery, SearchHit searchHit ) throws Exception {

		IProphetResult result = new IProphetResult();

		result.setScanFile( ScanParsingUtils.getFilenameFromReportedScan( spectrumQuery.getSpectrum() ) + runSummary.getRawData() );



		result.setScanNumber( (int)spectrumQuery.getStartScan() );
		result.setCharge( spectrumQuery.getAssumedCharge().intValue() );

		// if this is a crosslink or looplink, get the mass of the linker
		int type = PepXMLUtils.getHitType( searchHit );
		if( type == IProphetConstants.LINK_TYPE_CROSSLINK || type == IProphetConstants.LINK_TYPE_LOOPLINK ) {
			Xlink xl = searchHit.getXlink();
			result.setLinkerMass( xl.getMass() );
		}

		// get the kojak scores
		for( NameValueType score : searchHit.getSearchScore() ) {
			if( score.getName().equals( KojakConstants.NAME_KOJAK_SCORE ) ) {
				result.setKojakScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_DELTA_SCORE ) ) {
				result.setDeltaScore( new BigDecimal( score.getValueAttribute() ) );
			}

			else if( score.getName().equals( KojakConstants.NAME_PPM_ERROR ) ) {
				result.setPpmError( new BigDecimal( score.getValueAttribute() ) );
			}
		}

		// get the scores for peptideprophet and iprophet
		for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {

			if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
				InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny().get( 0 );
				result.setInterProphetScore( ipresult.getProbability() );
			}

			else if( analysisResult.getAnalysis().equals( "peptideprophet" ) ) {
				PeptideprophetResult ppresult = (PeptideprophetResult) analysisResult.getAny().get( 0 );
				result.setPeptideProphetScore( ppresult.getProbability() );
			}
		}


		if( result.getDeltaScore() == null )
			throw new Exception( "Missing delta score for result: " + spectrumQuery.getSpectrum() );

		if( result.getPpmError() == null )
			throw new Exception( "Missing PPM error for result: " + spectrumQuery.getSpectrum() );

		if( result.getKojakScore() == null )
			throw new Exception( "Missing kojak score for result: " + spectrumQuery.getSpectrum() );

		if( result.getInterProphetScore() == null )
			throw new Exception( "Missing iprophet score for result: " + spectrumQuery.getSpectrum() );

		if( result.getPeptideProphetScore() == null )
			throw new Exception( "Missing peptideprophet score for result: " + spectrumQuery.getSpectrum() );


		return result;

	}

}
