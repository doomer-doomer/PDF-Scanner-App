package com.example.camtesta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context context;
    private ArrayList<Emages> emages;

    public ImageAdapter(Context context, ArrayList<Emages> emages) {
        this.context = context;
        this.emages = emages;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.multi_image_gallery,parent,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageHolder holder, int position) {
        Emages images = emages.get(position);
        holder.SetDetails(images);
        holder.count.setText(String.valueOf("Page " + (position+1)));

    }

    @Override
    public int getItemCount() {
        return emages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder{

        private ImageView img2;
        private TextView count;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            img2 = itemView.findViewById(R.id.multi_img);
            count = itemView.findViewById(R.id.countviewdk);
        }
        void SetDetails(Emages emages){
            img2.setImageURI(emages.getImage());

        }
    }

}
