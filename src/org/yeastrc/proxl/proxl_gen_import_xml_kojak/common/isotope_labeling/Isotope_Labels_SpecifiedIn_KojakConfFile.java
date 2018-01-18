package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling;

/**
 * The Isotope Labels specified in the Kojak Conf File
 *
 */
public class Isotope_Labels_SpecifiedIn_KojakConfFile {

	/**
	 * 
	 */
	private String isotopeLabel_15N_filter_Value;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((isotopeLabel_15N_filter_Value == null) ? 0 : isotopeLabel_15N_filter_Value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Isotope_Labels_SpecifiedIn_KojakConfFile other = (Isotope_Labels_SpecifiedIn_KojakConfFile) obj;
		if (isotopeLabel_15N_filter_Value == null) {
			if (other.isotopeLabel_15N_filter_Value != null)
				return false;
		} else if (!isotopeLabel_15N_filter_Value.equals(other.isotopeLabel_15N_filter_Value))
			return false;
		return true;
	}
	

	public String getIsotopeLabel_15N_filter_Value() {
		return isotopeLabel_15N_filter_Value;
	}

	public void setIsotopeLabel_15N_filter_Value(String isotopeLabel_15N_filter_Value) {
		this.isotopeLabel_15N_filter_Value = isotopeLabel_15N_filter_Value;
	}

}
