package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

class CustomListAdapterTodoList extends BaseAdapter {
    private List<TodoListPoint> listTodoListPointsSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<TodoListPoint> listTodoListPoints;
    private Context context;

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
        TextView text1 = (TextView) convertView.findViewById(R.id.text1);
        final CheckBox cbDone = (CheckBox) convertView.findViewById(R.id.cbDone);
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){       // checkbox is checked
                    // HN-CHECK -> ask user if todo list point is really done
                }
            }
        });


        text1.setText(point.getName());
        //image1.setImageResource(R.drawable.test);;

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

    public List<TodoListPoint> getListTodoListPointsSelected() {
        return listTodoListPointsSelected;
    }

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
}
