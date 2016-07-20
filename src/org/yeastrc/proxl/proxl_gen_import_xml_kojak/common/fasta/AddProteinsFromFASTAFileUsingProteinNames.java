package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.fasta;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.taxonomy.main.GetTaxonomyId;

/**
 * 
 *
 */
public class AddProteinsFromFASTAFileUsingProteinNames {

	private static final Logger log = Logger.getLogger( AddProteinsFromFASTAFileUsingProteinNames.class );

	/**
	 * private constructor
	 */
	private AddProteinsFromFASTAFileUsingProteinNames(){}
	
	public static AddProteinsFromFASTAFileUsingProteinNames getInstance(  )  { 
		
		return new AddProteinsFromFASTAFileUsingProteinNames();
	}
	
	
	/**
	 * @param proxlInputRoot
	 * @param fastaFileWithPathFile
	 * @throws Exception
	 */
	public void addProteinsFromFASTAFile( ProxlInput proxlInputRoot, File fastaFileWithPathFile, Set<String> proteinNameStrings ) throws Exception {

		Map<String, Protein> proxlXMLProteinsKeyedByProteinSequenceMap = new HashMap<>();
		Map<String, DataByFastaHeaderName> dataKeyedByFASTA_Header_Name_Map = new HashMap<>();
		
		Set<String> proteinNameStringsFoundInFASTAFile = new HashSet<>();
		
		GetTaxonomyId getTaxonomyId = GetTaxonomyId.getInstance();
		
					
		if ( ! proteinNameStrings.isEmpty() ) {

			

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
					

//					String headerLine = fastaEntry.getHeaderLine();
//					
//					int HeaderLineNumber = fastaEntry.getHeaderLineNumber();
					

					Set<FASTAHeader> headers = fastaEntry.getHeaders();

					boolean anyFASTAHeaderNamesInProteinNameSet = false;
					
					List<String> allHeaderNames = new ArrayList<>();
					

					for ( FASTAHeader header : headers ) {
						
						String headerName = header.getName();
						allHeaderNames.add(headerName);
					}
					
					for ( FASTAHeader header : headers ) {
						
						String fastaHeaderLinePart = header.getLine();
//						String headerName = header.getName();
						

						for ( String proteinName : proteinNameStrings ) {

							if ( fastaHeaderLinePart.startsWith( proteinName ) ) {

								//  match found

								anyFASTAHeaderNamesInProteinNameSet = true;

								proteinNameStringsFoundInFASTAFile.add( proteinName );
							}
						}
						
						if ( anyFASTAHeaderNamesInProteinNameSet ) {
							
							break;
						}
					}
				
				
					if ( anyFASTAHeaderNamesInProteinNameSet ) {

						//			fastaEntry.getHeaderLine();
						//			fastaEntry.getHeaderLineNumber();
						//			fastaEntry.getHeaders();
						//			fastaEntry.getSequence();

						String proteinSequence = fastaEntry.getSequence();

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
								
								Integer taxonomyId = getTaxonomyId.getTaxonomyId( fastaHeader.getName(), fastaHeader.getDescription() );

								if ( taxonomyId != null ) {

									proteinAnnotation.setNcbiTaxonomyId( BigInteger.valueOf( taxonomyId ) );
								} else {

									proteinAnnotation.setNcbiTaxonomyId( BigInteger.valueOf( 0 ) );
								}

								DataByFastaHeaderName dataByFastaHeaderName = new DataByFastaHeaderName();

								dataByFastaHeaderName.protein = protein;
								dataByFastaHeaderName.fastaHeader = fastaHeader;


								dataKeyedByFASTA_Header_Name_Map.put( fastaHeader.getName(), dataByFastaHeaderName );
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
			
			
			if ( proteinNameStrings.size() != proteinNameStringsFoundInFASTAFile.size() ) {
				
				System.err.println( "" );
				
				System.err.println( "Some Protein names in the Kojak file were not found in the FASTA file:" );
				
				System.err.println( "Number of Protein names in the Kojak file:" + proteinNameStrings.size() );
				System.err.println( "Number of Protein names in the Kojak file that were in the FASTA file:"
						+ proteinNameStringsFoundInFASTAFile.size() );
				
				System.err.println( "Number of Protein names in the Kojak file that were NOT in the FASTA file:"  
						+ ( proteinNameStrings.size() - proteinNameStringsFoundInFASTAFile.size() ) );
				
				
				System.err.println( "" );
				
				System.err.println( "Sample of Protein names in the Kojak file that were NOT in the FASTA file:" );
				
				int counter = 0;
				
				for ( String proteinName : proteinNameStrings ) {
					
					if ( ! proteinNameStringsFoundInFASTAFile.contains(proteinName) ) {
						
						System.err.println( proteinName );
						
						counter++;
						if ( counter > 4 ) {
							
							break;
						}
					}
				}
				
				System.err.println( "" );
				System.err.println( "END:  Some Protein names in the Kojak file were not found in the FASTA file:" );

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
