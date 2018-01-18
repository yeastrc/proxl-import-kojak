package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants;

/**
 * Isotope label values to put in the Proxl XML file for Peptide and Protein Isotope Label
 * 
 * These must be supported in the Proxl Code.
 */
public class IsotopeLabelValuesConstants {

	/**
	 * Separator after peptide string before isotope label
	 */
	public static final String ISOTOPE_LABEL_PEPTIDE_SEPARATOR = "-";
	
	
	//  Isotope Label '15N' and combined forms
	
	public static final String ISOTOPE_LABEL_15N = "15N";
	
	public static final String ISOTOPE_LABEL_FOR_PROXL_XML_FILE_15N = ISOTOPE_LABEL_15N;
	
	public static final String ISOTOPE_LABEL__15N___FOR_END_OF_PEPTIDE_WITH_SEPARATOR = ISOTOPE_LABEL_PEPTIDE_SEPARATOR + ISOTOPE_LABEL_15N;

}
