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

package org.yeastrc.proxl.xml.kojak.objects;

import java.util.Objects;

public class PercolatorPSM {

	public PercolatorPSM(double svmScore, double qValue, double pValue, double pep, String reportedPeptide, String psmId, int scanNumber) {
		this.svmScore = svmScore;
		this.qValue = qValue;
		this.pValue = pValue;
		this.pep = pep;
		this.reportedPeptide = reportedPeptide;
		this.psmId = psmId;
		this.scanNumber = scanNumber;
	}

	public double getSvmScore() {
		return svmScore;
	}

	public double getqValue() {
		return qValue;
	}

	public double getpValue() {
		return pValue;
	}

	public double getPep() {
		return pep;
	}

	public String getReportedPeptide() {
		return reportedPeptide;
	}

	public String getPsmId() {
		return psmId;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	private double svmScore;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PercolatorPSM that = (PercolatorPSM) o;
		return Double.compare(that.svmScore, svmScore) == 0 &&
				Double.compare(that.qValue, qValue) == 0 &&
				Double.compare(that.pValue, pValue) == 0 &&
				Double.compare(that.pep, pep) == 0 &&
				scanNumber == that.scanNumber &&
				reportedPeptide.equals(that.reportedPeptide) &&
				psmId.equals(that.psmId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(svmScore, qValue, pValue, pep, reportedPeptide, psmId, scanNumber);
	}

	@Override
	public String toString() {
		return "PercolatorPSM{" +
				"svmScore=" + svmScore +
				", qValue=" + qValue +
				", pValue=" + pValue +
				", pep=" + pep +
				", reportedPeptide='" + reportedPeptide + '\'' +
				", psmId='" + psmId + '\'' +
				", scanNumber=" + scanNumber +
				'}';
	}

	private double qValue;
	private double pValue;
	private double pep;
	private String reportedPeptide;
	private String psmId;
	private int scanNumber;
}
