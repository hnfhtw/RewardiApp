package me.rewardi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomGridViewActivity extends BaseAdapter {

    private Context mContext;
    private List<Gadget> listGadgets;

    public CustomGridViewActivity(Context context, List<Gadget> listGadgets) {
        mContext = context;
        this.listGadgets = listGadgets;
    }

    @Override
    public int getCount() {
        return listGadgets.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(Gadget gadget){ listGadgets.add(gadget); }

    public void addItem(Box box){
        listGadgets.add(box);
    }

    public void addItem(SocketBoard socketBoard){
        listGadgets.add(socketBoard);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Log.d("GridView", "getView called");

        final Gadget gadget = listGadgets.get(i);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.content_home_gridview_layout, null);
        TextView textViewAndroid = (TextView) convertView.findViewById(R.id.android_gridview_text);
        ImageView imageViewAndroid = (ImageView) convertView.findViewById(R.id.android_gridview_image);
        textViewAndroid.setText(gadget.getName());
        Log.d("GridView", "Gadget Name = " + gadget.getName() + ", Gadget Trust Number = " + gadget.getTrustNumber());
        if(gadget instanceof SocketBoard){
            imageViewAndroid.setImageResource(R.mipmap.ic_rewardi_socket);
        }
        else if(gadget instanceof Box){
            imageViewAndroid.setImageResource(R.mipmap.ic_rewardi_box);
        }

        return convertView;
    }
}
