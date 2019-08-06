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

        // expecting a psmId like: QEP2_2018_1128_AZ_104_az753_AZ_511_3_1
        // last number is index of result for scan
        // second to last number is charge
        // third to list number is scan number
        // rest is what we want

        Pattern p = Pattern.compile( "^(.+)_\\d+_\\d+_\\d+$" );
        Matcher m = p.matcher( psmId );
        if( m.matches() ) {
            return m.group( 1 );
        }

        throw new Exception( "Could not find the pepXML prefix in PSM id: " + psmId );

    }
}
