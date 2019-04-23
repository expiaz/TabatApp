package expiaz.tabata.utils;

public class TimeService {

    /**
     * permet de formatter un nombre de secondes en format 'mm:ss'
     * @param time
     * @return
     */
    public static String toMMSS(int time){
        int secs = time;
        int mins = secs / 60;
        secs = secs % 60;

        return String.format("%02d", mins) + ":" + String.format("%02d", secs);
    }

}
