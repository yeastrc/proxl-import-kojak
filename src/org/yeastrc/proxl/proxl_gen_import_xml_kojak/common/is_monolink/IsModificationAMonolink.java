package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.utils.ModMassMatchWhenAdjustedToScale;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMass;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;


//import org.apache.log4j.Logger;

/**
 * 
 * Singleton
 */
public class IsModificationAMonolink {

//	private static final Logger log = Logger.getLogger( IsModificationAMonolink.class );
		
	private static final IsModificationAMonolink _instance = new IsModificationAMonolink();
	
	//  private constructor
	private IsModificationAMonolink() {}
	
	
	/**
	 * Get Singleton Instance
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IsModificationAMonolink getInstance(  ) {
		
		return _instance;
	}
	
	
	
	private Set<BigDecimal> monolinkModificationMasses;
	
	

	/**
	 * Set modification masses used to identify monolinks
	 * @param monolinkModificationMasses
	 */
	public void setMonolinkModificationMasses(Set<BigDecimal> monolinkModificationMasses) {
		
		this.monolinkModificationMasses = monolinkModificationMasses;
	}

	/**
	 * proxlInputRoot is expected to be populated from the conf file by this point.
	 * @param proxlInputRoot
	 */
	public void setMonolinkModificationMassesFromProxlInputObject( ProxlInput proxlInputRoot ) {
		
		if ( monolinkModificationMasses != null ) {
			
			return;  //  EARLY EXIT since already populated
		}
		
		Linkers linkers = proxlInputRoot.getLinkers();
		
		if ( linkers == null ) {
			
			return;  //  EARLY EXIT since no linkers
		}
		
		List<Linker> linkerList = linkers.getLinker();

		if ( linkerList == null || linkerList.isEmpty() ) {
			
			return;  //  EARLY EXIT since no linkers
		}
		
	
		
		for ( Linker linker : linkerList ) {
			
			MonolinkMasses monolinkMasses = linker.getMonolinkMasses();
			
			if ( monolinkMasses == null ) {
				
				continue;  // EARLY CONTINUE
			}
			
			List<MonolinkMass> monolinkMassList = monolinkMasses.getMonolinkMass();
			
			if ( monolinkMassList == null || monolinkMassList.isEmpty() ) {
				
				continue;  // EARLY CONTINUE
			}
			
			if ( monolinkModificationMasses == null ) {
				
				monolinkModificationMasses = new HashSet<>();
			}
			
			for ( MonolinkMass monolinkMass : monolinkMassList ) {
				
				monolinkModificationMasses.add( monolinkMass.getMass() );
			}
		}
		
		
	}

	/**
	 * Is the modification mass a monolink
	 * 
	 * @param modificationMass
	 * @return
	 */
	public boolean isModificationAMonolink( BigDecimal modificationMass ) {
		
		if ( monolinkModificationMasses == null ) {
			
			return false;
		}
		
		for ( BigDecimal modificationMassMonolink : monolinkModificationMasses ) {
		
			if ( ModMassMatchWhenAdjustedToScale.modMassMatchWhenAdjustedToScale( modificationMass, modificationMassMonolink ) ) {

				return true;
			}
		}
		
		return false;
	}
	
	

}
