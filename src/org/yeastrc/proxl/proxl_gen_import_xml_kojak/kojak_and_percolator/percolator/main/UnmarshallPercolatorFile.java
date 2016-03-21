package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

//import org.apache.log4j.Logger;


import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.utils.GetPercolatorJAXBContextObject;

/**
 * 
 *
 */
public class UnmarshallPercolatorFile {

//	private static final Logger log = Logger.getLogger(UnmarshallPercolatorFile.class);
			
	/**
	 * private constructor
	 */
	private UnmarshallPercolatorFile(){}
	
	public static UnmarshallPercolatorFile getInstance() {
		
		return new UnmarshallPercolatorFile();
	}
	
	
	/**
	 * @param percFile
	 * @return
	 * @throws Exception
	 */
	public IPercolatorOutput unmarshallPercolatorFile( File percFile ) throws Exception {
		

		// unmarshal our XML and get to work
		JAXBContext jaxbContext = GetPercolatorJAXBContextObject.getPercolatorJAXBContextObjectForAllFormats();
		
		
		Unmarshaller u = jaxbContext.createUnmarshaller();
		
		Object unmarshalledObject = null;
		
		try {
		
			unmarshalledObject = u.unmarshal( percFile );
		
		} catch ( Exception e ) {
			
			String exceptionMessage = e.toString();
			
			if ( exceptionMessage.contains( "unexpected element (uri:\"http:" ) ) {
				
				String msg = "Likely Unsupported Percolator Version encountered.";
				
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				
			}
			
			throw e;
		}
		
		if ( ! ( unmarshalledObject instanceof IPercolatorOutput ) ) {
			
			String msg = "Object unmarshalled from " + percFile.getAbsolutePath() 
					+ " cannot be cast to IPercolatorOutput.  unmarshalledObject.getClass().getCanonicalName(): " + unmarshalledObject.getClass().getCanonicalName();
			
			System.err.println( msg );
			System.out.println( msg );
			
			throw new Exception(msg);
		}
		
	    IPercolatorOutput percOutputRootObject = (IPercolatorOutput) unmarshalledObject;
	    
	    return percOutputRootObject;
	    
	    
	    

	    //  Example of casting the interface to the concrete object to access methods not in the interface.
	    //         Applicable for all the interfaces for the Percolator Out.
	    
//		if ( percOutputRootObject instanceof com.per_colator.percolator_out._13.PercolatorOutput ) {
//	              //  Cast to version 13
//			com.per_colator.percolator_out._13.PercolatorOutput percOutputRootObject13 = (com.per_colator.percolator_out._13.PercolatorOutput) percOutputRootObject;
//			
////			System.out.println( "percolator version 13 for input file: " + percFile.getAbsolutePath() );
//		}
//
//		
//		if ( percOutputRootObject instanceof com.per_colator.percolator_out._15.PercolatorOutput ) {
//        //  Cast to version 15
//			com.per_colator.percolator_out._15.PercolatorOutput percOutputRootObject15 = (com.per_colator.percolator_out._15.PercolatorOutput) percOutputRootObject;
//			
////			System.out.println( "percolator version 15 for input file: " + percFile.getAbsolutePath() );
//		}
	    
		
	}
}
