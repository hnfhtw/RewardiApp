package me.rewardi;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class GadgetOnButtonClickListener implements View.OnClickListener {

    private CustomListAdapterGadgets m_customListAdapterGadgets;
    private Gadget m_gadget;
    private TextView m_outputTextView;
    private ToggleButton m_btnStartStop;
    private Globals m_appState;
    FutureCallback<Response<String>> startStopSocketBoardCallback;
    FutureCallback<Response<String>> lockBoxCallback;

    public GadgetOnButtonClickListener(CustomListAdapterGadgets adapter, Gadget gadget, TextView textView, ToggleButton btn, Globals appState){
        m_customListAdapterGadgets = adapter;
        m_gadget = gadget;
        m_outputTextView = textView;
        m_btnStartStop = btn;
        m_appState = appState;

        startStopSocketBoardCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200 || result.getHeaders().code() == 202 || result.getHeaders().code() == 203) ) {
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("SocketBoard", "startStopSocketBoardCallback Element = " + element.toString());
                    Log.d("SocketBoard", "startStopSocketBoardCallback Header = " + result.getHeaders().code());
                    if (element == null || element.isJsonNull()) {   // no data in STOP SOCKETBOARD message -> stop timer and set TextView appropriately - then return.
                        m_appState.requestUserDataUpdate();
                        ((SocketBoard) m_gadget).setIsActive(false);
                        //m_outputTextView.setText("Socket Board switched OFF");
                        ActivityTimer tim1 = m_customListAdapterGadgets.getTimer(((SocketBoard) m_gadget).getId());
                        if (tim1 != null) {
                            tim1.cancel();
                            tim1.setOutputText("Socket Board switched OFF");
                            tim1.setStartValueMilis(0);
                        }
                        return;
                    } else {                       // if response contains payload it is a START SOCKETBOARD message -> parse object values (not really necessary actually), update object, start timer and set TextView appropriately
                        JsonObject obj = element.getAsJsonObject();
                        Log.d("SocketBoard", "startStopSocketBoardCallback Response Object = " + obj.toString());

                        SocketBoard socketBoard = SocketBoard.parseObject(obj);
                        m_customListAdapterGadgets.setItem((Gadget) socketBoard);
                        ActivityTimer tim = m_customListAdapterGadgets.getTimer(socketBoard.getId());

                        if (socketBoard.getIsActive()) {
                            m_btnStartStop.setChecked(true);
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

                            if (tim != null) {
                                tim.setStartValueMilis(startValueMilis);
                                tim.start();
                            }

                        } else {
                            m_outputTextView.setText("Socket Board switched OFF");
                            if (tim != null) {
                                tim.cancel();
                                tim.setStartValueMilis(0);
                            }
                        }
                        //m_customListAdapterGadgets.notifyDataSetChanged();
                    }
                }
                else{

                }
            }
        };

        lockBoxCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200 || result.getHeaders().code() == 202 || result.getHeaders().code() == 203) ) {
                    ((Box) m_gadget).setIsLocked(true);
                    m_btnStartStop.setEnabled(false);
                    m_btnStartStop.setChecked(true);
                    m_outputTextView.setText("Box locked!");
                }
                else{

                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        if(m_gadget instanceof Box) {
            if(m_btnStartStop.isChecked()){
                m_btnStartStop.setChecked(false);
                //m_btnStartStop.setEnabled(false);
                m_appState.sendMessageToServer(Globals.messageID.BOX_LOCK, ((Box)m_gadget).getId(),null, lockBoxCallback);
                //((Box)m_gadget).setIsLocked(true);
            }
        }
        else if(m_gadget instanceof SocketBoard){
            if(m_btnStartStop.isChecked()){
                m_btnStartStop.setChecked(false);
                JsonObject data = new JsonObject();
                data.addProperty("maxTime", ((SocketBoard)m_gadget).getMaxTimeSec());
                m_appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, ((SocketBoard)m_gadget).getId(),data, startStopSocketBoardCallback);
                //((SocketBoard)gadget).setIsActive(true);
            }
            else{
                m_appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, ((SocketBoard)m_gadget).getId(),null, startStopSocketBoardCallback);
                //((SocketBoard)gadget).setIsActive(false);
                //m_outputTextView.setText("Socket Board switched OFF");
            }
        }

    }
}
