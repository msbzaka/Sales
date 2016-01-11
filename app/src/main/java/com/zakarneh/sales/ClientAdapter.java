package com.zakarneh.sales;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by USER on 1/3/2016.
 */
public class ClientAdapter extends ArrayAdapter<client> {
    private final Context context;
    private List<client> values;
    private ViewHolder holder;

    ClientAdapter(Context context, List<client> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_client, parent, false);
            holder.position=position;
            holder.name = (TextView) convertView.findViewById(R.id.ClientNameView);
            holder.city = (TextView) convertView.findViewById(R.id.ClientCityView);
            holder.pn = (TextView) convertView.findViewById(R.id.ClientPhNoView);;
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        client c =values.get(position);
        if(c!=null) {

            holder.name.setText(c.getName());
            holder.city.setText("City : "+c.getCity());
            holder.pn.setText("Phone NO : "+c.getPhone());

        }
        return convertView;

    }
    static class ViewHolder {
        TextView name;
        TextView city;
        TextView pn;
        int position;
    }
}