package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.cutoffs_on_import;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak_and_percolator.percolator.constants.PercolatorGenImportXML_AnnotationNames_Constants;
import org.yeastrc.proxl_import.api.xml_dto.AnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.PsmAnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptideAnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotationCutoff;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;


/**
 * 
 *
 */
public class AddPercolatorCutoffsOnImport {

	/**
	 * private constructor
	 */
	private AddPercolatorCutoffsOnImport() { }

	public static AddPercolatorCutoffsOnImport getInstance() {
		return new AddPercolatorCutoffsOnImport();
	}
	
	/**
	 * @param qvaluePSMCutoffOnImport
	 * @param qvaluePeptideCutoffOnImport
	 * @param searchProgramInfo
	 */
	public void addCutoffsOnImport( BigDecimal qvaluePSMCutoffOnImport, BigDecimal qvaluePeptideCutoffOnImport, SearchProgramInfo searchProgramInfo ) {
		
		if ( qvaluePSMCutoffOnImport == null && qvaluePeptideCutoffOnImport == null ) {
			
			return;
		}
		
		AnnotationCutoffsOnImport annotationCutoffsOnImport = new AnnotationCutoffsOnImport();
		
		searchProgramInfo.setAnnotationCutoffsOnImport( annotationCutoffsOnImport );
		

		if ( qvaluePSMCutoffOnImport != null ) {
			
			PsmAnnotationCutoffsOnImport psmAnnotationCutoffsOnImport = new PsmAnnotationCutoffsOnImport();
			annotationCutoffsOnImport.setPsmAnnotationCutoffsOnImport( psmAnnotationCutoffsOnImport );
			
			List<SearchAnnotationCutoff> searchAnnotationCutoffList = psmAnnotationCutoffsOnImport.getSearchAnnotationCutoff();
			
			SearchAnnotationCutoff searchAnnotationCutoff = new SearchAnnotationCutoff();
			searchAnnotationCutoffList.add(searchAnnotationCutoff);
			
			searchAnnotationCutoff.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			searchAnnotationCutoff.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
			searchAnnotationCutoff.setCutoffValue( qvaluePSMCutoffOnImport );
		}
		


		if ( qvaluePeptideCutoffOnImport != null ) {
			
			ReportedPeptideAnnotationCutoffsOnImport reportedPeptideAnnotationCutoffsOnImport = new ReportedPeptideAnnotationCutoffsOnImport();
			annotationCutoffsOnImport.setReportedPeptideAnnotationCutoffsOnImport( reportedPeptideAnnotationCutoffsOnImport );
			
			List<SearchAnnotationCutoff> searchAnnotationCutoffList = reportedPeptideAnnotationCutoffsOnImport.getSearchAnnotationCutoff();
			
			SearchAnnotationCutoff searchAnnotationCutoff = new SearchAnnotationCutoff();
			searchAnnotationCutoffList.add(searchAnnotationCutoff);
			
			searchAnnotationCutoff.setSearchProgram( SearchProgramNameKojakImporterConstants.PERCOLATOR );
			searchAnnotationCutoff.setAnnotationName( PercolatorGenImportXML_AnnotationNames_Constants.ANNOTATION_NAME_Q_VALUE );
			searchAnnotationCutoff.setCutoffValue( qvaluePeptideCutoffOnImport );
		}
	}
		
}
