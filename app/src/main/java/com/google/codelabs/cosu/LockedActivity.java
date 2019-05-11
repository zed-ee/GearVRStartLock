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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.codelabs.cosu.vr.MainScene;

import org.gearvrf.GVRActivity;

public class LockedActivity extends GVRActivity {

    private String mCurrentPhotoPath;
    private DevicePolicyManager mDevicePolicyManager;
    private MainScene mMain = null;

    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String PHOTO_PATH = "Photo Path";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_locked);
        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        

        SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
        String savedPhotoPath = settings.getString(PHOTO_PATH, null);

        Intent intent = getIntent();

        String passedPhotoPath = intent.getStringExtra(
                MainActivity.EXTRA_FILEPATH);
        if (passedPhotoPath != null) {
            mCurrentPhotoPath = passedPhotoPath;
        } else {
            mCurrentPhotoPath = savedPhotoPath;
        }
        if (mCurrentPhotoPath != null) {
            mMain = new MainScene(this, mCurrentPhotoPath);
            setMain(mMain, "gvr.xml");


        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start lock task mode if its not already active
        if(mDevicePolicyManager.isLockTaskPermitted(this.getPackageName())){
            ActivityManager am = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            if (am.getLockTaskModeState() ==
                    ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        // Get editor object and make preference changes to save photo filepath
        SharedPreferences settings = getSharedPreferences(PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PHOTO_PATH, mCurrentPhotoPath);
        editor.commit();
    }
}
