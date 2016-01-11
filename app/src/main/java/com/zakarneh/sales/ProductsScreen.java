package com.zakarneh.sales;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.PerformanceTestCase;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class ProductsScreen extends AppCompatActivity {
    private boolean isTaken=false,mEditProd=false;
    private ImageButton addProduct;
    private ImageView ProdImg;
    private String mCurrentPhotoPath;
    private product mCurrentProduct;
    private RunDatabaseHelper db;
    private android.support.v7.app.ActionBar actionBar;
    private Drawable mIconCloseSearch, mIconOpenSearch;
    private boolean mSearchOpened;
    private MenuItem mSearchAction;
    private EditText mSearchEt;
    private String mSearchQuery;
    private List<product> mProds;
    private List<product> mProdsFiltered;
    private static String CURRENT_PROD="mCurrentProduct",CURRENT_PHOTO="mCurrentPhotoPath"
            ,IS_TAKEN="isTaken",EDIT_PROD="mEditProd",CURRENT_PROD_POS="mCurrentProductPosition";
    private File storageDir;
    ProductAdapter adapter;
    private int mCurrentProductPosition;
    private ListView ProductsLv;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static String ADAPTER="Adapter of ListView",SEARCH_OPENED="Search Opened",SEARCH_QUERY="Search Query";

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.products_screen);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20B020")));

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        db=new RunDatabaseHelper(this);
        mProds = db.getAllProducts();

        if (savedState == null) {
            mProdsFiltered = mProds;
            mSearchOpened = false;
            mSearchQuery = "";

        } else {
            //adapter=(ProductAdapter)savedState.getParcelable(ADAPTER);

            mSearchOpened = savedState.getBoolean(SEARCH_OPENED);
            mSearchQuery = savedState.getString(SEARCH_QUERY);
            mProdsFiltered =performSearch(mProds, mSearchQuery);
            mCurrentProduct=(product)savedState.getSerializable(CURRENT_PROD);
            mCurrentPhotoPath=savedState.getString(CURRENT_PHOTO);
            mCurrentProductPosition=savedState.getInt(CURRENT_PROD_POS);
            isTaken=savedState.getBoolean(IS_TAKEN);
            mEditProd=savedState.getBoolean(EDIT_PROD);

            /*mProds = savedState.getParcelableArrayList(CLIENTS);
            mProdsFiltered = savedState
                    .getParcelableArrayList(CLIENTS_FILTERED);
*/
        }

        adapter=new ProductAdapter(this,mProdsFiltered);
        ProductsLv=(ListView)findViewById(R.id.ProductsLv);
        ProductsLv.setAdapter(adapter);


        //mSearchOpened = false;

        mIconOpenSearch = getResources().getDrawable(R.drawable.ic_search_white_24dp);
        mIconCloseSearch = getResources().getDrawable(R.drawable.ic_close_white_24dp);
        registerForContextMenu(ProductsLv);


        db.close();
            }
    @Override
    public void onSaveInstanceState(Bundle out){
        super.onSaveInstanceState(out);
        out.putParcelable(ADAPTER, adapter);
        out.putBoolean(SEARCH_OPENED, mSearchOpened);
        out.putString(SEARCH_QUERY, mSearchQuery);
        out.putSerializable(CURRENT_PROD, mCurrentProduct);
        out.putString(CURRENT_PHOTO, mCurrentPhotoPath);
        out.putBoolean(IS_TAKEN, isTaken);
        out.putBoolean(EDIT_PROD,mEditProd);
        out.putInt(CURRENT_PROD_POS,mCurrentProductPosition);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.products_menu,menu);
        mSearchAction = menu.findItem(R.id.action_search_prod);
        if(mSearchOpened){
            openSearchBar(mSearchQuery);
        }
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search_prod);
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
                isTaken=true;
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //Toast.makeText(ProductsScreen.this, "PROOOOOOOOOOOOB", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName,".jpg",getExternalCacheDir());
        //Save Photo Path
        mCurrentPhotoPath = image.getAbsolutePath().substring(image.getAbsolutePath().lastIndexOf("/")+1);
        //Toast.makeText(this,storageDir+"/"+mCurrentPhotoPath,Toast.LENGTH_LONG).show();
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
               try {
                   product p = new product(name.getText().toString(), ""
                           , Double.parseDouble(price.getText().toString()), Integer.parseInt(no.getText().toString()));

                   mCurrentProduct = p;
                   TakePictureIntent();
               }
               catch (NumberFormatException e){
                   Toast.makeText(ProductsScreen.this,"Error Data!",Toast.LENGTH_SHORT).show();
                   Log.v("Products Screen",e.getMessage());
               }

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
    private void delete(final product p, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsScreen.this);
        builder.setTitle("Are you sure?");

// Set up the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteProduct(p);
                mProds=db.getAllProducts();
                mProdsFiltered.remove(position);
                adapter = new ProductAdapter(ProductsScreen.this,mProdsFiltered);
                ProductsLv.setAdapter(adapter);
                Toast.makeText(ProductsScreen.this, "The product is deleted successfully!", Toast.LENGTH_SHORT).show();
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
                 Toast.makeText(ProductsScreen.this, "The product is edited successfully!", Toast.LENGTH_SHORT).show();
             }
             else{
                 db.addProduct(mCurrentProduct);
                 mProds.add(mProds.size(),mCurrentProduct);
                 Toast.makeText(ProductsScreen.this, "The product is added successfully!", Toast.LENGTH_SHORT).show();
             }
             mProdsFiltered=performSearch(mProds,mSearchQuery);
             adapter = new ProductAdapter(ProductsScreen.this,mProdsFiltered);
             ProductsLv.setAdapter(adapter);

             File file = new File(getExternalCacheDir()+File.separator + mCurrentPhotoPath);
             Bitmap bmp = decodeSampledBitmapFromFile(file.getAbsolutePath(), 200, 200);
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
                mProdsFiltered = performSearch(mProds, mSearchQuery);
                adapter = new ProductAdapter(ProductsScreen.this, mProdsFiltered);
                ProductsLv.setAdapter(adapter);
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
        adapter = new ProductAdapter(ProductsScreen.this, mProds);
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, 0, 0, "Edit");
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id =item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        product p=adapter.getItem(info.position);
        if(id==1) {//DELETE
            delete(p,info.position);
            return true;
        }
        else if(id==0){//Edit
            mCurrentProductPosition=info.position;
            edit(p);
            return true;
        }
        return false;
    }
    private void edit(final product p){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsScreen.this);
        builder.setTitle("Edit Product");
        LinearLayout layout = new LinearLayout(ProductsScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout l1 = new LinearLayout(this);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        final EditText name = new EditText(ProductsScreen.this);
        name.setHint("New Product Name");
        final CheckBox nameC= new CheckBox(this);
        l1.addView(name);
        l1.addView(nameC);
        layout.addView(l1);

        LinearLayout l2 = new LinearLayout(ProductsScreen.this);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        final EditText price = new EditText(ProductsScreen.this);
        price.setInputType(InputType.TYPE_CLASS_NUMBER);
        price.setHint("New Price");
        final CheckBox priceC= new CheckBox(this);
        l2.addView(price);
        l2.addView(priceC);
        layout.addView(l2);

        LinearLayout l3 = new LinearLayout(ProductsScreen.this);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        final EditText no = new EditText(ProductsScreen.this);
        no.setHint("New Quantity");
        no.setInputType(InputType.TYPE_CLASS_NUMBER);
        final CheckBox quantityC= new CheckBox(this);
        l3.addView(no);
        l3.addView(quantityC);
        layout.addView(l3);

        final CheckBox photoC=new CheckBox(this);
        photoC.setText("Set New Photo");
        layout.addView(photoC);

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                product newP=p;
                if(nameC.isChecked())
                newP.setName(name.getText().toString());
                if(priceC.isChecked())
                newP.setPrice(Double.parseDouble(price.getText().toString()));
                if(quantityC.isChecked())
                newP.setAvailable(Integer.parseInt(no.getText().toString()));
                mCurrentProduct=newP;
                if(photoC.isChecked()) {
                    mEditProd = true;
                    TakePictureIntent();
                }
                if(!photoC.isChecked()) {
                    db.updateProduct(mCurrentProduct);
                    mProds.set(mCurrentProductPosition, mCurrentProduct);
                    adapter = new ProductAdapter(ProductsScreen.this,mProdsFiltered);
                    ProductsLv.setAdapter(adapter);
                    Toast.makeText(ProductsScreen.this, "The product edited successfully!", Toast.LENGTH_SHORT).show();
                }
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        ProductsLv.setAdapter(null);

    }

}
