package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

class CustomListAdapterGadgetHistory extends BaseAdapter {
    private List<HistoryItemGadget> gadgetHistoryList;
    private Context context;
    Globals appState;

    public CustomListAdapterGadgetHistory(Context context, List<HistoryItemGadget> gadgetHistoryList) {
        this.gadgetHistoryList = gadgetHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return gadgetHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return gadgetHistoryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(HistoryItemGadget historyItemGadget){
        gadgetHistoryList.add(0,historyItemGadget);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_history_gadgets, null);
        final HistoryItemGadget historyItemGadget = gadgetHistoryList.get(position);

        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewGadgetType = (TextView) convertView.findViewById(R.id.textViewGadgetType);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewDuration = (TextView) convertView.findViewById(R.id.textViewDuration);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);

        appState = ((Globals)context.getApplicationContext());

        if(historyItemGadget instanceof HistoryItemBox){
            HistoryItemBox historyItemBox = (HistoryItemBox) historyItemGadget;
            textViewName.setText(historyItemBox.getBox().getName());
            textViewGadgetType.setText("Box");
            textViewDate.setText(appState.parseServerTimeStampToLocalTimeFormat(historyItemBox.getTimestamp()));
            textViewDuration.setVisibility(View.INVISIBLE);
            textViewRewardi.setText("-" + Integer.toString(historyItemBox.getUsedRewardi()));
        }
        else if(historyItemGadget instanceof  HistoryItemSocketBoard){
            HistoryItemSocketBoard historyItemSocketBoard = (HistoryItemSocketBoard) historyItemGadget;
            textViewName.setText(historyItemSocketBoard.getSocketBoard().getName());
            textViewGadgetType.setText("Socket Board");
            textViewDate.setText(appState.parseServerTimeStampToLocalTimeFormat(historyItemSocketBoard.getTimestamp()));
            textViewDuration.setVisibility(View.VISIBLE);
            textViewDuration.setText(Double.toString(historyItemSocketBoard.getDuration()/60) + "min");
            textViewRewardi.setText("-" + Double.toString(historyItemSocketBoard.getUsedRewardi()));
        }

        return convertView;
    }

    public void sortItems(){
        Collections.sort(gadgetHistoryList);
        Collections.reverse(gadgetHistoryList);
    }
}
