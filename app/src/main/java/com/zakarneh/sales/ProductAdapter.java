package com.zakarneh.sales;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by USER on 1/3/2016.
 */
public class ProductAdapter extends ArrayAdapter<product> {
    private final Context context;
    private List<product> values;

    ProductAdapter(Context context, List<product> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_prod, parent, false);
        }
        product p =values.get(position);
        if(p!=null) {
            TextView name = (TextView) convertView.findViewById(R.id.ProdNameView);
            TextView price = (TextView) convertView.findViewById(R.id.ProdPriceView);
            TextView av = (TextView) convertView.findViewById(R.id.ProdAvView);
            ImageView img = (ImageView) convertView.findViewById(R.id.ProdImg);
            name.setText(p.getProduct_name());
            price.setText(p.getPrice() + " $");
            av.setText("Quantity : "+p.getAvailable());
            try {
                Bitmap m = BitmapFactory.decodeFile(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator + values.get(position).toString());
                if(m!=null)
                img.setImageBitmap(m);
            }
            catch (Exception e){

            }
        }
        return convertView;
    }
}