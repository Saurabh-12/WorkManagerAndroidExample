package com.sks.scope.storage.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 10101;
    public static final int SELECT_PHOTO = 20202;
    private ImageView imageview;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview = (ImageView) findViewById(R.id.imageview);

    }

    public void takePhoto(View view) {
        take_photo();
    }

    public void selectPhoto(View view) {
        select_photo();
    }


    public void take_photo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        String status= Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)) {
            File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.sks.scope.storage.example.fileProvider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
            //Taking image via camera Intent
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //      intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }else
        {

            Toast.makeText(MainActivity.this, "No memory card",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Get pictures from album
     * */
    public void select_photo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            openAlbum();
        }
    }
    /**
     * How to open an album
     * */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileOutputStream fout = null;
        switch (requestCode) {
            case TAKE_PHOTO :
                if (resultCode == RESULT_OK) {

                    String name= "hello.jpg";
                    Bundle bundle = data.getExtras();
                    //Get the data returned by the camera and convert it to picture format
                    Bitmap bitmap = (Bitmap)bundle.get("data");
                        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                      // File file = new File("/storage/emulated/0/Android/data/com.example.writepicture/files/Pictures/");
                       // File file = new File("/sdcard/sks/");
                    //File file = new File(Environment.getExternalStorageDirectory(), "saurabh");
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    String filename=file.getPath()+"/"+name;
                    try {
                        fout = new FileOutputStream(filename);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally{
                        try {
                            fout.flush();
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    imageview.setImageBitmap(bitmap);
                }
                break;
            case SELECT_PHOTO :
                if (resultCode == RESULT_OK) {
                    //Determine the phone system version number
                    if (Build.VERSION.SDK_INT > 19) {
                        //4.4 And above systems use this method to process pictures
                        handleImgeOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     *4.4 The following system processes the picture
     * */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    /**
     * 4.4 And above systems for processing pictures
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImgeOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //If it is a document type uri, it is processed through the document id
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Parse out the id in numeric format
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //If it is a content type uri, it is processed in the normal way
                imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //If it is a file type uri, you can directly get the picture path
                imagePath = uri.getPath();
            }
            //Show pictures based on picture path
            //    imagePath = "/sdcard/writepicture/hello.jpg";
            displayImage(imagePath);
        }
    }
    /**
     * Method for displaying pictures according to picture path
     * */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageview.setImageBitmap(bitmap);
        }else {
            Toast.makeText(MainActivity.this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Get real picture path by uri and selection
     * */
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }else {
                    Toast.makeText(MainActivity.this,"failed to get image",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}