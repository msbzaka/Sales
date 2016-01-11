package com.zakarneh.sales;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    private android.support.v7.app.ActionBar actionBar;
    private static String NOTE="WARN!!";
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));
        View.OnClickListener action=new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = null;
                if(v.getId()==R.id.button)
                    i=new Intent(MainScreen.this,ClientsScreen.class);
                else if(v.getId()==R.id.button2)
                    i=new Intent(MainScreen.this,ProductsScreen.class);
                else if(v.getId()==R.id.button3)
                    i=new Intent(MainScreen.this,SalesHistoryScreen.class);
                else if(v.getId()==R.id.button4)
                    i=new Intent(MainScreen.this,NotesScreen.class);
                MainScreen.this.startActivity(i);

            }
        };
        ((ImageButton)findViewById(R.id.button)).setOnClickListener(action);
        ((ImageButton)findViewById(R.id.button2)).setOnClickListener(action);
        ((ImageButton)findViewById(R.id.button3)).setOnClickListener(action);
        ((ImageButton)findViewById(R.id.button4)).setOnClickListener(action);

       // if(savedInstanceState == null){
        prefs= getSharedPreferences("Settings5", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor=prefs.edit();
        boolean notify=prefs.getBoolean(NOTE, false);
        RunDatabaseHelper db=new RunDatabaseHelper(this);
        if(notify) {
            List<product> mProds;
            mProds = db.getAllProducts();
            for (int i = 0; i < mProds.size(); i++) {
                if (mProds.get(i).getAvailable() == 0) {
                    long[] pattern = {0, 300, 0};
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.quan)
                            .setContentTitle("Warning!")
                            .setContentText("Some of Products are not Available.")
                            .setVibrate(pattern)
                            .setAutoCancel(true);


                    mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                    mBuilder.setAutoCancel(true);
                    NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(01234, mBuilder.build());
                    editor.putBoolean(NOTE, false);
                    editor.commit();
                    break;
                }
            //}
        }
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            about();
        return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void about(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        TextView title =new TextView(this);
        title.setText("\tAbout");
        title.setTextColor(Color.GREEN);
        title.setTextSize(30);
        title.setBackgroundColor(Color.BLACK);
        builder.setCustomTitle(title);
        TextView text =new TextView(this);
        text.setTextSize(20);
        text.setTextColor(Color.RED);
        text.setBackgroundColor(Color.parseColor("#660CF227"));
        text.setText("\n\t\t\tDEVELOPED BY : \n\t\t\tMosab Zakarneh\n\t\t\tAnas Zakarneh\n\t\t\tAbdullah Hanaysheh\n\t\t\tCHEERS =)\n");
        builder.setView(text);
        builder.create().show();
    }
    @Override
    protected void onDestroy(){
        SharedPreferences prefs= getSharedPreferences("Settings",Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean(NOTE, true);
        editor.commit();
        super.onDestroy();
    }

}
