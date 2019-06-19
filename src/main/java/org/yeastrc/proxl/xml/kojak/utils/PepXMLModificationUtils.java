package org.yeastrc.proxl.xml.kojak.utils;

import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.NameValueType;
import org.yeastrc.proxl.xml.kojak.objects.KojakDynamicMod;
import org.yeastrc.proxl.xml.kojak.objects.PepXMLModDefinition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PepXMLModificationUtils {

    public static boolean isMonolink( BigDecimal massDiff, String residue, Collection<PepXMLModDefinition> monolinkMods ) {

        for( PepXMLModDefinition pxd : monolinkMods ) {

            if( pxd.getMassDiff().equals( massDiff ) && pxd.getResidue().equals( residue ) ) {
                return true;
            }
        }

        return false;
    }

    public static Map<Integer, Collection<KojakDynamicMod>> getDynamicModMapForModInfoDataType(ModInfoDataType modInfo, String peptideSequence, Collection<PepXMLModDefinition> monolinkMods  ) throws Exception {

        Map<Integer, Collection<KojakDynamicMod>> mods = new HashMap<>();

        if( modInfo!= null && modInfo.getModAminoacidMass() != null && modInfo.getModAminoacidMass().size() > 0 ) {

            for (ModInfoDataType.ModAminoacidMass mam : modInfo.getModAminoacidMass()) {

                if( mam.getVariable() != null ) {

                    BigDecimal massDiff = BigDecimal.valueOf( mam.getVariable() ).stripTrailingZeros();
                    int position = mam.getPosition().intValueExact();
                    String residue = peptideSequence.substring(position - 1, position);

                    if (!mods.containsKey(position))
                        mods.put(position, new HashSet<>());

                    mods.get(position).add(new KojakDynamicMod(massDiff, isMonolink(massDiff, residue, monolinkMods)));


                }
            }
        }

        return mods;
    }

    public static KojakDynamicMod getNTerminalDynamicMod(ModInfoDataType modInfo, Collection<PepXMLModDefinition> nTerminalDynamicMods, Collection<PepXMLModDefinition> monolinkMods ) throws Exception {

        if( nTerminalDynamicMods.size() < 0 ) { return null; }

        if( modInfo == null ) { return null; }
        if( modInfo.getModNtermMass() == null ) { return null; }

        BigDecimal nTerminalModMass = BigDecimal.valueOf( modInfo.getModNtermMass() ).stripTrailingZeros();
        if( nTerminalModMass == null ) { return null; }

        for( PepXMLModDefinition modDefinition : nTerminalDynamicMods ) {
            if( modDefinition.getResidue().equals( "n" ) && modDefinition.getTotalMass().equals( nTerminalModMass ) ) {

                BigDecimal massDiff = modDefinition.getMassDiff();

                return new KojakDynamicMod( massDiff, isMonolink( massDiff, "n", monolinkMods ) );
            }

        }

        throw new Exception( "Got n-term mod mass but couldn't determine mass diff..." );
    }


    public static KojakDynamicMod getCTerminalDynamicMod(ModInfoDataType modInfo, Collection<PepXMLModDefinition> cTerminalDynamicMods, Collection<PepXMLModDefinition> monolinkMods ) throws Exception {

        if( modInfo == null ) { return null; }
        if( cTerminalDynamicMods.size() < 0 ) { return null; }
        if( modInfo.getModCtermMass() == null ) { return null; }

        BigDecimal cTerminalModMass = BigDecimal.valueOf( modInfo.getModCtermMass() ).stripTrailingZeros();
        if( cTerminalModMass == null ) { return null; }


        for( PepXMLModDefinition modDefinition : cTerminalDynamicMods ) {
            if( modDefinition.getResidue().equals( "c" ) && modDefinition.getTotalMass().equals( cTerminalModMass ) ) {

                BigDecimal massDiff = modDefinition.getMassDiff();

                return new KojakDynamicMod( massDiff, isMonolink( massDiff, "c", monolinkMods ) );
            }
        }

        throw new Exception( "Got c-term mod mass but couldn't determine mass diff..." );
    }

    public static Collection<PepXMLModDefinition> getMonolinkModsForSearch(MsmsPipelineAnalysis.MsmsRunSummary runSummary ) {

        MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary = runSummary.getSearchSummary().get( 0 );
        Collection<PepXMLModDefinition> mods = new HashSet<>();

        for(NameValueType nvt : searchSummary.getParameter() ) {
            if( nvt.getName().equals( "mono_link" ) ) {

                String[] fields = nvt.getValueAttribute().split( " " );
                BigDecimal massDiff = new BigDecimal( fields[ 1 ] );

                String residues = fields[0];
                for (int i = 0; i < residues.length(); i++){
                    String r = String.valueOf(residues.charAt(i));

                    mods.add( new PepXMLModDefinition( massDiff, null, r ) );
                }
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
                String residue = "n";

                // Kojak rounds terminal mods to four decimal places.
                mods.add( new PepXMLModDefinition( massdiff, terminalModification.getMass().setScale( 4, RoundingMode.HALF_UP ), residue ) );
                mods.add( new PepXMLModDefinition( massdiff, terminalModification.getMass().stripTrailingZeros(), residue ) );
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
                String residue = "c";

                // Kojak rounds terminal mods to four decimal places.
                mods.add( new PepXMLModDefinition( massdiff, terminalModification.getMass().setScale( 4, RoundingMode.HALF_UP ), residue ) );
                mods.add( new PepXMLModDefinition( massdiff, terminalModification.getMass().stripTrailingZeros(), residue ) );

            }
        }

        return mods;
    }

}
