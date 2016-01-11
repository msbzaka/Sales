package com.zakarneh.sales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by anas on 1/10/2016.
 */
public class SalesHistoryAdapter extends ArrayAdapter<sale> {
    private final Context context;
    private List<sale> values;
    private ViewHolder holder;
    static class ViewHolder {
        TextView clientName;
        TextView price;
        TextView productName;
        TextView qantity;
        TextView sumPrice;
        int position;
    }
    public SalesHistoryAdapter(Context context, List<sale> values){
        super(context,0,values);
        this.context=context;
        this.values=values;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_history, parent, false);
            holder.position=position;
            holder.clientName = (TextView) convertView.findViewById(R.id.HistoryClientNameView);
            holder.price = (TextView) convertView.findViewById(R.id.HistoryPrice);
            holder.productName=(TextView) convertView.findViewById(R.id.HistoryProductName);
            holder.qantity=(TextView) convertView.findViewById(R.id.HistoryQuantity);
            holder.sumPrice=(TextView) convertView.findViewById(R.id.HistorySumPrice);

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        sale s=values.get(position);
        if(s!=null) {

            holder.clientName.setText("Client Name : " + s.getClientName());
            holder.price.setText("One piece price : "+s.getPrice() + " $");
            holder.productName.setText("Product Name : "+s.getProductName());
            holder.qantity.setText("Quantity : "+s.getQuantity()+"");
            holder.sumPrice.setText("Total Price : "+s.getQuantity()*s.getPrice()+" $");


        }
        return convertView;
    }

}
