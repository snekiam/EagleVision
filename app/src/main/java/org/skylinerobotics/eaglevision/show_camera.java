package org.skylinerobotics.eaglevision;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

//opencv imports
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class show_camera extends AppCompatActivity implements CvCameraViewListener2 {

    //used for logging success or failure
    private static final String TAG = "OCVSample::Activity";

    //loads camera view of OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    //used in camera selection from menu (front or rear)
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;

    private View mContentView;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV successfully loaded");
                    mOpenCvCameraView.enableView();
                }break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    public show_camera(){
        Log.i(TAG,"Instantiated new "+ this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"OnCreate called");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_show_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.activity_show_camera_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        mContentView = findViewById(R.id.activity_show_camera_java_surface_view);
        hide();

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }

    }
    private void hide() {
        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, 300);
    }
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"internal OpenCV library not found.  Using OpenCV Manager");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,mLoaderCallback);
        }else{
            Log.d(TAG,"OpenCV library found inside package.  Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }
    public void onCameraViewStarted(int width, int height){
        mRgba = new Mat(height,width,CvType.CV_8UC4);
        mRgbaF = new Mat(height,width,CvType.CV_8UC4);
        mRgbaT = new Mat(width,height,CvType.CV_8UC4);
    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CvCameraViewFrame inputFrame){
        //TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        //rotate 90 degrees
//        Core.transpose(mRgba,mRgbaT);
//        Imgproc.resize(mRgbaT,mRgbaF,mRgbaF.size(),0,0,0);
//        Core.flip(mRgbaF,mRgba,1);

        return mRgba;
    }

}
