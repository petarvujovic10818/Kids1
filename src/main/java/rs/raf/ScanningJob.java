package rs.raf;

import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningJob {

    ScanType getType();
    Future<Map<String, Integer>> initiate(); //Vraca se future koji sadrzi mapu sa key/value vrednostima (eg. sword:10, monkey:4 itd)

}
