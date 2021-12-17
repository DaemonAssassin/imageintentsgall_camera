package com.gmail.mateendev3.setimagegcp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView ivProfileImage;
    Button btnSetImage;
    Bitmap mBitmapImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning appropriate view to members
        ivProfileImage = findViewById(R.id.iv_profile_image);
        btnSetImage = findViewById(R.id.btn_set_image);



        //creating cameraLauncher
        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getData() != null) {
                            mBitmapImage = (Bitmap) result.getData().getExtras().get("data");
                            ivProfileImage.setImageBitmap(mBitmapImage);
                        } else {
                            Toast.makeText(MainActivity.this, "No image captured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //creating galleryLauncher
        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            ivProfileImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //creating permission launcher
        ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result)
                            cameraLauncher.launch(takePicture());
                        else
                        Toast.makeText(MainActivity.this, "Not a permission to camera", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //applying click listener  to button
        btnSetImage.setOnClickListener(v -> {

                final CharSequence[] items = {"Capture Photo", "Select Photo"};
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Image or Capture")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (items[which].equals("Capture Photo")) {
                                   if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                       cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                                   } else {
                                       cameraLauncher.launch(takePicture());
                                       dialog.dismiss();
                                   }
                                } else if (items[which].equals("Select Photo")) {
                                    galleryLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                                    dialog.dismiss();
                                }
                            }
                        })
                        .create();
                dialog.show();
        });
    }

    private Intent takePicture() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }
}