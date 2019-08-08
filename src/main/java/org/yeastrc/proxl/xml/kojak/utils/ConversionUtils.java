package org.yeastrc.proxl.xml.kojak.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionUtils {

    /**
     * Parse the psm id to get the expected pepXML file prefix (everything before .pep.xml)
     *
     * @param psmId
     * @return The pepXML file prefix
     * @throws Exception If the psm id cannot be parsed
     */
    public static String getPepXMLPrefixFromPsmId( String psmId ) throws Exception {

        // expecting a psmId like: D-QEHFX_2019_0510_AZ_034_eh220_comet-1008-3.19
        // where QEHFX_2019_0510_AZ_034_eh220_comet is the name of the pep XML file

        Pattern p = Pattern.compile( "^[DT]\\-(\\S+)\\-\\d+\\-[0-9\\.]+$" );
        Matcher m = p.matcher( psmId );
        if( m.matches() ) {
            return m.group( 1 );
        }

        throw new Exception( "Could not find the pepXML prefix in PSM id: " + psmId );

    }
}
