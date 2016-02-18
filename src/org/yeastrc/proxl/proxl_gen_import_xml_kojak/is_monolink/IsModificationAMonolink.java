package org.yeastrc.proxl.proxl_gen_import_xml_kojak.is_monolink;

import java.math.BigDecimal;
import java.util.Set;

//import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class IsModificationAMonolink {

//	private static final Logger log = Logger.getLogger( IsModificationAMonolink.class );
		
	//  private constructor
	private IsModificationAMonolink() {}
	
	private static Set<BigDecimal> monolinkModificationMasses;

	/**
	 * Set modification masses used to identify monolinks
	 * @param monolinkModificationMasses
	 */
	public static void setMonolinkModificationMasses(
			Set<BigDecimal> monolinkModificationMasses) {
		IsModificationAMonolink.monolinkModificationMasses = monolinkModificationMasses;
	}


	/**
	 * Is the modification mass a monolink
	 * 
	 * @param modificationMass
	 * @return
	 */
	public static boolean isModificationAMonolink( BigDecimal modificationMass ) {
		
		if ( monolinkModificationMasses.contains( modificationMass ) ) {
			
			return true;
		}
		
		return false;
	}

}
