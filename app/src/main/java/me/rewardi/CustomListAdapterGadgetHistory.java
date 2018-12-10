package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

class CustomListAdapterGadgetHistory extends BaseAdapter {
    private List<HistoryItemBox> boxHistoryList;
    private Context context;

    public CustomListAdapterGadgetHistory(Context context, List<HistoryItemBox> boxHistoryList) {
        this.boxHistoryList = boxHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return boxHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return boxHistoryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(HistoryItemBox historyItemBox){
        boxHistoryList.add(0,historyItemBox);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_history_gadgets, null);
        final HistoryItemBox historyItemBox = boxHistoryList.get(position);

        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewGadgetType = (TextView) convertView.findViewById(R.id.textViewGadgetType);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewDuration = (TextView) convertView.findViewById(R.id.textViewDuration);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        textViewName.setText("-NAME- " + Integer.toString(historyItemBox.getId()));
        textViewGadgetType.setText("Box");
        textViewDate.setText(historyItemBox.getTimestamp());
        textViewDuration.setVisibility(View.INVISIBLE);
        //textViewDuration.setText(Integer.toString(activityHistoryItem.getDuration()/60) + "min");
        textViewRewardi.setText("-" + Integer.toString(historyItemBox.getUsedRewardi()));

        return convertView;
    }
}
