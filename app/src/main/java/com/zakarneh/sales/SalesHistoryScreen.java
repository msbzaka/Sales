package com.zakarneh.sales;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesHistoryScreen extends AppCompatActivity {
    private ListView SalesLv;
    private android.support.v7.app.ActionBar actionBar;
    private RunDatabaseHelper db;
    private List<sale> mSales;
    private List<sale> mSalesFiltered;
    private boolean mSearchOpened;
    private SalesHistoryAdapter adapter;
    private MenuItem mSearchAction;
    private EditText mSearchEt;
    private String mSearchQuery;
    private Drawable mIconCloseSearch, mIconOpenSearch;
    private EditText mFrom,mTo;
    private CheckBox DateFilter;
    private String tmp=null;
    private static String ADAPTER="Adapter of ListView",SEARCH_OPENED="Search Opened",SEARCH_QUERY="Search Query";
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.sales_history_screen);

        
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));


        mFrom=(EditText)findViewById(R.id.FromDate);
        mTo=(EditText)findViewById(R.id.ToDate);
        DateFilter=(CheckBox)findViewById(R.id.DateFilter);

        mFrom.setFocusable(false);
        mTo.setFocusable(false);
        Calendar calender= Calendar.getInstance();
        final int year,month,day;
        year=calender.get(Calendar.YEAR);
        month=calender.get(Calendar.MONTH);
        day=calender.get(Calendar.DAY_OF_MONTH);
        mFrom.setText(day + "/" + (month+1) + "/" + year);
        mTo.setText((day+1) + "/" + (month+1) + "/" + year);
        DateFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(mSearchOpened)
                        mSalesFiltered = performSearch(mSales, mSearchQuery);
                    else
                    mSalesFiltered=mSales;
                    mSalesFiltered = SearchBetweenDate(mSalesFiltered, mFrom.getText().toString(), mTo.getText().toString());

                    adapter=new SalesHistoryAdapter(SalesHistoryScreen.this,mSalesFiltered);
                    SalesLv.setAdapter(adapter);
                    mFrom.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     DatePickerDialog dateDialog = new DatePickerDialog(SalesHistoryScreen.this, new DatePickerDialog.OnDateSetListener() {

                                                         @Override
                                                         public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                                               int dayOfMonth) {
                                                             // TODO Auto-generated method stub
                                                             tmp=null;
                                                             tmp=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                                             if(tmp!=null) {
                                                                 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
                                                                 Date d1 = null, d2 = null;
                                                                 try {
                                                                     d1 = dateFormat.parse(mTo.getText().toString());
                                                                     d2 = dateFormat.parse(tmp);
                                                                     if (d2.before(d1))
                                                                         mFrom.setText(tmp);
                                                                     else
                                                                         Toast
                                                                       .makeText(SalesHistoryScreen.this, "Date Set Error !", Toast.LENGTH_SHORT).show();

                                                                 } catch (ParseException e) {
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         }
                                                     }, year, month, day);
                                                     dateDialog.show();

                                                 }
                                             }

                    );
                    mTo.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   final DatePickerDialog dateDialog = new DatePickerDialog(SalesHistoryScreen.this, new DatePickerDialog.OnDateSetListener() {

                                                       @Override
                                                       public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                                             int dayOfMonth) {

                                                           // TODO Auto-generated method stub
                                                           tmp=null;
                                                           tmp=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                                           if(tmp!=null) {
                                                               SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
                                                               Date d1 = null, d2 = null;
                                                               try {
                                                                   d1 = dateFormat.parse(mFrom.getText().toString());
                                                                   d2 = dateFormat.parse(tmp);
                                                                   if (d2.after(d1))
                                                                       mTo.setText(tmp);
                                                                   else
                                                                       Toast
                                                                     .makeText(SalesHistoryScreen.this, "Date Set Error !", Toast.LENGTH_SHORT).show();

                                                               } catch (ParseException e) {
                                                                   e.printStackTrace();
                                                               }
                                                           }

                                                       }
                                                   }, year, month, day+1);
                                                   dateDialog.show();
                                               }
                                           }

                    );
                } else {
                    mFrom.setOnClickListener(null);
                    mTo.setOnClickListener(null);
                    if (mSearchOpened)
                        mSalesFiltered = performSearch(mSales, mSearchQuery);
                    else
                        mSalesFiltered = mSales;
                    adapter = new SalesHistoryAdapter(SalesHistoryScreen.this, mSalesFiltered);
                    SalesLv.setAdapter(adapter);
                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mSearchOpened)
                    mSalesFiltered=performSearch(mSales,mSearchQuery);
                else
                    mSalesFiltered=mSales;
                mSalesFiltered=SearchBetweenDate(mSalesFiltered,mFrom.getText().toString(),mTo.getText().toString());
                adapter= new SalesHistoryAdapter(SalesHistoryScreen.this,mSalesFiltered);
                SalesLv.setAdapter(adapter);
            }
        };
        mFrom.addTextChangedListener(watcher);
        mTo.addTextChangedListener(watcher);
        
        db=new RunDatabaseHelper(this);
        mSales=db.getAllSales();
        for(int i=0;i<mSales.size();i++){
            mSales.get(i).setClientName(db.getClient(mSales.get(i).getClient_id()).getName());
            mSales.get(i).setProductName(db.getProduct(mSales.get(i).getProduct_id()).getProduct_name());
        }

        if (savedState == null) {
            mSalesFiltered = mSales;
            mSearchOpened = false;
            mSearchQuery = "";
        } else {
            //adapter=(SalesHistoryAdapter)savedState.getParcelable(ADAPTER);
            mSearchOpened = savedState.getBoolean(SEARCH_OPENED);
            mSearchQuery = savedState.getString(SEARCH_QUERY);
            mSalesFiltered =performSearch(mSales, mSearchQuery);
            
        }
        mIconOpenSearch = getResources().getDrawable(R.drawable.ic_search_white_24dp);
        mIconCloseSearch = getResources().getDrawable(R.drawable.ic_close_white_24dp);

        adapter=new SalesHistoryAdapter(this,mSalesFiltered);
        SalesLv=(ListView)findViewById(R.id.SalesLv);
        SalesLv.setAdapter(adapter);
    }
    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        //out.putParcelable(ADAPTER, adapter);
        out.putBoolean(SEARCH_OPENED, mSearchOpened);
        out.putString(SEARCH_QUERY, mSearchQuery);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        mSearchAction = menu.findItem(R.id.action_search_history);
        if(mSearchOpened){
            openSearchBar(mSearchQuery);
        }
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search_history);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_search_history){
            if(mSearchOpened)
            closeSearchBar();
         else
                openSearchBar(mSearchQuery);


            return true;
        }
        else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private List<sale> SearchByName(List<sale> Sales, String name) {

        // First we split the query so that we're able
        // to search word by word (in lower case).
        // String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty list to fill with matches.
        List<sale> SalesFiltered = new ArrayList<sale>();

        // Go through initial releases and perform search.
        for (sale s: Sales) {

            // Content to search through (in lower case).
            String content = (
                    s.getClientName()
            ).toLowerCase();

            if (content.contains(name.toLowerCase()))
                SalesFiltered.add(s);

        }

        return SalesFiltered;
    }
    private List<sale> SearchInDate(List<sale> Sales, String date) {

        // First we split the query so that we're able
        // to search word by word (in lower case).
        // String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty list to fill with matches.
        List<sale> SalesFiltered = new ArrayList<sale>();

        // Go through initial releases and perform search.
        for (sale s: Sales) {

            // Content to search through (in lower case).
            String content = s.getDate();


            if(content.equals(date))
                SalesFiltered.add(s);

        }

        return SalesFiltered;
    }
    private List<sale> SearchBetweenDate(List<sale> Sales, String date1,String date2) {
        //Toast.makeText(SalesHistoryScreen.this,"date1 : "+date1,Toast.LENGTH_LONG).show();
        // First we split the query so that we're able
        // to search word by word (in lower case).
        // String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty list to fill with matches.
        List<sale> SalesFiltered = new ArrayList<sale>();
        Date d1=null,d2 = null,d3;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            d1 =dateFormat.parse(date1);
            d2=dateFormat.parse(date2);
            //Toast.makeText(SalesHistoryScreen.this,"d1 : "+d1,Toast.LENGTH_LONG).show();
            //Toast.makeText(SalesHistoryScreen.this,"d2 : "+d2,Toast.LENGTH_LONG).show();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Go through initial releases and perform search.
        for (sale s: Sales) {

            // Content to search through (in lower case).
            String content = s.getDate();
            try {

                d3=dateFormat.parse(content);
                //Toast.makeText(SalesHistoryScreen.this,"d3 : "+d3,Toast.LENGTH_LONG).show();
                if(d1.before(d3)&&d2.after(d3))
                    SalesFiltered.add(s);
            }
            catch (Exception e){
                e.printStackTrace();
            }




        }

        return SalesFiltered;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        SalesLv.setAdapter(null);

    }
        private void openSearchBar(String queryText) {

            // Set custom view on action bar.
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar);

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
                    mSalesFiltered = performSearch(mSales, mSearchQuery);
                    if(DateFilter.isChecked())
                        mSalesFiltered=SearchBetweenDate(mSalesFiltered,mFrom.getText().toString(),mTo.getText().toString());
                    adapter = new SalesHistoryAdapter(SalesHistoryScreen.this, mSalesFiltered);
                    SalesLv.setAdapter(adapter);
                }
            });
            mSearchEt.setText(queryText);
            //mSearchEt.requestFocus();

            // Change search icon accordingly.
            mSearchAction.setIcon(mIconCloseSearch);
            mSearchOpened = true;

        }
        private void closeSearchBar() {

            // Remove custom view.
            actionBar.setDisplayShowCustomEnabled(false);

            // Change search icon accordingly.
            if(DateFilter.isChecked())
            mSalesFiltered=SearchBetweenDate(mSales,mFrom.getText().toString(),mTo.getText().toString());
            else
            mSalesFiltered=mSales;
            adapter = new SalesHistoryAdapter(SalesHistoryScreen.this, mSalesFiltered);
            SalesLv.setAdapter(adapter);
            mSearchAction.setIcon(mIconOpenSearch);
            mSearchOpened = false;
        }
        private List<sale> performSearch(List<sale> Sales, String query) {

            // First we split the query so that we're able
            // to search word by word (in lower case).
            String[] queryByWords = query.toLowerCase().split("\\s+");

            // Empty lists to fill with matches.
            List<sale> SalesFiltered = new ArrayList<sale>();

            // Go through initial releases and perform search.
            for (sale s : mSales) {

                // Content to search through (in lower case).
                String content = (
                        s.getClientName()
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
                        SalesFiltered.add(s);
                    }

                }

            }

            return SalesFiltered;
        }
}
