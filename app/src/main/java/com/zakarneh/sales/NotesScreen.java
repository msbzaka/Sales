package com.zakarneh.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class NotesScreen extends AppCompatActivity {
    private String m_Text="";
    private ListView NotesLv;
    private ImageButton addNote;
    private RunDatabaseHelper db;
    private CustomAdapter adapter;
    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));

        db=new RunDatabaseHelper(this);
        NotesLv=(ListView)findViewById(R.id.NotesLv);
        adapter=new CustomAdapter(this,db.getAllNotes());
        NotesLv.setAdapter(adapter);


        db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_add_note){
            add();
            return true;
        }
        else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesScreen.this);
        builder.setTitle("Add Note");
        LinearLayout layout = new LinearLayout(NotesScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(NotesScreen.this);
        name.setHint("The Note");
        layout.addView(name);

        final EditText date = new EditText(NotesScreen.this);
        date.setHint("Date(dd/mm/yyyy)");
        layout.addView(date);

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                note n=new note(name.getText().toString(), date.getText().toString());
                db.addNote(n);
                adapter.add(n);
                adapter.notifyDataSetChanged();
                Toast.makeText(NotesScreen.this, "Note added successfully!", Toast.LENGTH_LONG).show();
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
}
