package rs.raf;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WebScanner implements Callable {

    List<String> urls = new ArrayList<>(); // lista gde cuvamo pregledane url-ove

    private String jobData;

    private String url;

    public WebScanner(String jobData, String url){
        this.jobData = jobData;
        this.url = url;
    }


    private String getDataFromUrl(String url){
        if(!urls.contains(url)){
            urls.add(url);
        } else return "";

        String cleanedHTML = "";
//        if(jumps == 0){

            //url: "https://en.wikipedia.org/wiki/LeBron_James"
            //test jsoup library;
//          String cleanedHTML = "";
            try {
                StringBuffer sb = new StringBuffer();
                Document doc = Jsoup.connect(url).get();
                doc.select("p").forEach(sb::append);
                cleanedHTML = Jsoup.clean(String.valueOf(sb), Safelist.none());
//              System.out.println(cleanedHTML.length());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//        } else {
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");
//                for()
                for (Element link : links) {
                    if(link.attr("href").startsWith("http") || link.attr("href").startsWith("https")){
//                        System.out.println("Link: " + link.attr("href"));
                    }
//                    System.out.println("Link: " + link.attr("href"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//        }
        return cleanedHTML;
    }

    private Map<String, Integer> getWordCounter(String jobString){
        Map<String, Integer> map = new HashMap<>();

        String tmpString = jobString.replaceAll("[;:,.!?()<>]", "").toLowerCase();
        String arr[] = tmpString.split(" ");

        for(String s: arr){
            map.merge(s, 1, Integer::sum);
        }

        return map;
    }

    @Override
    public Object call() throws Exception {
        return getWordCounter(getDataFromUrl(url));
    }

    public List<String> getUrls() {
        return urls;
    }
}
