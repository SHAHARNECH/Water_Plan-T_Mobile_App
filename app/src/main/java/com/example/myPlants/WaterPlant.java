package com.example.myPlants;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * The type Water plant- handles plant soil check if needed water
 */
public class WaterPlant extends AppCompatActivity {
   private TextView soil, name;
   private Button yes, no, cancel;
   private String plant_name, plant_date,plant_family,plant_description,plant_next, plant_img;
   private int plant_irrigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_plant);
        final Intent intent = getIntent();

        soil = findViewById(R.id.soil_dry);
        name = findViewById(R.id.water_name);
        yes = findViewById(R.id.btnYes);
        no = findViewById(R.id.btnNo);
        cancel = findViewById(R.id.btnCancel);

        plant_name= intent.getStringExtra("name");
        plant_family= intent.getStringExtra("family");
        plant_description= intent.getStringExtra("description");
        plant_irrigation= intent.getIntExtra("irrigation",0);
        plant_date= intent.getStringExtra("date");
        plant_next= intent.getStringExtra("next");
        plant_img = intent.getStringExtra("image");
        name.setText(plant_name);
        soil.setText("Is the soil dry?");

        //cancel actions
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //soil is dry- needs water
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plant plant = new Plant(plant_name, plant_family, plant_description,plant_irrigation,plant_date, plant_next,plant_img);
                //sets next irrigation to today+dry days
                plant.setNextIrr(plant.getIrrigation());
                plant.setId(intent.getIntExtra("id",1));
                if (new PlantHandler(WaterPlant.this).update(plant)){
                    Toast.makeText(WaterPlant.this, "See you next soil check!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WaterPlant.this, "Failed updating", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        });
        //soil is not dry yet
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plant plant = new Plant(plant_name, plant_family, plant_description,plant_irrigation,plant_date, plant_next,plant_img);
                //sets next irrigation to two days from today
                plant.setNextIrr(2);
                plant.setId(intent.getIntExtra("id",1));
                if (new PlantHandler(WaterPlant.this).update(plant)){
                    Toast.makeText(WaterPlant.this, "See you next soil check!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WaterPlant.this, "Failed updating", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
