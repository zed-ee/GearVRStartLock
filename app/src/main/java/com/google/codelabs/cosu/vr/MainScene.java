package com.google.codelabs.cosu.vr;

import android.graphics.Color;


import com.google.codelabs.cosu.MainActivity;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;



public class MainScene extends GVRMain {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected boolean serviceNotRunning = true;

    private GVRContext mGVRContext = null;
    private GVRActivity mActivity;

    private VideoScene mVideoScene = null;
    GVRScene mMainScene = null;



    private String welcomeText ="Tere tulemast Tartu 1913 virtuaal reaalsusesse.\n\n Elamuse saamiseks liigu kaardil märgitud asukohtadesse";
    private boolean initialized = false;

    private GVRPicker mPicker = null;
    private boolean restart = false;
    private float deviceRotation;
    private String mCurrentPhotoPath;
    public MainScene(GVRActivity mainActivity, String photoPath) {
        mActivity = mainActivity;
        mCurrentPhotoPath = photoPath;
    }

    /** Called when the activity is first created. */
    @Override
    public void onInit(GVRContext gvrContext) {
        mGVRContext = gvrContext;
        mMainScene = mGVRContext.getMainScene();
        GVRCameraRig cameraRig = mMainScene.getMainCameraRig();
        cameraRig.getLeftCamera().setBackgroundColor(Color.DKGRAY);
        cameraRig.getRightCamera().setBackgroundColor(Color.DKGRAY);
        mVideoScene = new VideoScene(mGVRContext, mActivity);
        mGVRContext.setMainScene(mMainScene);
        mMainScene.addSceneObject(mVideoScene);
        mVideoScene.init(mGVRContext, mCurrentPhotoPath);

    }



}
