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

package org.yeastrc.proxl.xml.kojak.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercolatorParsingUtils {

	/**
	 * Examples:
	 * T-46425-94.65
	 * T-44282-90.35-2
	 * T-UWPRLumos_2020_0124_AZ_094_AZ815_xlink05-858-4.03
	 * @param scanId
	 * @return
	 */
	public static int getScanNumberFromScanId( String scanId ) {


		Pattern p = null;
		Matcher m = null;

		// try T-46425-94.65
		{
			p = Pattern.compile("^[TD]-(\\d+)-\\d+\\.\\d+$");
			m = p.matcher(scanId);

			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
		}

		// try T-44282-90.35-2
		{
			p = Pattern.compile("^[TD]-(\\d+)-\\d+\\.\\d+-\\d+$");
			m = p.matcher(scanId);

			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
		}

		// T-UWPRLumos_2020_0124_AZ_094_AZ815_xlink05-858-4.03
		{
			p = Pattern.compile("^[TD]-\\S+-(\\d+)-\\d+\\.\\d+-\\d+$");
			m = p.matcher(scanId);

			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
		}
		
		throw new IllegalArgumentException( "Scan id is not of the expected syntax. Got: " + scanId + ", expected something like: T-46425-94.6" );
	}
	
}
