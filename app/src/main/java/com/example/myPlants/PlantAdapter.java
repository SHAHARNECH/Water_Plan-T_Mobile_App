package com.example.myPlants;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * The type Plant adapter.
 */
public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantHolder> {

    private ArrayList<Plant> plants;
    private Context context;
    private ItemClicked itemClicked;
    private ViewGroup parent;


    /**
     * Instantiates a new Plant adapter.
     *
     * @param arrayList   the plant list
     * @param context     the context
     * @param itemClicked the item clicked
     */
    public PlantAdapter(ArrayList<Plant> arrayList, Context context, ItemClicked itemClicked){
        plants = arrayList;
        Collections.sort(plants);
        this.context = context;
        this.itemClicked = itemClicked;
    }

    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plant_holder,parent,false);
        this.parent = parent;
        return new PlantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantHolder holder, int position) {
        Plant pl = plants.get(position);
        holder.name.setText(pl.getName());
        holder.description.setText(pl.getDescription());
        holder.date.setText(pl.getPlant_birthdate());
        holder.family.setText(pl.getFamily());
        String path = pl.getImage();
        File direct = new File(path);

        //if picture has been taken
        if (direct.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inPurgeable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(direct.getAbsolutePath(), bmOptions);
                bitmap = RotateBitmap(bitmap, 90);
                holder.img.setImageBitmap(bitmap);
        }
        //else get default picture by family kind
        else{
            int id = context.getResources().getIdentifier(pl.getImage().toLowerCase(), "drawable", context.getPackageName());
            holder.img.setImageResource(id);
        }

        //set water button visible if needed watering
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
            try{
                if(f.parse(pl.getNextIrr()).before(today) || !(f.parse(pl.getNextIrr()).after(today))){
                    holder.imgWater.setVisibility(View.VISIBLE);
                }
            }catch (ParseException e){}
    }

    /**
     * Rotate camera image.
     *
     * @param source the source
     * @param angle  the angle
     * @return the bitmap
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    /**
     * The type Plant holder.
     */
    class PlantHolder extends RecyclerView.ViewHolder{

        protected ImageView img;
        protected TextView name;
        protected TextView description;
        protected ImageView imgEdit;
        protected ImageView imgWater;
        protected TextView date;
        protected TextView family;


        /**
         * Instantiates a new Plant holder.
         *
         * @param itemView the view
         */
        public PlantHolder(@NonNull final View itemView) {
            super(itemView);
            family = itemView.findViewById(R.id.hld_kind);
            name = itemView.findViewById(R.id.txt_plant_name);
            description = itemView.findViewById(R.id.txt_plant_description);
            date = itemView.findViewById(R.id.hld_cal);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgWater = itemView.findViewById(R.id.img_water);
            img = itemView.findViewById(R.id.image_plant);

            //edit plant
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClicked.onClick(getAdapterPosition(),itemView);
                }
            });
            //water plant
            imgWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClicked.onClickWater(getAdapterPosition(),itemView);
                }
            });
        }
    }

    interface ItemClicked{
        void onClick(int position, View view);
        void onClickWater(int position, View view);
    }
}
