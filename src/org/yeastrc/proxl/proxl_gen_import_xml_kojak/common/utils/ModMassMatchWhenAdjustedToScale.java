package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ModMassMatchWhenAdjustedToScale {


	/**
	 * @param modificationMassInKojakFileRecord
	 * @param modificationMassToCompare
	 * @return
	 */
	public static boolean modMassMatchWhenAdjustedToScale(
			
			BigDecimal modificationMassInKojakFileRecord, 
			BigDecimal modificationMassToCompare
			) {

		int modMassToCompareScale = Math.min( modificationMassInKojakFileRecord.scale(), modificationMassToCompare.scale() );
		
		BigDecimal modificationMassToCompareScaleSet =
				modificationMassInKojakFileRecord.setScale( modMassToCompareScale, RoundingMode.HALF_UP );
		
		BigDecimal modificationMassMonolinkScaleSet =
				modificationMassToCompare.setScale( modMassToCompareScale, RoundingMode.HALF_UP );
		
		if ( modificationMassToCompareScaleSet.equals( modificationMassMonolinkScaleSet ) ) {
			
			return true;
		}
		
		return false;
	}

}
