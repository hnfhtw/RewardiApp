package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CustomListAdapterTodoListHistory extends BaseAdapter {
    private List<HistoryItemTodoListPoint> todoListHistoryList;
    private Context context;

    public CustomListAdapterTodoListHistory(Context context, List<HistoryItemTodoListPoint> todoListHistoryList) {
        this.todoListHistoryList = todoListHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
         return todoListHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return todoListHistoryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(HistoryItemTodoListPoint historyItemTodoListPoint){
        todoListHistoryList.add(0,historyItemTodoListPoint);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_history_todolist, null);
        final HistoryItemTodoListPoint todoListHistoryItem = todoListHistoryList.get(position);

        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        textViewName.setText("-NAME- " + Integer.toString(todoListHistoryItem.getId()));
        textViewDate.setText(todoListHistoryItem.getTimestamp());
        textViewRewardi.setText("-R-");

        return convertView;
    }
}
