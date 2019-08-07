package org.yeastrc.proxl.xml.kojak.reader;

import org.yeastrc.proxl.xml.kojak.objects.*;
import org.yeastrc.proxl.xml.kojak.utils.KojakParsingUtils;

import java.util.Map;

public class KojakPercolatorValidator {

	/**
	 * Validates All The Things. Ensures all percolator results are found in the expected places in the
	 * parsed kojak results--including expected PSMs being present in expected pepXML files.
	 *
	 * @param kojakResults
	 * @param percolatorResults
	 * @throws Exception
	 */
	public static void validateData(KojakResults kojakResults, PercolatorResults percolatorResults ) throws Exception {

		for( String percolatorReportedPeptide : percolatorResults.getReportedPeptideResults().keySet() ) {

			// all the percolator PSMs for this peptide, keyed on pepXML file name then scan number
			Map<String, Map<Integer, PercolatorPSM>> percolatorPeptideDataMap = percolatorResults.getReportedPeptideResults().get( percolatorReportedPeptide ).getPercolatorPSMs();

			// get the KojakReportedPeptide to use as a key to get the Kojak PSMs
			KojakReportedPeptide kojakReportedPeptide = KojakParsingUtils.getKojakReportedPeptideForString(percolatorReportedPeptide, kojakResults);
			if(kojakReportedPeptide == null ) {
				throw new Exception("Unable to find any kojak results for reported peptide: " + percolatorReportedPeptide);
			}

			// all kojak PSMs for this peptide, keyed on pepXML file name then scan number
			Map<String, Map<Integer, KojakPSMResult>> kojakMap = kojakResults.getKojakResults().get( kojakReportedPeptide );
			if (kojakMap == null ) {
				throw new Exception("Unable to find any kojak results for reported peptide: " + kojakReportedPeptide);
			}

			for( String pepXMLFileName : percolatorPeptideDataMap.keySet() ) {

				if (!kojakMap.containsKey(pepXMLFileName)) {
					throw new Exception("Unable to find any kojak results in " + pepXMLFileName + " for reported peptide: " + kojakReportedPeptide );
				}

				for (int scanNumber : percolatorPeptideDataMap.get( pepXMLFileName ).keySet() ) {

					if (!kojakMap.get(pepXMLFileName).containsKey(scanNumber)) {
						throw new Exception("Error: Could not find PSM data in " + pepXMLFileName + " for scan number " + scanNumber );
					}

				}
			}

		}
	}
}
