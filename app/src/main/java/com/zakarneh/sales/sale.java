package com.zakarneh.sales;

/**
 * Created by anas on 12/31/2015.
 */
/*
create table sales (" +
                "sale_id INTEGER primary key autoincrement,client_id INTEGER not null REFERENCES clients(client_id),
                product_id INTEGER not null REFERENCES products(product_id),quantity INTEGER ,price REAL,sale_date date "
 */
public class sale {
    private int sale_id,client_id,product_id,quantity;
    private double price;
    private String date;
    sale(){

    }
    sale(int cli,int pro,int qua,double pri,String d){
        client_id=cli;
        product_id=pro;
        quantity=qua;
        price=pri;
        date=d;
    }
    sale(int id,int cli,int pro,int qua,double pri,String d){
        sale_id=id;
        client_id=cli;
        product_id=pro;
        quantity=qua;
        price=pri;
        date=d;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProduct_id() {
        return product_id;
    }

    public double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSale_id() {
        return sale_id;
    }

    @Override
    public String toString() {
        return sale_id+" "+client_id+" "+product_id+" "+quantity+" "+price+" "+date;
    }
}
