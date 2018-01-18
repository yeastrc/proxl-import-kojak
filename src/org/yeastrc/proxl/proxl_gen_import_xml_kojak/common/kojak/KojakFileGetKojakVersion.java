package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.File;

/**
 * Only get Kojak version from Kojak output file
 *
 */
public class KojakFileGetKojakVersion {
	
	/**
	 * private constructor
	 */
	private KojakFileGetKojakVersion() {}
	static public KojakFileGetKojakVersion getInstance() throws Exception {
		return new KojakFileGetKojakVersion();
	}

	/**
	 * @param kojakFile
	 * @return
	 * @throws Exception
	 */
	public String kojakFileGetKojakVersion( File kojakFile ) throws Exception {
		
		return KojakFileReader.getKojakVersionOnly( kojakFile );
	}
}
