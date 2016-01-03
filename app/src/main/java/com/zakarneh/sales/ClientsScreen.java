package com.zakarneh.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ClientsScreen extends AppCompatActivity {
    private ListView ClientsLv;
    private ImageButton addClient;
    private RunDatabaseHelper db;
    private CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clients_screen);
        ClientsLv=(ListView)findViewById(R.id.ClientsLv);

        db=new RunDatabaseHelper(this);
        adapter=new CustomAdapter(this,db.getAllClients());
        ClientsLv.setAdapter(adapter);

        addClient=(ImageButton)findViewById(R.id.addClient);
        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientsScreen.this);
                builder.setTitle("Add Client");
                LinearLayout layout = new LinearLayout(ClientsScreen.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText name = new EditText(ClientsScreen.this);
                name.setHint("Client Name");
                layout.addView(name);

                final EditText city = new EditText(ClientsScreen.this);
                city.setHint("City Name");
                layout.addView(city);

                final EditText no = new EditText(ClientsScreen.this);
                no.setHint("Phone Number");
                no.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(no);

                builder.setView(layout);

// Set up the buttons
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client c=new client(name.getText().toString(), city.getText().toString(), no.getText().toString());
                        db.addclient(c);
                        adapter.add(c);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ClientsScreen.this,"Client added successfully!",Toast.LENGTH_LONG).show();

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        db.close();
    }
}
