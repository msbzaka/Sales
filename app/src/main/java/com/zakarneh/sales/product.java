package com.zakarneh.sales;

import java.io.Serializable;

/**
 * Created by anas on 12/31/2015.
 */
/*
 "create table products (" +
                "product_id INTEGER primary key autoincrement,product_name TEXT,photo TEXT,price REAL,available INTEGER )"
 */
public class product implements Serializable {
    private int product_id,available;
    private String product_name,photo;
    private double price;
    product(){

    }
    product(int id,String name,String pho,double pri,int av){
        product_id=id;
        product_name=name;
        photo=pho;
        price=pri;
        available=av;
    }
    product(String name,String pho,double pri,int av){

        product_name=name;
        photo=pho;
        price=pri;
        available=av;
    }
    void setName(String name){
        product_name=name;
    }
    void setPhoto(String name){
        photo=name;
    }
    void setAvailable(int av){
        available=av;
    }
    void setPrice(double pri){
        price=pri;
    }
    String getProduct_name(){
        return product_name;
    }
    String getPhoto(){
        return  photo;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailable() {
        return available;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    @Override
    public String toString() {
        //return product_id+" "+product_name+" "+photo+" "+price+" "+available;
        return product_name;
    }
}
