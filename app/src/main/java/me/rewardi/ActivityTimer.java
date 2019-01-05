package me.rewardi;

import android.os.CountDownTimer;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

public class ActivityTimer extends CountDownTimer {
    private long totalSeconds;
    private long intervalSeconds;
    private TextView outputText;
    private long startValueMilis;

    ActivityTimer(long totalSeconds, long intervalSeconds, long startValueMilis){
        super(totalSeconds * 1000, intervalSeconds * 1000);
        this.totalSeconds = totalSeconds;
        this.intervalSeconds = intervalSeconds;
        this.startValueMilis = startValueMilis;
        this.outputText = null;
    }

    @Override
    public void onTick(long l) {

        long millis = (totalSeconds * 1000 - l) + startValueMilis;

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        if(outputText != null) {
            outputText.setText("Active since: " + hms);
        }
    }

    @Override
    public void onFinish() { }

    public long getTotalSeconds() {
        return totalSeconds;
    }
    public void setTotalSeconds(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
    public long getIntervalSeconds() {
        return intervalSeconds;
    }
    public void setIntervalSeconds(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }
    public TextView getOutputText() {
        return outputText;
    }
    public void setOutputText(TextView outputText) {
        this.outputText = outputText;
    }
    public long getStartValueMilis() {
        return startValueMilis;
    }
    public void setStartValueMilis(long startValueMilis) {
        this.startValueMilis = startValueMilis;
    }
}
