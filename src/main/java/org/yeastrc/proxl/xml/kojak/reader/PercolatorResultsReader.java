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

package org.yeastrc.proxl.xml.kojak.reader;


import org.yeastrc.proteomics.percolator.out.PercolatorOutXMLUtils;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.xml.kojak.objects.*;
import org.yeastrc.proxl.xml.kojak.utils.ConversionUtils;
import org.yeastrc.proxl.xml.kojak.utils.PercolatorParsingUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PercolatorResultsReader {

	/**
	 * Get the parsed percolator results for the given percolator xml data file
	 * 
	 * @param files
	 * @return
	 * @throws Throwable
	 */
	public static PercolatorResults getPercolatorResults(File[] files ) throws Throwable {

		String version = null;
		Map<String, PercolatorPeptideData> percolatorPeptideData = new HashMap<>();

		for( File file : files ) {
			IPercolatorOutput po = getIPercolatorOutput(file);

			if( version == null ) {
				version = getPercolatorVersion(po);
			}

			Map<String, PercolatorPSM> psmIdPSMMap = getPercolatorPSMs(po);
			percolatorPeptideData.putAll( getPercolatorPeptideData(po, psmIdPSMMap) );
		}
		
		return new PercolatorResults(version, percolatorPeptideData);
	}

	
	/**
	 * Get a map of percolator peptide => map of scan number => percolator psm
	 * 
	 * @param po The IPercolatorOutput JAXB object created from parsing the XML
	 * @param psmIdPSMMap A map of all PercolatorPSMs found, keyed on their reported psm id string
	 * @return
	 * @throws Exception 
	 */
	protected static Map<String, PercolatorPeptideData> getPercolatorPeptideData(IPercolatorOutput po, Map<String, PercolatorPSM> psmIdPSMMap ) throws Exception {
		
		Map<String, PercolatorPeptideData> resultsMap = new HashMap<>();
		
		// loop through the repoted peptides
	    for( IPeptide xpeptide : po.getPeptides().getPeptide() ) {

	    	PercolatorPeptideScores percolatorPeptide = getPercolatorPeptideFromJAXB( xpeptide );
	    	
	    	if( resultsMap.containsKey( percolatorPeptide ) )
	    		throw new Exception( "Found two instances of the same reported peptide: " + percolatorPeptide + " and " + resultsMap.get( percolatorPeptide ) );
	    	
	    	Map<String, Map<Integer, PercolatorPSM>> psmsForPeptide = getPercolatorPSMsForPeptide( xpeptide, psmIdPSMMap );
	    	
	    	if( psmsForPeptide == null || psmsForPeptide.keySet().size() < 1 )
	    		throw new Exception( "Found no PSMs for peptide: " + percolatorPeptide );
	    	
	    	PercolatorPeptideData rpData = new PercolatorPeptideData( percolatorPeptide, psmsForPeptide );
	    	resultsMap.put(percolatorPeptide.getReportedPeptide(), rpData);
	    }
		
		
		return resultsMap;
	}
	
	/**
	 * Get a map of scan_file_id => scan number => PercolatorPSM for all PSMs associated with the supplied JAXB peptide object
	 * @param xpeptide
	 * @param psmIdPSMMap
	 * @return
	 * @throws Exception
	 */
	protected static Map<String, Map<Integer, PercolatorPSM>> getPercolatorPSMsForPeptide(IPeptide xpeptide, Map<String, PercolatorPSM> psmIdPSMMap ) throws Exception {
		
		Map<String, Map<Integer, PercolatorPSM>> psmsForPeptide = new HashMap<>();
		
		for( String psmId : xpeptide.getPsmIds().getPsmId() ) {

			if( !psmIdPSMMap.containsKey( psmId ) )
				throw new Exception( "Peptide contains psmId: " + psmId + ", but no PSM with that id was found. Peptide: " + xpeptide.getPeptideId() );

			String pepXMLFileName = ConversionUtils.getPepXMLPrefixFromPsmId( psmId ) + ".pep.xml";
			if( !psmsForPeptide.containsKey( pepXMLFileName ) ) {
				psmsForPeptide.put( pepXMLFileName, new HashMap<>() );
			}

			PercolatorPSM psm = psmIdPSMMap.get( psmId );	
			
			if( !psm.getReportedPeptide().equals( xpeptide.getPeptideId() ) )
				throw new Exception( "PSM (" + psm + ") has a different reported peptide than this peptide id: " + xpeptide.getPeptideId() );
			
			if( psmsForPeptide.get( pepXMLFileName ).containsKey( psm.getScanNumber() ) ) {
				throw new Exception( "Got more than one entry for " + pepXMLFileName + " for the same scan to the same reported peptide... Scan number: " + psm.getScanNumber() );
			}
			
			psmsForPeptide.get( pepXMLFileName ).put( psm.getScanNumber(), psm );
		}
		
		return psmsForPeptide;
	}

	
	
	/**
	 * Get the PercolatorPeptide object for the given JAXB representation of a percolator peptide
	 * 
	 * @param xpeptide
	 * @return
	 */
	protected static PercolatorPeptideScores getPercolatorPeptideFromJAXB( IPeptide xpeptide ) {
		
		PercolatorPeptideScoresBuilder ppb = new PercolatorPeptideScoresBuilder();

		ppb.setPep( Double.valueOf( xpeptide.getPep() ) );
		ppb.setpValue( Double.valueOf( xpeptide.getPValue() ) );
		ppb.setqValue( Double.valueOf( xpeptide.getQValue() ) );
		ppb.setReportedPeptide( xpeptide.getPeptideId() );
		ppb.setSvmScore( Double.valueOf( xpeptide.getSvmScore() ) );
		
		return ppb.createPercolatorPeptideScores();
	}
	
	
	/**
	 * Return a collection of all the PercolatorPSMs parsed from the JAXB top level percolator XML object
	 * 
	 * @param po
	 * @return
	 */
	protected static Map<String, PercolatorPSM> getPercolatorPSMs( IPercolatorOutput po ) {
		
		Map<String, PercolatorPSM> psmIdPSMMap = new HashMap<>();
		
	    // loop through PSMs
	    for( IPsm xpsm : po.getPsms().getPsm() ) {
	    	
	    	PercolatorPSM psm = getPercolatorPSMFromJAXB( xpsm );
	    	psmIdPSMMap.put( psm.getPsmId(), psm );

	    }
		
		return psmIdPSMMap;
	}
	
	/**
	 * Get a PercolatorPSM from the JAXB object generated from parsing the XML
	 * @param xpsm
	 * @return
	 */
	protected static PercolatorPSM getPercolatorPSMFromJAXB( IPsm xpsm ) {
		
		PercolatorPSMBuilder psmb = new PercolatorPSMBuilder();
		
		psmb.setPep( Double.valueOf( xpsm.getPep() ) );
		psmb.setPsmId( xpsm.getPsmId() );
		psmb.setpValue( Double.valueOf( xpsm.getPValue() ) );
		psmb.setqValue( Double.valueOf( xpsm.getQValue() ) );
		psmb.setReportedPeptide( xpsm.getPeptideSeq().getSeq() );
		psmb.setScanNumber( PercolatorParsingUtils.getScanNumberFromScanId( xpsm.getPsmId() ) );
		psmb.setSvmScore( Double.valueOf( xpsm.getSvmScore() ) );
		
		return psmb.createPercolatorPSM();
	}
	

	/**
	 * Get the version of percolator used to generate the XML. If unable to determine
	 * return "unknown"
	 * 
	 * @param po
	 * @return
	 */
	protected static String getPercolatorVersion( IPercolatorOutput po ) {
		
		String version = null;
		
		try {
			
			version = po.getPercolatorVersion();
			
		} catch ( Exception e ) {
			
			version = "unknown";
			
		}
		
		return version;
	}
	
	
	/**
	 * Get the top level JAXB object for the given percolator XML file
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected static IPercolatorOutput getIPercolatorOutput(File file ) throws Exception {
		
		String xsdVersion = PercolatorOutXMLUtils.getXSDVersion( file );
		
		JAXBContext jaxbContext = JAXBContext.newInstance( "com.per_colator.percolator_out._" + xsdVersion );
		Unmarshaller u = jaxbContext.createUnmarshaller();
		IPercolatorOutput po = (IPercolatorOutput)u.unmarshal( file );
	
		return po;
	}

	
}
