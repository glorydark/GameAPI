package gameapi.utils;

public class TimeRecord {

    protected long startMillis;

    public TimeRecord(long startMillis){
        this.startMillis = startMillis;
    }

    public long getDuration(){
        return System.currentTimeMillis() - this.startMillis;
    }

}
