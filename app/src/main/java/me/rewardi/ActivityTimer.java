/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : ActivityTimer.java
 * Purpose    : Timer class used to show the expired run-time of switched-on socket boards or
 *              started activities; timer is preloaded depending on the "start time" that is
 *              obtained from the server
 ********************************************************************************************/

package me.rewardi;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class ActivityTimer extends CountDownTimer {
    private long totalSeconds;
    private long intervalSeconds;
    private TextView outputTextView;
    private long startValueMilis;
    private int idOfActivity;

    ActivityTimer(long totalSeconds, long intervalSeconds, long startValueMilis, int idOfActivity){
        super(totalSeconds * 1000, intervalSeconds * 1000);
        this.totalSeconds = totalSeconds;
        this.intervalSeconds = intervalSeconds;
        this.startValueMilis = startValueMilis;
        this.outputTextView = null;
        this.idOfActivity = idOfActivity;
    }

    @Override
    public void onTick(long l) {    // on every timer tick, calculate the expired run-time and show it in the outputTextView

        long millis = (totalSeconds * 1000 - l) + startValueMilis;

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        if(outputTextView != null) {
            outputTextView.setText("Active since: " + hms);
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
    public TextView getOutputTextView() {
        return outputTextView;
    }
    public void setOutputTextView(TextView outputTextView) { this.outputTextView = outputTextView; }
    public long getStartValueMilis() {
        return startValueMilis;
    }
    public void setStartValueMilis(long startValueMilis) {
        this.startValueMilis = startValueMilis;
    }
    public int getIdOfActivity() { return idOfActivity; }
    public void setIdOfActivity(int idOfActivity) { this.idOfActivity = idOfActivity; }
    public void setOutputText(String outputText){
        if(outputTextView != null) {
            outputTextView.setText(outputText);
        }
    }
}
