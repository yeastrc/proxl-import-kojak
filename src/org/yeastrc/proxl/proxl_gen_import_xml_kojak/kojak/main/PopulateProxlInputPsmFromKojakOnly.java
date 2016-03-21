package org.yeastrc.proxl.proxl_gen_import_xml_kojak.kojak.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.constants.SearchProgramNameKojakImporterConstants;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.exceptions.ProxlGenXMLDataException;
import org.yeastrc.proxl.proxl_gen_import_xml_kojak.common.kojak.KojakPsmDataObject;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;

public class PopulateProxlInputPsmFromKojakOnly {

//	private static final Logger log = Logger.getLogger( PopulateProxlInputPsmFromKojakOnly.class );

	//  private constructor
	private PopulateProxlInputPsmFromKojakOnly() {}


	public static PopulateProxlInputPsmFromKojakOnly getInstance() {

		return new PopulateProxlInputPsmFromKojakOnly();
	}




	/**
	 * Populate a single PSM in the output object structure for Proxl XML file
	 * 
	 * @param percolatorPeptide
	 * @return
	 * @throws ProxlGenXMLDataException 
	 */
	public Psm populateProxlInputPsm( KojakPsmDataObject kojakPsmDataObject ) throws ProxlGenXMLDataException {

		Psm proxlInputPsm = new Psm();

		proxlInputPsm.setPrecursorCharge( BigInteger.valueOf( kojakPsmDataObject.getCharge() ) );

		proxlInputPsm.setLinkerMass( kojakPsmDataObject.getLinkerMass() );

		//  Leave null since only have one scan file.  Whatever scan file is imported will be used.
		//		proxlInputPsm.setScanFileName(  );

		proxlInputPsm.setScanNumber( BigInteger.valueOf( kojakPsmDataObject.getScanNumber() ) );


		///////////////////////////////////////

		/////////////    Filterable Annotations

		{
			FilterablePsmAnnotations filterablePsmAnnotations = new FilterablePsmAnnotations();
			proxlInputPsm.setFilterablePsmAnnotations( filterablePsmAnnotations );

			List<FilterablePsmAnnotation> filterablePsmAnnotationList =
					filterablePsmAnnotations.getFilterablePsmAnnotation();


			////////////   Kojak annotations

			{
				Map<String, BigDecimal> kojakFilteredAnnotations = kojakPsmDataObject.getFilteredAnnotations();

				for ( Map.Entry<String, BigDecimal> entry : kojakFilteredAnnotations.entrySet() ) {

					FilterablePsmAnnotation filterablePsmAnnotation = new FilterablePsmAnnotation();
					filterablePsmAnnotationList.add( filterablePsmAnnotation );

					filterablePsmAnnotation.setAnnotationName( entry.getKey() );

					filterablePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );

					filterablePsmAnnotation.setValue( entry.getValue() );

				}
			}

		}


		///////////////////////////////////////

		/////////////    Descriptive Annotations

		{
			DescriptivePsmAnnotations descriptivePsmAnnotations = new DescriptivePsmAnnotations();
			proxlInputPsm.setDescriptivePsmAnnotations( descriptivePsmAnnotations );

			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
					descriptivePsmAnnotations.getDescriptivePsmAnnotation();


			////////////   Kojak annotations

			{
				Map<String, String> kojakDescriptiveAnnotations = kojakPsmDataObject.getDescriptiveAnnotations();

				for ( Map.Entry<String, String> entry : kojakDescriptiveAnnotations.entrySet() ) {

					DescriptivePsmAnnotation descriptivePsmAnnotation = new DescriptivePsmAnnotation();
					descriptivePsmAnnotationList.add( descriptivePsmAnnotation );

					descriptivePsmAnnotation.setAnnotationName( entry.getKey() );

					descriptivePsmAnnotation.setSearchProgram( SearchProgramNameKojakImporterConstants.KOJAK );

					descriptivePsmAnnotation.setValue( entry.getValue() );
				}
			}
		}

		//////////////////////////////

		return proxlInputPsm;
	}

}
