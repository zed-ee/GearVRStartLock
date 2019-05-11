// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codelabs.cosu;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private Button takePicButton;
    private ImageView imageView;
    private String mCurrentPhotoPath;
    private int permissionCheck;
    public DevicePolicyManager mDevicePolicyManager;

    public static final String EXTRA_FILEPATH =
            "com.google.codelabs.cosu.EXTRA_FILEPATH";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final String FILE_TAG = "File Creation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);

        // Setup button which calls intent to camera app to take a picture
        takePicButton = (Button) findViewById(R.id.pic_button);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("video/mp4");
                try {
                    startActivityForResult(fileintent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(
                            getApplicationContext(),R.string.no_camera_apps,Toast.LENGTH_SHORT)
                            .show();

                }


            }
        });

        imageView = (ImageView) findViewById(R.id.main_imageView);

        // Check to see if permission to access external storage is granted,
        // and request if not
        permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //setImageToView();
            Uri uri = data.getData();
            String documentId = DocumentsContract.getDocumentId(uri);
            String idArr[] = documentId.split(":");
            if(idArr.length == 2) {
                String docType = idArr[0];
                String realDocId = idArr[1];

                mCurrentPhotoPath = getApplicationContext().getExternalFilesDir(
                        realDocId).getAbsolutePath();

                File path = Environment.getExternalStoragePublicDirectory(realDocId);
                mCurrentPhotoPath = path.getAbsolutePath();

                Toast.makeText(
                        getApplicationContext(), mCurrentPhotoPath, Toast.LENGTH_SHORT)
                        .show();

                if (mDevicePolicyManager.isLockTaskPermitted(
                        getApplicationContext().getPackageName())) {
                    Intent lockIntent = new Intent(getApplicationContext(),
                            LockedActivity.class);
                    lockIntent.putExtra(EXTRA_FILEPATH, mCurrentPhotoPath);
                    startActivity(lockIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.not_lock_whitelisted, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                // If request is cancelled, results array is empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck=grantResults[0];
                } else {
                    takePicButton.setEnabled(false);
                }
                return;
            }
        }
    }


}
