package com.example.camtesta;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Pdfs {

    private String filename;
    private Bitmap imageUrl;
    private ImageButton sharebtn;
    private ImageButton delete;
    private String stamp;

    private ImageButton edit;

//Construtors


    public String getStamp() {
        return stamp;
    }

    public Pdfs(String filename, Bitmap imageUrl, String stamp) {
        this.filename = filename;
        this.imageUrl = imageUrl;
        this.sharebtn = sharebtn;
        this.delete = delete;
        this.edit = edit;
        this.stamp = stamp;
    }

    public ImageButton getEdit() {
        return edit;
    }
    public ImageButton getDelete() {
        return delete;
    }

    public ImageButton getSharebtn() {
        return sharebtn;
    }
    //Getters


    public String getFilename() {
        return filename;
    }

    public Bitmap getImageUrl() {
        return imageUrl;
    }


}
