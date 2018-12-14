package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CustomListAdapterTodoList extends BaseAdapter {
    private List<TodoListPoint> listTodoListPointsSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<TodoListPoint> listTodoListPoints;
    private Context context;
    Globals appState;

    public CustomListAdapterTodoList(Context context, List<TodoListPoint> listTodoListPoints) {
        this.listTodoListPoints = listTodoListPoints;
        this.context = context;
        listTodoListPointsSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
    }

    @Override
    public int getCount() {
         return listTodoListPoints.size();
    }

    @Override
    public Object getItem(int position) {
        return listTodoListPoints.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(TodoListPoint point){
        listTodoListPoints.add(point);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_todolist, null);
        final TodoListPoint point = listTodoListPoints.get(position);
        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        TextView textViewRewardi2 = (TextView) convertView.findViewById(R.id.textViewRewardi2);
        final CheckBox cbDone = (CheckBox) convertView.findViewById(R.id.cbDone);

        appState = ((Globals)context.getApplicationContext());
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){       // checkbox is checked
                    // HN-CHECK -> ask user if todo list point is really done
                    appState.sendMessageToServer(Globals.messageID.TODO_DONE,point.getId(),null, null);
                    point.setDone(true);
                    cbDone.setEnabled(false);
                }
            }
        });

        textViewName.setText(point.getName());
        textViewRewardi.setText("Earn "+Integer.toString(point.getRewardi())+" Rewardi");
        textViewRewardi2.setText(Integer.toString(point.getRewardi()));
        return convertView;
    }

    public void handleLongPress(int position, View view){
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listTodoListPointsSelected.remove(listTodoListPoints.get(position));
            view.setBackgroundResource(R.color.colorWhite);
        }else{
            listTodoListPointsSelected.add(listTodoListPoints.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.colorDarkGray);
        }

    }

    public List<TodoListPoint> getListTodoListPointsSelected() { return listTodoListPointsSelected; }

    public void setListTodoListPointsSelected(List<TodoListPoint> listTodoListPointsSelected) {
        this.listTodoListPointsSelected = listTodoListPointsSelected;
    }

    public void removeSelectedTodoListPoints(){
        listTodoListPoints.removeAll(listTodoListPointsSelected);
        listTodoListPointsSelected.clear();
        for(View view : listSelectedRows)
            view.setBackgroundResource(R.color.colorWhite);
        listSelectedRows.clear();
    }

    public void removeTodoListPoint(int pointId){
        boolean todoListPointFound = false;
        int i = 0;
        for(i = 0; i<listTodoListPointsSelected.size(); ++i){
            if(listTodoListPointsSelected.get(i).getId() == pointId){
                todoListPointFound = true;
                break;
            }
        }
        if(todoListPointFound == false){
            return;
        }
        else{
            listTodoListPoints.remove(listTodoListPointsSelected.get(i));
            listTodoListPointsSelected.remove(i);
            listSelectedRows.get(i).setBackgroundResource(R.color.colorWhite);
            listSelectedRows.remove(i);
        }
    }

    public void setItem(TodoListPoint point){
        int i = 0;
        boolean todoListPointFound = false;
        for(i = 0; i<listTodoListPoints.size(); ++i){
            if(listTodoListPoints.get(i).getId() == point.getId()){
                todoListPointFound = true;
                break;
            }
        }
        if(todoListPointFound == false){
            return;
        }
        else{
            listTodoListPoints.set(i, point);
        }
    }
}
