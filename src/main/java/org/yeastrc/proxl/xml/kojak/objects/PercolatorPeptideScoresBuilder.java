package org.yeastrc.proxl.xml.kojak.objects;

public class PercolatorPeptideScoresBuilder {
    private double svmScore;
    private double qValue;
    private double pValue;
    private double pep;
    private String reportedPeptide;

    public PercolatorPeptideScoresBuilder setSvmScore(double svmScore) {
        this.svmScore = svmScore;
        return this;
    }

    public PercolatorPeptideScoresBuilder setqValue(double qValue) {
        this.qValue = qValue;
        return this;
    }

    public PercolatorPeptideScoresBuilder setpValue(double pValue) {
        this.pValue = pValue;
        return this;
    }

    public PercolatorPeptideScoresBuilder setPep(double pep) {
        this.pep = pep;
        return this;
    }

    public PercolatorPeptideScoresBuilder setReportedPeptide(String reportedPeptide) {
        this.reportedPeptide = reportedPeptide;
        return this;
    }

    public PercolatorPeptideScores createPercolatorPeptideScores() {
        return new PercolatorPeptideScores(svmScore, qValue, pValue, pep, reportedPeptide);
    }
}