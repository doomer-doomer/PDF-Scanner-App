package com.example.camtesta;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class MultiShowPhotoCam extends AppCompatActivity {

    RecyclerView image_recycler;
    private ImageAdapter imageAdapter;
    private ArrayList<Emages> emagesArrayList;
    private Emages emages;
    ImageButton addbtn;
    ImageButton editbtn;
    ImageButton save;
    ImageButton delete;
    ImageButton backbtn;
    int count=0;
    int countfor=1;
    Bitmap bitmaps;
    String number=null;
    int val=0;
    int del=1;
    String path = "";
    String pages = "Total Pages : ";
    public Bitmap mybitmap;
    TextView countview;
    ProgressDialog progress;


    @SuppressLint({"MissingInflatedId", "WrongThread", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_show_photo_cam);

        progress = new ProgressDialog(MultiShowPhotoCam.this);
        progress.setTitle("Processing"); // setting title
        progress.setMessage("Please Wait ..."); // creating message
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // style of indicator
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

        backbtn = findViewById(R.id.backbtnmulticam);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiShowPhotoCam.this);
                builder.setIcon(R.drawable.danger);
                builder.setMessage("All your progress will be deleted!");
                builder.setTitle("Leave");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    emagesArrayList.clear();
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
        });

        countview = findViewById(R.id.countview2);
        int totalpages = MainActivity.multipics.size();
        countview.setText(pages + String.valueOf(totalpages));


        editbtn = findViewById(R.id.editbtnmulticam);
        editbtn.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 5;
                layoutParams.height = height - 5;
                editbtn.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                editbtn.setLayoutParams(layoutParams);
            }
            return false;
        });
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog2 = new Dialog(MultiShowPhotoCam.this);
                dialog2.setContentView(R.layout.pagecollector);
                Window window = dialog2.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Button dialogButton = (Button) dialog2.findViewById(R.id.btn_okay_edit_2);
                Button dialogButton2 = (Button) dialog2.findViewById(R.id.btn_cancel_edit_2);
                EditText text = dialog2.findViewById(R.id.txt_input_edit_3);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        if(!text.getText().toString().equals("") && text.getText().toString().length() > 0){
                            val = Integer.parseInt(String.valueOf(text.getText()));
                            if(val > MainActivity.multipics.size() || val==0){
                                Toast.makeText(getApplicationContext(),"Exceeds Page limit!", Toast.LENGTH_SHORT).show();
                            } else {
                                String col = "#CBCBCB";
                                String col2 = "#737373";
                                String col3 = "#FFFFFF";
                                String dest = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                                UCrop.Options options = new UCrop.Options();
                                options.setToolbarTitle("Edit your picture");
                                options.setCropGridCornerColor(Color.parseColor(col));
                                options.setCropGridColor(Color.parseColor(col));
                                options.setCropFrameColor(Color.parseColor(col2));
                                options.setRootViewBackgroundColor(Color.parseColor(col3));
                                options.setDimmedLayerColor(Color.parseColor(col));
                                options.setLogoColor(Color.parseColor(col2));
                                options.setStatusBarColor(Color.parseColor(col));
                                options.setToolbarWidgetColor(Color.parseColor(col));
                                options.setToolbarColor(Color.parseColor(col2));

                                UCrop.of(MainActivity.multipics.get(val - 1), Uri.fromFile(new File(getCacheDir(), dest)))
                                        .withOptions(options)
                                        .withAspectRatio(4, 3)
                                        .useSourceImageAspectRatio()
                                        .withMaxResultSize(2000, 2000)
                                        .start(MultiShowPhotoCam.this);
                                dialog2.dismiss();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Please select a Page",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationz;
                dialog2.getWindow().setGravity(Gravity.BOTTOM);


            }
        });

        delete = findViewById(R.id.deleteforcamz);
        delete.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 5;
                layoutParams.height = height - 5;
                delete.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                delete.setLayoutParams(layoutParams);
            }
            return false;
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog3 = new Dialog(MultiShowPhotoCam.this);
                dialog3.setContentView(R.layout.pagedeletor);
                Window window = dialog3.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Button dialogButton = (Button) dialog3.findViewById(R.id.btn_okay_edit_4);
                Button dialogButton2 = (Button) dialog3.findViewById(R.id.btn_cancel_edit_4);
                EditText text = dialog3.findViewById(R.id.txt_input_edit_4);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!text.getText().toString().equals("") && text.getText().toString().length() > 0){
                            del = Integer.parseInt(String.valueOf(text.getText()));
                            if(del > MainActivity.multipics.size() || del==0){
                                Toast.makeText(getApplicationContext(),"Exceeds Page Limit!", Toast.LENGTH_SHORT).show();
                            }else {
                                emagesArrayList.remove(del-1);
                                MainActivity.multipics.remove(del-1);
                                int totalpages = MainActivity.multipics.size();
                                countview.setText(pages + String.valueOf(totalpages));
                                imageAdapter.notifyDataSetChanged();
                                dialog3.dismiss();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Please select a Page",Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });
                dialog3.show();
                dialog3.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationz;
                dialog3.getWindow().setGravity(Gravity.BOTTOM);
            }
        });



        save = findViewById(R.id.savemulticam);
        save.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 5;
                layoutParams.height = height - 5;
                save.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                save.setLayoutParams(layoutParams);
            }
            return false;
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MultiShowPhotoCam.this);
                dialog.setContentView(R.layout.customdialog);
                Window window = dialog.getWindow();
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_okay);
                Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_cancel);
                EditText text = dialog.findViewById(R.id.txt_input);
                DateTimeFormatter myFormatObj = null;
                String txt = null;
                LocalDateTime myDateObj = null;
                String newfil = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    myDateObj = LocalDateTime.now();
                    myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
                    String formattedDate = myDateObj.format(myFormatObj);
                    txt = "Doc " + formattedDate;
                }else{
                    Date date = new Date();
                    txt = "Doc_" + date;
                    txt = txt.replace(" GMT+05:30 ", " ");
                }
                text.setHint(txt);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String samefileename = null;
                        String filename = text.getText().toString();
                        //saveTempBitmap(bmp,filename);
                        if(filename.equals("")){
                            DateTimeFormatter myFormatObj = null;
                            LocalDateTime myDateObj = null;
                            String newfil = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                myDateObj = LocalDateTime.now();
                                myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
                                String formattedDate = myDateObj.format(myFormatObj);
                                filename = "Doc " + formattedDate;
                            }else{
                                Date date = new Date();
                                filename = "Doc_" + date;
                                filename = filename.replace(" GMT+05:30 ", " ");
                            }

                            try {
                                if(isExternalStorageWritable()==true){
                                    createPdf(filename,MainActivity.multipics);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getApplicationContext(),"No Pages!",Toast.LENGTH_SHORT).show();
                        }else{
                            if(count==0){
                                try {
                                    if(isExternalStorageWritable()==true){
                                        createPdf(filename,MainActivity.multipics);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                try {
                                    createPdf(filename,MainActivity.multipics);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        //Toast.makeText(getApplicationContext(),filename + " is saved successfully!",Toast.LENGTH_SHORT).show();
                    }
                });
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationz;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


            }
        });
        addbtn = findViewById(R.id.multiaddbtncam);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultiShowPhotoCam.this,CamPrev.class);
                startActivity(intent);
            }
        });

        addbtn.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 5;
                layoutParams.height = height - 5;
                addbtn.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                addbtn.setLayoutParams(layoutParams);
            }
            return false;
        });

        image_recycler = findViewById(R.id.multipicrecycle);
        image_recycler.setLayoutManager(new LinearLayoutManager(MultiShowPhotoCam.this, LinearLayoutManager.HORIZONTAL,false));
        emagesArrayList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this,emagesArrayList);
        image_recycler.setAdapter(imageAdapter);
        for(int i=0;i<MainActivity.multipics.size();i++){
            createListData(MainActivity.multipics.get(i));
            int tpages = MainActivity.multipics.size();
            countview.setText(pages + String.valueOf(tpages));
        }

    }

    private void createListData(Uri file) {
        Emages emages = new Emages(file);
        emagesArrayList.add(emages);

        imageAdapter.notifyDataSetChanged();
    }


    private void createPdf(String filename, ArrayList<Uri> uri) throws IOException {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;
        PdfDocument document = new PdfDocument();
        String image_path2;
        for(int k=0;k<MainActivity.multipics.size();k++) {
            //bitmaps = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri[k]);
            ContentResolver contentResolver = getContentResolver();
            try {
                if(Build.VERSION.SDK_INT < 28) {
                    bitmaps = MediaStore.Images.Media.getBitmap(contentResolver, uri.get(k));
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri.get(k));
                    bitmaps = ImageDecoder.decodeBitmap(source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmaps.getWidth(), bitmaps.getHeight(), k+1).create();

            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);

            bitmaps = Bitmap.createScaledBitmap(bitmaps, bitmaps.getWidth(), bitmaps.getHeight(), true);
            bitmaps = bitmaps.copy(Bitmap.Config.ARGB_8888, true);


            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmaps, 0, 0, null);
            document.finishPage(page);
        }

        File folder = getExternalFilesDir("MyPDFScanner");
        File[] list = folder.listFiles();
        //Toast.makeText(getApplicationContext(),count_img + " pdf are present!",Toast.LENGTH_SHORT).show();
        File filePath = new File(folder, filename + ".pdf");

        if (filePath.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MultiShowPhotoCam.this);
            builder.setMessage(filename + " has been overwritten!");
            builder.setTitle("Alert !");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
                filePath.delete();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        try {
            document.writeTo(new FileOutputStream(filePath));
            //Toast.makeText(this, filename + " has been saved Successful!", Toast.LENGTH_LONG).show();
            MainActivity.multipics.clear();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        progress.setTitle("Saving " + filename); // setting title
        progress.setMessage("Please Wait ..."); // creating message
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // style of indicator
        progress.setIndeterminate(true);
        progress.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MultiShowPhotoCam.this, MainActivity.class);
                intent.putExtra("FILENAME", filename);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                progress.dismiss();
            }

        }.start();
        document.close();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiShowPhotoCam.this);
        builder.setIcon(R.drawable.danger);
        builder.setMessage("All your progress will be deleted!");
        builder.setTitle("Leave");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            emagesArrayList.clear();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            MainActivity.multipics.remove(val-1);
            emagesArrayList.remove(val-1);
            MainActivity.multipics.add(val-1, resultUri);
            Emages emages = new Emages(resultUri);
            emagesArrayList.add(val-1,emages);
            imageAdapter.notifyDataSetChanged();
            progressbar("Updating Image");



        }
    }

    public void progressbar(String msg){
        progress.setTitle(msg); // setting title
        progress.setMessage("Please Wait ..."); // creating message
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // style of indicator
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