package com.zakarneh.sales;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/7/2016.
 */
public class SellProductAdapter extends ArrayAdapter<product> {
    private final Context context;
    private List<product> values;
    private String QuantityT[];
    private boolean CheckB[];
    private ViewHolder holder;

    SellProductAdapter(Context context, List<product> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
        int size=values.size();
        QuantityT=new String[size];
        CheckB=new boolean[size];
    }
    @Override
    public android.view.View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        holder.position=position;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_sell_prod, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.SellProdNameView);
            holder.price = (TextView) convertView.findViewById(R.id.SellProdPriceView);
            holder.av = (TextView) convertView.findViewById(R.id.SellProdAvView);
            holder.img = (ImageView) convertView.findViewById(R.id.SellProdImg);

            holder.QuantityText=(EditText)convertView.findViewById(R.id.QuantityText);
            holder.QuantityText.setText(QuantityT[position]);
            holder.CheckProd=(CheckBox)convertView.findViewById(R.id.CheckProd);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
           // holder.QuantityText.setText(QuantityT[position]);
        }
        product p = values.get(position);

        if(p!=null) {
            holder.name.setText(p.getProduct_name());
            holder.price.setText(p.getPrice() + " $");
            holder.av.setText("Quantity : " + p.getAvailable());

            holder.QuantityText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    QuantityT[position] = s.toString();
               }
            });

            holder.CheckProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckB[position] = isChecked;
                }
            });
            holder.CheckProd.setChecked(CheckB[position]);
            if(holder.img!=null){
                holder.img.setTag(p.getPhoto());
                new LoadImage(holder.img).execute();
            }
        }
        return convertView;
    }
    boolean getCheckB(int position){
        return CheckB[position];
    }
    String getQuantity(int position){
        return QuantityT[position];
    }
    static class ViewHolder {
        TextView name;
        TextView price;
        TextView av;
        ImageView img;
        EditText QuantityText;
        CheckBox CheckProd;
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
}
