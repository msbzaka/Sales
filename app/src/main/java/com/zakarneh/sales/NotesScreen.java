package com.zakarneh.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_screen);
        db=new RunDatabaseHelper(this);
        NotesLv=(ListView)findViewById(R.id.NotesLv);
        adapter=new CustomAdapter(this,db.getAllNotes());
        NotesLv.setAdapter(adapter);

        addNote=(ImageButton)findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        db.close();
    }
}
