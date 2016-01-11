package com.zakarneh.sales;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotesScreen extends AppCompatActivity {
    private ListView NotesLv;
    private RunDatabaseHelper db;
    private NoteAdapter adapter;
    private List<note> mNotes;
    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_screen);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));

        db=new RunDatabaseHelper(this);
        NotesLv=(ListView)findViewById(R.id.NotesLv);
        mNotes=db.getAllNotes();
        adapter=new NoteAdapter(this,mNotes);
        NotesLv.setAdapter(adapter);
        registerForContextMenu(NotesLv);
        db.close();
        
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, 0, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id =item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        note n=adapter.getItem(info.position);
        if(id==0) {//DELETE
            //Toast.makeText(NotesScreen.this, ""+n.getNote_text(), Toast.LENGTH_SHORT).show();
            delete(n,info.position);
            return true;
        }
        return super.onContextItemSelected(item);
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

        final TextView date = new TextView(NotesScreen.this);
        date.setHint("Date(dd/mm/yyyy)");
        layout.addView(date);

        builder.setView(layout);
        Calendar calender= Calendar.getInstance();
        final int year,month,day;
        year=calender.get(Calendar.YEAR);
        month=calender.get(Calendar.MONTH);
        day=calender.get(Calendar.DAY_OF_MONTH);
        date.setFocusable(false);
        date.setText(day + "/" + (month + 1) + "/" + year);
       /* date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dateDialog = new DatePickerDialog(NotesScreen.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        date.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                    }},year,month,day);
                    dateDialog.show();

                }
            }

            );*/

// Set up the buttons

            builder.setPositiveButton("ADD",new DialogInterface.OnClickListener()

            {
                @Override
                public void onClick (DialogInterface dialog,int which){
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                note n = new note(name.getText().toString(), timeStamp);
                db.addNote(n);
                mNotes=db.getAllNotes();
                adapter=new NoteAdapter(NotesScreen.this,mNotes);
                NotesLv.setAdapter(adapter);
                Toast.makeText(NotesScreen.this, "The note is added successfully!", Toast.LENGTH_LONG).show();
            }
            }

            );
            builder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener()

            {
                @Override
                public void onClick (DialogInterface dialog,int which){
                dialog.cancel();
            }
            }

            );

            builder.show();
        }
    private void delete(final note n, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesScreen.this);
        builder.setTitle("Are you sure?");

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteNote(n);
                mNotes=db.getAllNotes();
                adapter=new NoteAdapter(NotesScreen.this,mNotes);
                NotesLv.setAdapter(adapter);
                Toast.makeText(NotesScreen.this, "The note is deleted successfully!", Toast.LENGTH_SHORT).show();
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
