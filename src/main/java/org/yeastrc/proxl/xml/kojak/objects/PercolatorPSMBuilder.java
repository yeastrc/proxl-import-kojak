package org.yeastrc.proxl.xml.kojak.objects;

public class PercolatorPSMBuilder {
    private double svmScore;
    private double qValue;
    private double pValue;
    private double pep;
    private String reportedPeptide;
    private String psmId;
    private int scanNumber;

    public PercolatorPSMBuilder setSvmScore(double svmScore) {
        this.svmScore = svmScore;
        return this;
    }

    public PercolatorPSMBuilder setqValue(double qValue) {
        this.qValue = qValue;
        return this;
    }

    public PercolatorPSMBuilder setpValue(double pValue) {
        this.pValue = pValue;
        return this;
    }

    public PercolatorPSMBuilder setPep(double pep) {
        this.pep = pep;
        return this;
    }

    public PercolatorPSMBuilder setReportedPeptide(String reportedPeptide) {
        this.reportedPeptide = reportedPeptide;
        return this;
    }

    public PercolatorPSMBuilder setPsmId(String psmId) {
        this.psmId = psmId;
        return this;
    }

    public PercolatorPSMBuilder setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
        return this;
    }

    public PercolatorPSM createPercolatorPSM() {
        return new PercolatorPSM(svmScore, qValue, pValue, pep, reportedPeptide, psmId, scanNumber);
    }
}