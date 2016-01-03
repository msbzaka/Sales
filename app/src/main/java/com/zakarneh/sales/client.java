package com.zakarneh.sales;
/**
 * Created by anas on 12/31/2015.
 */
/*
"create table clients (" +
                "client_id INTEGER primary key autoincrement,client_name TEXT,city TEXT,phone TEXT"
 */
public class client {
    private int client_id;
    private String client_name,city,phone;

    client(){

    }
    client(String name,String ci,String pho){
        client_name=name;
        city=ci;
        phone=pho;
    }
    client(int id,String name,String ci,String pho){
        client_id=id;
        client_name=name;
        city=ci;
        phone=pho;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getClient_id() {
        return client_id;
    }

    void setName(String name){
        client_name=name;
    }
    void setPhone(String name){
        phone=name;
    }
    void setCity(String ci){
        city=ci;
    }

    String getName(){
        return client_name;
    }
    String getPhone(){
        return  phone;
    }

    public String  getCity() {
        return city;
    }

    @Override
    public String toString() {
        //return client_id+" "+client_name+" "+city+" "+phone;
        return client_name;
    }
}
