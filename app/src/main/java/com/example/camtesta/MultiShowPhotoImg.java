package com.example.camtesta;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicConvolve3x3;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MultiShowPhotoImg extends AppCompatActivity {

    RecyclerView image_recycler;
    private ImageAdapter imageAdapter;
    private ArrayList<Emages> emagesArrayList;
    private Emages emages;
    ImageButton addbtn;
    ImageButton editbtn;
    ImageButton save;
    ImageButton delete;
    ImageButton backbtn;
    TextView countview;
    ImageButton sharp;
    int count=0;
    int countfor=1;
    Bitmap bitmaps;
    String number=null;
    String pages = "Total Pages : ";
    int val=0;
    int del=1;
    ProgressDialog progress;
    Context context;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_show_photo_img);

        String filename = getIntent().getStringExtra("myimage");
        Bitmap bitmap;
        String image_path= getIntent().getStringExtra("imagePath");

        progress = new ProgressDialog(MultiShowPhotoImg.this);
        progressbar("Processing");

        sharp = findViewById(R.id.sharp);
        sharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog3 = new Dialog(MultiShowPhotoImg.this);
                dialog3.setContentView(R.layout.sharpness);
                Window window = dialog3.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Button dialogButton = (Button) dialog3.findViewById(R.id.sharp1);
                Button dialogButton2 = (Button) dialog3.findViewById(R.id.sharp2);
                EditText text = dialog3.findViewById(R.id.pg);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!text.getText().toString().equals("") && text.getText().toString().length() > 0){
                            del = Integer.parseInt(String.valueOf(text.getText()));
                            if(del > MainActivity.multipics.size() || del==0){
                                Toast.makeText(getApplicationContext(),"Exceeds Page Limit!", Toast.LENGTH_SHORT).show();
                            }else{
                                File newfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + System.currentTimeMillis() + ".jpg");
                                Uri uri = null;
                                Bitmap oldbit = null;
                                Bitmap newbit = null;
                                ContentResolver contentResolver = getContentResolver();
                                try {
                                    if(Build.VERSION.SDK_INT < 28) {
                                        oldbit = MediaStore.Images.Media.getBitmap(contentResolver, MainActivity.multipics.get(del-1));
                                    } else {
                                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, MainActivity.multipics.get(del-1));
                                        oldbit = ImageDecoder.decodeBitmap(source);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                loadBitmapSharp(newbit,oldbit);
                                MainActivity.multipics.remove(del-1);
                                emagesArrayList.remove(del-1);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                newbit.compress(Bitmap.CompressFormat.JPEG, 50 , bos);
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
                                MainActivity.multipics.add(del-1, uri);
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
                        if(!text.getText().toString().equals("") && text.getText().toString().length() > 0){
                            del = Integer.parseInt(String.valueOf(text.getText()));
                            if(del > MainActivity.multipics.size() || del==0){
                                Toast.makeText(getApplicationContext(),"Exceeds Page Limit!", Toast.LENGTH_SHORT).show();
                            }else{
                                File newfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + System.currentTimeMillis() + ".jpg");
                                Uri uri = null;
                                Bitmap oldbit = null;
                                Bitmap newbit = null;
                                ContentResolver contentResolver = getContentResolver();
                                try {
                                    if(Build.VERSION.SDK_INT < 28) {
                                        oldbit = MediaStore.Images.Media.getBitmap(contentResolver, MainActivity.multipics.get(del-1));
                                    } else {
                                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, MainActivity.multipics.get(del-1));
                                        oldbit = ImageDecoder.decodeBitmap(source);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                loadBitmapSharp2(newbit,oldbit);
                                MainActivity.multipics.remove(del-1);
                                emagesArrayList.remove(del-1);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                newbit.compress(Bitmap.CompressFormat.JPEG, 50 , bos);
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
                                MainActivity.multipics.add(del-1, uri);
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
                dialog3.show();
                dialog3.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationz;
                dialog3.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

        backbtn = findViewById(R.id.backbtnmultiimg);
        backbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               emagesArrayList.clear();
               MainActivity.multipics.clear();
               onBackPressed();
           }
       });

        countview = findViewById(R.id.countview);
        int totalpages = MainActivity.multipics.size();
        countview.setText(pages + String.valueOf(totalpages));

        editbtn = findViewById(R.id.editbtnmulti);
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
                final Dialog dialog2 = new Dialog(MultiShowPhotoImg.this);
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
                            if(val >MainActivity.multipics.size() || val==0){
                                Toast.makeText(getApplicationContext(),"Exceeds Page Limit!", Toast.LENGTH_SHORT).show();
                            }else{
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
                                        .start(MultiShowPhotoImg.this);
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

        delete = findViewById(R.id.deleteforimgz);
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
                final Dialog dialog3 = new Dialog(MultiShowPhotoImg.this);
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
                            }else{
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



        save = findViewById(R.id.savemulti);
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
                final Dialog dialog = new Dialog(MultiShowPhotoImg.this);
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
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String filename = text.getText().toString();
                        //saveTempBitmap(bmp,filename);
                        if (filename.equals("")) {
                            try {
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
                                createPdf(filename, MainActivity.multipics);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getApplicationContext(),"No Pages!",Toast.LENGTH_SHORT).show();
                        } else {
                            if (count == 0) {
                                try {
                                    if (isExternalStorageWritable() == true) {
                                        createPdf(filename, MainActivity.multipics);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    createPdf(filename, MainActivity.multipics);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
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
        addbtn = findViewById(R.id.mybtn);
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
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.addCategory(Intent.CATEGORY_OPENABLE);
                getIntent.setType("image/*");
                getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

               launchSomeActivity.launch(chooserIntent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        image_recycler = findViewById(R.id.picrecycle);
        image_recycler.setLayoutManager(new LinearLayoutManager(MultiShowPhotoImg.this, LinearLayoutManager.HORIZONTAL,false));
        //image_recycler.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false));
        emagesArrayList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this,emagesArrayList);
        image_recycler.setAdapter(imageAdapter);
        for(int z=0;z<MainActivity.multipics.size();z++){
            createListData(MainActivity.multipics.get(z));
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
                File newfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + System.currentTimeMillis() + ".jpg");
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
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(640, 960, k+1).create();

                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#ffffff"));
                canvas.drawPaint(paint);

                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 4;
                bitmaps = Bitmap.createScaledBitmap(bitmaps, bitmaps.getWidth(), bitmaps.getHeight(), true);
                bitmaps = Bitmap.createScaledBitmap(bitmaps,bitmaps.getWidth()/2,bitmaps.getHeight()/2,false);
                bitmaps = bitmaps.copy(Bitmap.Config.ARGB_8888, true);
                options.inJustDecodeBounds=false;



                paint.setColor(Color.BLUE);
                canvas.drawBitmap(bitmaps, 0, 0, null);
                document.finishPage(page);
        }

        File folder = getExternalFilesDir("MyPDFScanner");
        File[] list = folder.listFiles();
        File filePath = new File(folder, filename + ".pdf");

            if (filePath.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiShowPhotoImg.this);
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
            MainActivity.multipics.clear();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        progress.setTitle("Saving " + filename);
        progress.setMessage("Please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MultiShowPhotoImg.this, MainActivity.class);
                intent.putExtra("FILENAME", filename);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                progress.dismiss();
            }
        }.start();


        document.close();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiShowPhotoImg.this);
        builder.setIcon(R.drawable.danger);
        builder.setTitle("Leave");
        builder.setMessage("All your progress will be deleted!");
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    progressbar("Adding Images");
                    MainActivity.mClipData = result.getData().getClipData();
                    if (result.getData().getClipData() != null) {
                        for (int z = 0; z < MainActivity.mClipData.getItemCount(); z++) {
                            ClipData.Item item = MainActivity.mClipData.getItemAt(z);
                            Uri uri = item.getUri();
                            MainActivity.multipics.add(uri);
                            createListData(uri);
                        }
                        int totalpages = MainActivity.multipics.size();
                        countview.setText(pages + String.valueOf(totalpages));
                    }else{
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            MainActivity.multipics.add(selectedImageUri);
                            createListData(selectedImageUri);
                            int totalpages = MainActivity.multipics.size();
                            countview.setText(pages + String.valueOf(totalpages));
                        }
                    }
                }
            });

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

    public Bitmap doSharpen(Bitmap original, float[] radius) {
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bitmap = Bitmap.createScaledBitmap(original,original.getWidth(),original.getHeight(),false);
        }
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Context MultiShowPhotoImg = null;
        RenderScript rs = RenderScript.create(this);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, mutableBitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(radius);
        convolution.forEach(allocOut);

        allocOut.copyTo(mutableBitmap);
        rs.destroy();

        return mutableBitmap;
    }

    // low
    private void loadBitmapSharp(Bitmap yourbitmap,Bitmap getbitmap) {
        float[] sharp = { -0.60f, -0.60f, -0.60f, -0.60f, 5.81f, -0.60f,
                -0.60f, -0.60f, -0.60f };
//you call the method above and just paste the bitmap you want to apply it and the float of above
        yourbitmap = doSharpen(getbitmap, sharp);
    }

    // medium
    private void loadBitmapSharp1(Bitmap yourbitmap,Bitmap getbitmap) {
        float[] sharp = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f,
                0.0f

        };
//you call the method above and just paste the bitmap you want to apply it and the float of above
        yourbitmap = doSharpen(getbitmap, sharp);
    }

    // high
    private void loadBitmapSharp2(Bitmap yourbitmap,Bitmap bitmap) {
        float[] sharp = { -0.15f, -0.15f, -0.15f, -0.15f, 2.2f, -0.15f, -0.15f,
                -0.15f, -0.15f
        };
        //you call the method above and just paste the bitmap you want to apply it and the float of above
        yourbitmap = doSharpen(bitmap, sharp);
    }


}
