package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.constants;

/**
 * Update DefaultVisibleAnnotationsConstants as needed when change this class
 *
 */
public class PercolatorGenImportXML_AnnotationNames_Constants {

	
	//  Update DefaultVisibleAnnotationsConstants as needed when change this class
	
	//  Peptide and PSM
	
	//  Filterable
	
	public static final String ANNOTATION_NAME_Q_VALUE = "q-value";
	public static final String ANNOTATION_DESCRIPTION_Q_VALUE = "The minimum false discovery among all predictions with this score or better.";
	
	public static final String ANNOTATION_NAME_P_VALUE = "p-value";
	public static final String ANNOTATION_DESCRIPTION_P_VALUE = "The computed p-value.";

	public static final String ANNOTATION_NAME_SVM_SCORE = "SVM Score";
	public static final String ANNOTATION_DESCRIPTION_SVM_SCORE = "Support vector machine score. The signed distance from x to the decision boundary. More strongly positive scores indicate a more confident prediction.";

	public static final String ANNOTATION_NAME_PEP = "PEP";
	public static final String ANNOTATION_DESCRIPTION_PEP = "Posterior error probability. The probability that this identification is incorrect.";

	//  Descriptive
	
//	public static final String ANNOTATION_NAME_CALC_MASS = "Calc Mass";

	public static final String ANNOTATION_NAME_SCAN_NUMBER = "Scan Number";
}
