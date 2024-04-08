package rs.raf;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    //job queue
    static BlockingQueue<Job> blockingQueue = new LinkedBlockingQueue<>();

    //pool za file
    static ForkJoinPool forkJoinPool = new ForkJoinPool();

    //pool za web
    static ExecutorService webPool = Executors.newCachedThreadPool();
    static ExecutorCompletionService<Map<String, Integer>> results = new ExecutorCompletionService<>(webPool);

    //pool za result retriever
    static ExecutorService resultRetrieverPool = Executors.newCachedThreadPool();
    static ExecutorCompletionService<JobResult> retrieverResults = new ExecutorCompletionService<>(resultRetrieverPool);

    static JobDispatcher jobDispatcher = new JobDispatcher();
    static DirectoryCrawler directoryCrawler = null;

    static ResultRetriever resultRetriever = new ResultRetriever();
    static boolean CLIworking = true;

    public static void main(String[] args) {

        Thread jobDispatcherThread = new Thread(jobDispatcher);
        jobDispatcherThread.start();


        Properties prop = new Properties();
        String fileName = "src/main/resources/config.txt";
        try(FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Integer> commandMap = new HashMap<>();
        commandMap.put("ad", 1);
        commandMap.put("aw", 1);
        commandMap.put("get", 1);
        commandMap.put("query", 1);
        commandMap.put("cws", 0);
        commandMap.put("cfs", 0);
        commandMap.put("stop", 0);

        Scanner scanner = new Scanner(System.in);
        String myCommand = scanner.nextLine();
        String[] s = myCommand.split(" ");

        if(commandMap.containsKey(s[0])) {
            String finalMyCommand = s[0];
            String[] finalS = s;
            commandMap.forEach((key, value) -> {
                if (key.equals(finalMyCommand)) {
                    if(finalS.length - 1 == value){
                        System.out.println("Ucitava se komanda " + finalS[0]);
                        if(value != 0){
                            ucitajKomandu(finalS[0], finalS[1]);
                        } else {
                            ucitajKomandu(finalS[0], "");
                        }

                    }else{
                        System.out.println("ERRROR =>>>>> Pogresan broj argumenata");
                    }
                }
            });
        }
        else {
            System.out.println("ERROR =>>>> Nepostojuca komanda");
        }

        while(CLIworking){
            myCommand = scanner.nextLine();
            s = myCommand.split(" ");

            if(commandMap.containsKey(s[0])) {
                String finalMyCommand = s[0];
                String[] finalS1 = s;
                commandMap.forEach((key, value) -> {
                    if (key.equals(finalMyCommand)) {
                        if(finalS1.length - 1 == value){
                            System.out.println("Ucitava se komanda " + finalS1[0]);
                            if(value != 0){
                                ucitajKomandu(finalS1[0], finalS1[1]);
                            } else {
                                ucitajKomandu(finalS1[0], "");
                            }

                        }else{
                            System.out.println("ERRROR =>>>>> Pogresan broj argumenata");
                        }
                    }
                });
            }
            else {
                System.out.println("ERROR =>>>> Nepostojuca komanda");
            }

        }

        for(JobResult result: resultRetriever.getResultList()){
            System.out.println("JobName: " + result.getJobName() + " JobType: " + result.getScanType() + " Result: " + result.getJobResult());
        }

    }
    private static void ucitajKomandu(String komanda, String parametar) {
//        Thread dirCrawlerThread = null;
//        DirectoryCrawler directoryCrawler = null;
        switch(komanda){
            case "ad":
                //make dirCrawler a thread;
//                directoryCrawler = new DirectoryCrawler("src/main/resources/" + parametar, ucitajPrefix(), ucitajSleepTime(), true);
                directoryCrawler = new DirectoryCrawler("src/main/resources/" + parametar, ucitajPrefix(), ucitajSleepTime());
                Thread dirCrawlerThread = new Thread(directoryCrawler);
                dirCrawlerThread.start();
//                List<String> xd = new ArrayList<>();
//                xd = dirCrawler.findCorpusDirectory();
//                System.out.println("SIZE " + xd.size());
//                System.out.println("1st element " + xd.get(0));
//                System.out.println("2nd element " + xd.get(1));
//                System.out.println("3rd element " + xd.get(2));
//                System.out.println("4th element " + xd.get(3));
//                System.out.println("IZ MAINA LISTA: " + dirCrawler.findCorpusDirectory());
                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "aw":
                int jumps = ucitajJumps();
//                if(jumps > 0){

//                    while(jumps != 0){
//
//                    }
//                } else {
                    System.out.println("UDJE ODJE");
                    addWebJobs(parametar, jumps);
//                }
//                String jobString = getWebString("https://en.wikipedia.org/wiki/LeBron_James");
//                Job job = new Job(ScanType.WEB, jobString);
//                try {
//                    blockingQueue.put(job);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                try {
//                    Future<Map<String, Integer>> result = results.take();
//                    System.out.println("WEB REZ: " + result.get());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }




//                try {
//                    results.submit(new WebScanner("https://en.wikipedia.org/wiki/LeBron_James", 3));
//                    Future<Map<String, Integer>> result = results.take();
//                    System.out.println("WEB REZULTAT: " + result.get());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "get":
                String[] x = parametar.split("\\|");
                if(x[0].equalsIgnoreCase("web")){
                    retrieverResults.submit(new ResultRetrieverWorker(x[1], ScanType.WEB));
                } else if(x[0].equalsIgnoreCase("file")){
                    retrieverResults.submit(new ResultRetrieverWorker(x[1], ScanType.FILE));
                }

                try {
                    Future<JobResult> result = retrieverResults.take();
                    JobResult jobResult = result.get();
                    System.out.println("JobName: " + jobResult.getJobName() + "; JobType: " + jobResult.getScanType() + "; JobResult: " + jobResult.getJobResult());

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "query":
                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "cws":
                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "cfs":
                System.out.println("Zavrsena komanda " + komanda + " sa parametrom " + parametar);
                break;
            case "stop":
                System.out.println("TU JE STOP");
                jobDispatcher.setWorking(false);
                try {
                    blockingQueue.put(new Job(ScanType.POISON_PILL, "", ""));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                webPool.shutdown();
                if(directoryCrawler != null) {
                    try {
                        Thread.sleep(5000);
                        directoryCrawler.setWorking(false);
                        forkJoinPool.shutdown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                CLIworking = false;
                break;
        }
    }

    public static void addWebJobs(String url, int jumps){
        if(jumps < 0) {
            return;
        }
        System.out.println("OBRADA LINKA  " + url);
            String jobString = getWebString(url);
            Job job = new Job(ScanType.WEB, jobString, url);
            try {
                blockingQueue.put(job);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//            try {
//                Future<Map<String, Integer>> result = results.take();
//                System.out.println("WEB REZ: " + result.get());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }


            for(String link: getWebLinks(url, jumps)){
                addWebJobs(link, jumps-1);
            }

    }
    public static String getWebString(String url){
        String cleanedHTML = "";
        try {
            StringBuilder sb = new StringBuilder();
            Document doc = Jsoup.connect(url).get();
            doc.select("p").forEach(sb::append);
            cleanedHTML = Jsoup.clean(String.valueOf(sb), Safelist.none());
//              System.out.println(cleanedHTML.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cleanedHTML;
    }

    public static List<String> getWebLinks(String url, int counter){
        List<String> myList = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                if(myList.size() == counter){
                    break;
                }
                if(link.attr("href").startsWith("http") || link.attr("href").startsWith("https")){
                    myList.add(link.attr("href"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return myList;
    }

    public static String ucitajPrefix(){
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.txt";
        try(FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(prop.getProperty("keywords"));
//        System.out.println(prop.getProperty("file_corpus_prefix"));
        return prop.getProperty("file_corpus_prefix");
    }

    public static int ucitajJumps(){
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.txt";
        try(FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(prop.getProperty("hop_count"));
    }

    public static int ucitajSleepTime(){
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.txt";
        try(FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(prop.getProperty("keywords"));
//        System.out.println(prop.getProperty("file_corpus_prefix"));
        return Integer.parseInt(prop.getProperty("dir_crawler_sleep_time"));
    }
}