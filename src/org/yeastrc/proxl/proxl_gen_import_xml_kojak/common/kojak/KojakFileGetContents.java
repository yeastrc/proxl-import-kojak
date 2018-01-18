package org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.isotope_labeling.Isotope_Labels_SpecifiedIn_KojakConfFile;

/**
 * Get the contents of the Kojak file
 *
 */
public class KojakFileGetContents {

	private static final Logger log = Logger.getLogger(KojakFileReader.class);
	
	
	/**
	 * Method result
	 *
	 */
	public static class KojakFileGetContentsResult {
		
		private KojakFileReader kojakFileReader;
		private List<KojakPsmDataObject> kojakPsmDataObjectList;
		
		public KojakFileReader getKojakFileReader() {
			return kojakFileReader;
		}
		public void setKojakFileReader(KojakFileReader kojakFileReader) {
			this.kojakFileReader = kojakFileReader;
		}
		public List<KojakPsmDataObject> getKojakPsmDataObjectList() {
			return kojakPsmDataObjectList;
		}
		public void setKojakPsmDataObjectList(List<KojakPsmDataObject> kojakPsmDataObjectList) {
			this.kojakPsmDataObjectList = kojakPsmDataObjectList;
		}
	}
	
	
	/**
	 * private constructor
	 */
	private KojakFileGetContents() {}
	static public KojakFileGetContents getInstance() throws Exception {
		return new KojakFileGetContents();
	}
	
	public KojakFileGetContentsResult kojakFileGetContents( File inputFile, Isotope_Labels_SpecifiedIn_KojakConfFile isotopes_SpecifiedIn_KojakConfFile ) throws Exception {

		List<KojakPsmDataObject> kojakPsmDataObjectInitialList = new ArrayList<>( 1000000 );
		KojakFileReader kojakFileReader = null;
		
		try {
			
			//  The reader reads the version line and the header lines in the getInstance(...) method
			
			kojakFileReader = KojakFileReader.getInstance( inputFile, isotopes_SpecifiedIn_KojakConfFile );
			
			//  Process the data lines:

			while (true) {

				KojakPsmDataObject kojakPsmDataObject;

				try {
					kojakPsmDataObject = kojakFileReader.getNextKojakLine();
					
				} catch ( Exception e ) {
					String msg = "Error reading Kojak file (file: " + inputFile.getAbsolutePath() + ") .";
					log.error( msg, e );
					throw e;
				}

				if ( kojakPsmDataObject == null ) {

					break;  //  EARLY EXIT from LOOOP
				}
				
				kojakPsmDataObjectInitialList.add( kojakPsmDataObject );
			}
		} catch ( Exception e ) {
			String msg = "Error processing Kojak file: " + inputFile.getAbsolutePath();
			log.error( msg );
			throw e;
		} finally {
			if ( kojakFileReader != null  ) {
				kojakFileReader.close();
			}
		}
		

//		if ( kojakFileReader.getProgramVersion() != null ) {
//			String kojakVersion = kojakFileReader.getProgramVersion();
//			boolean versionNotSupported = false;
//			if ( kojakVersion.startsWith( "1.6" ) ) {
//				versionNotSupported = true;
//			} else {
//				if ( kojakVersion.startsWith( "1." ) ) {
//					if ( kojakVersion.length() > 2
//							&& kojakVersion.charAt( 2 ) >= '6' ) { 
//						versionNotSupported = true;
//					}
//				} else {
//					versionNotSupported = true;
//				}
//			}
//			if ( versionNotSupported ) {
//				String msg = "Kojak version '1.6' and later is not supported with Percolator";
//				System.out.println( msg );
//				System.err.println( msg );
//				throw new Exception( msg );
//			}
//		}
				
		
//		// Transfer to Set to remove duplicates, see KojakPsmDataObject.equals()
//		Set<KojakPsmDataObject> kojakPsmDataObjectSet = new HashSet<>( kojakPsmDataObjectInitialList );
//		
//		List<KojakPsmDataObject> kojakPsmDataObjectFinalList = new ArrayList<>( kojakPsmDataObjectSet );
//		
//		Collections.sort( kojakPsmDataObjectFinalList ); Sort on kojakFileLineNumber

		List<KojakPsmDataObject> kojakPsmDataObjectFinalList = kojakPsmDataObjectInitialList;
				
		KojakFileGetContentsResult kojakFileGetContentsResult = new KojakFileGetContentsResult();
		kojakFileGetContentsResult.setKojakFileReader( kojakFileReader );
		kojakFileGetContentsResult.setKojakPsmDataObjectList( kojakPsmDataObjectFinalList );
		
		return kojakFileGetContentsResult;
	
	}	
}
