package expiaz.tabata.utils;

import android.os.CountDownTimer;

public class Timer {

    private long time;
    private long interval;
    private CountDownTimer timer;

    private long left;
    private boolean paused;

    private Callbackable<Void, Long> onTick;
    private Callbackable<Void, Long> onPause;
    private Callbackable<Void, Long> onFinish;

    public Timer(long time, long interval,
                 Callbackable<Void, Long> onTick,
                 Callbackable<Void, Long> onPause,
                 Callbackable<Void, Long> onFinish
    ){
        this.time = time;
        this.left = time;
        this.interval = interval;
        this.onTick = onTick;
        this.onPause = onPause;
        this.onFinish = onFinish;

        this.paused = true;
    }

    public void start(){
        timer = new CountDownTimer(left, interval) {

            public void onTick(long millisUntilFinished) {
                left = millisUntilFinished;
                if(onTick != null){
                    onTick.call(left);
                }
            }

            public void onFinish() {
                left = 0;
                if(onFinish != null){
                    onFinish.call(left);
                }
            }
        };

        timer.start();
        paused = false;
    }

    public void pause() {
        if (timer != null && isRunning()) {
            paused = true;
            timer.cancel(); // Arrete le CountDownTimer
            // notifie le listener
            if(onPause != null){
                onPause.call(left);
            }
        }
    }

    public void restart(){
        if(timer != null && !isRunning()){
            this.start();
        }
    }

    public void stop(){
        timer.cancel();
        left = time;
        timer = null;
        paused = false;
    }

    public boolean isRunning(){
        return this.paused == false;
    }

}
