package me.rewardi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

class CustomListAdapterEarnedRewardiHistory extends BaseAdapter {
    private List<HistoryItemEarnedRewardi> earnedRewardiHistoryList;
    private Context context;
    Globals appState;

    public CustomListAdapterEarnedRewardiHistory(Context context, List<HistoryItemEarnedRewardi> earnedRewardiHistoryList) {
        this.earnedRewardiHistoryList = earnedRewardiHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return earnedRewardiHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return earnedRewardiHistoryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(HistoryItemEarnedRewardi historyItemEarnedRewardi){
        earnedRewardiHistoryList.add(0,historyItemEarnedRewardi);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_history_earned_rewardi, null);
        final HistoryItemEarnedRewardi historyItemEarnedRewardi = earnedRewardiHistoryList.get(position);

        appState = ((Globals)context.getApplicationContext());
        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewType = (TextView) convertView.findViewById(R.id.textViewType);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewDuration = (TextView) convertView.findViewById(R.id.textViewDuration);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);

        String supervisorMessage = historyItemEarnedRewardi.getSupervisorMessage();
        String supervisorName = historyItemEarnedRewardi.getSupervisorName();
        boolean granted = historyItemEarnedRewardi.getGranted();

        if(historyItemEarnedRewardi instanceof HistoryItemTodoListPoint){
            HistoryItemTodoListPoint historyItemTodoListPoint = (HistoryItemTodoListPoint) historyItemEarnedRewardi;
            textViewName.setText(historyItemTodoListPoint.getTodoListPoint().getName());
            textViewDate.setText(appState.parseServerTimeStampToLocalTimeFormat(historyItemTodoListPoint.getTimestamp()));
            textViewDuration.setVisibility(View.INVISIBLE);
            int acquiredRewardi = historyItemTodoListPoint.getAcquiredRewardi();
            if(granted){
                textViewRewardi.setText("+" + Integer.toString(acquiredRewardi));
                if(supervisorMessage.length() > 0 ){
                    textViewType.setText("Todo List Point\n" + supervisorName + " (supervisor) confirmed");
                }
                else{
                    textViewType.setText("Todo List Point");
                }

            }
            else{
                if(supervisorMessage.length() > 0 ){
                    textViewType.setText("Todo List Point\n" + supervisorName + " (supervisor) denied");
                    convertView.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xc0));        // grey
                }
                else{
                    textViewType.setText("Todo List Point\n" + supervisorName + " (supervisor) confirmation open");
                    convertView.setBackgroundColor(Color.rgb(0xff, 0xe4, 0xe1));        // rosarot
                }
                textViewRewardi.setText("0");
                textViewRewardi.setTextColor(Color.DKGRAY);

            }

        }
        else if(historyItemEarnedRewardi instanceof  HistoryItemManualActivity){
            HistoryItemManualActivity historyItemManualActivity = (HistoryItemManualActivity) historyItemEarnedRewardi;
            textViewName.setText(historyItemManualActivity.getActivity().getName());
            textViewDate.setText(appState.parseServerTimeStampToLocalTimeFormat(historyItemManualActivity.getTimestamp()));
            textViewDuration.setVisibility(View.VISIBLE);
            textViewDuration.setText(Double.toString(historyItemManualActivity.getDuration()/60) + "min");
            double acquiredRewardi = historyItemManualActivity.getAcquiredRewardi();
            if(granted){
                textViewRewardi.setText("+" + Double.toString(acquiredRewardi));
                if(supervisorMessage.length() > 0 ){
                    textViewType.setText("Activity\n" + supervisorName + " (supervisor) confirmed");
                }
                else{
                    textViewType.setText("Activity");
                }
            }
            else{
                if(supervisorMessage.length() > 0 ){
                    textViewType.setText("Activity\n" + supervisorName + " (supervisor) denied");
                    convertView.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xc0));        // grey
                }
                else{
                    textViewType.setText("Activity\n" + supervisorName + " (supervisor) confirmation open");
                    convertView.setBackgroundColor(Color.rgb(0xff, 0xe4, 0xe1));        // rosarot
                }
                textViewRewardi.setText("0");
                textViewRewardi.setTextColor(Color.DKGRAY);
            }
        }

        return convertView;
    }

    public void sortItems(){
        Collections.sort(earnedRewardiHistoryList);
        Collections.reverse(earnedRewardiHistoryList);
    }
}
