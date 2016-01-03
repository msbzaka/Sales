package com.zakarneh.sales;

/**
 * Created by anas on 12/31/2015.
 */
/*
create table notes ("+
                        "note_id INTEGER primary key autoincrement,note_text TEXT,note_date date"
 */
public class note {
    private int note_id;
    private String note_text;
    private String note_date;
    note(){

    }
    note(String no,String  d){
        note_text=no;
        note_date=d;
    }
    note(int id,String no,String d){
        note_id=id;
        note_text=no;
        note_date=d;
    }
    public void setNote_date(String  note_date) {
        this.note_date = note_date;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getNote_date() {
        return note_date;
    }

    public String getNote_text() {
        return note_text;
    }

    public int getNote_id() {
        return note_id;
    }

    @Override
    public String toString() {
        //return note_id+"  "+note_text+" "+note_date;
        return note_text;
    }
}
