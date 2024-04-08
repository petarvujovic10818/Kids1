package rs.raf;

import java.util.Map;
import java.util.concurrent.Future;

public class Job implements ScanningJob{

    private String jobName;

    private ScanType jobType;

    private String dataString;

    private String url;

    public Job(ScanType jobType, String dataString, String url){
        this.jobType = jobType;
        this.dataString = dataString;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ScanType getJobType() {
        return jobType;
    }

    public void setJobType(ScanType jobType) {
        this.jobType = jobType;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    @Override
    public ScanType getType() {
        return this.jobType;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return null;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
