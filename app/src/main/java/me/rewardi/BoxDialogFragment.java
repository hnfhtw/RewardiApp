/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : BoxDialogFragment.java
 * Purpose    : Fragment opened if a Box item is clicked in the Home activity;
 *              Shows Box information (name, locked/unlocked, rewardi per open) and allows to lock box
 ********************************************************************************************/

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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.parceler.Parcels;

public class BoxDialogFragment extends DialogFragment {

    private Context context;
    Globals appState;
    FutureCallback<Response<String>> lockBoxCallback;   // callback function that is called on server response to the request "lock Box"

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_box, null);
        builder.setView(view);

        Bundle bundle = getArguments();
        final Box box = Parcels.unwrap(bundle.getParcelable("box"));
        Log.d("BoxDialog", "Box Name = " + box.getName());
        TextView textViewGadgetName = (TextView) view.findViewById(R.id.textViewGadgetName);
        TextView textViewRewardi = (TextView) view.findViewById(R.id.textViewRewardi);
        final TextView textViewActive = (TextView) view.findViewById(R.id.textViewActive);
        final ToggleButton btnStartStop = (ToggleButton) view.findViewById(R.id.btnStartStop);
        textViewGadgetName.setText(box.getName());
        btnStartStop.setText("Lock Box");
        btnStartStop.setTextOff("Lock Box");
        btnStartStop.setTextOn("Box Locked");
        textViewRewardi.setText("Cost: " + Integer.toString(box.getRewardiPerOpen()) + " Rewardi / open");

        if(box.getIsLocked()){  // set UI elements correctly depending on lock status of box
            btnStartStop.setChecked(true);
            btnStartStop.setEnabled(false);
            textViewActive.setText("Box locked!");
        }
        else{
            textViewActive.setText("Box unlocked!");
        }

        context = getActivity();
        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {   // if btnStartStop is clicked and Box is not yet locked -> send lock request to server
                    if(btnStartStop.isChecked()){
                        btnStartStop.setChecked(false);
                        appState.sendMessageToServer(Globals.messageID.BOX_LOCK, box.getId(),null, lockBoxCallback);
                    }
                }
        });

        lockBoxCallback = new FutureCallback<Response<String>>() {  // callback function that is called on server response to the request "lock Box"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200 || result.getHeaders().code() == 202 || result.getHeaders().code() == 203) ) {
                    box.setIsLocked(true);
                    btnStartStop.setEnabled(false);
                    btnStartStop.setChecked(true);
                    textViewActive.setText("Box locked!");
                }
                else{

                }
            }
        };

        return builder.create();
        }
    }
