package me.rewardi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SocketBoardDialogFragment extends DialogFragment {

    private Context context;
    Globals appState;
    FutureCallback<Response<String>> startStopSocketBoardCallback;
    SocketBoard socketBoard;
    ActivityTimer socketBoardTimer;
    private SocketBoardDialogListener listener;

    public SocketBoardDialogListener getListener() { return listener; }
    public void setListener(SocketBoardDialogListener listener) { this.listener = listener; }

    public interface SocketBoardDialogListener {
        void onFinishSocketBoardDialog(SocketBoard socketBoard);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_socketboard, null);
        builder.setView(view);

        Bundle bundle = getArguments();
        socketBoard = Parcels.unwrap(bundle.getParcelable("socketboard"));
        Log.d("SocketBoardDialog", "SocketBoard Name = " + socketBoard.getName());
        TextView textViewGadgetName = (TextView) view.findViewById(R.id.textViewGadgetName);
        TextView textViewRewardi = (TextView) view.findViewById(R.id.textViewRewardi);
        TextView textViewMaxTime = (TextView) view.findViewById(R.id.textViewMaxTime);
        final TextView textViewActive = (TextView) view.findViewById(R.id.textViewActive);
        final ToggleButton btnStartStop = (ToggleButton) view.findViewById(R.id.btnStartStop);
        textViewGadgetName.setText(socketBoard.getName());
        textViewMaxTime.setText("Auto switch off after " + socketBoard.getMaxTimeSec()/60 + "min");
        btnStartStop.setText("Switch On");
        btnStartStop.setTextOff("Switch On");
        btnStartStop.setTextOn("Switch Off");
        textViewRewardi.setText("Cost: " + Integer.toString(socketBoard.getRewardiPerHour()) + " Rewardi / h");

        long startValueMilis = 0;
        if(socketBoard.getIsActive()){
            String activeSince = socketBoard.getActiveSince().substring(0,18);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {
                Date date = format.parse(activeSince);
                Date currentTime = Calendar.getInstance().getTime();
                startValueMilis = currentTime.getTime() - date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(startValueMilis < 0){
                startValueMilis = 0;
            }
        }
        socketBoardTimer = new ActivityTimer(6000000,1, startValueMilis, socketBoard.getId());
        socketBoardTimer.setOutputTextView(textViewActive);
        if(socketBoard.getIsActive()) {
            socketBoardTimer.start();
            btnStartStop.setChecked(true);
        }
        else {
            textViewActive.setText("Socket Board switched OFF");
            btnStartStop.setChecked(false);
        }

        context = getActivity();
        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(btnStartStop.isChecked()){
                    btnStartStop.setChecked(false);
                    JsonObject data = new JsonObject();
                    data.addProperty("maxTime", socketBoard.getMaxTimeSec());
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, socketBoard.getId(),data, startStopSocketBoardCallback);
                    //socketBoard.setIsActive(true);
                }
                else{
                    appState = ((Globals)context.getApplicationContext());
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, socketBoard.getId(),null, startStopSocketBoardCallback);
                    //socketBoard.setIsActive(false);
                }
            }
        });

        startStopSocketBoardCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200 || result.getHeaders().code() == 202 || result.getHeaders().code() == 203) ) {
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("SocketBoard", "startStopSocketBoardCallback - Element = " + element.toString());
                    if (element.isJsonNull()) {   // no data in STOP SOCKETBOARD message -> stop timer and set TextView appropriately - then return.
                        appState.requestUserDataUpdate();
                        socketBoard.setIsActive(false);
                        textViewActive.setText("Socket Board switched OFF");
                        btnStartStop.setChecked(false);
                        if (socketBoardTimer != null) {
                            socketBoardTimer.cancel();
                            socketBoardTimer.setStartValueMilis(0);
                        }
                        Log.d("SocketBoard", "startStopSocketBoardCallback - STOP SOCKET received");
                        return;
                    } else {                       // if response contains payload it is a START SOCKETBOARD message -> parse object values (not really necessary actually), update object, start timer and set TextView appropriately
                        JsonObject obj = element.getAsJsonObject();
                        Log.d("SocketBoard", "startStopSocketBoardCallback Response Object = " + obj.toString());

                        socketBoard = SocketBoard.parseObject(obj);

                        if (socketBoard.getIsActive()) {
                            btnStartStop.setChecked(true);
                            String actSince = socketBoard.getActiveSince().substring(0, 19);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            long startValueMilis = 0;
                            try {
                                Date date = format.parse(actSince);
                                Date currentTime = Calendar.getInstance().getTime();
                                startValueMilis = currentTime.getTime() - date.getTime();
                                Log.d("SocketBoard", "ActSince = " + actSince);
                                Log.d("SocketBoard", "Current Time = " + currentTime);
                                Log.d("SocketBoard", "Start Value in ms = " + startValueMilis);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            if (startValueMilis < 0) {
                                startValueMilis = 0;
                            }

                            if (socketBoardTimer != null) {
                                socketBoardTimer.setStartValueMilis(startValueMilis);
                                socketBoardTimer.start();
                            }
                            btnStartStop.setChecked(true);

                        } else {
                            textViewActive.setText("Socket Board switched OFF");
                            btnStartStop.setChecked(false);
                            if (socketBoardTimer != null) {
                                socketBoardTimer.cancel();
                                socketBoardTimer.setStartValueMilis(0);
                            }
                        }
                        listener.onFinishSocketBoardDialog(socketBoard);    // return updated SocketBoard object
                    }
                }
                else if(e == null){
                    Log.d("SocketBoard", "startStopSocketBoardCallback Response Header = " + result.getHeaders().code());
                }
                else{
                    Log.d("SocketBoard", "startStopSocketBoardCallback Error = " + e.toString());
                }
            }
        };

        return builder.create();
        }
    }
