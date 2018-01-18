package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.builder;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels.ProteinIsotopeLabel;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.taxonomy.main.GetTaxonomyId;

/**
 * Build the MatchedProteins section of the ProXL XML docs. This is done by finding all proteins in the FASTA
 * file that contains any of the peptide sequences found in the experiment. 
 * 
 * This is generalized enough to be usable by any pipeline
 * 
 * @author mriffle
 *
 */
public class MatchedProteinsBuilder {

	public static MatchedProteinsBuilder getInstance() { return new MatchedProteinsBuilder(); }
	
	/**
	 * Add all target proteins from the FASTA file that contain any of the peptides found in the experiment
	 * to the proxl xml document in the matched proteins sectioni.
	 * 
	 * @param proxlInputRoot
	 * @param fastaFile
	 * @param isotopeLabels
	 * @param decoyIdentifiers
	 * @throws Exception
	 */
	public void buildMatchedProteins( ProxlInput proxlInputRoot, File fastaFile, List<MatchedProteins_IsotopeLabel_Param> isotopeLabels, Collection<String> decoyIdentifiers ) throws Exception {
		
		// get all distinct peptides found in this search
//		Collection<PeptideOrProteinDistinctHolder> distinctPeptide = getDistinctPeptides( proxlInputRoot );
		
		// the proteins we've found
		Map<PeptideOrProteinDistinctHolder, Collection<FastaProteinAnnotation>> proteins = getProteins( fastaFile, decoyIdentifiers, isotopeLabels );
		
		// create the XML and add to root element
		buildAndAddMatchedProteinsToXML( proxlInputRoot, proteins );
		
	}
	
	/**
	 * Do the work of building the matched peptides element and adding to proxl xml root
	 * 
	 * @param proxlInputRoot
	 * @param proteins
	 * @throws Exception
	 */
	private void buildAndAddMatchedProteinsToXML( ProxlInput proxlInputRoot, Map<PeptideOrProteinDistinctHolder, Collection<FastaProteinAnnotation>> proteins ) throws Exception {
		
		MatchedProteins xmlMatchedProteins = new MatchedProteins();
		proxlInputRoot.setMatchedProteins( xmlMatchedProteins );
		
		for( Map.Entry<PeptideOrProteinDistinctHolder, Collection<FastaProteinAnnotation>> entry : proteins.entrySet() ) {
			
			PeptideOrProteinDistinctHolder peptideOrProteinDistinctHolder = entry.getKey();
			String sequence = peptideOrProteinDistinctHolder.sequence;
			String isotopeLabelString = peptideOrProteinDistinctHolder.isotopeLabel;
			
			Protein xmlProtein = new Protein();
        	xmlMatchedProteins.getProtein().add( xmlProtein );
        	
        	xmlProtein.setSequence( sequence );
        	
        	if ( isotopeLabelString != null ) {
        		// Add isotope label
        		ProteinIsotopeLabels proteinIsotopeLabels = new ProteinIsotopeLabels();
        		xmlProtein.setProteinIsotopeLabels( proteinIsotopeLabels );
        		ProteinIsotopeLabel proteinIsotopeLabel = new ProteinIsotopeLabel();
        		proteinIsotopeLabels.setProteinIsotopeLabel( proteinIsotopeLabel );
        		proteinIsotopeLabel.setLabel( isotopeLabelString );
        	}
        	
        	for( FastaProteinAnnotation anno : entry.getValue() ) {
        		ProteinAnnotation xmlProteinAnnotation = new ProteinAnnotation();
        		xmlProtein.getProteinAnnotation().add( xmlProteinAnnotation );
        		
        		xmlProteinAnnotation.setName( anno.getName() );
        		
        		if( anno.getDescription() != null )
        			xmlProteinAnnotation.setDescription( anno.getDescription() );
        			
        		if( anno.getTaxonomId() != null )
        			xmlProteinAnnotation.setNcbiTaxonomyId( new BigInteger( anno.getTaxonomId().toString() ) );
        	}
		}
	}
	

	/**
	 * Get a map of the distinct target protein sequences mapped to a collection of target annotations for that sequence
	 * from the given fasta file
	 * 
	 * Old Description, is Incorrect:
	 * 
	 * Get a map of the distinct target protein sequences mapped to a collection of target annotations for that sequence
	 * from the given fasta file, where the sequence contains any of the supplied peptide sequences
	 * 
	 * @param allPetpideSequences
	 * @param decoyIdentifiers
	 * @return
	 * @throws Exception
	 */
	private Map<PeptideOrProteinDistinctHolder, Collection<FastaProteinAnnotation>> getProteins( 
			File fastaFile, Collection<String> decoyIdentifiers, List<MatchedProteins_IsotopeLabel_Param> isotopeLabels ) throws Exception {
		
		///  !!!!!  WARNING  Only use peptides if also add in the I & L equivalence code
		
		Map<PeptideOrProteinDistinctHolder, Collection<FastaProteinAnnotation>> proteinAnnotations = new HashMap<>();
		
		FASTAReader fastaReader = null;
		
		try {
			
			fastaReader = FASTAReader.getInstance( fastaFile );
			
			for( FASTAEntry entry = fastaReader.readNext(); entry != null; entry = fastaReader.readNext() ) {

				if( isDecoyFastaEntry( entry, decoyIdentifiers ) )
					continue;
				
				for( FASTAHeader header : entry.getHeaders() ) {
					
					FastaProteinAnnotation anno = new FastaProteinAnnotation();
					anno.setName( header.getName() );
					anno.setDescription( header.getDescription() );
					
            		Integer taxId = GetTaxonomyId.getInstance().getTaxonomyId( header.getName(), header.getDescription() );
            		if( taxId != null ) {
            			anno.setTaxonomId( taxId );
            		}

            		//  Determine if header name starts with the isotope label identifier in the Kojak.conf file
            		String isotopeLabel_ForProxlXML_File = null;
            		for ( MatchedProteins_IsotopeLabel_Param isotopeLabelParam : isotopeLabels ) {
            			if ( header.getName().startsWith( isotopeLabelParam.getIsotopeLabel_ProteinNamePrefix() ) ) {
            				isotopeLabel_ForProxlXML_File = isotopeLabelParam.getIsotopeLabel_ForProxlXMLFile();
            				break;
            			}
            		}
            		
            		//  Key to Map, protein sequence and isotopeLabel (if found in header name)
					PeptideOrProteinDistinctHolder peptideOrProteinDistinctHolder = new PeptideOrProteinDistinctHolder();
					peptideOrProteinDistinctHolder.sequence = entry.getSequence();
					peptideOrProteinDistinctHolder.isotopeLabel = isotopeLabel_ForProxlXML_File;

            		Collection<FastaProteinAnnotation> fastaProteinAnnotationsForKey = proteinAnnotations.get( peptideOrProteinDistinctHolder );
            		
					if( fastaProteinAnnotationsForKey == null ) {
						fastaProteinAnnotationsForKey = new HashSet<>();
						proteinAnnotations.put( peptideOrProteinDistinctHolder, fastaProteinAnnotationsForKey );
					}
					
					fastaProteinAnnotationsForKey.add( anno );
				}
			}
			
		} finally {
			if( fastaReader != null ) {
				fastaReader.close();
				fastaReader = null;
			}
		}
		
		return proteinAnnotations;
	}
	
	/**
	 * Return true if the supplied FASTA entry is a decoy entry. False otherwise.
	 * An entry is considered a decoy if any of the supplied decoy identifiers are present
	 * in any of the FASTA names
	 * 
	 * @param entry
	 * @param decoyIdentifiers
	 * @return
	 */
	private boolean isDecoyFastaEntry( FASTAEntry entry, Collection<String> decoyIdentifiers ) {

		for( String decoyId : decoyIdentifiers ) {			
			for( FASTAHeader header : entry.getHeaders() ) {

				if( header.getName().contains( decoyId ) )
					return true;
				
			}
			
		}
		
		return false;
		
	}
	
	
//	/**
//	 * Get all distinct peptides from a proxlxml doc's reported peptide section
//	 * 
//	 * @param proxlInputRoot
//	 * @return
//	 * @throws Exception
//	 */
//	private Collection<PeptideOrProteinDistinctHolder> getDistinctPeptides( ProxlInput proxlInputRoot ) throws Exception {
//		
//		Collection<PeptideOrProteinDistinctHolder> distinctPeptides = new HashSet<>();
//		
//		ReportedPeptides reportedPeptides = proxlInputRoot.getReportedPeptides();
//		
//		if ( reportedPeptides != null ) {
//
//			List<ReportedPeptide> reportedPeptideList = reportedPeptides.getReportedPeptide();
//			
//			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
//				
//				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
//					
//					if ( reportedPeptides != null ) {
//
//						Peptides peptidesProxlXML = reportedPeptide.getPeptides();
//						List<Peptide> peptideProxlXMLList = peptidesProxlXML.getPeptide();
//
//						if ( peptideProxlXMLList != null && ( ! peptideProxlXMLList.isEmpty() ) ) {
//							
//							for ( Peptide peptideProxlXML : peptideProxlXMLList ) {
//								
//								PeptideOrProteinDistinctHolder peptideUniqueHolder = new PeptideOrProteinDistinctHolder();
//								peptideUniqueHolder.sequence = peptideProxlXML.getSequence();
//								
//								PeptideIsotopeLabels peptideIsotopeLabels = peptideProxlXML.getPeptideIsotopeLabels();
//								if ( peptideIsotopeLabels != null ) {
//									PeptideIsotopeLabel peptideIsotopeLabel = peptideIsotopeLabels.getPeptideIsotopeLabel();
//									if ( peptideIsotopeLabel != null ) {
//										peptideUniqueHolder.isotopeLabel = peptideIsotopeLabel.getLabel();
//									}
//								}
//								
//								distinctPeptides.add( peptideUniqueHolder );
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		
//		return distinctPeptides;
//	}
	
	/**
	 * Holder of Peptide Data to track a unique peptide
	 *
	 * Currently the peptide sequence and the isotope label (if one is assigned)
	 */
	private static class PeptideOrProteinDistinctHolder {
		
		private String sequence;
		private String isotopeLabel; // Assume only one
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((isotopeLabel == null) ? 0 : isotopeLabel.hashCode());
			result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeptideOrProteinDistinctHolder other = (PeptideOrProteinDistinctHolder) obj;
			if (isotopeLabel == null) {
				if (other.isotopeLabel != null)
					return false;
			} else if (!isotopeLabel.equals(other.isotopeLabel))
				return false;
			if (sequence == null) {
				if (other.sequence != null)
					return false;
			} else if (!sequence.equals(other.sequence))
				return false;
			return true;
		}
	}
	
	/**
	 * An annotation for a protein in a Fasta file
	 * 
	 * @author mriffle
	 *
	 */
	private class FastaProteinAnnotation {
		
		public int hashCode() {
			
			String hashString = this.getName();
			
			if( this.getDescription() != null )
				hashString += this.getDescription();
			
			if( this.getTaxonomId() != null )
				hashString += this.getTaxonomId().intValue();
			
			return hashString.hashCode();
		}
		
		/**
		 * Return true if name, description and taxonomy are all the same, false otherwise
		 */
		public boolean equals( Object o ) {
			try {
				
				FastaProteinAnnotation otherAnno = (FastaProteinAnnotation)o;
				
				if( !this.getName().equals( otherAnno.getName() ) )
					return false;
				
				
				if( this.getDescription() == null ) {
					if( otherAnno.getDescription() != null )
						return false;
				} else {
					if( otherAnno.getDescription() == null )
						return false;
				}
				
				if( !this.getDescription().equals( otherAnno.getDescription() ) )
					return false;
				
				
				if( this.getTaxonomId() == null ) {
					if( otherAnno.getTaxonomId() != null )
						return false;
				} else {
					if( otherAnno.getTaxonomId() == null )
						return false;
				}
				
				if( !this.getTaxonomId().equals( otherAnno.getTaxonomId() ) )
					return false;
				
				
				return true;
				
			} catch( Exception e ) {
				return false;
			}
		}

		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Integer getTaxonomId() {
			return taxonomId;
		}
		public void setTaxonomId(Integer taxonomId) {
			this.taxonomId = taxonomId;
		}

		
		
		private String name;
		private String description;
		private Integer taxonomId;
		
	}
	
}
