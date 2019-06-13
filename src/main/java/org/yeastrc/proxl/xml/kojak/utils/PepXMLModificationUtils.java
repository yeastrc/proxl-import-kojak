package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import org.yeastrc.proxl.xml.kojak.objects.PepXMLModDefinition;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PepXMLModificationUtils {

    public static Map<Integer, Collection<BigDecimal>> getDynamicModMapForModInfoDataType(ModInfoDataType modInfo, String peptideSequence, Collection<PepXMLModDefinition> dynamicMods, Collection<PepXMLModDefinition> staticMods  ) throws Exception {

        Map<Integer, Collection<BigDecimal>> mods = new HashMap<>();

        if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {

            for (ModInfoDataType.ModAminoacidMass mam : modInfo.getModAminoacidMass()) {

                BigDecimal massDiff = PepXMLModificationUtils.getMassDiffForReportedMod(dynamicMods, mam, peptideSequence);
                if (massDiff != null) {

                    int position = mam.getPosition().intValueExact();

                    if (!mods.containsKey(position))
                        mods.put(position, new HashSet<>());

                    mods.get(position).add(massDiff);

                } else {

                    // sanity check, ensure it's a static mod if it's not a dynamic mod. if it's not... blow up, something's wrong
                    massDiff = PepXMLModificationUtils.getMassDiffForReportedMod(staticMods, mam, peptideSequence);
                    if (massDiff == null) {
                        throw new Exception("Got a mod that was not a static mod or a dynamic mod for hit.");
                    }
                }
            }
        }

        return mods;
    }

    public static BigDecimal getNTerminalModMassDiff( ModInfoDataType modInfo, Collection<PepXMLModDefinition> dynamicMods ) {

        if( modInfo == null ) { return null; }
        if( dynamicMods.size() < 0 ) { return null; }
        if( modInfo.getModNtermMass() == null ) { return null; }

        BigDecimal nTerminalModMass = BigDecimal.valueOf( modInfo.getModNtermMass() ).stripTrailingZeros();
        if( nTerminalModMass == null ) { return null; }


        for( PepXMLModDefinition modDefinition : dynamicMods ) {
            if( modDefinition.getResidue().equals( "n" ) && modDefinition.getTotalMass().equals( nTerminalModMass ) ) {
                return modDefinition.getMassDiff();
            }
        }

        return null;
    }


    public static BigDecimal getCTerminalModMassDiff( ModInfoDataType modInfo, Collection<PepXMLModDefinition> dynamicMods ) {

        if( modInfo == null ) { return null; }
        if( dynamicMods.size() < 0 ) { return null; }
        if( modInfo.getModCtermMass() == null ) { return null; }

        BigDecimal cTerminalModMass = BigDecimal.valueOf( modInfo.getModCtermMass() ).stripTrailingZeros();
        if( cTerminalModMass == null ) { return null; }


        for( PepXMLModDefinition modDefinition : dynamicMods ) {
            if( modDefinition.getResidue().equals( "c" ) && modDefinition.getTotalMass().equals( cTerminalModMass ) ) {
                return modDefinition.getMassDiff();
            }
        }

        return null;
    }

    /**
     * Get all the dynamic mods defined for this search in the pepXML file. Because pepXML INSISTS on reporting
     * mod masses as the mod mass + amino acid mass, and this is the only place in the process that the mass
     * diff associated with total masses are reported, we need to grab this so we can pass the correct mass
     * diff along for proxl xml file creation.
     *
     * @param runSummary
     * @return
     */
    public static Collection<PepXMLModDefinition> getDynamicModsForSearch(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );
        Collection<PepXMLModDefinition> mods = new HashSet<>();

        for(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification aminoAcidModification : searchSummary.getAminoacidModification() ) {

            if( aminoAcidModification.getVariable().equals( "Y" ) ) {

                BigDecimal massdiff = aminoAcidModification.getMassdiff().stripTrailingZeros();
                BigDecimal totalmass = aminoAcidModification.getMass().stripTrailingZeros();
                String residue = aminoAcidModification.getAminoacid();

                mods.add( new PepXMLModDefinition( massdiff, totalmass, residue ) );
            }
        }

        return mods;
    }

    /**
     * Get all static mods defined for this search. Needed to determine if a reported mod for a peptide is
     * a static or dynamic mod.
     *
     * @param runSummary
     * @return
     */
    public static Collection<PepXMLModDefinition> getStaticModsForSearch(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );
        Collection<PepXMLModDefinition> mods = new HashSet<>();

        for(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification aminoAcidModification : searchSummary.getAminoacidModification() ) {

            if( aminoAcidModification.getVariable().equals( "N" ) ) {

                BigDecimal massdiff = aminoAcidModification.getMassdiff().stripTrailingZeros();
                BigDecimal totalmass = aminoAcidModification.getMass().stripTrailingZeros();
                String residue = aminoAcidModification.getAminoacid();

                mods.add( new PepXMLModDefinition( massdiff, totalmass, residue ) );
            }
        }

        return mods;
    }

    public static Collection<PepXMLModDefinition> getNTerminalDynamicModsForSearch(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );
        Collection<PepXMLModDefinition> mods = new HashSet<>();

        for(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.TerminalModification terminalModification : searchSummary.getTerminalModification() ) {

            if( terminalModification.getVariable().equals( "Y" ) && terminalModification.getTerminus().equals( "n" ) ) {

                BigDecimal massdiff = terminalModification.getMassdiff().stripTrailingZeros();
                BigDecimal totalmass = terminalModification.getMass().stripTrailingZeros();
                String residue = "n";

                mods.add( new PepXMLModDefinition( massdiff, totalmass, residue ) );
            }
        }

        return mods;
    }

    public static Collection<PepXMLModDefinition> getCTerminalDynamicModsForSearch(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );
        Collection<PepXMLModDefinition> mods = new HashSet<>();

        for(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.TerminalModification terminalModification : searchSummary.getTerminalModification() ) {

            if( terminalModification.getVariable().equals( "Y" ) && terminalModification.getTerminus().equals( "c" ) ) {

                BigDecimal massdiff = terminalModification.getMassdiff().stripTrailingZeros();
                BigDecimal totalmass = terminalModification.getMass().stripTrailingZeros();
                String residue = "c";

                mods.add( new PepXMLModDefinition( massdiff, totalmass, residue ) );
            }
        }

        return mods;
    }

    /**
     * Get the mass diff associated with the reported mod in the pep XML file. These are reported as the total mass
     * (amino acid mass + mod mass), so this is necessary to get the actual mass diff that was searched for
     * that resulted in that mod match.
     *
     * @param modDefinitions The mod definition parsed from the pepXML previously (see getDynamicModsForSearch an
     *                       getStaticModsForSearch.
     * @param pepXMLReportedMod The jaxb object with the mod data reported for a peptide.
     * @param peptideSequence The naked sequence (no mod info) for the peptide that was matched
     * @return The mass diff or null if there was no matching mod in modDefinitions
     */
    public static BigDecimal getMassDiffForReportedMod(Collection<PepXMLModDefinition> modDefinitions, ModInfoDataType.ModAminoacidMass pepXMLReportedMod, String peptideSequence) {

        if( modDefinitions.size() < 1 ) { return null; }

        int position = pepXMLReportedMod.getPosition().intValueExact();
        String testResidue = peptideSequence.substring( position - 1, position );
        BigDecimal testMass = BigDecimal.valueOf( pepXMLReportedMod.getMass() ).stripTrailingZeros();

        for( PepXMLModDefinition modDefinition : modDefinitions ) {

            if( modDefinition.getResidue().equals( testResidue ) && modDefinition.getTotalMass().equals( testMass ) ) {
                return modDefinition.getMassDiff();
            }
        }

        return null;
    }

}
