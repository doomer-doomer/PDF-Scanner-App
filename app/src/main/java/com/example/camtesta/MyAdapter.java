package com.example.camtesta;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.AdapterHolder> {

    private Context context;
    private ArrayList<Pdfs> pdfs;
    View emptyView;


    //Constructor

    public MyAdapter(Context context, ArrayList<Pdfs> pdfs) {
        this.context = context;
        this.pdfs = pdfs;

    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_storing_layout,parent,false);
        return new AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.AdapterHolder holder, int position) {

        Pdfs mypdf = pdfs.get(position);
        holder.SetDetails(mypdf);

    }

    @Override
    public int getItemCount() {
        return pdfs.size();
    }


    class AdapterHolder extends RecyclerView.ViewHolder{

        private TextView txt;
        private ImageView img;
        private ImageButton sharebtn;
        private ImageButton delete;
        private ImageButton edit;
        private TextView stamp;
        private ImageButton more;
        private SwipeRefreshLayout swipeRefreshLayout;

        public AdapterHolder(@NonNull View itemView) {
            super(itemView);

            txt = itemView.findViewById(R.id.textname);
            img = itemView.findViewById(R.id.imageView4);
            sharebtn = itemView.findViewById(R.id.shareicon);
            delete = itemView.findViewById(R.id.deletebtn);
            edit = itemView.findViewById(R.id.editbtn);
            stamp= itemView.findViewById(R.id.stamp);
            more = itemView.findViewById(R.id.moreoptions);
            swipeRefreshLayout = (SwipeRefreshLayout) itemView.findViewById(R.id.refresh);

        }

        @SuppressLint("ClickableViewAccessibility")
        void SetDetails(Pdfs pdfs){
            txt.setText(pdfs.getFilename());
           img.setImageBitmap(pdfs.getImageUrl());
           stamp.setText(pdfs.getStamp());

            img.setOnTouchListener((view, motionEvent) -> {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                //Code to convert height and width in dp.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutParams.width = width - 15;
                    layoutParams.height = height - 15;

                    img.setLayoutParams(layoutParams);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    layoutParams.width = width;
                    layoutParams.height = height;
                    img.setLayoutParams(layoutParams);
                }
                return false;
            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        File folder = context.getExternalFilesDir("MyPDFScanner");
                        File filepath = new File(folder, pdfs.getFilename() + ".pdf");
                        if(filepath.exists()){
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",filepath);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(uri, "application/pdf") ;
                            context.startActivity(Intent.createChooser(intent, "Open this PDF"));
                        }else{
                            Toast.makeText(context.getApplicationContext(),"ERROE", Toast.LENGTH_SHORT).show();
                        }
                    }catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });


            sharebtn.setOnTouchListener((view, motionEvent) -> {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                //Code to convert height and width in dp.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutParams.width = width - 5;
                    layoutParams.height = height - 5;
                    sharebtn.setLayoutParams(layoutParams);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    layoutParams.width = width;
                    layoutParams.height = height;
                    sharebtn.setLayoutParams(layoutParams);
                }
                return false;
            });

            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        File folder = context.getExternalFilesDir("MyPDFScanner");
                        File filepath = new File(folder, pdfs.getFilename()+ ".pdf");
                        //File filechk = new File("Android/data/com.example.cameratesta/files/MyPDFScanner/" + pdfs.getFilename());
                        if(filepath.exists()){
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",filepath);
                            intent.setDataAndType(uri, "application/pdf") ;
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            context.startActivity(Intent.createChooser(intent, "Share this PDF"));
                        }else{
                            Toast.makeText(context.getApplicationContext(),"ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(context.getApplicationContext(),"Share",Toast.LENGTH_SHORT).show();
                }
            });


            delete.setOnTouchListener((view, motionEvent) -> {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                //Code to convert height and width in dp.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.danger);
                    builder.setMessage("Do you want to delete " + pdfs.getFilename());
                    builder.setTitle("Delete !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.dismiss();
                        File folder = context.getExternalFilesDir("MyPDFScanner");
                        File[] list = folder.listFiles();
                        File filepath = new File(folder, pdfs.getFilename()+ ".pdf");
                        if(filepath.exists()){
                            filepath.delete();
                            int count_cam = 0;
                            for (File f: list){
                                String name = f.getName();
                                if (name.endsWith(".pdf"))
                                    ++count_cam;
                            }
                            Toast.makeText(context.getApplicationContext(),count_cam-1 + " pdf are present!",Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context.getApplicationContext(),"Pull down to Refresh",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            edit.setOnTouchListener((view, motionEvent) -> {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                //Code to convert height and width in dp.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    layoutParams.width = width - 5;
                    layoutParams.height = height - 5;
                    edit.setLayoutParams(layoutParams);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    layoutParams.width = width;
                    layoutParams.height = height;
                    edit.setLayoutParams(layoutParams);
                }
                return false;
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.editnamedialog);
                    Window window = dialog.getWindow();
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_okay_edit);
                    Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_cancel_edit);
                    EditText text = dialog.findViewById(R.id.txt_input_edit);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if(text.getText().toString().equals("")){
                                Toast.makeText(context.getApplicationContext(), "No filename detected!",Toast.LENGTH_SHORT).show();
                            }else if(text.getText().toString().equals(pdfs.getFilename())){
                                Toast.makeText(context.getApplicationContext(), "File has the same name",Toast.LENGTH_SHORT).show();
                            }else{
                                String filename = text.getText().toString();
                                File folder = context.getExternalFilesDir("MyPDFScanner");
                                File[] list = folder.listFiles();
                                File filepath = new File(folder, pdfs.getFilename()+ ".pdf");
                                int count_cam = 0;
                                File newfile = new File(folder,filename + ".pdf");
                                filepath.renameTo(newfile);
                                //Toast.makeText(context.getApplicationContext(),"Pull down to Refresh",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
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

            more.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.moredialog);
                    Window window = dialog.getWindow();
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Button dialogButton = (Button) dialog.findViewById(R.id.modify);
                    Button dialogButton2 = (Button) dialog.findViewById(R.id.pdftodoc);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Bitmap bitmap= null;
                            File folder = context.getExternalFilesDir("MyPDFScanner");
                            File[] list = folder.listFiles();
                            File filepath = new File(folder, pdfs.getFilename()+ ".pdf");
                            try {
                                int pages,i;
                                ParcelFileDescriptor fd = ParcelFileDescriptor.open(filepath, ParcelFileDescriptor.MODE_READ_ONLY);
                                PdfRenderer renderer = new PdfRenderer(fd);
                                pages = renderer.getPageCount();
                                for(i=0;i<pages;i++){
                                    bitmap = Bitmap.createBitmap(640,960, Bitmap.Config.ARGB_4444);
                                    PdfRenderer.Page page = renderer.openPage(i);
                                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
                                    Uri uri = Uri.parse(path);
                                    MainActivity.multipics.add(uri);
                                    page.close();
                                }
                                renderer.close();
                                Intent intent = new Intent(context.getApplicationContext(), MultiShowPhotoCam.class);
                                context.startActivity(intent);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
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


        }
    }
}
