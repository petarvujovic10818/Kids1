package rs.raf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class JobDispatcher implements Runnable{

    private boolean working = false;

    public JobDispatcher(){
        this.working = true;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public boolean getWorking(){
        return this.working;
    }

    @Override
    public void run() {

        while(working){
            Job myJob = null;
            try {
                myJob = Main.blockingQueue.take();

                if(myJob.getType() == ScanType.FILE){
                    Future<Map<String,Integer>> result =  Main.forkJoinPool.submit(new FileScanner(0, myJob.getDataString().length(), myJob.getDataString()));

                    try {
                        Map<String,Integer> mm = result.get();
                        JobResult jobResult = new JobResult(myJob.getUrl(), myJob.getType());
                        jobResult.setJobResult(mm);
                        Main.resultRetriever.addResult(jobResult);
//                        System.out.println("RESULT " + mm);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                }else if(myJob.getType() == ScanType.WEB){
                    Main.results.submit(new WebScanner(myJob.getDataString(), myJob.getUrl()));


                    try {
                        Future<Map<String, Integer>> result = Main.results.take();
                        JobResult jobResult = new JobResult(myJob.getUrl(), myJob.getType());
                        jobResult.setJobResult(result.get());
                        Main.resultRetriever.addResult(jobResult);
//                        System.out.println("WEB REZ: " + result.get());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("I GOT POISON PILL, I WILL STOP WORKING SOON...");
                    this.working = false;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Gasi se JobDispatcher....");
    }

    private int getUpperBound(){
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.txt";
        try(FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Integer.parseInt(prop.getProperty("file_scanning_size_limit"));
    }

}
