package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 *
 */
public class KojakPsmDataObject {

	private int scanNumber;
	private int charge;
	
	private BigDecimal linkerMass;
	
	private String peptide_1;
	private String peptide_2;
	
	private String link_1;
	private String link_2;

	private String protein_1;
	private String protein_2;
	
	private String obsMass;
	private String psmMass;
	private String ppmError;
	
//	Obs Mass	Charge	PSM Mass	PPM Error


	
	private Map<String, BigDecimal> filteredAnnotations;
	
	private Map<String, String> descriptiveAnnotations;

	
	
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
	
}
