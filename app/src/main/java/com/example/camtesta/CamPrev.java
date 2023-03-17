package com.example.camtesta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.impl.Camera2CameraControl;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraX;
import androidx.camera.core.CaptureConfig;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.OnFocusListener;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CamPrev extends AppCompatActivity {

    TextureView view_finder;
    ImageButton img_cap;
    ImageButton backbtn;
    ImageButton button;
    TextView textView;
    ProgressDialog progress;
    CameraControl cameraControl;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_prev);

        progress = new ProgressDialog(CamPrev.this);
        view_finder = findViewById(R.id.view_finder);
        img_cap = findViewById(R.id.img_cap);
        backbtn = findViewById(R.id.backbtn);
        button = findViewById(R.id.editbtnfor);
        textView = findViewById(R.id.mycountforcam);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        view_finder.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cameraControl = new CameraControl() {
                    @Override
                    public void setCropRegion(Rect crop) {

                    }

                    @Override
                    public void focus(Rect focus, Rect metering, @Nullable OnFocusListener listener, @Nullable Handler handler) {
                        
                    }

                    @Override
                    public void focus(Rect focus, Rect metering) {

                    }

                    @Override
                    public FlashMode getFlashMode() {
                        return null;
                    }

                    @Override
                    public void setFlashMode(FlashMode flashMode) {

                    }

                    @Override
                    public void enableTorch(boolean torch) {

                    }

                    @Override
                    public boolean isTorchOn() {
                        return false;
                    }

                    @Override
                    public boolean isFocusLocked() {
                        return false;
                    }

                    @Override
                    public void triggerAf() {

                    }

                    @Override
                    public void triggerAePrecapture() {

                    }

                    @Override
                    public void cancelAfAeTrigger(boolean cancelAfTrigger, boolean cancelAePrecaptureTrigger) {

                    }

                    @Override
                    public void submitCaptureRequests(List<CaptureConfig> captureConfigs) {

                    }
                };
                return false;
            }
        });

        startCamera();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.multipics.size()==0){
                    Toast.makeText(getApplicationContext(),"No Images Captured", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(CamPrev.this,MultiShowPhotoCam.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                }
            }
        });
        piccount();

        backbtn.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 15;
                layoutParams.height = height - 15;
                backbtn.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                backbtn.setLayoutParams(layoutParams);
            }
            return false;
        });;


    }

    void piccount(){
        String pic = "Pictures Captured : ";
        textView.setText(pic + String.valueOf(MainActivity.multipics.size()));
    }

    public void startCamera(){
        CameraX.unbindAll();

        Rational aspectRatio = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            aspectRatio = new Rational(3,4);
        }
        Size screen = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            screen = new Size(720,1280);
            //view_finder.getWidth(),view_finder.getHeight()
        }

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();

        Preview preview=  new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup)view_finder.getParent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    parent.removeView(view_finder);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    parent.addView(view_finder,0);
                }
                //Render Refreshed Camera Screen
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view_finder.setSurfaceTexture(output.getSurfaceTexture());
                }
                updateTransform();
            }
        });

        ImageCaptureConfig imageCaptureConfig = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY).setFlashMode(FlashMode.AUTO).setLensFacing(CameraX.LensFacing.BACK)
                    .setTargetResolution(screen).setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        }

        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
        imageCapture.setTargetRotation(Surface.ROTATION_0);
        imageCapture.setTargetAspectRatio(aspectRatio);

        img_cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                    file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + System.currentTimeMillis() + ".jpg");
                }

                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        File newfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + System.currentTimeMillis() + ".jpg");
                        Bitmap mybitmap=null;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 4;

                        final int REQUIRED_SIZE = 100;

                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE &&
                                options.outHeight / scale / 2 >= REQUIRED_SIZE) {
                            scale *= 2;
                        }

                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        options.inJustDecodeBounds = false;

                        Uri uri=null;
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        mybitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        mybitmap = Bitmap.createBitmap(mybitmap,0,0,mybitmap.getWidth(),mybitmap.getHeight(),matrix,false);
                        mybitmap = Bitmap.createScaledBitmap(mybitmap,640,960,false);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        mybitmap.compress(Bitmap.CompressFormat.JPEG, 50 , bos);
                        byte[] bitmapdata = bos.toByteArray();

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(newfile);
                            fos.write(bitmapdata);
                            uri = Uri.fromFile(newfile);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            mybitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                      // Uri uri = Uri.fromFile(file);

                        MainActivity.multipics.add(uri);
                        piccount();
                        process();

                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Failed to Capture the Image " + message;
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                        if(cause!=null){
                            cause.printStackTrace();
                        }

                    }
                });

            }
        });

        img_cap.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 15;
                layoutParams.height = height - 15;
                img_cap.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                img_cap.setLayoutParams(layoutParams);
            }
            return false;
        });;;

        //Bind to LifeCycle
        CameraX.bindToLifecycle((LifecycleOwner)this,preview,imageCapture);

    }

    public void updateTransform(){

        Matrix matrix = new Matrix();

        float width = view_finder.getMeasuredWidth();
        float height = view_finder.getMeasuredHeight();

        float x = width/2f;
        float y = height/2f;

        int rotateDeg = 90;
        int rotation = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            rotation = (int)view_finder.getRotation();
        }

        switch (rotation){
            case Surface
                    .ROTATION_0:
                rotateDeg=0;
                break;

            case Surface
                    .ROTATION_90:
                rotateDeg=90;
                break;
            case Surface
                    .ROTATION_180:
                rotateDeg=180;
                break;
            case Surface
                    .ROTATION_270:
                rotateDeg=270;
                break;
            default:
                return;
        }

        matrix.postRotate((float)rotateDeg,x,y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            view_finder.setTransform(matrix);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CamPrev.this);
        builder.setIcon(R.drawable.danger);
        builder.setMessage("All your progress will be deleted!");
        builder.setTitle("Leave");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            MainActivity.multipics.clear();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void process(){
        progress.setTitle("Adding Image");
        progress.setMessage("Please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }

        }.start();
    }


}