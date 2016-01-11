package com.zakarneh.sales;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/3/2016.
 */
public class ProductAdapter extends ArrayAdapter<product> implements Parcelable {
    private final Context context;
    private List<product> values;
    private ViewHolder holder;

    ProductAdapter(Context context, List<product> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    @Override
    public android.view.View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_prod, parent, false);
            holder.position=position;
            holder.name = (TextView) convertView.findViewById(R.id.ProdNameView);
            holder.price = (TextView) convertView.findViewById(R.id.ProdPriceView);
            holder.av = (TextView) convertView.findViewById(R.id.ProdAvView);
            holder.img = (ImageView) convertView.findViewById(R.id.ProdImg);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        product p =values.get(position);
        if(p!=null) {

            holder.name.setText(p.getProduct_name());
            holder.price.setText(p.getPrice() + " $");
            holder.av.setText("Quantity : "+p.getAvailable());
            if(holder.img!=null){
                holder.img.setTag(p.getPhoto());
                new LoadImage(holder.img).execute();
            }

        }
        return convertView;
    }
    static class ViewHolder {
        TextView name;
        TextView price;
        TextView av;
        ImageView img;
        int position;
    }
    class LoadImage extends AsyncTask<Object, Void, Bitmap>{

        private ImageView imv;
        private String path;

        public LoadImage(ImageView imv) {
            this.imv = imv;
            this.path = imv.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            File file = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + "/" + path);

            if(file.exists()){
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if (!imv.getTag().toString().equals(path)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                return;
            }

            if(result != null && imv != null){
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            }else{
                imv.setVisibility(View.GONE);
            }
        }

    }

    protected ProductAdapter(Parcel in) {
        super((Context)in.readValue(Context.class.getClassLoader()),0,in.readArrayList(product.class.getClassLoader()));
        context = (Context)in.readValue(Context.class.getClassLoader());
        if (in.readByte() == 0x01) {
            values = new ArrayList<product>();
            in.readList(values, product.class.getClassLoader());
        } else {
            values = null;
        }
        holder = (ViewHolder) in.readValue(ViewHolder.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeValue(context);
        if (values == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(values);
        }
        //dest.writeValue(holder);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductAdapter> CREATOR = new Parcelable.Creator<ProductAdapter>() {
        @Override
        public ProductAdapter createFromParcel(Parcel in) {
            return new ProductAdapter(in);
        }

        @Override
        public ProductAdapter[] newArray(int size) {
            return new ProductAdapter[size];
        }
    };
}