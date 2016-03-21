package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.psm_processing;

import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PsmIdSplitObject;

/**
 * 
 *
 */
public class PsmMatchingPSMDataHolder {


	private IPsm percolatorPsmData;
	private PsmIdSplitObject psmIdSplitObject;
	
	private KojakPsmDataObject kojakPsmDataObject;
	
	private boolean kojakMatchesMultiplePercolatorPsms;
	
	
	
	public boolean isKojakMatchesMultiplePercolatorPsms() {
		return kojakMatchesMultiplePercolatorPsms;
	}
	public void setKojakMatchesMultiplePercolatorPsms(
			boolean kojakMatchesMultiplePercolatorPsms) {
		this.kojakMatchesMultiplePercolatorPsms = kojakMatchesMultiplePercolatorPsms;
	}
	public KojakPsmDataObject getKojakPsmDataObject() {
		return kojakPsmDataObject;
	}
	public void setKojakPsmDataObject(KojakPsmDataObject kojakPsmDataObject) {
		this.kojakPsmDataObject = kojakPsmDataObject;
	}
	public IPsm getPercolatorPsmData() {
		return percolatorPsmData;
	}
	public void setPercolatorPsmData(IPsm percolatorPsmData) {
		this.percolatorPsmData = percolatorPsmData;
	}
	public PsmIdSplitObject getPsmIdSplitObject() {
		return psmIdSplitObject;
	}
	public void setPsmIdSplitObject(PsmIdSplitObject psmIdSplitObject) {
		this.psmIdSplitObject = psmIdSplitObject;
	}
	
}
