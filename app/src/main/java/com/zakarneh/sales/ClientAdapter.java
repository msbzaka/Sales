package com.zakarneh.sales;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by USER on 1/3/2016.
 */
public class ClientAdapter extends ArrayAdapter<client> {
    private final Context context;
    private List<client> values;

    ClientAdapter(Context context, List<client> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_view, parent, false);
        }
        TextView text=(TextView) convertView.findViewById(R.id.ItemText);
        text.setText(values.get(position).toString());
        return convertView;
    }
}