package com.zakarneh.sales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
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
        ((Button)findViewById(R.id.button)).setOnClickListener(action);
        ((Button)findViewById(R.id.button2)).setOnClickListener(action);
        ((Button)findViewById(R.id.button3)).setOnClickListener(action);
        ((Button)findViewById(R.id.button4)).setOnClickListener(action);

        ((Button)findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
