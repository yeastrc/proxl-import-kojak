package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercolatorFileContentsConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PercolatorFileAndUnmarshalledObject;

/**
 * 
 *
 */
public class VerifyAllPercolatorVersionsSame_RetrievePercolatorVersion {

	private static final Logger log = Logger.getLogger(VerifyAllPercolatorVersionsSame_RetrievePercolatorVersion.class);
	

	/**
	 * @param percolatorFileAndUnmarshalledObjectList
	 * @return percolatorVersion
	 * @throws Exception Percolator files have different versions
	 */
	public static String verifyAllPercolatorVersionsSame_RetrievePercolatorVersion( List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList ) throws Exception {
		
		
		//  Confirm that the version in all the files is the same and return the percolator version.
		

		String prevPercolatorVersion = null;
		
		for ( PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject : percolatorFileAndUnmarshalledObjectList ) {
			
			IPercolatorOutput percOutputRootObject = percolatorFileAndUnmarshalledObject.getPercOutputRootObject();
			
			String percolatorVersionInXML = percOutputRootObject.getPercolatorVersion();
			
			String percolatorVersion = getPercolatorVersionFromPercolatorVersionInXML( percolatorVersionInXML );
			
			if ( prevPercolatorVersion == null ) {
				
				prevPercolatorVersion = percolatorVersion;
			} else {
				
				if ( ! prevPercolatorVersion.equals( percolatorVersion ) ) {
					
					String msg = "Percolator files cannot have different Percolator versions."
							+ "  prev version: " + prevPercolatorVersion + ", compare version: " + percolatorVersion;
					
					log.error( msg );
					
					throw new Exception(msg);
				}
			}
		}
		
		return prevPercolatorVersion;
	}
	
	
	
	/**
	 * @param percolatorVersionInXML
	 * @return
	 * @throws Exception
	 */
	private static String getPercolatorVersionFromPercolatorVersionInXML( String percolatorVersionInXML ) throws Exception {
		
		if ( ! percolatorVersionInXML.startsWith( PercolatorFileContentsConstants.PERCOLATOR_VERSION_PREFIX ) ) {
			
			String msg = "Percolator version in XML does not start with |" + PercolatorFileContentsConstants.PERCOLATOR_VERSION_PREFIX
					+ "|.  Percolator version in XML: " + percolatorVersionInXML;
			
			log.error( msg );
			
			throw new Exception(msg);
		}
		
		String percolatorVersion = percolatorVersionInXML.substring( PercolatorFileContentsConstants.PERCOLATOR_VERSION_PREFIX.length() );
		
		return percolatorVersion;
	}
	
	
	
}
