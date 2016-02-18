package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.objects.PercolatorFileAndUnmarshalledObject;



/**
 * 
 *
 */
public class VerifyNoDuplicatePercolatorPSMs {

	private static final Logger log = Logger.getLogger(VerifyNoDuplicatePercolatorPSMs.class);
	
	
	/**
	 * @param percolatorFileAndUnmarshalledObjectList
	 * @throws Exception - when duplicate Percolator PSM found
	 */
	public static void verifyNoDuplicatePercolatorPSMs( List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList ) throws Exception {
		
		
		//  Check if the same Percolator PSM is in more than one file
		
		
		Set<String> percolatorPsmIds = new HashSet<>();
		
		
		
		for ( PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject : percolatorFileAndUnmarshalledObjectList ) {
			
			IPercolatorOutput percOutputRootObject = percolatorFileAndUnmarshalledObject.getPercOutputRootObject();

			List<? extends IPsm> iPsmList = percOutputRootObject.getPsms().getPsm();


			// loop through PSMs
			for( IPsm psm : iPsmList ) {

				String psmId = psm.getPsmId();
				
				String psmUniqueKey = psmId + psm.getPeptideSeq().getSeq();

				
				// percolatorPsmIds.add(...) returns false if already in the set
				
				if ( ! percolatorPsmIds.add( psmUniqueKey ) ) {
					
					String msg = "This PSM id is in the percolator files more than once: "
							+ "\n psm_id: "+ psmId 
							+ "\n psm.getPeptideSeq().getSeq(): " + psm.getPeptideSeq().getSeq()
							+ "\n psmUniqueKey: " + psmUniqueKey;
					
					log.error( msg );
					
					System.err.println( msg );
					
					throw new Exception( msg );
				}
				
			}
		}
		
	}
	
}
