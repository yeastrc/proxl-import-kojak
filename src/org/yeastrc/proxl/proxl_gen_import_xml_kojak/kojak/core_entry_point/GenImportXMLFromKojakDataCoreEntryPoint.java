package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.core_entry_point;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.command_line_options_container.CommandLineOptionsContainer;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.is_monolink.IsModificationAMonolink;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.ProcessKojakConfFile;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main.ProcessKojakFileOnly;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.annotation_sort_order.AddKojakAndPercolatorAnnotationSortOrder;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.default_visible_annotations.AddKojakAndPercolatorDefaultVisibleAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

/**
 * This is the internal core entry point to generating the import XML from Kojak data
 *
 */
public class GenImportXMLFromKojakDataCoreEntryPoint {


	private static final Logger log = Logger.getLogger( GenImportXMLFromKojakDataCoreEntryPoint.class );
	

	/**
	 * private constructor
	 */
	private GenImportXMLFromKojakDataCoreEntryPoint() { }

	public static GenImportXMLFromKojakDataCoreEntryPoint getInstance() {
		
		return new GenImportXMLFromKojakDataCoreEntryPoint();
	}
	
	

	
	/**
	 * @param projectId
	 * @param fastaFilename
	 * @param kojakConfFilenameCommandLine
	 * @param linkerNameString
	 * @param searchName
	 * @param proteinNameDecoyPrefix
	 * @param forceDropKojakDuplicateRecordsOptOnCommandLine
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

			File kojakOutputFile,
			File kojakConfFile,
			
			File outputFile
			
			) throws Exception {
		
		
		IsModificationAMonolink.setMonolinkModificationMasses( monolinkModificationMasses );
		
		
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
			
			
			ProcessKojakFileOnly.getInstance().processKojakFile( kojakOutputFile, proxlInputRoot );

			
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
