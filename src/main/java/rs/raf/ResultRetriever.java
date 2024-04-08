package rs.raf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ResultRetriever{

    private List<JobResult> resultList;

    public ResultRetriever(){
        this.resultList = new ArrayList<>();
    }

    public void addResult(JobResult jobResult){

        if(!this.resultList.contains(jobResult)){
            this.resultList.add(jobResult);
        }
    }

    public List<JobResult> getResultList() {
        return resultList;
    }

}
