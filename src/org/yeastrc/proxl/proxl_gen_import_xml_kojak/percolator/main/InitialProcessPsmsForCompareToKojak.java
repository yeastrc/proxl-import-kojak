package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.main;

import java.util.List;

//import org.apache.log4j.Logger;

import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.objects.PercolatorFileAndUnmarshalledObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.psm_processing.PsmMatchingAndCollection;



/**
 * 
 *
 */
public class InitialProcessPsmsForCompareToKojak {


//	private static final Logger log = Logger.getLogger(InitialProcessPsmsForCompareToKojak.class);
	
	//  private constructor
	private InitialProcessPsmsForCompareToKojak(){}
	
	private static InitialProcessPsmsForCompareToKojak instance = new InitialProcessPsmsForCompareToKojak();
	
	public static InitialProcessPsmsForCompareToKojak getInstance() {
		
		return instance;
	}


	
	/**
	 * @param percolatorFileAndUnmarshalledObjectList
	 * @return
	 * @throws Exception
	 */
	public PsmMatchingAndCollection initialProcessPsmsForCompareToKojak( List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList ) throws Exception {
		
	    
	    System.out.println("InitialProcessPsmsForCompareToKojakAndScans: Starting processing PSMs in <psms>");

	    PsmMatchingAndCollection psmProcessing = PsmMatchingAndCollection.getInstance();
		
		for ( PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject : percolatorFileAndUnmarshalledObjectList ) {
		
			IPercolatorOutput percOutputRootObject = percolatorFileAndUnmarshalledObject.getPercOutputRootObject();

			List<? extends IPsm> iPsmList = percOutputRootObject.getPsms().getPsm();


			// loop through PSMs, save all psm-level info to percPsmToKojakMatching
			for( IPsm xpsm : iPsmList ) {

				psmProcessing.addPercolatorPsm( xpsm );
			}
		}
		
	    System.out.println("InitialProcessPsmsForCompareToKojakAndScans: Done processing PSMs in <psms>");

	    return psmProcessing;
	}
	
	
}
