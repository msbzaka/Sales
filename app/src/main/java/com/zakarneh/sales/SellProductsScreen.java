package com.zakarneh.sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SellProductsScreen extends AppCompatActivity {
    private boolean isTaken=false,mEditProd=false;
    private ImageButton addProduct;
    private ImageView ProdImg;
    private String mCurrentPhotoPath;
    private product mCurrentProduct;
    private RunDatabaseHelper db;
    private Button ProdNext,ProdBack;
    private android.support.v7.app.ActionBar actionBar;
    private Drawable mIconCloseSearch, mIconOpenSearch;
    private boolean mSearchOpened;
    private MenuItem mSearchAction,mAddAction;
    private EditText mSearchEt;
    private String mSearchQuery;
    private List<product> mProds;
    private List<product> mProdsFiltered;
    private static String Prods="Initial Prods",Prods_FILTERED="Filtered Prods"
            ,SEARCH_OPENED="Search Opened",SEARCH_QUERY="Search Query";
    private File storageDir;
    private SellProductAdapter adapter;
    private int mCurrentProductPosition;
    private ListView ProductsLv;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private int ClientID;
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.products_screen);

        Intent i = getIntent();
        ClientID=i.getIntExtra("ClientID", 0);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        db=new RunDatabaseHelper(this);


        if (savedState == null) {
            mProds = db.getAllProducts();
            mProdsFiltered = mProds;
            mSearchOpened = false;
            mSearchQuery = "";
        } else {
            /*mProds = savedState.getParcelableArrayList(CLIENTS);
            mProdsFiltered = savedState
                    .getParcelableArrayList(CLIENTS_FILTERED);
            mSearchOpened = savedState.getBoolean(SEARCH_OPENED);
            mSearchQuery = savedState.getString(SEARCH_QUERY);*/
        }

        ProductsLv=(ListView)findViewById(R.id.ProductsLv);
        adapter=new SellProductAdapter(this,mProds);
        //ProductsLv.setItemsCanFocus(true);
        ProductsLv.setAdapter(adapter);
        ProductsLv.setItemsCanFocus(true);
        /*ProductsLv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 0, 0, "Edit");
                menu.add(Menu.NONE, 1, 1, "Delete");
            }
        });
        ProductsLv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                Log.v("Item",position+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        mSearchOpened = false;

        mIconOpenSearch = getResources()
                .getDrawable(R.drawable.ic_search_white_24dp);
        mIconCloseSearch = getResources()
                .getDrawable(R.drawable.ic_close_white_24dp);
        registerForContextMenu(ProductsLv);
       /* ProdBack=(Button)findViewById(R.id.ProdBack);
        ProdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nImg++;
                if (nImg == mProds.size())
                    nImg = 0;
                Bitmap m = BitmapFactory.decodeFile(storageDir + mProds.get(nImg).getPhoto());
                ProdImg.setImageBitmap(m);

            }
        });


        ProdNext=(Button)findViewById(R.id.ProdNext);
        ProdNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nImg--;
                if(nImg<0)
                    nImg=mProds.size()-1;
                Bitmap m =BitmapFactory.decodeFile(storageDir+mProds.get(nImg).getPhoto());
                ProdImg.setImageBitmap(m);

            }
        });*/

        db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.sell_prod_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search_prod);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_submit){
            submit();
            return true;
        }
        else if(id==R.id.action_search_prod){
            if (mSearchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
            return true;
        }
        else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit(){
        for(int i=0;i<mProds.size();i++){

            View v=adapter.getView(i, null, ProductsLv);
            Toast.makeText(this,((TextView)v.findViewById(R.id.SellProdNameView)).getText()+"",Toast.LENGTH_LONG).show();
            boolean checked=((CheckBox)v.findViewById(R.id.CheckProd)).isChecked();
            Toast.makeText(this,checked+"",Toast.LENGTH_LONG).show();

            if(checked){
                int quantity=Integer.parseInt(((EditText) v.findViewById(R.id.QuantityText)).getText().toString());
                double price=adapter.getItem(i).getPrice();
                int prod_id=adapter.getItem(i).getProduct_id();
                Calendar c = Calendar.getInstance();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                sale s=new sale(ClientID,prod_id,quantity,price,timeStamp);
                db.addsale(s);
            }
        }
    }
    private void delete(final product p, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(SellProductsScreen.this);
        builder.setTitle("Are you sure?");

// Set up the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteProduct(p);
                mProds=db.getAllProducts();
                mProdsFiltered.remove(position);
                adapter = new SellProductAdapter(SellProductsScreen.this,mProdsFiltered);
                ProductsLv.setAdapter(adapter);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_TAKE_PHOTO){

            mCurrentProduct.setPhoto(mCurrentPhotoPath);
            if(mEditProd){
                db.updateProduct(mCurrentProduct);
                mProds.set(mCurrentProductPosition, mCurrentProduct);
                mEditProd=false;
            }
            else{
                db.addProduct(mCurrentProduct);
                mProds.add(mCurrentProduct);
            }
            adapter = new SellProductAdapter(SellProductsScreen.this,mProdsFiltered);
            ProductsLv.setAdapter(adapter);

            File file = new File(getExternalCacheDir()+File.separator + mCurrentPhotoPath);
            Bitmap bmp = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 1000);
            File fileBmp=new File(storageDir+"/"+mCurrentPhotoPath);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fileBmp);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
    private void openSearchBar(String queryText) {

        // Set custom view on action bar.
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.prods_action_bar);

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
                mProdsFiltered = performSearch(mProds, mSearchQuery);
                adapter = new SellProductAdapter(SellProductsScreen.this, mProdsFiltered);
                ProductsLv.setAdapter(adapter);
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
        adapter = new SellProductAdapter(SellProductsScreen.this, mProds);
        ProductsLv.setAdapter(adapter);
        mSearchAction.setIcon(mIconOpenSearch);
        mSearchOpened = false;
    }
    private List<product> performSearch(List<product> Clients, String query) {

        // First we split the query so that we're able
        // to search word by word (in lower case).
        String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty lists to fill with matches.
        List<product> ClientsFiltered = new ArrayList<product>();

        // Go through initial releases and perform search.
        for (product p : mProds) {

            // Content to search through (in lower case).
            String content = (
                    p.toString()
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
                    ClientsFiltered.add(p);
                }

            }

        }

        return ClientsFiltered;
    }

}
