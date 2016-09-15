package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.KojakAnnotationTypeConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl_import.api.xml_dto.AnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.PsmAnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotationCutoff;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;

/**
 * 
 *
 */
public class AddKojakCutoffOnImport {

	/**
	 * private constructor
	 */
	private AddKojakCutoffOnImport() { }

	public static AddKojakCutoffOnImport getInstance() {
		return new AddKojakCutoffOnImport();
	}
	
	/**
	 * @param scoreCutoffOnImport
	 * @param searchProgramInfo
	 */
	public void addCutoffsOnImport( BigDecimal scoreCutoffOnImport, SearchProgramInfo searchProgramInfo ) {
		
		if ( scoreCutoffOnImport == null ) {
			
			return;
		}
		
		AnnotationCutoffsOnImport annotationCutoffsOnImport = new AnnotationCutoffsOnImport();
		
		searchProgramInfo.setAnnotationCutoffsOnImport( annotationCutoffsOnImport );
		
		PsmAnnotationCutoffsOnImport psmAnnotationCutoffsOnImport = new PsmAnnotationCutoffsOnImport();
		annotationCutoffsOnImport.setPsmAnnotationCutoffsOnImport( psmAnnotationCutoffsOnImport );

		List<SearchAnnotationCutoff> searchAnnotationCutoffList = psmAnnotationCutoffsOnImport.getSearchAnnotationCutoff();

		SearchAnnotationCutoff searchAnnotationCutoff = new SearchAnnotationCutoff();
		searchAnnotationCutoffList.add(searchAnnotationCutoff);

		searchAnnotationCutoff.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );
		searchAnnotationCutoff.setAnnotationName( KojakAnnotationTypeConstants.KOJAK_ANNOTATION_NAME_SCORE );
		searchAnnotationCutoff.setCutoffValue( scoreCutoffOnImport );
	}
}
