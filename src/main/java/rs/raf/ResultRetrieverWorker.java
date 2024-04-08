package rs.raf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ResultRetrieverWorker implements Callable {

    private String jobName;

    private ScanType scanType;

    public ResultRetrieverWorker(String jobName, ScanType scanType){
        this.jobName = jobName;
        this.scanType = scanType;
    }

    @Override
    public Object call() throws Exception {
        if(this.scanType.equals(ScanType.FILE)){
            this.jobName = "src/main/resources/" + this.jobName;
        }
        JobResult jResult = new JobResult(this.jobName, this.scanType);
        Map<String, Integer> myMap = new HashMap<>();

        for(JobResult jobResult: Main.resultRetriever.getResultList()){
            if(jobResult.getJobName().equals(jobName) && jobResult.getScanType().equals(scanType)){
                myMap.putAll(jobResult.getJobResult());
            }
        }

        jResult.setJobResult(myMap);

        return jResult;
    }


}
