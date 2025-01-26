package starlock;

import java.util.ArrayList;

public class StarService {

    protected static boolean licensed = false;
    protected static String[] user = null;
    protected static boolean onRunning = false;
    protected static long time = 0L;
    protected static ArrayList data = new ArrayList<>();

    public void run(){ //TODO: старт серверного чека
        if(!onRunning){
            onRunning = true;
            new Thread(() -> {
                while(true){
                    //TODO: сам чек
                }
            }).run();
        }
    }

    public void check(){
        long current = StarUtils.getTimestamp();
        long difference = (current - time);
        if(difference > 300 || difference <= 0)
            throw new RuntimeException("[X] Handshake is invalid!");
        if(!licensed)
            throw new RuntimeException("[X] You dont have license, upgrade old license our buy new!");
    }

    static {
        if(time != 0L || onRunning || !data.isEmpty() || user != null)
            throw new RuntimeException("[X] Detected modified code!");
        if(StarService.class.getResource("StarLock.class") == null
                || StarService.class.getResource("StarUtils.class") == null){
            throw new RuntimeException("[X] Classes not found!");
        }
    }
}
