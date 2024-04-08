package rs.raf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class FileScanner extends RecursiveTask {

    private int start;
    private int end;

    private String jobString;

    public FileScanner(int start, int end, String jobString){
        this.start = start;
        this.end = end;
        this.jobString = jobString;
    }

    private Map<String, Integer> getWordCounter(String jobString){
        Map<String, Integer> map = new HashMap<>();

        String tmpString = jobString.replaceAll("[;:,.!?()]", "").toLowerCase();
        String arr[] = tmpString.split(" ");

        for(String s: arr){
            map.merge(s, 1, Integer::sum);
        }

        return map;
    }

    @Override
    protected Object compute() {
        Map<String, Integer> wordCounterMap = new HashMap<>();
        if(end - start < 7000){
//            System.out.println("START: " + start + " END: " + end);
            wordCounterMap.putAll(getWordCounter(jobString));
        } else {
            int mid = ((end-start) / 2) + start;
//            System.out.println("START: " + start + " END: " + end + " MID: " + mid);
            String tmpStringLeft = jobString.substring(start, mid);
            String tmpStringRight = jobString.substring(mid + 1, end);
//            System.out.println("LEFT LENGHT: " + tmpStringLeft.length() + " RIGHT LENGHT: " + tmpStringRight.length());
            FileScanner left = new FileScanner(start, mid, tmpStringLeft);
            FileScanner right = new FileScanner(mid, end, tmpStringRight);

            left.fork();

            Map<String, Integer> rightResult = (Map<String, Integer>) right.compute();
            Map<String, Integer> leftResult = (Map<String, Integer>) left.join();

            wordCounterMap.putAll(rightResult);
            wordCounterMap.putAll(leftResult);

        }

        return wordCounterMap;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
