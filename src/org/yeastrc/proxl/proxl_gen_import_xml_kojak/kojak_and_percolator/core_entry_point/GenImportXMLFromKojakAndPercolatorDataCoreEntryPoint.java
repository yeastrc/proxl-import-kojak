package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.core_entry_point;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.annotation_sort_order.AddKojakAndPercolatorAnnotationSortOrder;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.command_line_options_container.CommandLineOptionsContainer;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.default_visible_annotations.AddKojakAndPercolatorDefaultVisibleAnnotations;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.ProcessKojakConfFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.kojak.ProcessKojakFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main.InitialProcessPsmsForCompareToKojak;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main.ProcessPercolatorFileList;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.main.UnmarshallPercolatorFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.objects.PercolatorFileAndUnmarshalledObject;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils.VerifyAllPercolatorVersionsSame_RetrievePercolatorVersion;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.utils.VerifyNoDuplicatePercolatorPSMs;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.psm_processing.PsmMatchingAndCollection;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

/**
 * This is the internal core entry point to generating the import XML from Kojak and Percolator data
 *
 */
public class GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint {

	private static final Logger log = Logger.getLogger( GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint.class );
	

	/**
	 * private constructor
	 */
	private GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint() { }

	public static GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint getInstance() {
		
		return new GenImportXMLFromKojakAndPercolatorDataCoreEntryPoint();
	}
	
	

	
	/**
	 * @param projectId
	 * @param fastaFilename
	 * @param kojakConfFilenameCommandLine
	 * @param linkerNameString
	 * @param searchName
	 * @param proteinNameDecoyPrefix
	 * @param forceDropKojakDuplicateRecordsOptOnCommandLine
	 * @param percolatorFileList
	 * @param kojakOutputFileList
	 * @param scanFile
	 * @throws Exception
	 */
	public void doGenFile( 
			
			String fastaFilename,
			String linkerNameString,
			String searchName,
			String proteinNameDecoyPrefix,
			
			Set<BigDecimal> monolinkModificationMasses,
			
			boolean forceDropKojakDuplicateRecordsOptOnCommandLine,

			List<File> percolatorFileList, 
			File kojakOutputFile,
			File kojakConfFile,
			
			File outputFile
			
			) throws Exception {
		
		if ( monolinkModificationMasses != null ) {

			IsModificationAMonolink.getInstance().setMonolinkModificationMasses( monolinkModificationMasses );
		}
		
		
		//  The object graph that will be serialized to generate the import XML file 
		
		ProxlInput proxlInputRoot = new ProxlInput();
		
		proxlInputRoot.setFastaFilename( fastaFilename );
		
		
//		proxlInputRoot.setComment(  );
		
		proxlInputRoot.setName( searchName );
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		proxlInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		
		AddKojakAndPercolatorDefaultVisibleAnnotations.getInstance().addDefaultVisibleAnnotations( searchProgramInfo );
		
		AddKojakAndPercolatorAnnotationSortOrder.getInstance().addAnnotationSortOrder( searchProgramInfo );
		
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );
		
		Linkers linkers = new Linkers();
		proxlInputRoot.setLinkers( linkers );

		List<Linker> linkerList = linkers.getLinker();
		
		Linker linker = new Linker();
		linkerList.add( linker );
		
		linker.setName( linkerNameString );

		//  TODO  Add more info to Linker??

		try {

			if ( forceDropKojakDuplicateRecordsOptOnCommandLine ) {

				CommandLineOptionsContainer.setForceDropKojakDuplicateRecordsOptOnCommandLine(true);
			}
			
			
			if ( kojakOutputFile == null ) {
				
				String msg = "kojakOutputFile cannot be null";
				log.error( msg );
				
				throw new IllegalArgumentException(msg);
			}
			

			if ( kojakConfFile == null ) {
				
				String msg = "kojakConfFile cannot be null";
				log.error( msg );
				
				throw new IllegalArgumentException(msg);
			}

			

			List<PercolatorFileAndUnmarshalledObject> percolatorFileAndUnmarshalledObjectList = new ArrayList<>( percolatorFileList.size() );

			//  Unmarshall the Percolator file(s)

			for ( File percFile : percolatorFileList ) {

				IPercolatorOutput percOutputRootObject = 
						UnmarshallPercolatorFile.getInstance().unmarshallPercolatorFile( percFile );

				PercolatorFileAndUnmarshalledObject percolatorFileAndUnmarshalledObject = new PercolatorFileAndUnmarshalledObject();

				percolatorFileAndUnmarshalledObject.setPercolatorFile( percFile );
				percolatorFileAndUnmarshalledObject.setPercOutputRootObject( percOutputRootObject );

				percolatorFileAndUnmarshalledObjectList.add( percolatorFileAndUnmarshalledObject );
			}

			
			//  Throws Exception on error
			String percolatorVersion =
					VerifyAllPercolatorVersionsSame_RetrievePercolatorVersion.verifyAllPercolatorVersionsSame_RetrievePercolatorVersion( percolatorFileAndUnmarshalledObjectList );

			//  Throws Exception on error
			VerifyNoDuplicatePercolatorPSMs.verifyNoDuplicatePercolatorPSMs( percolatorFileAndUnmarshalledObjectList );

			
			
			PsmMatchingAndCollection psmMatchingAndCollection = 
					InitialProcessPsmsForCompareToKojak.getInstance().initialProcessPsmsForCompareToKojak( percolatorFileAndUnmarshalledObjectList );

			
			ProcessKojakConfFile.getInstance().processKojakConfFile( kojakConfFile, proxlInputRoot );
			

			if ( StringUtils.isNotEmpty( proteinNameDecoyPrefix ) ) {
				
				//  Decoys provided on command line so Override decoys from conf file 

				DecoyLabels decoyLabels = new DecoyLabels();
				proxlInputRoot.setDecoyLabels( decoyLabels );

				List<DecoyLabel> decoyLabelList = decoyLabels.getDecoyLabel();

				DecoyLabel decoyLabel = new DecoyLabel();
				decoyLabelList.add( decoyLabel );

				decoyLabel.setPrefix( proteinNameDecoyPrefix );
			}
			
			
			
			ProcessKojakFile.getInstance().processKojakFile( 
							kojakOutputFile,
							proxlInputRoot, 
							psmMatchingAndCollection );

			if ( ! psmMatchingAndCollection.doAllPercPSMsHaveKojakRecords() ) {
				
				String msg = "Not all Percolator PSMs have Kojak records.";
				log.error( msg );
				throw new ProxlGenXMLDataException(msg);
			}
			
			ProcessPercolatorFileList.getInstance().processPercolatorFileList(
					percolatorFileAndUnmarshalledObjectList, proxlInputRoot, psmMatchingAndCollection, percolatorVersion );

			
			
			try {
			
				CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( outputFile, proxlInputRoot );
				
			} catch ( Exception e ) {
				
				String msg = "Error writing output file: " + e.toString();
				log.error( msg, e );
				
				throw new Exception(msg, e);
			}

			
			
			
		} catch ( Exception e ) {
			
			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );
			
			e.printStackTrace( System.out );
			e.printStackTrace( System.err );
			
			
			throw e;
		}
		
	}
}
