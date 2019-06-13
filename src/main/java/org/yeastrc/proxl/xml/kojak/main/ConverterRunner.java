/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *
 * Copyright 2019 University of Washington - Seattle, WA
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

package org.yeastrc.proxl.xml.kojak.main;

import org.yeastrc.proxl.xml.kojak.objects.ConversionParameters;
import org.yeastrc.proxl.xml.kojak.objects.KojakPSMResult;
import org.yeastrc.proxl.xml.kojak.objects.KojakReportedPeptide;
import org.yeastrc.proxl.xml.kojak.reader.KojakPepXMLReader;

import java.util.Collection;
import java.util.Map;

public class ConverterRunner {

    public static ConverterRunner createInstance() { return new ConverterRunner(); }

    public void runConversion(ConversionParameters conversionParameters ) throws Throwable {

        System.err.print( "Reading pepXML data into memory..." );
        Map<KojakReportedPeptide, Collection<KojakPSMResult>> kojakResuls = KojakPepXMLReader.getInstance().getResultsFromAnalysis( conversionParameters.getKojakAnalysis() );
        System.err.println( " Done." );

        /*

        System.err.print( "Writing out XML..." );
        (new XMLBuilder()).buildAndSaveXML( conversionParameters, tppResults, cometParams, ppErrorAnalysis, ipErrorAnalysis );
        System.err.println( " Done." );
        */

    }

}
