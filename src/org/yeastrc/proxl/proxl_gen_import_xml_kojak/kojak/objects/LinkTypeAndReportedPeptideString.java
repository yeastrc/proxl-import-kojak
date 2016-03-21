package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.objects;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.enums.KojakGenImportInternalLinkTypeEnum;

public class LinkTypeAndReportedPeptideString {

	private KojakGenImportInternalLinkTypeEnum KojakGenImportInternalLinkTypeEnum;
	private String reportedPeptideString;
	
	
	public KojakGenImportInternalLinkTypeEnum getKojakGenImportInternalLinkTypeEnum() {
		return KojakGenImportInternalLinkTypeEnum;
	}
	public void setKojakGenImportInternalLinkTypeEnum(
			KojakGenImportInternalLinkTypeEnum kojakGenImportInternalLinkTypeEnum) {
		KojakGenImportInternalLinkTypeEnum = kojakGenImportInternalLinkTypeEnum;
	}
	public String getReportedPeptideString() {
		return reportedPeptideString;
	}
	public void setReportedPeptideString(String reportedPeptideString) {
		this.reportedPeptideString = reportedPeptideString;
	}
}
