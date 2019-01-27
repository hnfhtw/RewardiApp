package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

class CustomListAdapterActivityHistory extends BaseAdapter {
    private List<HistoryItemManualActivity> activityHistoryList;
    private Context context;
    Globals appState;

    public CustomListAdapterActivityHistory(Context context, List<HistoryItemManualActivity> activityHistoryList) {
        this.activityHistoryList = activityHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
         return activityHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return activityHistoryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(HistoryItemManualActivity historyItemManualActivity){
        activityHistoryList.add(0,historyItemManualActivity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_history_activities, null);
        final HistoryItemManualActivity activityHistoryItem = activityHistoryList.get(position);

        appState = ((Globals)context.getApplicationContext());
        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewDuration = (TextView) convertView.findViewById(R.id.textViewDuration);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        textViewName.setText(activityHistoryItem.getActivity().getName());
        textViewDate.setText(appState.parseServerTimeStampToLocalTimeFormat(activityHistoryItem.getTimestamp()));
        textViewDuration.setText(Integer.toString(activityHistoryItem.getDuration()/60) + "min");
        textViewRewardi.setText(Double.toString(activityHistoryItem.getAcquiredRewardi()));

        return convertView;
    }
}
