package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.fasta;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * 
 *
 */
public class AddProteinsFromFASTAFileUsingPeptideSequence {

	private static final Logger log = Logger.getLogger( AddProteinsFromFASTAFileUsingPeptideSequence.class );

	/**
	 * private constructor
	 */
	private AddProteinsFromFASTAFileUsingPeptideSequence(){}
	
	public static AddProteinsFromFASTAFileUsingPeptideSequence getInstance(  )  { 
		
		return new AddProteinsFromFASTAFileUsingPeptideSequence();
	}
	
	
	/**
	 * @param proxlInputRoot
	 * @param fastaFileWithPathFile
	 * @throws Exception
	 */
	public void addProteinsFromFASTAFile( ProxlInput proxlInputRoot, File fastaFileWithPathFile, Collection<String> decoyIdentificationStringFromConfFileList ) throws Exception {
		

		
		//  Copy all peptide sequences to Set
		
		Set<String> allPetpideSequences = new HashSet<>();
		
		ReportedPeptides reportedPeptides = proxlInputRoot.getReportedPeptides();
		
		if ( reportedPeptides != null ) {

			List<ReportedPeptide> reportedPeptideList = reportedPeptides.getReportedPeptide();

			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {

				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {

					if ( reportedPeptides != null ) {

						Peptides peptidesProxlXML = reportedPeptide.getPeptides();

						List<Peptide> peptideProxlXMLList = peptidesProxlXML.getPeptide();

						if ( peptideProxlXMLList != null && ( ! peptideProxlXMLList.isEmpty() ) ) {


							for ( Peptide peptideProxlXML : peptideProxlXMLList ) {

								allPetpideSequences.add( peptideProxlXML.getSequence() );
							}
						}
					}
				}
			}
		}
		
		Map<String, Protein> proxlXMLProteinsKeyedByProteinSequenceMap = new HashMap<>();
		Map<String, DataByFastaHeaderName> dataKeyedByFASTA_Header_Name_Map = new HashMap<>();
		
					
		if ( ! allPetpideSequences.isEmpty() ) {

			

			FASTAReader fastaReader = null;

			//		fastaFileWithPathFile

			try {

				fastaReader = FASTAReader.getInstance( fastaFileWithPathFile );
				
				//  Loop forever, break inside loop when no more entries found
				while ( true ) {

					FASTAEntry fastaEntry = null;
				
					try {
						
						fastaEntry = fastaReader.readNext();
						
					} catch ( Exception e ) {
						
						
						throw e;
					}
					
					if ( fastaEntry == null ) {
				
						//  No more entries found so exit loop
						
						break;  //  EARLY LOOP EXIT
					}

					Set<FASTAHeader> headers = fastaEntry.getHeaders();

					if ( ! IsAllFASTAHeadersDecoy.getInstance().isAllFASTAHeadersDecoy( headers, decoyIdentificationStringFromConfFileList ) ) {

						//			fastaEntry.getHeaderLine();
						//			fastaEntry.getHeaderLineNumber();
						//			fastaEntry.getHeaders();
						//			fastaEntry.getSequence();

						String proteinSequence = fastaEntry.getSequence();

						boolean proteinSequenceContainsPeptideSequence = false;

						for ( String peptideSequence : allPetpideSequences ) {

							if ( proteinSequence.contains( peptideSequence ) ) {

								proteinSequenceContainsPeptideSequence = true;
								break;
							}
						}

						if ( proteinSequenceContainsPeptideSequence ) {

							

							//  Determine if protein sequence already added, and use that Protein object if it is
							
							Protein protein = proxlXMLProteinsKeyedByProteinSequenceMap.get( proteinSequence );
							
							if ( protein == null ) {

								MatchedProteins matchedProteins = proxlInputRoot.getMatchedProteins();

								if ( matchedProteins == null ) {

									matchedProteins = new MatchedProteins();
									proxlInputRoot.setMatchedProteins( matchedProteins );
								}

								List<Protein> proteinList = matchedProteins.getProtein();

								protein = new Protein();

								proteinList.add( protein );

								protein.setSequence( proteinSequence );
							}

							
							//  Validate that this header name does not already exist in file 
							//  or has same protein sequence, header name, and header description
							//  Throws exception if error
							validateHeaderNames( headers, proteinSequence, dataKeyedByFASTA_Header_Name_Map );
							
							
							
							proxlXMLProteinsKeyedByProteinSequenceMap.put(proteinSequence, protein);

							List<ProteinAnnotation> proteinAnnotationList = protein.getProteinAnnotation();

							for ( FASTAHeader fastaHeader : headers ) {
								
								if ( ! isProteinHeaderInProxlXMLProtein( fastaHeader, proteinAnnotationList ) ) {
									
									//  Header with this name and description not in proteinAnnotationList so add it

									ProteinAnnotation proteinAnnotation = new ProteinAnnotation();
									proteinAnnotationList.add(proteinAnnotation);

									proteinAnnotation.setName( fastaHeader.getName() );
									proteinAnnotation.setDescription( fastaHeader.getDescription() );

									//  TODO  Need to get taxonomy id
									proteinAnnotation.setNcbiTaxonomyId( BigInteger.valueOf( 0 ) );
									
									DataByFastaHeaderName dataByFastaHeaderName = new DataByFastaHeaderName();
									
									dataByFastaHeaderName.protein = protein;
									dataByFastaHeaderName.fastaHeader = fastaHeader;
									

									dataKeyedByFASTA_Header_Name_Map.put( fastaHeader.getName(), dataByFastaHeaderName );
								}
							}
						}
					}
				}

			} catch ( Exception e ) {

				String msg = "Error processing FASTA file: " + fastaFileWithPathFile.getAbsolutePath();
				log.error( msg );
				throw e;

			} finally {

				if ( fastaReader != null  ) {

					fastaReader.close();
				}

			}

		}
		
		
	}

	/**
	 * Validate that this header name does not already exist in file 
	 * or has same protein sequence, header name, and header description
	 * 
	 * @param headers
	 * @param proteinSequence
	 * @param dataKeyedByFASTA_Header_Name_Map
	 * @throws ProxlGenXMLDataException
	 */
	private void validateHeaderNames( 
			Set<FASTAHeader> headers, 
			String proteinSequence, 
			Map<String, DataByFastaHeaderName> dataKeyedByFASTA_Header_Name_Map ) throws ProxlGenXMLDataException {


		for ( FASTAHeader fastaHeader : headers ) {
			
			DataByFastaHeaderName dataByFastaHeaderName = dataKeyedByFASTA_Header_Name_Map.get( fastaHeader.getName() );

			if ( dataByFastaHeaderName != null ) {

				Protein proteinForHeaderName = dataByFastaHeaderName.protein;
				FASTAHeader fastaHeaderOther = dataByFastaHeaderName.fastaHeader;

				//  Already processed this header name so make sure has same protein sequence, header name, and header description
				if ( ! proteinSequence.equals( proteinForHeaderName.getSequence() ) ) {

					String msg = "Duplicate Header name in FASTA file for more than one sequence.  "
							+ "Header name:  " + fastaHeader.getName();
					log.error( msg );
					throw new ProxlGenXMLDataException( msg );
				}

				if ( ! fastaHeaderMatchesFastaHeader( fastaHeader, fastaHeaderOther ) ) {

					String msg = "Duplicate Header name in FASTA file with different descriptions.  "
							+ "Header name:  " + fastaHeader.getName();
					log.error( msg );
					throw new ProxlGenXMLDataException( msg );
					
				}
				



			}
		}
	}
	

	/**
	 * @param fastaHeader
	 * @param proteinAnnotation
	 * @return
	 */
	private boolean fastaHeaderMatchesFastaHeader(FASTAHeader fastaHeader_1, FASTAHeader fastaHeader_2) {
		
	
		if ( fastaHeader_1.getName().equals( fastaHeader_2.getName() ) ) { 
			
			if ( fastaHeader_1.getDescription() == null && fastaHeader_2.getDescription() == null ) {
				
				return true;
			}
			
			if ( ( fastaHeader_1.getDescription() != null )
					&& fastaHeader_1.getDescription().equals( fastaHeader_2.getDescription() ) ) {
				
				return true;
			}
		}
		
		return false;
	}
	

	private static class DataByFastaHeaderName {
		
		private Protein protein;
		private FASTAHeader fastaHeader;
		
	}
	
	
	/**
	 * If name matches and either both descriptions are null or description matches, return true
	 * else return false.
	 * 
	 * @param fastaHeader
	 * @param proteinAnnotationList
	 * @return
	 */
	private boolean isProteinHeaderInProxlXMLProtein( FASTAHeader fastaHeader, List<ProteinAnnotation> proteinAnnotationList ) {
		
		for ( ProteinAnnotation proteinAnnotation : proteinAnnotationList ) {

			if ( fastaHeaderMatchesProteinAnnotation( fastaHeader, proteinAnnotation ) ) {
				
				return true;
			}
		}

		return false;
	}

	
	
	/**
	 * @param fastaHeader
	 * @param proteinAnnotation
	 * @return
	 */
	private boolean fastaHeaderMatchesProteinAnnotation(FASTAHeader fastaHeader, ProteinAnnotation proteinAnnotation) {
		
	
		if ( proteinAnnotation.getName().equals( fastaHeader.getName() ) ) { 
			
			if ( proteinAnnotation.getDescription() == null && fastaHeader.getDescription() == null ) {
				
				return true;
			}
			
			if ( ( proteinAnnotation.getDescription() != null )
					&& proteinAnnotation.getDescription().equals( fastaHeader.getDescription() ) ) {
				
				return true;
			}
		}
		
		return false;
	}
		
}
