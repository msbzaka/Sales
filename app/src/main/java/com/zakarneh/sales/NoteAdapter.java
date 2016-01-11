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
public class NoteAdapter extends ArrayAdapter<note> {
    private final Context context;
    private List<note> values;

    NoteAdapter(Context context, List<note> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_note, parent, false);
        }
        TextView cont=(TextView) convertView.findViewById(R.id.NoteCont);
        cont.setText(values.get(position).getNote_text());
        TextView date=(TextView) convertView.findViewById(R.id.NoteDate);
        date.setText(values.get(position).getNote_date());
        return convertView;
    }
}