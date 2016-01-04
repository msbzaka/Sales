package com.zakarneh.sales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductsScreen extends AppCompatActivity {
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
    ProductAdapter adapter;
    int nImg=0;
    private ListView ProductsLv;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.products_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

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
        adapter=new ProductAdapter(this,mProds);
        ProductsLv.setAdapter(adapter);

        mSearchOpened = false;

        mIconOpenSearch = getResources()
                .getDrawable(R.drawable.ic_search_white_24dp);
        mIconCloseSearch = getResources()
                .getDrawable(R.drawable.ic_close_white_24dp);
        
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
        inflater.inflate(R.menu.products_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search_prod);
        mAddAction=menu.findItem(R.id.action_add_prod);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_add_prod){
            add();
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
    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        //Save Photo Path
        mCurrentPhotoPath = image.getAbsolutePath().substring(image.getAbsolutePath().lastIndexOf("/")+1);
        //Toast.makeText(this,mCurrentPhotoPath,Toast.LENGTH_LONG).show();
        return image;
    }

    private void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsScreen.this);
        builder.setTitle("Add Product");
        LinearLayout layout = new LinearLayout(ProductsScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(ProductsScreen.this);
        name.setHint("Product Name");
        layout.addView(name);

        final EditText price = new EditText(ProductsScreen.this);
        price.setInputType(InputType.TYPE_CLASS_NUMBER);
        price.setHint("Price");
        layout.addView(price);

        final EditText no = new EditText(ProductsScreen.this);
        no.setHint("Quantity");
        no.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(no);

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                product p=new product(name.getText().toString(),""
                        ,Double.parseDouble(price.getText().toString()),Integer.parseInt(no.getText().toString()));
                db.addProduct(p);
                adapter.add(p);
                adapter.notifyDataSetChanged();
                mCurrentProduct=p;
                TakePictureIntent();

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();    }
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode==REQUEST_TAKE_PHOTO){
             Bitmap photo = BitmapFactory.decodeFile(storageDir+"/"+mCurrentPhotoPath,null);
             photo = Bitmap.createScaledBitmap(photo, 100, 100, false);
             ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

             File f = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                     + File.separator + mCurrentPhotoPath);
             try {
                 f.createNewFile();
                 FileOutputStream fo = new FileOutputStream(f);
                 fo.write(bytes.toByteArray());
                 fo.close();
                 mCurrentProduct.setPhoto(mCurrentPhotoPath);
                 adapter.notifyDataSetChanged();
                 Toast.makeText(ProductsScreen.this,"Product added successfully!",Toast.LENGTH_LONG).show();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             finally {

             }

         }
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
                adapter = new ProductAdapter(ProductsScreen.this, mProdsFiltered);
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
        mSearchAction.setIcon(mIconOpenSearch);
        mSearchOpened = false;
    }
    private List<product> performSearch(List<product> Clients, String query) {

        // First we split the query so that we're able
        // to search word by word (in lower case).
        String[] queryByWords = query.toLowerCase().split("\\s+");

        // Empty list to fill with matches.
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
