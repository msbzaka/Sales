package com.zakarneh.sales;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/7/2016.
 */
public class SellProductAdapter {
    private static List<product> AllValues;
    private static int AllQuantityT[]=null;
    private static boolean AllCheckB[]=null;
    private static Context context;

    static void initializeValues(List<product> ALL,int[] All1, boolean[] All2,Context c){
        AllValues=ALL;
        context=c;
        if(All1==null&&All2==null) {
            int size = ALL.size();
            if (size != 0) {
                AllQuantityT = new int[size];
                AllCheckB = new boolean[size];
            }
        }
        else{
            AllQuantityT=All1;
            AllCheckB=All2;
        }
    }
    static int[] getAllQuantityT(){
        return AllQuantityT;
    }
    static boolean[] getAllCeckB(){
        return AllCheckB;
    }
    static class CustomProductAdapter extends ArrayAdapter<product> implements Parcelable {
        private List<product> values;
        private int QuantityT[]=null;
        private boolean CheckB[]=null;
        private int SemiPos[]=null;
        private ViewHolder holder;
        private AlertDialog DLog;
        private AlertDialog.Builder builder;
        private EditText Q;

        CustomProductAdapter(Context context, List<product> values) {
            super(context, 0, values);
            SellProductAdapter.context = context;
            this.values = values;
            int size = values.size();
            if(size!=0) {
                QuantityT = new int[size];
                CheckB = new boolean[size];
                SemiPos = new int[size];
                int c = 0;
                for (int i = 0; i < AllValues.size() && c < values.size(); i++) {
                    if (values.get(c).getProduct_id() == AllValues.get(i).getProduct_id()) {
                        try {
                            QuantityT[c] = AllQuantityT[i];
                            CheckB[c] = AllCheckB[i];
                            SemiPos[c] = i;
                            c++;
                        }
                        catch (IndexOutOfBoundsException e){
                            Log.v("Inside Sell Product Adapter","IndexOutOfBoundsException");
                        }
                    }
                }
            }

        }


        @Override
        public android.view.View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            holder.position = position;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_sell_prod, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.SellProdNameView);
                holder.price = (TextView) convertView.findViewById(R.id.SellProdPriceView);
                holder.av = (TextView) convertView.findViewById(R.id.SellProdAvView);
                holder.img = (ImageView) convertView.findViewById(R.id.SellProdImg);

                holder.CheckProd = (CheckBox) convertView.findViewById(R.id.CheckProd);
                holder.CheckProd
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void onCheckedChanged(final CompoundButton view,
                                                         boolean isChecked) {
                                final int POS = (Integer) view.getTag();
                                if (view.isChecked() && CheckB[POS] != true) {
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Enter the quantity you want");
                                    Q = new EditText(context);
                                    Q.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    Q.setHint("");
                                    builder.setView(Q);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                int q = Integer.parseInt(Q.getText().toString());
                                                int Av = values.get(POS).getAvailable();
                                                if (q == 0)
                                                    view.setChecked(false);
                                                else if (q <= Av) {
                                                    setQuantity(POS, q);
                                                    view.setText(q + "");
                                                    setCheckB(POS, true);
                                                    view.setChecked(true);
                                                    CustomProductAdapter.this.notifyDataSetChanged();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(context, "The Quantity you entered is BIGGER than the available quantity.", Toast.LENGTH_LONG).show();
                                                    if (QuantityT[POS] == 0)
                                                        view.setChecked(false);
                                                }
                                            } catch (NumberFormatException e) {
                                                if (((CheckBox) view).getText().equals("0"))
                                                    view.setChecked(false);
                                            }
                                        }
                                    });
                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            notifyDataSetChanged();
                                        }
                                    });

                                    DLog = builder.create();
                                    DLog.show();
                                    DLog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                                }
                                setCheckB(POS,view.isChecked());
                                if (!view.isChecked()) {
                                    view.setText("0");
                                    setQuantity(POS, 0);
                                }
                            }
                        });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                // holder.QuantityText.setText(QuantityT[position]);
            }
            product p = values.get(position);

            if (p != null) {
                holder.name.setText(p.getProduct_name());
                holder.price.setText(p.getPrice() + " $");
                holder.av.setText("Available Quantity : " + p.getAvailable());

                holder.CheckProd.setTag(position);
                holder.CheckProd.setChecked(CheckB[position]);
                if (QuantityT[position] == 0) {
                    holder.CheckProd.setChecked(false);
                    setCheckB(position,false);
                }

                holder.CheckProd.setText(QuantityT[position] + "");
                if (holder.img != null) {
                    holder.img.setTag(p.getPhoto());
                    new LoadImage(holder.img).execute();
                }
            }
            return convertView;
        }
        void setCheckB(int pos,boolean check){
            CheckB[pos] = check;
            AllCheckB[SemiPos[pos]] = check;
            Log.v(SemiPos+"",AllCheckB[SemiPos[pos]]+"");
        }
        boolean getCheckB(int position) {
            return AllCheckB[position];
        }
        void setQuantity(int pos,int quantity){
            try {
                QuantityT[pos] = quantity;
                AllQuantityT[SemiPos[pos]] = quantity;
                Log.v(SemiPos+"",AllQuantityT[SemiPos[pos]]+"");
            }
            catch(IndexOutOfBoundsException e){
                Log.v("Inside Sell Product Adapter","IndexOutOfBoundsException");
            }
        }
        int getQuantity(int position) {
            return AllQuantityT[position];
        }

        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

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

                if (file.exists()) {
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

                if (result != null && imv != null) {
                    imv.setVisibility(View.VISIBLE);
                    imv.setImageBitmap(result);
                } else {
                    imv.setVisibility(View.GONE);
                }
            }

        }

        protected CustomProductAdapter(Parcel in) {
            super(context,0,in.readArrayList(product.class.getClassLoader()));
            values = new ArrayList<product>();
            if (in.readByte() == 0x01) {
                in.readList(values, product.class.getClassLoader());
            }
            holder = (ViewHolder) in.readValue(ViewHolder.class.getClassLoader());
            DLog = (AlertDialog) in.readValue(AlertDialog.class.getClassLoader());
            builder = (AlertDialog.Builder) in.readValue(AlertDialog.Builder.class.getClassLoader());
            Q = (EditText) in.readValue(EditText.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (values == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(values);
            }
/*            dest.writeValue(holder);
            dest.writeValue(DLog);
            dest.writeValue(builder);
            dest.writeValue(Q);*/
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<CustomProductAdapter> CREATOR = new Parcelable.Creator<CustomProductAdapter>() {
            @Override
            public CustomProductAdapter createFromParcel(Parcel in) {
                return new CustomProductAdapter(in);
            }

            @Override
            public CustomProductAdapter[] newArray(int size) {
                return new CustomProductAdapter[size];
            }
        };
    }
    public boolean[] getAllCheckB(){
        return AllCheckB;
    }
    static class ViewHolder{
        TextView name;
        TextView price;
        TextView av;
        ImageView img;
        CheckBox CheckProd;
        int position;
    }
}

