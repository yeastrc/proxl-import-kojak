package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 *
 */
public class KojakPsmDataObject { 
	
	/**
	 * Line number in Kojak file
	 */
	private int kojakFileLineNumber;

	private int scanNumber;
	private int charge;
	
	private BigDecimal linkerMass;
	
	private String peptide_1;
	private String peptide_2;

	//  Used to create reported peptide
	private String peptide_1_Isotope_Label_Suffix_From_Kojak_File;
	private String peptide_2_Isotope_Label_Suffix_From_Kojak_File;

	private String peptide_1_Isotope_Label_For_ProxlXML_File;
	private String peptide_2_Isotope_Label_For_ProxlXML_File;
	
	private String link_1;
	private String link_2;

	private BigDecimal peptide_1_score;
	private BigDecimal peptide_2_score;
	
	// Not Stored 
	private String protein_1;
	private String protein_1_site;
	private String protein_2;
	private String protein_2_site;

	
	private String obsMass;
	private String psmMass;
	private String ppmError;
	
//	Obs Mass	Charge	PSM Mass	PPM Error


	
	private Map<String, BigDecimal> filteredAnnotations;
	
	private Map<String, String> descriptiveAnnotations;



	@Override
	public String toString() {
		return "KojakPsmDataObject [scanNumber=" + scanNumber + ", charge=" + charge + ", linkerMass=" + linkerMass
				+ ", peptide_1=" + peptide_1 + ", peptide_2=" + peptide_2
				+ ", peptide_1_Isotope_Label_For_ProxlXML_File=" + peptide_1_Isotope_Label_For_ProxlXML_File
				+ ", peptide_2_Isotope_Label_For_ProxlXML_File=" + peptide_2_Isotope_Label_For_ProxlXML_File
				+ ", link_1=" + link_1 + ", link_2=" + link_2 + ", peptide_1_score=" + peptide_1_score
				+ ", peptide_2_score=" + peptide_2_score + ", protein_1=" + protein_1 + ", protein_1_site="
				+ protein_1_site + ", protein_2=" + protein_2 + ", protein_2_site=" + protein_2_site + ", obsMass="
				+ obsMass + ", psmMass=" + psmMass + ", ppmError=" + ppmError + ", filteredAnnotations="
				+ filteredAnnotations + ", descriptiveAnnotations=" + descriptiveAnnotations + "]";
	}

	
	//  Excluded from hashCode and equals:  protein_1, protein_1_site, protein_2, protein_2_site, filteredAnnotations, descriptiveAnnotations


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charge;
		result = prime * result + ((link_1 == null) ? 0 : link_1.hashCode());
		result = prime * result + ((link_2 == null) ? 0 : link_2.hashCode());
		result = prime * result + ((linkerMass == null) ? 0 : linkerMass.hashCode());
		result = prime * result + ((obsMass == null) ? 0 : obsMass.hashCode());
		result = prime * result + ((peptide_1 == null) ? 0 : peptide_1.hashCode());
		result = prime * result + ((peptide_1_Isotope_Label_For_ProxlXML_File == null) ? 0
				: peptide_1_Isotope_Label_For_ProxlXML_File.hashCode());
		result = prime * result + ((peptide_1_score == null) ? 0 : peptide_1_score.hashCode());
		result = prime * result + ((peptide_2 == null) ? 0 : peptide_2.hashCode());
		result = prime * result + ((peptide_2_Isotope_Label_For_ProxlXML_File == null) ? 0
				: peptide_2_Isotope_Label_For_ProxlXML_File.hashCode());
		result = prime * result + ((peptide_2_score == null) ? 0 : peptide_2_score.hashCode());
		result = prime * result + ((ppmError == null) ? 0 : ppmError.hashCode());
		result = prime * result + ((psmMass == null) ? 0 : psmMass.hashCode());
		result = prime * result + scanNumber;
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
		KojakPsmDataObject other = (KojakPsmDataObject) obj;
		if (charge != other.charge)
			return false;
		if (link_1 == null) {
			if (other.link_1 != null)
				return false;
		} else if (!link_1.equals(other.link_1))
			return false;
		if (link_2 == null) {
			if (other.link_2 != null)
				return false;
		} else if (!link_2.equals(other.link_2))
			return false;
		if (linkerMass == null) {
			if (other.linkerMass != null)
				return false;
		} else if (!linkerMass.equals(other.linkerMass))
			return false;
		if (obsMass == null) {
			if (other.obsMass != null)
				return false;
		} else if (!obsMass.equals(other.obsMass))
			return false;
		if (peptide_1 == null) {
			if (other.peptide_1 != null)
				return false;
		} else if (!peptide_1.equals(other.peptide_1))
			return false;
		if (peptide_1_Isotope_Label_For_ProxlXML_File == null) {
			if (other.peptide_1_Isotope_Label_For_ProxlXML_File != null)
				return false;
		} else if (!peptide_1_Isotope_Label_For_ProxlXML_File.equals(other.peptide_1_Isotope_Label_For_ProxlXML_File))
			return false;
		if (peptide_1_score == null) {
			if (other.peptide_1_score != null)
				return false;
		} else if (!peptide_1_score.equals(other.peptide_1_score))
			return false;
		if (peptide_2 == null) {
			if (other.peptide_2 != null)
				return false;
		} else if (!peptide_2.equals(other.peptide_2))
			return false;
		if (peptide_2_Isotope_Label_For_ProxlXML_File == null) {
			if (other.peptide_2_Isotope_Label_For_ProxlXML_File != null)
				return false;
		} else if (!peptide_2_Isotope_Label_For_ProxlXML_File.equals(other.peptide_2_Isotope_Label_For_ProxlXML_File))
			return false;
		if (peptide_2_score == null) {
			if (other.peptide_2_score != null)
				return false;
		} else if (!peptide_2_score.equals(other.peptide_2_score))
			return false;
		if (ppmError == null) {
			if (other.ppmError != null)
				return false;
		} else if (!ppmError.equals(other.ppmError))
			return false;
		if (psmMass == null) {
			if (other.psmMass != null)
				return false;
		} else if (!psmMass.equals(other.psmMass))
			return false;
		if (scanNumber != other.scanNumber)
			return false;
		return true;
	}



	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public BigDecimal getLinkerMass() {
		return linkerMass;
	}

	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}

	public String getPeptide_1() {
		return peptide_1;
	}

	public void setPeptide_1(String peptide_1) {
		this.peptide_1 = peptide_1;
	}

	public String getPeptide_2() {
		return peptide_2;
	}

	public void setPeptide_2(String peptide_2) {
		this.peptide_2 = peptide_2;
	}

	public String getLink_1() {
		return link_1;
	}

	public void setLink_1(String link_1) {
		this.link_1 = link_1;
	}

	public String getLink_2() {
		return link_2;
	}

	public void setLink_2(String link_2) {
		this.link_2 = link_2;
	}

	public Map<String, BigDecimal> getFilteredAnnotations() {
		return filteredAnnotations;
	}

	public void setFilteredAnnotations(Map<String, BigDecimal> filteredAnnotations) {
		this.filteredAnnotations = filteredAnnotations;
	}

	public Map<String, String> getDescriptiveAnnotations() {
		return descriptiveAnnotations;
	}

	public void setDescriptiveAnnotations(Map<String, String> descriptiveAnnotations) {
		this.descriptiveAnnotations = descriptiveAnnotations;
	}

	public String getObsMass() {
		return obsMass;
	}

	public void setObsMass(String obsMass) {
		this.obsMass = obsMass;
	}

	public String getPsmMass() {
		return psmMass;
	}

	public void setPsmMass(String psmMass) {
		this.psmMass = psmMass;
	}

	public String getPpmError() {
		return ppmError;
	}

	public void setPpmError(String ppmError) {
		this.ppmError = ppmError;
	}

	public String getProtein_1() {
		return protein_1;
	}

	public void setProtein_1(String protein_1) {
		this.protein_1 = protein_1;
	}

	public String getProtein_2() {
		return protein_2;
	}

	public void setProtein_2(String protein_2) {
		this.protein_2 = protein_2;
	}

	public BigDecimal getPeptide_1_score() {
		return peptide_1_score;
	}

	public void setPeptide_1_score(BigDecimal peptide_1_score) {
		this.peptide_1_score = peptide_1_score;
	}

	public BigDecimal getPeptide_2_score() {
		return peptide_2_score;
	}

	public void setPeptide_2_score(BigDecimal peptide_2_score) {
		this.peptide_2_score = peptide_2_score;
	}

	public String getProtein_1_site() {
		return protein_1_site;
	}

	public void setProtein_1_site(String protein_1_site) {
		this.protein_1_site = protein_1_site;
	}

	public String getProtein_2_site() {
		return protein_2_site;
	}

	public void setProtein_2_site(String protein_2_site) {
		this.protein_2_site = protein_2_site;
	}
	
	public String getPeptide_1_Isotope_Label_For_ProxlXML_File() {
		return peptide_1_Isotope_Label_For_ProxlXML_File;
	}


	public void setPeptide_1_Isotope_Label_For_ProxlXML_File(String peptide_1_Isotope_Label_For_ProxlXML_File) {
		this.peptide_1_Isotope_Label_For_ProxlXML_File = peptide_1_Isotope_Label_For_ProxlXML_File;
	}


	public String getPeptide_2_Isotope_Label_For_ProxlXML_File() {
		return peptide_2_Isotope_Label_For_ProxlXML_File;
	}


	public void setPeptide_2_Isotope_Label_For_ProxlXML_File(String peptide_2_Isotope_Label_For_ProxlXML_File) {
		this.peptide_2_Isotope_Label_For_ProxlXML_File = peptide_2_Isotope_Label_For_ProxlXML_File;
	}


	public int getKojakFileLineNumber() {
		return kojakFileLineNumber;
	}


	public void setKojakFileLineNumber(int kojakFileLineNumber) {
		this.kojakFileLineNumber = kojakFileLineNumber;
	}


	public String getPeptide_1_Isotope_Label_Suffix_From_Kojak_File() {
		return peptide_1_Isotope_Label_Suffix_From_Kojak_File;
	}


	public void setPeptide_1_Isotope_Label_Suffix_From_Kojak_File(String peptide_1_Isotope_Label_Suffix_From_Kojak_File) {
		this.peptide_1_Isotope_Label_Suffix_From_Kojak_File = peptide_1_Isotope_Label_Suffix_From_Kojak_File;
	}


	public String getPeptide_2_Isotope_Label_Suffix_From_Kojak_File() {
		return peptide_2_Isotope_Label_Suffix_From_Kojak_File;
	}


	public void setPeptide_2_Isotope_Label_Suffix_From_Kojak_File(String peptide_2_Isotope_Label_Suffix_From_Kojak_File) {
		this.peptide_2_Isotope_Label_Suffix_From_Kojak_File = peptide_2_Isotope_Label_Suffix_From_Kojak_File;
	}

}
