package rs.raf;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;
import java.util.regex.Pattern;

public class DirectoryCrawler implements Runnable{

    private String directoryName;
    private String corpusPrefix;

    private int sleepTime;

    //mapa u koju pamtimo last modified vrednosti
    private Map<String, Long> myMap = new HashMap<>();

    private boolean isWorking = false;

    public DirectoryCrawler(String directoryName, String corpusPrefix, int sleepTime){
        this.directoryName = directoryName;
        this.corpusPrefix = corpusPrefix;
        this.sleepTime = sleepTime;
        this.isWorking = true;
    }

    public List<String> findCorpusDirectory() {
        List<String> dataList =  new ArrayList<>();
        File file = new File(this.directoryName);
//        String[] directories = file.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File current, String name) {
//                System.out.println("MMMOLIM " + name);
//                return new File(current, name).isDirectory() && name.startsWith(corpusPrefix);
//            }
//        });
//        System.out.println(this.getSubdirs(file));
        for(File f: this.getSubdirs(file)){

//            String data = "";
//            String fName = f.getName();

            if(f.getName().startsWith(corpusPrefix)){
                StringBuilder str = new StringBuilder();
                for(File fl: f.listFiles()){
                    StringBuilder data = new StringBuilder();
                    if(fl.getName().endsWith(".txt")){
                        if(!myMap.containsKey(fl.getName())){
                            myMap.put(fl.getName(), fl.lastModified());
                        } else {
                            //ako vec imamo trenutni file proveri mu last modified vrednost
//                            System.out.println(myMap.get(fl.getName()));
                            for(Map.Entry<String, Long> entry: myMap.entrySet()){
                                if(entry.getKey().equals(fl.getName())){
                                    long value = entry.getValue();
                                    if(value != fl.lastModified()){
                                        myMap.put(fl.getName(), fl.lastModified());
                                    }
                                }
                            }
                        }
                        try{
                            Scanner myReader = new Scanner(fl);
                            while(myReader.hasNextLine()){
                                data.append(myReader.nextLine());
                            }

                            str.append(data);
                            myReader.close();

                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }

                }

                dataList.add(str.toString());
            }

        }

        return dataList;
    }

    List<File> getSubdirs(File file) {
        List<File> subdirs = Arrays.asList(Objects.requireNonNull(file.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        })));
        subdirs = new ArrayList<File>(subdirs);

        List<File> deepSubdirs = new ArrayList<File>();
        for(File subdir : subdirs) {
            deepSubdirs.addAll(getSubdirs(subdir));
        }
        subdirs.addAll(deepSubdirs);
        return subdirs;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public void setCorpusPrefix(String corpusPrefix) {
        this.corpusPrefix = corpusPrefix;
    }

    public String getCorpusPrefix() {
        return corpusPrefix;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public void run() {
        while (isWorking) {
//            System.out.println("Directory crawler zapoceo trazenje....");
            List<String> txtStringovi = findCorpusDirectory();
            for (String s : txtStringovi) {
                try {
                    if(!Main.blockingQueue.contains(new Job(ScanType.FILE, s, directoryName))){
                        Main.blockingQueue.put(new Job(ScanType.FILE, s, directoryName));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
//            System.out.println("Directory crawler spava...");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            System.out.println("Directory crawler se probudio....");
        }
        System.out.println("Gasi se thread directory crawler :(");
    }
}
