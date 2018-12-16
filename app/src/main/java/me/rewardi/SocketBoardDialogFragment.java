package me.rewardi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;

import org.parceler.Parcels;

import java.net.Socket;

public class SocketBoardDialogFragment extends DialogFragment {

    private Context context;
    Globals appState;

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
        final SocketBoard socketBoard = Parcels.unwrap(bundle.getParcelable("socketboard"));
        Log.d("SocketBoardDialog", "SocketBoard Name = " + socketBoard.getName());
        TextView textViewGadgetName = (TextView) view.findViewById(R.id.textViewGadgetName);
        TextView textViewRewardi = (TextView) view.findViewById(R.id.textViewRewardi);
        TextView textViewMaxTime = (TextView) view.findViewById(R.id.textViewMaxTime);
        TextView textViewActive = (TextView) view.findViewById(R.id.textViewActive);
        final ToggleButton btnStartStop = (ToggleButton) view.findViewById(R.id.btnStartStop);
        textViewGadgetName.setText(socketBoard.getName());
        textViewMaxTime.setText("Auto switch off after " + socketBoard.getMaxTimeSec()/60 + "min");
        btnStartStop.setText("Switch On");
        btnStartStop.setTextOff("Switch On");
        btnStartStop.setTextOn("Switch Off");
        textViewRewardi.setText("Cost: " + Integer.toString(socketBoard.getRewardiPerHour()) + " Rewardi / h");
            if(socketBoard.getIsActive()){
                btnStartStop.setChecked(true);
                textViewActive.setText("Socket Board switched ON");
            }
            else{
                textViewActive.setText("Socket Board switched OFF");
            }

        context = getActivity();
        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(btnStartStop.isChecked()){
                    JsonObject data = new JsonObject();
                    data.addProperty("maxTime", socketBoard.getMaxTimeSec());
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, socketBoard.getId(),data, null);
                    socketBoard.setIsActive(true);
                }
                else{
                    appState = ((Globals)context.getApplicationContext());
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, socketBoard.getId(),null, null);
                    socketBoard.setIsActive(false);
                }
            }
        });
        return builder.create();
        }
    }
