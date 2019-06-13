package org.yeastrc.proxl.xml.kojak.constants;

import java.util.HashMap;
import java.util.Map;

public class KojakConstants {

	public static final String PSM_SCORE_NAME_KOJAK_SCORE = "kojak_score";
	public static final String PSM_SCORE_NAME_DELTA_SCORE = "delta_score";
	public static final String PSM_SCORE_NAME_PPM_ERROR = "ppm_error";
	public static final String PSM_SCORE_NAME_EVALUE = "e-value";
	public static final String PSM_SCORE_NAME_ION_MATCH = "ion_match";
	public static final String PSM_SCORE_NAME_CONSECUTIVE_ION_MATCH = "consecutive_ion_match";

	public static final String PSM_PER_PEPTIDE_SCORE_NAME_SCORE = "score";
	public static final String PSM_PER_PEPTIDE_SCORE_NAME_RANK = "rank";
	public static final String PSM_PER_PEPTIDE_SCORE_NAME_EVALUE = "e-value";
	public static final String PSM_PER_PEPTIDE_SCORE_NAME_ION_MATCH = "ion_match";
	public static final String PSM_PER_PEPTIDE_SCORE_NAME_CONSECUTIVE_ION_MATCH = "consecutive_ion_match";

	/**
	 * A map of amino acid single letter codes to masses used by Kojak, as found in
	 * https://github.com/mhoopmann/kojak/blob/master/KIons.cpp
	 */
	public static final Map<String, Double> AA_MASS;
	
	static {
		
		AA_MASS = new HashMap<String, Double>();
		
		AA_MASS.put( "A", 71.0371103 );
		AA_MASS.put( "C", 103.0091803 );
		AA_MASS.put( "D", 115.0269385 );
		AA_MASS.put( "E", 129.0425877 );
		AA_MASS.put( "F", 147.0684087 );
		AA_MASS.put( "G", 57.0214611 );
		AA_MASS.put( "H", 137.0589059 );
		AA_MASS.put( "I", 113.0840579 );
		AA_MASS.put( "K", 128.0949557 );
		AA_MASS.put( "L", 113.0840579 );
		AA_MASS.put( "M", 131.0404787 );
		AA_MASS.put( "N", 114.0429222 );
		AA_MASS.put( "P", 97.0527595 );
		AA_MASS.put( "Q", 128.0585714 );
		AA_MASS.put( "R", 156.1011021 );
		AA_MASS.put( "S", 87.0320244 );
		AA_MASS.put( "T", 101.0476736 );
		AA_MASS.put( "V", 99.0684087 );
		AA_MASS.put( "W", 186.0793065 );
		AA_MASS.put( "Y", 163.0633228 );
		
	}
	
}
