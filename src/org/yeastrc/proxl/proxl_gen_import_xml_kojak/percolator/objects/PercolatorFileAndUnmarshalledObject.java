package org.yeastrc.proxl.proxl_gen_import_xml_kojak.percolator.objects;

import java.io.File;

import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;

/**
 * A percolator File object and that percolator file unmarshalled
 *
 */
public class PercolatorFileAndUnmarshalledObject {

	private File percolatorFile;
	private IPercolatorOutput percOutputRootObject;

//	private PercolatorFileDTO percolatorFileDTO;
	
	
	
//	public PercolatorFileDTO getPercolatorFileDTO() {
//		return percolatorFileDTO;
//	}
//
//	public void setPercolatorFileDTO(PercolatorFileDTO percolatorFileDTO) {
//		this.percolatorFileDTO = percolatorFileDTO;
//	}

	public File getPercolatorFile() {
		return percolatorFile;
	}

	public void setPercolatorFile(File percolatorFile) {
		this.percolatorFile = percolatorFile;
	}

	public IPercolatorOutput getPercOutputRootObject() {
		return percOutputRootObject;
	}

	public void setPercOutputRootObject(IPercolatorOutput percOutputRootObject) {
		this.percOutputRootObject = percOutputRootObject;
	}
}
