package com.zakarneh.sales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductsScreen extends AppCompatActivity {
    private String Add_Product_Name="",Add_Product_File="";
    private ImageButton addProduct;
    private ImageView ProdImg;
    private String mCurrentPhotoPath;
    private RunDatabaseHelper db;
    private CustomAdapter adapter;
    private Button ProdNext,ProdBack;
    List<product> Prods=null;
    private File storageDir;
    int nImg=0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_screen);
        addProduct=(ImageButton)findViewById(R.id.addProduct);
        ProdImg=(ImageView)findViewById(R.id.ProdImg);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        db=new RunDatabaseHelper(this);
        Prods=db.getAllProducts();

        Bitmap m =BitmapFactory.decodeFile(storageDir+"/"+Prods.get(1).getPhoto());
        ProdImg.setImageBitmap(m);
        Toast.makeText(this,storageDir+"/"+Prods.get(1).getPhoto(),Toast.LENGTH_LONG).show();
       /* ProdBack=(Button)findViewById(R.id.ProdBack);
        ProdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nImg++;
                if (nImg == Prods.size())
                    nImg = 0;
                Bitmap m = BitmapFactory.decodeFile(storageDir + Prods.get(nImg).getPhoto());
                ProdImg.setImageBitmap(m);

            }
        });


        ProdNext=(Button)findViewById(R.id.ProdNext);
        ProdNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nImg--;
                if(nImg<0)
                    nImg=Prods.size()-1;
                Bitmap m =BitmapFactory.decodeFile(storageDir+Prods.get(nImg).getPhoto());
                ProdImg.setImageBitmap(m);

            }
        });*/


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsScreen.this);
                builder.setTitle("Add Product");
// Set up the input
                final EditText input = new EditText(ProductsScreen.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TakePictureIntent();
                        Toast.makeText(ProductsScreen.this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
                        product p = new product("a", mCurrentPhotoPath, 55, 1);
                        db.addProduct(p);
                        Prods.add(p);

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
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsScreen.this);
        builder.setTitle("Add Product");
        builder.setView(ProductsScreen.this.getLayoutInflater().inflate(R.layout.add_product,null));
// Set up the input
        final EditText input = (EditText)findViewById(R.id.TextProd);
        final TextView text =(TextView)findViewById(R.id.FileProd);
        Button btn=(Button)findViewById(R.id.BtnProd);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

// Set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Add_Product_Name = input.getText().toString();
                Add_Product_File = text.getText().toString();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

}
