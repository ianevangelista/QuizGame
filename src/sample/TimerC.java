package sample;

import java.util.TimerTask;
import java.util.Timer;

public class TimerC {

    private static Timer timer;
    private static int teller;

    public static void timer(){
        teller = 30;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(teller == 0) turnOfTimer();
                System.out.println(teller);
            }
        };
        timer.schedule(task, 0, +1000);
    }

    public static void turnOfTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static void main(String[] args) {
        timer();

    }
}
