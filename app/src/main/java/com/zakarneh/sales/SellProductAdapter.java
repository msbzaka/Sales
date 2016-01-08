package com.zakarneh.sales;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by USER on 1/7/2016.
 */
public class SellProductAdapter extends ArrayAdapter<product> {
    private final Context context;
    private List<product> values;

    SellProductAdapter(Context context, List<product> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_sell_prod, parent, false);
        }
        product p =values.get(position);
        if(p!=null) {
            TextView name = (TextView) convertView.findViewById(R.id.SellProdNameView);
            TextView price = (TextView) convertView.findViewById(R.id.SellProdPriceView);
            TextView av = (TextView) convertView.findViewById(R.id.SellProdAvView);
            ImageView img = (ImageView) convertView.findViewById(R.id.SellProdImg);
            name.setText(p.getProduct_name());
            price.setText(p.getPrice() + " $");
            av.setText("Quantity : "+p.getAvailable());
            try {
                Bitmap m = BitmapFactory.decodeFile(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        + "/" + p.getPhoto().toString());
                /*context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator + values.get(position).toString()*/
                if(m!=null)
                    img.setImageBitmap(m);
            }
            catch (Exception e){

            }
        }
        return convertView;
    }
}
