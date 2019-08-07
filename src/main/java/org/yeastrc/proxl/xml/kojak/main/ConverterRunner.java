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

import org.yeastrc.proxl.xml.kojak.builder.XMLBuilder;
import org.yeastrc.proxl.xml.kojak.constants.ConverterConstants;
import org.yeastrc.proxl.xml.kojak.objects.ConversionParameters;
import org.yeastrc.proxl.xml.kojak.objects.KojakResults;
import org.yeastrc.proxl.xml.kojak.objects.PercolatorResults;
import org.yeastrc.proxl.xml.kojak.reader.KojakPepXMLReader;
import org.yeastrc.proxl.xml.kojak.reader.KojakPercolatorValidator;
import org.yeastrc.proxl.xml.kojak.reader.PercolatorResultsReader;

import java.io.File;
import java.util.Collection;

public class ConverterRunner {

    public static ConverterRunner createInstance() { return new ConverterRunner(); }

    public void runConversion(ConversionParameters conversionParameters ) throws Throwable {

        System.err.print( "Reading pepXML data into memory..." );
        KojakResults kojakResuls = KojakPepXMLReader.getInstance().getResultsFromAnalysis( conversionParameters.getKojakAnalysis() );
        System.err.println( " Done." );

        System.err.print( "Reading Percolator XML data into memory..." );
        PercolatorResults percResults = PercolatorResultsReader.getPercolatorResults( conversionParameters.getPercolatorAnalysis().getOutXMLFiles() );
        System.err.println( " Done." );

        System.err.print( "Verifying all percolator results have comet results..." );
        KojakPercolatorValidator.validateData( kojakResuls, percResults );
        System.err.println( " Done." );

        int runType = conversionParameters.getPercolatorAnalysis() == null ? ConverterConstants.RUN_TYPE_KOJAK_ONLY : ConverterConstants.RUN_TYPE_KOJAK_PERCOLATOR;

        System.err.print( "Writing out XML..." );
        (new XMLBuilder()).buildAndSaveXML( conversionParameters, kojakResuls, runType );
        System.err.println( " Done." );


    }

}
