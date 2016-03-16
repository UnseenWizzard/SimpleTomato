package org.port0.nriedmann.simpletomato;

import java.util.Observable;

/**
 * Created by nicol on 3/10/2016.
 */
public class TimerObservable extends Observable {
    private static TimerObservable instance = new TimerObservable();

    public class TimerInfo{
        private boolean is_done;
        private long time_till_done_ms;
        public long getTime_till_done_ms() {
            return time_till_done_ms;
        }
        public boolean is_done() {
            return is_done;
        }
        public TimerInfo(boolean is_done,long time_till_done_ms){
            this.is_done=is_done;
            this.time_till_done_ms=time_till_done_ms;
        }
    }

    public TimerObservable(){
    }

    public static TimerObservable getInstance(){
        return instance;
    }

    public void update(boolean is_done,long time_till_done_ms){
        synchronized (this){
            setChanged();
            notifyObservers(new TimerInfo(is_done,time_till_done_ms));
        }
    }
}
