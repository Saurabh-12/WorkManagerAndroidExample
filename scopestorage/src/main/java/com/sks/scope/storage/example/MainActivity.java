package com.sks.scope.storage.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            //Taking image via camera Intent
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileOutputStream fout = null;
        switch (requestCode) {
            case TAKE_PHOTO :
                if (resultCode == RESULT_OK) {

                    String picName= "hello.jpg";
                    Bundle bundle = data.getExtras();
                    //Get the data returned by the camera and convert it to picture format
                    Bitmap bitmap = (Bitmap)bundle.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                            bitmap, picName, "Saurabh");
                    Log.d("Saurabh", "Path "+path);
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
        Bitmap bitmap = getBitmapImage(uri,null);
        displayImage(bitmap);
    }
    /**
     * 4.4 And above systems for processing pictures
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImgeOnKitKat(Intent data) {
        Bitmap bitmap = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //If it is a document type uri, it is processed through the document id
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Parse out the id in numeric format
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                bitmap = getBitmapImage(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                bitmap = getBitmapImage(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //If it is a content type uri, it is processed in the normal way
                bitmap = getBitmapImage(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //If it is a file type uri, you can directly get the picture path
                String imagePath = uri.getPath();
                Bitmap btmp = BitmapFactory.decodeFile(imagePath);
                imageview.setImageBitmap(btmp);
            }
            //Show pictures based on picture path
            //    imagePath = "/sdcard/writepicture/hello.jpg";
            displayImage(bitmap);
        }
    }
    /**
     * Method for displaying pictures according to picture path
     * */
    private void displayImage(Bitmap bitmap) {
        if (bitmap != null) {
            imageview.setImageBitmap(bitmap);
        }else {
            Toast.makeText(MainActivity.this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Get real picture path by uri and selection
     *
     * @return*/
    private Bitmap getBitmapImage(Uri uri, String selection) {
        InputStream inputStream = null;
        Bitmap image = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, selection, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath =  cursor.getString(column_index);
            image = BitmapFactory.decodeFile(imagePath);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return image;
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