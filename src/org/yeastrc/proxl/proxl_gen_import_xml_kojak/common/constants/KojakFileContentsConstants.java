package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KojakFileContentsConstants {
	
	
	public static final Charset KOJAK_FILE_INPUT_CHARACTER_SET = StandardCharsets.US_ASCII;


	public static final String PROGRAM_VERSION_LINE_STARTS_WITH_KOJAK = "Kojak version "; 
	
	//  These are extracted for specific processing and storage
	
	public static final String SCAN_NUMBER_HEADER_LABEL = "Scan Number";
	
	public static final String PEPTIDE_1_HEADER_LABEL = "Peptide #1";
	
	//  Link #1 position
	public static final String LINK_1_HEADER_LABEL_Pre_1_6_1 = "Link #1";        // Before 1.6.1, maybe changed sooner
	public static final String LINK_1_HEADER_LABEL_Start_1_6_1 = "Linked AA #1"; // On and after 1.6.1, maybe changed sooner

	public static final String PEPTIDE_2_HEADER_LABEL = "Peptide #2";
	
	//  Link #2 position
	public static final String LINK_2_HEADER_LABEL_Pre_1_6_1 = "Link #2";        // Before 1.6.1, maybe changed sooner
	public static final String LINK_2_HEADER_LABEL_Start_1_6_1 = "Linked AA #2"; // On and after 1.6.1, maybe changed sooner

	public static final String CHARGE_HEADER_LABEL = "Charge";

	public static final String LINKER_MASS_HEADER_LABEL = "Linker Mass";

	
	///   Columns to skip:
	
	public static final String PROTEIN_1_HEADER_LABEL = "Protein #1";
	public static final String PROTEIN_2_HEADER_LABEL = "Protein #2";
	
	public static final String PROTEIN_1_SITE_HEADER_LABEL = "Protein #1 Site";
	public static final String PROTEIN_2_SITE_HEADER_LABEL = "Protein #2 Site";
	
	

	//  PSM Per Peptide E-value - PSM Per Peptide Filterable Annotations
	public static final String PEPTIDE_1_E_VALUE_HEADER_LABEL = "Peptide #1 E-value";
	public static final String PEPTIDE_2_E_VALUE_HEADER_LABEL = "Peptide #2 E-value";
	
	
	//  PSM Per Peptide Scores - PSM Per Peptide Filterable Annotations
	public static final String PEPTIDE_1_SCORE_HEADER_LABEL = "Peptide #1 Score";
	public static final String PEPTIDE_2_SCORE_HEADER_LABEL = "Peptide #2 Score";

	
	//   Filterable annotations
	
	//  e-value: The expect value, or the number of hits with this score or better one can expect to see by chance.(smaller is better) 
	public static final String E_VALUE_HEADER_LABEL = "E-value";
	
//	Score is Kojak's primary score assignment (bigger is better) 
//	dScore is the difference between that score and the next score (bigger is better).
	public static final String SCORE_HEADER_LABEL = "Score";
	public static final String DSCORE_HEADER_LABEL = "dScore";
	
	public static final String PEPDIFF_HEADER_LABEL = "Pep. Diff.";

	
	//  All other are descriptive annotations

	
	
	
	
	
}
