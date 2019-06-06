//package org.yeastrc.proxl.xml.kojak.reader;
//
//import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
//import net.systemsbiology.regis_web.pepxml.InterprophetResult;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.AnalysisResult;
//import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.Xlink.LinkedPeptide;
//import org.yeastrc.proxl.xml.iprophet.constants.IProphetConstants;
//import org.yeastrc.proxl.xml.iprophet.utils.PepXMLUtils;
//
//import java.math.BigDecimal;
//import java.util.Collection;
//import java.util.HashSet;
//
///**
// * Find all protein names in the results
// *
// * @author mriffle
// *
// */
//public class IProphetProteinNameCollector {
//
//	private static final IProphetProteinNameCollector _INSTANCE = new IProphetProteinNameCollector();
//	public static IProphetProteinNameCollector getInstance() { return _INSTANCE; }
//	private IProphetProteinNameCollector() { }
//
//	public Collection<String> getProteinNames( IProphetAnalysis analysis ) throws Exception {
//
//		Collection<String> proteinNames = new HashSet<String>();
//
//		for( MsmsRunSummary runSummary : analysis.getAnalysis().getMsmsRunSummary() ) {
//			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
//				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
//					for( SearchHit searchHit : searchResult.getSearchHit() ) {
//						for( AnalysisResult analysisResult : searchHit.getAnalysisResult() ) {
//							if( analysisResult.getAnalysis().equals( "interprophet" ) ) {
//
//								// only one interprophet result will appear for a search hit, and we are only
//								// interested in search hits with an interprophet result.
//
//								// skip this if it's a decoy
//								if( PepXMLUtils.isDecoy( analysis.getDecoyIdentifiers(), searchHit) )
//									continue;
//
//								// skip if the score is 0, we don't import these
//								InterprophetResult ipresult = (InterprophetResult) analysisResult.getAny().get( 0 );
//								if( ipresult.getProbability().compareTo( new BigDecimal( "0" ) ) == 0 )
//									continue;
//
//								if( PepXMLUtils.getHitType( searchHit ) == IProphetConstants.LINK_TYPE_CROSSLINK ) {
//
//									for( LinkedPeptide linkedPeptide : searchHit.getXlink().getLinkedPeptide() ) {
//
//										Collection<String> proteinsToAdd = new HashSet<>();
//
//										if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(),  linkedPeptide.getProtein() ) )
//											proteinsToAdd.add( linkedPeptide.getProtein() );
//
//										if( linkedPeptide.getAlternativeProtein() != null ) {
//											for( AltProteinDataType altProtein : linkedPeptide.getAlternativeProtein() ) {
//												if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(),  altProtein.getProtein() ) )
//													proteinsToAdd.add( altProtein.getProtein() );
//											}
//										}
//
//										if( proteinsToAdd.size() < 1 ) {
//
//											String hitSummary = "";
//
//											hitSummary += "protein: " + linkedPeptide.getProtein() + "\n";
//
//											if( searchHit.getAlternativeProtein() != null ) {
//												for( AltProteinDataType altProtein : linkedPeptide.getAlternativeProtein() ) {
//													hitSummary += "alt protein: " + altProtein.getProtein() + "\n";
//												}
//											}
//
//											throw new Exception( "Got no proteins to add for this target hit: " + hitSummary );
//										}
//
//										proteinNames.addAll( proteinsToAdd );
//									}
//
//								} else {
//
//									// for looplinks and unlinkeds
//
//									Collection<String> proteinsToAdd = new HashSet<>();
//
//									if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(),  searchHit.getProtein() ) )
//										proteinsToAdd.add( searchHit.getProtein() );
//
//									if( searchHit.getAlternativeProtein() != null ) {
//										for( AltProteinDataType altProtein : searchHit.getAlternativeProtein() ) {
//
//											if( !PepXMLUtils.isDecoyName( analysis.getDecoyIdentifiers(),  altProtein.getProtein() ) )
//												proteinsToAdd.add( altProtein.getProtein() );
//										}
//									}
//
//									if( proteinsToAdd.size() < 1 ) {
//
//										String hitSummary = "";
//
//										hitSummary += "protein: " + searchHit.getProtein() + "\n";
//
//										if( searchHit.getAlternativeProtein() != null ) {
//											for( AltProteinDataType altProtein : searchHit.getAlternativeProtein() ) {
//												hitSummary += "alt protein: " + altProtein.getProtein() + "\n";
//											}
//										}
//
//										throw new Exception( "Got no proteins to add for this target hit: " + hitSummary );
//									}
//
//									proteinNames.addAll( proteinsToAdd );
//
//								}
//
//
//
//
//							}
//						}
//					}
//				}
//			}
//		}
//
//		return proteinNames;
//
//	}
//
//}
