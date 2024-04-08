package rs.raf;

import java.util.HashMap;
import java.util.Map;

public class JobResult {

    private String jobName;

    private ScanType scanType;

    private Map<String, Integer> jobResult;

    public JobResult(String jobName, ScanType scanType) {
        this.jobName = jobName;
        this.scanType = scanType;
        this.jobResult = new HashMap<>();
    }

    public Map<String, Integer> getJobResult() {
        return jobResult;
    }

    public void setJobResult(Map<String, Integer> jobResult) {
        this.jobResult = jobResult;
    }

    public ScanType getScanType() {
        return scanType;
    }

    public void setScanType(ScanType scanType) {
        this.scanType = scanType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
