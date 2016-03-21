package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KojakFileContentsConstants {
	
	
	public static final Charset KOJAK_FILE_INPUT_CHARACTER_SET = StandardCharsets.US_ASCII;


	public static final String PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK = "Kojak version "; 
	
	//  These are extracted for specific processing and storage
	
	public static final String SCAN_NUMBER_HEADER_LABEL = "Scan Number";
	
	public static final String PEPTIDE_1_HEADER_LABEL = "Peptide #1";
	public static final String LINK_1_HEADER_LABEL = "Link #1";

	public static final String PEPTIDE_2_HEADER_LABEL = "Peptide #2";
	public static final String LINK_2_HEADER_LABEL = "Link #2";

	public static final String CHARGE_HEADER_LABEL = "Charge";

	public static final String LINKER_MASS_HEADER_LABEL = "Linker Mass";

	
	///   Columns to skip:
	
	public static final String PROTEIN_1_HEADER_LABEL = "Protein #1";
	public static final String PROTEIN_2_HEADER_LABEL = "Protein #2";

	
	
	
	//   Filterable annotations
	
	
//	Score is Kojak's primary score assignment (bigger is better) 
//	dScore is the difference between that score and the next score (bigger is better).
	public static final String SCORE_HEADER_LABEL = "Score";
	public static final String DSCORE_HEADER_LABEL = "dScore";
	public static final String PEPDIFF_HEADER_LABEL = "Pep. Diff.";

	
	//  All other are descriptive annotations

	
	
	
	
	
}
