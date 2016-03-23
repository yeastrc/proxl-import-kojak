package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_crosslink_looplink_in_conf;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.utils.ModMassMatchWhenAdjustedToScale;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;


/**
 * 
 *
 */
public class IsCrosslinkOrLooplinkMassInConf {


	private static final IsCrosslinkOrLooplinkMassInConf _instance = new IsCrosslinkOrLooplinkMassInConf();

	//  private constructor
	private IsCrosslinkOrLooplinkMassInConf() {}


	/**
	 * Get Singleton Instance
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IsCrosslinkOrLooplinkMassInConf getInstance(  ) {

		return _instance;
	}

	/**
	 * @param linkerMass
	 * @param proxlInputRoot
	 * @return
	 */
	public boolean isCrosslinkOrLooplinkMassInConf( BigDecimal linkerMass, ProxlInput proxlInputRoot ) {
		
		Linkers linkers = proxlInputRoot.getLinkers();
		
		List<Linker> linkerList = linkers.getLinker();
		
		for ( Linker linker : linkerList ) {
			
			CrosslinkMasses crosslinkMasses = linker.getCrosslinkMasses();
			
			if ( crosslinkMasses != null ) {
				
				List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();
				
				for ( CrosslinkMass crosslinkMass : crosslinkMassList ) {
					
					if ( ModMassMatchWhenAdjustedToScale.modMassMatchWhenAdjustedToScale(linkerMass, crosslinkMass.getMass() ) ) {
						
						return true;
					}
				}
			}
		}
		
		return false;
	}

}
