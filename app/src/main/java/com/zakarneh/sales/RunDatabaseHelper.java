package com.zakarneh.sales;

/**
 * Created by anas on 1/2/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anas on 12/31/2015.
 */
public class RunDatabaseHelper extends SQLiteOpenHelper {
    //data base name
    private static final String DB_NAME = "PROJECT_DB";
    private static final int VERSION = 1;
    public RunDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "products" table
        db.execSQL("create table products (" +
                "product_id INTEGER primary key autoincrement, product_name TEXT , photo TEXT , price REAL , available INTEGER )");
        // Create the "clients" table
        db.execSQL("create table clients (" +
                "client_id INTEGER primary key autoincrement,client_name TEXT,city TEXT,phone TEXT )");
        //create the "sales" table
        db.execSQL("create table sales (" +
                "sale_id INTEGER primary key autoincrement,client_id INTEGER not null REFERENCES clients(client_id),product_id INTEGER not null REFERENCES products(product_id),quantity INTEGER ,price REAL,sale_date TEXT ) ");
        db.execSQL(
                "create table notes (" +
                        "note_id INTEGER primary key autoincrement,note_text TEXT,note_date TEXT )"
        );

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement schema changes and data massage here when upgrading
        //Drop older tables
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS clients");
        db.execSQL("DROP TABLE IF EXISTS sales");
        db.execSQL("DROP TABLE IF EXISTS notes");
        //create fresh tables
        this.onCreate(db);
    }
    public void addProduct(product pro){

        //get reference to database


        SQLiteDatabase db= this.getWritableDatabase();

        //

        ContentValues values = new ContentValues();
        values.put("product_name",pro.getProduct_name());
        values.put("photo", pro.getPhoto());
        values.put("price", pro.getPrice());
        values.put("available", pro.getAvailable());

        //insert
        db.insert("products", // table
                null, //nullColumnHack
                values);
        Log.d("add product : ", pro.toString());
        //close
        //db.close();
    }
    private static final String[] product_columns = {"product_id","product_name","photo","price","available"};
    public product getProduct(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("products", // a. table
                        product_columns, // b. column names
                        "product_id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        product pro = new product(Integer.parseInt((cursor.getString(0))),cursor.getString(1),cursor.getString(2),Double.parseDouble(cursor.getString(3)),Integer.parseInt(cursor.getString(4)));
        //log
        Log.d("getBook(" + id + ")", pro.toString());

        // 5. return book
        return pro;

    }
    public List<product> getAllProducts() {
        List<product> products = new ArrayList<product>();

        // 1. build the query
        String query = "SELECT  * FROM " + "products";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        product pro = null;
        if (cursor.moveToFirst()) {
            do {
                pro = new product();
                pro.setProduct_id(Integer.parseInt(cursor.getString(0)));
                pro.setProduct_name(cursor.getString(1));
                pro.setPhoto(cursor.getString(2));
                pro.setPrice(Double.parseDouble(cursor.getString(3)));
                pro.setAvailable(Integer.parseInt(cursor.getString(4)));
                // Add book to books
                products.add(pro);
            } while (cursor.moveToNext());
        }

        Log.d("getAllProducts()", products.toString());

        // return books
        return products;
    }
    public int updateProduct(product pro) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("product_name",pro.getProduct_name());
        values.put("photo", pro.getPhoto());
        values.put("price", pro.getPrice());
        values.put("available", pro.getAvailable());

        // 3. updating row
        int i = db.update("products", //table
                values, // column/value
                "product_id"+" = ?", // selections
                new String[] { String.valueOf(pro.getProduct_id()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public void deleteProduct(product pro) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("products",
                "product_id" + " = ?",
                new String[]{String.valueOf(pro.getProduct_id())});

        // 3. close
        db.close();

        Log.d("deleteProduct", pro.toString());

    }
    public void addsale(sale sa){

        //get reference to database


        SQLiteDatabase db= this.getWritableDatabase();

        //

        ContentValues values = new ContentValues();
        values.put("client_id",sa.getClient_id());
        values.put("product_id", sa.getProduct_id());
        values.put("quantity", sa.getQuantity());
        values.put("price", sa.getPrice());
        values.put("sale_date", String.valueOf(sa.getDate()));

        //insert
        db.insert("sales", // table
                null, //nullColumnHack
                values);
        Log.d("add client : ", sa.toString());
        //close
        db.close();
    }
    private static final String[] sale_columns = {"client_id","product_id","quantity","price","sale_date"};
    public sale getSale(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("sales", // a. table
                        sale_columns, // b. column names
                        "sale_id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        sale sa = new sale(Integer.parseInt((cursor.getString(0))),Integer.parseInt((cursor.getString(1))),Integer.parseInt((cursor.getString(2))),Integer.parseInt((cursor.getString(3))),Double.parseDouble(cursor.getString(4)), cursor.getString(5));
        //log
        Log.d("getSale(" + id + ")", sa.toString());

        // 5. return book
        return sa;

    }
    public List<sale> getAllSales() {
        List<sale> sales = new ArrayList<sale>();

        // 1. build the query
        String query = "SELECT  * FROM " + "sales";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        sale sa = null;
        if (cursor.moveToFirst()) {
            do {
                sa = new sale();
                sa.setSale_id(Integer.parseInt(cursor.getString(0)));
                sa.setClient_id(Integer.parseInt(cursor.getString(1)));
                sa.setProduct_id(Integer.parseInt(cursor.getString(2)));
                sa.setQuantity(Integer.parseInt(cursor.getString(3)));
                sa.setPrice(Double.parseDouble(cursor.getString(4)));
                sa.setDate(cursor.getString(5));
                // Add book to books
                sales.add(sa);
            } while (cursor.moveToNext());
        }

        Log.d("getAllClients()", sales.toString());

        // return books
        return sales;
    }
    public int updateSale(sale sa) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("client_id",sa.getClient_id());
        values.put("product_id", sa.getProduct_id());
        values.put("quantity", sa.getQuantity());
        values.put("price", sa.getPrice());
        values.put("sale_date", String.valueOf(sa.getDate()));


        // 3. updating row
        int i = db.update("sales", //table
                values, // column/value
                "sale_id"+" = ?", // selections
                new String[] { String.valueOf(sa.getSale_id()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public void deleteSale(sale sa) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("sales",
                "sale_id" + " = ?",
                new String[]{String.valueOf(sa.getSale_id())});

        // 3. close
        db.close();

        Log.d("deleteSale", sa.toString());

    }
    public void addclient(client cli){

        //get reference to database


        SQLiteDatabase db= this.getWritableDatabase();

        //

        ContentValues values = new ContentValues();
        values.put("client_name",cli.getName());
        values.put("city", cli.getCity());
        values.put("phone", cli.getPhone());

        //insert
        db.insert("clients", // table
                null, //nullColumnHack
                values);
        Log.d("add client : ", cli.toString());
        //close
        db.close();
    }
    private static final String[] client_columns = {"client_id","client_name","city","phone"};
    public client getClient(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("clients", // a. table
                        client_columns, // b. column names
                        "client_id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        client cli=null;
        try {
            cli = new client(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        catch (Exception e){
            return  null;
        }
        //log
        Log.d("getClient("+id+")", cli.toString());

        // 5. return book
        return cli;

    }
    public List<client> getAllClients() {
        List<client> clients = new ArrayList<client>();

        // 1. build the query
        String query = "SELECT  * FROM " + "clients";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        client cli = null;
        if (cursor.moveToFirst()) {
            do {
                cli = new client();
                cli.setClient_id(Integer.parseInt(cursor.getString(0)));
                cli.setName(cursor.getString(1));
                cli.setCity(cursor.getString(2));
                cli.setPhone(cursor.getString(3));
                // Add book to books
                clients.add(cli);
            } while (cursor.moveToNext());
        }

        Log.d("getAllClients()", clients.toString());

        // return books
        return clients;
    }
    public int updateClient(client cli) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("client_name",cli.getName());
        values.put("city", cli.getCity());
        values.put("phone", cli.getPhone());


        // 3. updating row
        int i = db.update("clients", //table
                values, // column/value
                "client_id"+" = ?", // selections
                new String[] { String.valueOf(cli.getClient_id()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public void deleteClient(client cli) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("clients",
                "client_id"+" = ?",
                new String[] { String.valueOf(cli.getClient_id()) });

        // 3. close
        db.close();

        Log.d("deleteClient", cli.toString());

    }
    public void addNote(note no){

        //get reference to database


        SQLiteDatabase db= this.getWritableDatabase();

        //

        ContentValues values = new ContentValues();
        values.put("note_text",no.getNote_text());
        values.put("note_date",no.getNote_date().toString());

        //insert
        db.insert("notes", // table
                null, //nullColumnHack
                values);
        Log.d("add note : ", no.toString());
        //close
        db.close();
    }
    private static final String[] note_columns = {"note_id","note_text","note_date"};
    public note getNote(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("notes", // a. table
                        note_columns, // b. column names
                        "note_id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        note no = new note(Integer.parseInt((cursor.getString(0))),cursor.getString(1),cursor.getString(2));
        //log
        Log.d("getNote(" + id + ")", no.toString());

        // 5. return book
        return no;

    }
    public List<note> getAllNotes() {
        List<note> Notes = new ArrayList<note>();

        // 1. build the query
        String query = "SELECT  * FROM " + "notes";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        note no = null;
        if (cursor.moveToFirst()) {
            do {
                no = new note();
                no.setNote_id(Integer.parseInt(cursor.getString(0)));
                no.setNote_text(cursor.getString(1));
                no.setNote_date(cursor.getString(2));
                // Add book to books
                Notes.add(no);
            } while (cursor.moveToNext());
        }

        Log.d("getAllNotes()", Notes.toString());

        // return books
        return Notes;
    }
    public int updateNote(note no) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("note_text",no.getNote_text());
        values.put("note_date", no.getNote_date());


        // 3. updating row
        int i = db.update("notes", //table
                values, // column/value
                "note_id"+" = ?", // selections
                new String[] { String.valueOf(no.getNote_id()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public void deleteNote(note no) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("notes",
                "note_id"+" = ?",
                new String[] { String.valueOf(no.getNote_id()) });

        // 3. close
        db.close();

        Log.d("deleteClient", no.toString());

    }
    public List<sale> getAllSales(String startdate, String enddate) {
        List<sale> sales = new ArrayList<sale>();

        // 1. build the query
        String query = "SELECT * FROM sales WHERE sale_date BETWEEN ?  AND ?";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{startdate, enddate});

        // 3. go over each row, build book and add it to list
        sale sa = null;
        if (cursor.moveToFirst()) {
            do {
                sa = new sale();
                sa.setSale_id(Integer.parseInt(cursor.getString(0)));
                sa.setClient_id(Integer.parseInt(cursor.getString(1)));
                sa.setProduct_id(Integer.parseInt(cursor.getString(2)));
                sa.setQuantity(Integer.parseInt(cursor.getString(3)));
                sa.setPrice(Double.parseDouble(cursor.getString(4)));
                sa.setDate(cursor.getString(5));
                // Add sale to sales

                sales.add(sa);
            } while (cursor.moveToNext());
        }

        Log.d("getAllSales(date,date)", sales.toString());

        // return books
        return sales;
    }

}
