package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;

/**
 * Result from KojakConfFileReader
 *
 */
public class KojakConfFileReaderResult {

	private ConfigurationFile configurationFile;

	private List<StaticModification> staticModificationListForThisFile = new ArrayList<>();

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



}
