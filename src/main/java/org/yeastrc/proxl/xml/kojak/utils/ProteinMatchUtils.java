/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.proxl.xml.kojak.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProteinMatchUtils {

	/**
	 * Returns true if the protein sequence contains the peptide sequence. No check is made
	 * for tryptic ends. I/L are treated as the same residue for matching.
	 * 
	 * @param proteinSequence
	 * @param reportedPeptide
	 * @return
	 */
	public static boolean proteinContainsReportedPeptide( String proteinSequence, String reportedPeptide ) {
		
		if( reportedPeptide.equals( "" ) )
			throw new IllegalArgumentException( "Peptide sequence cannot be empty." );
		
		// handle leucine, iso-leucine
		reportedPeptide = reportedPeptide.replaceAll( "L", "I" );
		proteinSequence = proteinSequence.replaceAll( "L",  "I" );
		
		if( proteinSequence.contains( reportedPeptide ) )
			return true;
		
		return false;
	}

	private static final Pattern[] taxonomyPatterns = {
			Pattern.compile( "\\b+OX=(\\d+)\\b+" ),		// uniprot header
			Pattern.compile( "\\b+TaxID=(\\d+)\\b+" ),	// uniref header
			Pattern.compile( "\\b+tax_id=(\\d+)\\b+", Pattern.CASE_INSENSITIVE ),
			Pattern.compile( "\\b+taxonomy=(\\d+)\\b+", Pattern.CASE_INSENSITIVE ),
			Pattern.compile( "\\b+tax=(\\d+)\\b+", Pattern.CASE_INSENSITIVE ),
	};

	public static Integer getNCBITaxonomyIdFromFASTAHeader( String fastaHeader ) {

		for( Pattern p : taxonomyPatterns ) {

			Matcher m = p.matcher( fastaHeader );
			if( m.find() ) {
				return Integer.valueOf( m.group( 1 ) );
			}
		}

		return null;
	}
	
}
