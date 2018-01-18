package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;

/**
 * Result from KojakConfFileReader
 *
 */
public class KojakConfFileReaderResult {

	private ConfigurationFile configurationFile;

	private List<StaticModification> staticModificationListForThisFile = new ArrayList<>();
	
	private List<String> decoyIdentificationStringFromConfFileList;
	
	private Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile;
	
	private File fastaFile;

	/**
	 * Not currently used here but is used by YRC for it's processing so must keep this and populate it in KojakConfFileReader
	 */
	private String kojakInputFilenamePossiblyWithPath;
	
	

	public ConfigurationFile getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(ConfigurationFile configurationFile) {
		this.configurationFile = configurationFile;
	}

	public List<StaticModification> getStaticModificationListForThisFile() {
		return staticModificationListForThisFile;
	}

	public void setStaticModificationListForThisFile(
			List<StaticModification> staticModificationListForThisFile) {
		this.staticModificationListForThisFile = staticModificationListForThisFile;
	}

	public String getKojakInputFilenamePossiblyWithPath() {
		return kojakInputFilenamePossiblyWithPath;
	}

	public void setKojakInputFilenamePossiblyWithPath(
			String kojakInputFilenamePossiblyWithPath) {
		this.kojakInputFilenamePossiblyWithPath = kojakInputFilenamePossiblyWithPath;
	}

	public List<String> getDecoyIdentificationStringFromConfFileList() {
		return decoyIdentificationStringFromConfFileList;
	}

	public void setDecoyIdentificationStringFromConfFileList(
			List<String> decoyIdentificationStringFromConfFileList) {
		this.decoyIdentificationStringFromConfFileList = decoyIdentificationStringFromConfFileList;
	}

	public File getFastaFile() {
		return fastaFile;
	}

	public void setFastaFile(File fastaFile) {
		this.fastaFile = fastaFile;
	}

	public Isotope_Labels_SpecifiedIn_KojakConfFile getIsotopes_SpecifiedIn_KojakConfFile() {
		return isotopes_SpecifiedIn_KojakConfFile;
	}

	public void setIsotopes_SpecifiedIn_KojakConfFile(
			Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile) {
		this.isotopes_SpecifiedIn_KojakConfFile = isotopes_SpecifiedIn_KojakConfFile;
	}


}
