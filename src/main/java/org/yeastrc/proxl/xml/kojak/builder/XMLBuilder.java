/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.proxl.xml.kojak.builder;

import org.yeastrc.proxl.xml.kojak.annotations.*;
import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl.xml.kojak.objects.*;
import org.yeastrc.proxl_import.api.xml_dto.*;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmPerPeptideAnnotationTypes;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

public class XMLBuilder {

	public void buildAndSaveXML( ConversionParameters conversionParameters,
								 KojakResults kojakResults,
								 PercolatorResults percolatorResults,
								 int runType
			                    ) throws Exception {

		KojakAnalysis analysis = conversionParameters.getKojakAnalysis();

		// root node of the XML
		ProxlInput proxlInputRoot = new ProxlInput();

		proxlInputRoot.setFastaFilename( analysis.getFastaFile().getName() );
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		proxlInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		//
		// Define the sort order
		//
		AnnotationSortOrder annotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( annotationSortOrder );
		
		PsmAnnotationSortOrder psmAnnotationSortOrder = new PsmAnnotationSortOrder();
		annotationSortOrder.setPsmAnnotationSortOrder( psmAnnotationSortOrder );
		
		psmAnnotationSortOrder.getSearchAnnotation().addAll(PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder( runType ) );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		// add in kojak
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add(searchProgram);

			searchProgram.setName(ConverterConstants.PROGRAM_NAME_KOJAK);
			searchProgram.setDisplayName(ConverterConstants.PROGRAM_NAME_KOJAK);
			searchProgram.setVersion(kojakResults.getKojakVersion());

			// add psm annotation types
			{
				PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
				searchProgram.setPsmAnnotationTypes(psmAnnotationTypes);

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
				psmAnnotationTypes.setFilterablePsmAnnotationTypes(filterablePsmAnnotationTypes);
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll(PSMAnnotationTypes.getFilterablePsmAnnotationTypes(ConverterConstants.PROGRAM_NAME_KOJAK, runType));

			}

			// add psm per-peptide annotation types
			{
				PsmPerPeptideAnnotationTypes psmPerPeptideAnnotationTypes = new PsmPerPeptideAnnotationTypes();
				searchProgram.setPsmPerPeptideAnnotationTypes(psmPerPeptideAnnotationTypes);

				FilterablePsmPerPeptideAnnotationTypes filterablePsmPerPeptideAnnotationTypes = new FilterablePsmPerPeptideAnnotationTypes();
				psmPerPeptideAnnotationTypes.setFilterablePsmPerPeptideAnnotationTypes(filterablePsmPerPeptideAnnotationTypes);
				filterablePsmPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationType().addAll(PSMPerPeptideAnnotationTypes.getFilterablePsmPerPeptideAnnotationTypes(ConverterConstants.PROGRAM_NAME_KOJAK, runType));

			}
		}

		// add in percolator if necessary
		if(percolatorResults != null) {
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add(searchProgram);

			searchProgram.setName(ConverterConstants.PROGRAM_NAME_PERCOLATOR);
			searchProgram.setDisplayName(ConverterConstants.PROGRAM_NAME_PERCOLATOR);
			searchProgram.setVersion(percolatorResults.getPercolatorVersion());

			// add psm annotation types
			{
				PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
				searchProgram.setPsmAnnotationTypes(psmAnnotationTypes);

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
				psmAnnotationTypes.setFilterablePsmAnnotationTypes(filterablePsmAnnotationTypes);
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().addAll(PSMAnnotationTypes.getFilterablePsmAnnotationTypes(ConverterConstants.PROGRAM_NAME_KOJAK, runType));

			}
		}


		//
		// Define which psm annotation types are visible by default
		//
		{
			DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
			searchProgramInfo.setDefaultVisibleAnnotations(xmlDefaultVisibleAnnotations);
			{
				VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
				xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations(xmlVisiblePsmAnnotations);

				xmlVisiblePsmAnnotations.getSearchAnnotation().addAll(PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes(runType));
			}

			//
			// Define which psm per-peptide annotation types are visible by default
			//
			{
				VisiblePsmPerPeptideAnnotations xmlVisiblePsmPerPeptideAnnotations = new VisiblePsmPerPeptideAnnotations();
				xmlDefaultVisibleAnnotations.setVisiblePsmPerPeptideAnnotations(xmlVisiblePsmPerPeptideAnnotations);

				xmlVisiblePsmPerPeptideAnnotations.getSearchAnnotation().addAll(PSMPerPeptideDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes( runType ));
			}
		}

		//
		// Define the linker information
		//
		Linkers linkers = new Linkers();
		proxlInputRoot.setLinkers( linkers );

		for( KojakCrosslinker kojakLinker : conversionParameters.getKojakAnalysis().getKojakConfReader().getCrosslinkers() ) {

			Linker xLinker = new Linker();
			linkers.getLinker().add(xLinker);

			xLinker.setName( kojakLinker.getName() );

			LinkedEnds xLinkedEnds = new LinkedEnds();
			xLinker.setLinkedEnds(xLinkedEnds);

			for ( LinkableEnd linkableEnd : kojakLinker.getLinkableEnds().getLinkableEnds() ) {

				LinkedEnd xLinkedEnd = new LinkedEnd();
				xLinkedEnds.getLinkedEnd().add(xLinkedEnd);

				Residues xResidues = new Residues();
				xResidues.getResidue().addAll( linkableEnd.getResidues() );

				xLinkedEnd.setResidues(xResidues);

				if( linkableEnd.isnTerminal() ) {

					ProteinTermini xmlProteinTermini = new ProteinTermini();
					xLinkedEnd.setProteinTermini( xmlProteinTermini );

					ProteinTerminus xmlProteinTerminus = new ProteinTerminus();
					xmlProteinTerminus.setTerminusEnd( ProteinTerminusDesignation.N );
					xmlProteinTerminus.setDistanceFromTerminus( new BigInteger( "0" ) );
					xmlProteinTermini.getProteinTerminus().add( xmlProteinTerminus );

					xmlProteinTerminus = new ProteinTerminus();
					xmlProteinTerminus.setTerminusEnd( ProteinTerminusDesignation.N );
					xmlProteinTerminus.setDistanceFromTerminus( new BigInteger( "1" ) );
					xmlProteinTermini.getProteinTerminus().add( xmlProteinTerminus );

				}

				if( linkableEnd.iscTerminal() ) {

					ProteinTermini xmlProteinTermini = new ProteinTermini();
					xLinkedEnd.setProteinTermini( xmlProteinTermini );

					ProteinTerminus xmlProteinTerminus = new ProteinTerminus();
					xmlProteinTerminus.setTerminusEnd( ProteinTerminusDesignation.C );
					xmlProteinTerminus.setDistanceFromTerminus( new BigInteger( "0" ) );
					xmlProteinTermini.getProteinTerminus().add( xmlProteinTerminus );
				}
			}

			CrosslinkMasses masses = new CrosslinkMasses();
			xLinker.setCrosslinkMasses(masses);

			CrosslinkMass xlinkMass = new CrosslinkMass();
			xLinker.getCrosslinkMasses().getCrosslinkMass().add(xlinkMass);

			// set the mass for this crosslinker to the calculated mass for the crosslinker, as defined in the properties file
			xlinkMass.setMass( kojakLinker.getMassMod() );
		}
		
		//
		// Define the static mods
		//

		Map<String, BigDecimal> staticMods = conversionParameters.getKojakAnalysis().getKojakConfReader().getStaticMods();

		if( staticMods.keySet().size() > 1 ) {
			StaticModifications smods = new StaticModifications();
			proxlInputRoot.setStaticModifications( smods );
			
			for( String moddedResidue : staticMods.keySet() ) {
					
					StaticModification xmlSmod = new StaticModification();
					xmlSmod.setAminoAcid( moddedResidue );
					xmlSmod.setMassChange( staticMods.get( moddedResidue ) );
					
					smods.getStaticModification().add( xmlSmod );
			}
		}
		
		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );

		Map<KojakReportedPeptide, Map<String, Map<Integer, KojakPSMResult>>> kojakResultsMap = kojakResults.getKojakResults();

		// iterate over each distinct reported peptide
		for( KojakReportedPeptide rp : kojakResultsMap.keySet() ) {

			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add(xmlReportedPeptide);

			xmlReportedPeptide.setReportedPeptideString(rp.toString());

			if (rp.getType() == ConverterConstants.LINK_TYPE_CROSSLINK)
				xmlReportedPeptide.setType(LinkType.CROSSLINK);
			else if (rp.getType() == ConverterConstants.LINK_TYPE_LOOPLINK)
				xmlReportedPeptide.setType(LinkType.LOOPLINK);
			else
				xmlReportedPeptide.setType(LinkType.UNLINKED);

			Peptides xmlPeptides = new Peptides();
			xmlReportedPeptide.setPeptides(xmlPeptides);

			// process all peptides in this reported peptide
			int peptideIndex = 0;
			for (KojakPeptide kojakPeptide : rp.getKojakPeptides()) {

				peptideIndex++;

				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add(xmlPeptide);

				xmlPeptide.setSequence(kojakPeptide.getSequence());
				xmlPeptide.setUniqueId(String.valueOf(peptideIndex));

				if ((kojakPeptide.getDynamicModifications() != null && kojakPeptide.getDynamicModifications().size() > 0) ||
						kojakPeptide.getnTerminalMod() != null || kojakPeptide.getcTerminalMod() != null) {

					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications(xmlModifications);

					if (kojakPeptide.getDynamicModifications() != null) {

						for (int position : kojakPeptide.getDynamicModifications().keySet()) {

							for (KojakDynamicMod kojakDynamicMod : kojakPeptide.getDynamicModifications().get(position)) {

								Modification xmlModification = new Modification();
								xmlModifications.getModification().add(xmlModification);

								xmlModification.setMass(kojakDynamicMod.getMassDiff());
								xmlModification.setPosition(new BigInteger(String.valueOf(position)));
								xmlModification.setIsMonolink(kojakDynamicMod.isMonolink());

							}
						}
					}

					// add n-terminal mod
					if (kojakPeptide.getnTerminalMod() != null) {
						Modification xmlModification = new Modification();
						xmlModifications.getModification().add(xmlModification);

						xmlModification.setMass(kojakPeptide.getnTerminalMod().getMassDiff());
						xmlModification.setIsNTerminal(true);
						xmlModification.setIsMonolink(kojakPeptide.getnTerminalMod().isMonolink());
					}

					// add c-terminal mod
					if (kojakPeptide.getcTerminalMod() != null) {
						Modification xmlModification = new Modification();
						xmlModifications.getModification().add(xmlModification);

						xmlModification.setMass(kojakPeptide.getcTerminalMod().getMassDiff());
						xmlModification.setIsCTerminal(true);
						xmlModification.setIsMonolink(kojakPeptide.getcTerminalMod().isMonolink());
					}
				}

				// add in the linked position(s) in this peptide
				if (kojakPeptide.getLinkedPositions() != null) {

					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions(xmlLinkedPositions);

					for (int linkedPosition : kojakPeptide.getLinkedPositions()) {

						LinkedPosition xmlLinkedPosition = new LinkedPosition();
						xmlLinkedPositions.getLinkedPosition().add(xmlLinkedPosition);
						xmlLinkedPosition.setPosition(new BigInteger(String.valueOf(linkedPosition)));
					}
				}

				// add in stable isotope labels
				if (kojakPeptide.getN15Label() != null) {

					Peptide.PeptideIsotopeLabels xmlPeptideIsotopeLabels = new Peptide.PeptideIsotopeLabels();
					xmlPeptide.setPeptideIsotopeLabels(xmlPeptideIsotopeLabels);

					Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel xmlPeptideIsotopeLabel = new Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel();
					xmlPeptideIsotopeLabels.setPeptideIsotopeLabel(xmlPeptideIsotopeLabel);
					xmlPeptideIsotopeLabel.setLabel("15N");
				}

			}

			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms(xmlPsms);

			// iterate over all PSMs for this reported peptide
			for (String pepXMLFileName : kojakResultsMap.get(rp).keySet()) {
				for( Integer scanNumber : kojakResultsMap.get(rp).get(pepXMLFileName).keySet() ) {

					KojakPSMResult result = kojakResultsMap.get(rp).get(pepXMLFileName).get(scanNumber);

					Psm xmlPsm = new Psm();
					xmlPsms.getPsm().add(xmlPsm);

					xmlPsm.setScanNumber(new BigInteger(String.valueOf(result.getScanNumber())));
					xmlPsm.setPrecursorCharge(new BigInteger(String.valueOf(result.getCharge())));
					xmlPsm.setScanFileName(result.getScanFile());

					if (rp.getType() == ConverterConstants.LINK_TYPE_CROSSLINK || rp.getType() == ConverterConstants.LINK_TYPE_LOOPLINK)
						xmlPsm.setLinkerMass(result.getLinkerMass());

					// add in the filterable PSM annotations (e.g., score)
					FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
					xmlPsm.setFilterablePsmAnnotations(xmlFilterablePsmAnnotations);

					// handle kojak score
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_SCORE);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(result.getKojakScore());
					}

					// handle delta score
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_DELTA_SCORE);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(result.getDeltaScore());
					}

					// handle ppm error
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_PPM_ERROR);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(result.getPpmError());
					}

					// handle e-value
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_EVALUE);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(result.getEvalue());
					}

					// handle ion match
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_ION_MATCH);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(new BigDecimal(result.getIonMatch()).stripTrailingZeros());
					}

					// handle consecutive ion match
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH);
						xmlFilterablePsmAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
						xmlFilterablePsmAnnotation.setValue(new BigDecimal(result.getConsecutiveIonMatch()).stripTrailingZeros());
					}

					// add in each matched peptide's scores if applicable
					if (result.getPerPeptidePSMResults() != null && result.getPerPeptidePSMResults().size() == 2) {

						Psm.PerPeptideAnnotations xmlPerPeptideAnnotations = new Psm.PerPeptideAnnotations();
						xmlPsm.setPerPeptideAnnotations(xmlPerPeptideAnnotations);

						int counter = 0;
						for (KojakPerPeptidePSM perPeptidePSM : result.getPerPeptidePSMResults()) {

							Psm.PerPeptideAnnotations.PsmPeptide xmlPsmPeptide = new Psm.PerPeptideAnnotations.PsmPeptide();
							xmlPerPeptideAnnotations.getPsmPeptide().add(xmlPsmPeptide);

							// add in the unique id for each per peptide psm scores
							if (perPeptidePSM.getLinkedPeptide().equals(rp.getKojakPeptides().get(0))) {

								if (rp.getKojakPeptides().get(0).equals(rp.getKojakPeptides().get(1)) && counter == 1) {
									xmlPsmPeptide.setUniqueId(String.valueOf(1));
								} else {
									xmlPsmPeptide.setUniqueId(String.valueOf(0));
								}

							} else if (perPeptidePSM.getLinkedPeptide().equals(rp.getKojakPeptides().get(1))) {
								xmlPsmPeptide.setUniqueId(String.valueOf(1));
							} else {
								throw new Exception("Did not get match to either reported peptide peptide for the per peptide PSM annotations");
							}


							FilterablePsmPerPeptideAnnotations xmlFilterablePsmPerPeptideAnnotations = new FilterablePsmPerPeptideAnnotations();
							xmlPsmPeptide.setFilterablePsmPerPeptideAnnotations(xmlFilterablePsmPerPeptideAnnotations);

							// handle e-value
							{
								FilterablePsmPerPeptideAnnotation xmlFilterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
								xmlFilterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation().add(xmlFilterablePsmPerPeptideAnnotation);

								xmlFilterablePsmPerPeptideAnnotation.setAnnotationName(PSMPerPeptideAnnotationTypes.KOJAK_ANNOTATION_TYPE_EVALUE);
								xmlFilterablePsmPerPeptideAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
								xmlFilterablePsmPerPeptideAnnotation.setValue(perPeptidePSM.getEvalue());
							}

							// handle rank
							{
								FilterablePsmPerPeptideAnnotation xmlFilterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
								xmlFilterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation().add(xmlFilterablePsmPerPeptideAnnotation);

								xmlFilterablePsmPerPeptideAnnotation.setAnnotationName(PSMPerPeptideAnnotationTypes.KOJAK_ANNOTATION_TYPE_RANK);
								xmlFilterablePsmPerPeptideAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
								xmlFilterablePsmPerPeptideAnnotation.setValue(new BigDecimal(perPeptidePSM.getRank()).stripTrailingZeros());
							}

							// handle score
							{
								FilterablePsmPerPeptideAnnotation xmlFilterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
								xmlFilterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation().add(xmlFilterablePsmPerPeptideAnnotation);

								xmlFilterablePsmPerPeptideAnnotation.setAnnotationName(PSMPerPeptideAnnotationTypes.KOJAK_ANNOTATION_TYPE_SCORE);
								xmlFilterablePsmPerPeptideAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
								xmlFilterablePsmPerPeptideAnnotation.setValue(perPeptidePSM.getKojakScore());
							}

							// handle ions matched
							{
								FilterablePsmPerPeptideAnnotation xmlFilterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
								xmlFilterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation().add(xmlFilterablePsmPerPeptideAnnotation);

								xmlFilterablePsmPerPeptideAnnotation.setAnnotationName(PSMPerPeptideAnnotationTypes.KOJAK_ANNOTATION_TYPE_ION_MATCH);
								xmlFilterablePsmPerPeptideAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
								xmlFilterablePsmPerPeptideAnnotation.setValue(new BigDecimal(perPeptidePSM.getIonMatch()).stripTrailingZeros());
							}

							// handle consecutive ions matched
							{
								FilterablePsmPerPeptideAnnotation xmlFilterablePsmPerPeptideAnnotation = new FilterablePsmPerPeptideAnnotation();
								xmlFilterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation().add(xmlFilterablePsmPerPeptideAnnotation);

								xmlFilterablePsmPerPeptideAnnotation.setAnnotationName(PSMPerPeptideAnnotationTypes.KOJAK_ANNOTATION_TYPE_CONSECUTIVE_ION_MATCH);
								xmlFilterablePsmPerPeptideAnnotation.setSearchProgram(ConverterConstants.PROGRAM_NAME_KOJAK);
								xmlFilterablePsmPerPeptideAnnotation.setValue(new BigDecimal(perPeptidePSM.getConsecutiveIonMatch()).stripTrailingZeros());
							}

							counter++;
						}
					}


				}//end iterating over all PSMs for a reported peptide
			}
			
			
		}// end iterating over distinct reported peptides


		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				proxlInputRoot,
				analysis.getFastaFile(),
				analysis.getKojakConfReader().getDecoyFilter(),
				analysis.getKojakConfReader().get15NFilter()
		);
		
		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );

		for( File confFile : conversionParameters.getKojakAnalysis().getKojakConfFiles() ) {

			xmlConfigurationFile.setSearchProgram( ConverterConstants.PROGRAM_NAME_KOJAK );
			xmlConfigurationFile.setFileName( confFile.getName() );
			xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( confFile.getAbsolutePath() ) ) );

		}

		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( conversionParameters.getProxlXMLOutFile(), proxlInputRoot);
		
	}
	

	
	
}
