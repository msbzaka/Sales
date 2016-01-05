package com.zakarneh.sales;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClientsScreen extends AppCompatActivity {
    private ListView ClientsLv;
    private ImageButton addClient;
    private RunDatabaseHelper db;
    private ClientAdapter adapter;
    private Drawable mIconCloseSearch, mIconOpenSearch;
    private boolean mSearchOpened;
    private MenuItem mSearchAction,mAddAction;
    private android.support.v7.app.ActionBar actionBar;
    private EditText mSearchEt;
    private String mSearchQuery;
    private List<client> mClients;
    private List<client> mClientsFiltered;
    private static String CLIENTS="Initial Clients",CLIENTS_FILTERED="Filtered Clients"
            ,SEARCH_OPENED="Search Opened",SEARCH_QUERY="Search Query";
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.clients_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));

        
        
        db=new RunDatabaseHelper(this);
        mClients=db.getAllClients();

        if (savedState == null) {
            mClients = db.getAllClients();
            mClientsFiltered = mClients;
            mSearchOpened = false;
            mSearchQuery = "";
        } else {
            /*mClients = savedState.getParcelableArrayList(CLIENTS);
            mClientsFiltered = savedState
                    .getParcelableArrayList(CLIENTS_FILTERED);
            mSearchOpened = savedState.getBoolean(SEARCH_OPENED);
            mSearchQuery = savedState.getString(SEARCH_QUERY);*/
        }
        adapter=new ClientAdapter(this,mClientsFiltered);
        ClientsLv=(ListView)findViewById(R.id.ClientsLv);
        ClientsLv.setAdapter(adapter);
        mSearchOpened = false;

        registerForContextMenu(ClientsLv);
        
        mIconOpenSearch = getResources()
                .getDrawable(R.drawable.ic_search_white_24dp);
        mIconCloseSearch = getResources()
                .getDrawable(R.drawable.ic_close_white_24dp);




        db.close();
    }
   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CLIENTS, (ArrayList<client>) mClients);
        outState.putParcelableArrayList(CLIENTS_FILTERED,
                (ArrayList<client>) mClientsFiltered);
        outState.putBoolean(SEARCH_OPENED, mSearchOpened);
        outState.putString(SEARCH_QUERY, mSearchQuery);
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.clients_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search_client);
        mAddAction=menu.findItem(R.id.action_add_client);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_client) {
            if (mSearchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
            return true;
        }
        else if(id==R.id.action_add_client){
            add();
            return true;
        }
        else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    private void openSearchBar(String queryText) {

        // Set custom view on action bar.
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.clients_action_bar);

        // Search edit text field setup.
        mSearchEt = (EditText) actionBar.getCustomView()
                .findViewById(R.id.etSearch);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchQuery = mSearchEt.getText().toString();
                mClientsFiltered=performSearch(mClients,mSearchQuery);
                adapter=new ClientAdapter(ClientsScreen.this,mClientsFiltered);
                ClientsLv.setAdapter(adapter);
            }
        });
        mSearchEt.setText(queryText);
        mSearchEt.requestFocus();

        // Change search icon accordingly.
        mSearchAction.setIcon(mIconCloseSearch);
        mSearchOpened = true;

    }
    private void closeSearchBar() {

        // Remove custom view.
        actionBar.setDisplayShowCustomEnabled(false);

        // Change search icon accordingly.
        mSearchAction.setIcon(mIconOpenSearch);
        mSearchOpened = false;
    }
    private void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsScreen.this);
        builder.setTitle("Add Client");
        LinearLayout layout = new LinearLayout(ClientsScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(ClientsScreen.this);
        name.setHint("Client Name");
        layout.addView(name);

        final EditText city = new EditText(ClientsScreen.this);
        city.setHint("City");
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
    private List<client> performSearch(List<client> Clients, String query) {

        // First we split the query so that we're able
        // to search word by word (in lower case).
        String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty list to fill with matches.
        List<client> ClientsFiltered = new ArrayList<client>();

        // Go through initial releases and perform search.
        for (client c : mClients) {

            // Content to search through (in lower case).
            String content = (
                    c.toString()
            ).toLowerCase();

            for (String word : queryByWords) {

                // There is a match only if all of the words are contained.
                int numberOfMatches = queryByWords.length;

                // All query words have to be contained,
                // otherwise the release is filtered out.
                if (content.contains(word)) {
                    numberOfMatches--;
                } else {
                    break;
                }

                // They all match.
                if (numberOfMatches == 0) {
                    ClientsFiltered.add(c);
                }

            }

        }

        return ClientsFiltered;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, 0, 0, "Edit");
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id =item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        client c=adapter.getItem(info.position);
        if(id==1) {//DELETE
            delete(c, info.position);
            return true;
        }
        else if(id==0){//Edit
            edit(c,info.position);
            return true;
        }
        return false;
    }
    private void edit(final client c, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsScreen.this);
        builder.setTitle("Edit Client");
        LinearLayout layout = new LinearLayout(ClientsScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(ClientsScreen.this);
        name.setHint("New Client Name");
        layout.addView(name);

        final EditText city = new EditText(ClientsScreen.this);
        city.setHint("New City");
        layout.addView(city);

        final EditText no = new EditText(ClientsScreen.this);
        no.setHint("New Phone Number");
        no.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(no);

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client newC=c;
                newC.setName(name.getText().toString());
                newC.setCity(city.getText().toString());
                newC.setPhone(no.getText().toString());
                db.updateClient(newC);
                mClients.set(position,newC);
                adapter = new ClientAdapter(ClientsScreen.this,mClientsFiltered);
                ClientsLv.setAdapter(adapter);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void delete(final client c, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientsScreen.this);
        builder.setTitle("Are you sure?");

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteClient(c);
                mClients=db.getAllClients();
                mClientsFiltered.remove(position);
                adapter = new ClientAdapter(ClientsScreen.this,mClientsFiltered);
                ClientsLv.setAdapter(adapter);
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
