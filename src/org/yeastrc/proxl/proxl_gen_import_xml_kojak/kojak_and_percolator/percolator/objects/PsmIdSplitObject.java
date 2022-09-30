package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects;

/**
 * The "psm_id" split into it's parts
 *
 */
public class PsmIdSplitObject {
	
	/**
	 * original psm string
	 */
	private String psmIdString;
	
	private String scanFilename_MainPart_String;
	
	private String scanNumberString;

	private int scanNumber = 0;
	
//	private String chargeString;


	public String getPsmIdString() {
		return psmIdString;
	}

	public void setPsmIdString(String psmIdString) {
		this.psmIdString = psmIdString;
	}
	


	public String getScanNumberString() {
		return scanNumberString;
	}

	public void setScanNumberString(String scanNumberString) {
		this.scanNumberString = scanNumberString;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public String getScanFilename_MainPart_String() {
		return scanFilename_MainPart_String;
	}

	public void setScanFilename_MainPart_String(String scanFilename_MainPart_String) {
		this.scanFilename_MainPart_String = scanFilename_MainPart_String;
	}

}
