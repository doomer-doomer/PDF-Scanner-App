package com.example.camtesta;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButtonA;
    ImageButton imageButtonB;
    RecyclerView normalView;
    ImageView imageView;
    TextView textView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter adapter;
    private ArrayList<Pdfs> pdfarraylist;
    private Parcelable recyclerViewState;
    public int c=0;
    public static String name;
    public static Bitmap mainbitmap;
    public static String filecount[] = new String[10];
    public static ArrayList<Uri> multipics = new ArrayList<>();
    ProgressDialog progress;
    public static ClipData mClipData;


    private int REQUEST_CODE_PERMISSION=101;
    private final String[] REQUIRED_PERMISSION = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setTheme(R.style.Theme_CamTestA_Launcher);
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_to_top,R.anim.slide_to_bottom);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(MainActivity.this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                normalView.invalidate();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_to_top,R.anim.slide_to_bottom);
            }
        });

        Intent intent = getIntent();
        String filename = intent.getStringExtra("FILENAME");
        int data = intent.getIntExtra("ID",2);

        if(allPermissionGranted()){

        }else {
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSION,REQUEST_CODE_PERMISSION);
        }

        imageButtonA = findViewById(R.id.cambtn);
        imageButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoCam();
            }
        });

        imageButtonA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                //Code to convert height and width in dp.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutParams.width = width - 8;
                    layoutParams.height = height - 8;
                    imageButtonA.setLayoutParams(layoutParams);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    layoutParams.width = width;
                    layoutParams.height = height;
                    imageButtonA.setLayoutParams(layoutParams);
                }
                return false;
            }
        });

        imageButtonB = findViewById(R.id.gallerybtn);
        imageButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageChooser();
            }
        });

        imageButtonB.setOnTouchListener((view, motionEvent) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

            //Code to convert height and width in dp.
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                layoutParams.width = width - 8;
                layoutParams.height = height - 8;
                imageButtonB.setLayoutParams(layoutParams);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                layoutParams.width = width;
                layoutParams.height = height;
                imageButtonB.setLayoutParams(layoutParams);
            }
            return false;
        });

        textView = findViewById(R.id.hiddentxt);
        imageView = findViewById(R.id.hiddenimg);

        File folder = getExternalFilesDir("MyPDFScanner");
        File[] list = folder.listFiles();
        for (File f: list) {
            String name = f.getName();
            if (name.endsWith(".pdf")){
                ++c;
            }
            if(list==null){
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }else{
                textView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        }

        normalView = findViewById(R.id.normalrecycle);
        normalView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewState = normalView.getLayoutManager().onSaveInstanceState();
        normalView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        pdfarraylist = new ArrayList<>();
        adapter = new MyAdapter(this,pdfarraylist);
        normalView.setAdapter(adapter);
        //normalView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        try {
            createListData(filename,data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createListData(String filename,int x) throws IOException {
        //Adding Data to Recycler View
        ParcelFileDescriptor pfd;
        Pdfs[] pdfs = new Pdfs[c];
        File folder = getExternalFilesDir("MyPDFScanner");
        File filepath = new File(folder, filename + ".pdf");
        File[] list = folder.listFiles();
        if(list==null){

        }else{
            for(File f: list){
                for(int i=0;i<1;i++){
                    filecount[i] = f.getName();
                    name = f.getName();
                    String newname = name.replace(".pdf","");
                    //mainbitmap = BitmapFactory.decodeFile(filepath + "/"+ f.getName());
                    File file = new File(folder, f.getName());
                    Date lastModDate = new Date(file.lastModified());
                    long filsizekb = Integer.parseInt(String.valueOf(file.length()/(1024)));
                    String stamp = "" + lastModDate + " : " + filsizekb + " kb";
                    String newstamp = stamp.replace(" GMT+05:30 ", "\n");
                    pfd = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer renderer = new PdfRenderer(pfd);
                    PdfRenderer.Page page = renderer.openPage(0);

                    mainbitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    mainbitmap = Bitmap.createScaledBitmap(mainbitmap,mainbitmap.getWidth()/5 ,mainbitmap.getHeight()/5,false);

                    page.render(mainbitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    pdfs[i] = new Pdfs(newname,mainbitmap,newstamp);
                    pdfarraylist.add(pdfs[i]);
                    page.close();
                    renderer.close();
                }
            }
        }

        adapter.notifyDataSetChanged();

    }


    public void gotoCam(){
        Intent intent = new Intent(this,CamPrev.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    private boolean allPermissionGranted(){
        for(String permssion : REQUIRED_PERMISSION){
            if(ContextCompat.checkSelfPermission(this,permssion)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
            System.exit(0);
        });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void imageChooser()
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.setType("image/*");
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        launchSomeActivity.launch(chooserIntent);
    }

    public void progressbar(String msg){
        progress.setTitle(msg); // setting title
        progress.setMessage("Please Wait, Loading..."); // creating message
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

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    mClipData = result.getData().getClipData();
                    if (result.getData().getClipData() != null){
                        for (int z = 0; z < mClipData.getItemCount(); z++) {
                            ClipData.Item item = mClipData.getItemAt(z);
                            Uri uri = item.getUri();
                            multipics.add(uri);
                        }
                        Intent in = new Intent(MainActivity.this,MultiShowPhotoImg.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                }else{
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            multipics.add(selectedImageUri);
                        }
                        Intent in = new Intent(MainActivity.this,MultiShowPhotoImg.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                    }
            }
        });





}

