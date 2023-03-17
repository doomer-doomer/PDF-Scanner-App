package com.example.camtesta;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.TextView;

public class Emages {
    private Uri image;



    public Uri getImage() {
        return image;
    }

    public Emages(Uri image) {
        this.image = image;
    }
}
