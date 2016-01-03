package com.zakarneh.sales;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class SalesHistoryScreen extends AppCompatActivity {
    private ListView SalesLv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_history_screen);
        SalesLv=(ListView)findViewById(R.id.SalesLv);
    }
}
