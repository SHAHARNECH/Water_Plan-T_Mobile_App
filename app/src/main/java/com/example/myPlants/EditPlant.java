package com.example.myPlants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * The type Edit plant- handles plant changes and update.
 */
public class EditPlant extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private EditText editName, edtDescription, edtIrrigation;
    private CalendarView cal;
    private Button btnCancel, btnSave,uploadImg;
    private String selectedDate, image, name,descript,selectedFamily,currentPhotoPath;
    private LinearLayout linearLayout;
    private Spinner family;
    private String[] oldFiles;
    private File storageDir;
    private int id_num;
    private ArrayList<String> plant_family;
    private HashMap<String, Integer> plant_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        final Intent intent = getIntent();
        //for spinner options
        plant_list = new HashMap<>();
        plant_list.put("Custom", 0);
        plant_list.put("Cutting", 7);
        plant_list.put("Pothos", 3);
        plant_list.put("Monstera", 14);
        plant_list.put("Succulent", 30);
        plant_list.put("Orchid", 30);
        plant_list.put("Calathea", 20);
        plant_family = new ArrayList<>(plant_list.keySet());

        id_num = intent.getIntExtra("id", 0);
        image = intent.getStringExtra("image");
        name=intent.getStringExtra("name");
        descript = intent.getStringExtra("description");

        linearLayout = findViewById(R.id.btn_holder);
        edtIrrigation = findViewById(R.id.edt_edit_irrigation);
        edtDescription = findViewById(R.id.edt_edit_descrption);
        editName = findViewById(R.id.edt_edit_name);
        cal = findViewById(R.id.edt_cal);
        family = findViewById(R.id.edt_spinner);
        uploadImg = findViewById(R.id.upload_img);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        edtDescription.setText(descript);
        editName.setText(name);
        String s = String.valueOf(intent.getIntExtra("irrigation", 0));
        edtIrrigation.setText(s);

        //spinner actions
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, plant_family);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        family.setAdapter(aa);
        family.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFamily = plant_family.get(i);
                edtIrrigation.setText(String.valueOf(plant_list.get(selectedFamily)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //date actions
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int y, int m, int d) {
                int month = m + 1;
                selectedDate = "" + d + "/" + month + "/" + y;
            }
        });

        //cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPhotoPath!=null){
                    File newFile = new File(currentPhotoPath);
                    newFile.delete();
                }
                onBackPressed();
            }
        });

        //save changes
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPhotoPath!=null){
                    for(String s: oldFiles){
                        File del= new File(storageDir.getAbsolutePath(), s);
                        if(!(del.getAbsolutePath().equals(currentPhotoPath))){
                            del.delete();
                        }
                    }
                }
                descript= edtDescription.getText().toString();
                name = editName.getText().toString();
                if("".equals(name)){
                    name = "Default "+selectedFamily;
                }
                String next = intent.getStringExtra("next");
                int irr = Integer.parseInt(edtIrrigation.getText().toString());
                Plant plant = new Plant(name, selectedFamily, descript, irr, selectedDate, next, image);
                plant.setId(id_num);
                if (new PlantHandler(EditPlant.this).update(plant)) {
                    Toast.makeText(EditPlant.this, "Plant updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPlant.this, "Failed updating", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        });

        try {
            selectedDate = intent.getStringExtra("date");
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            if (!(selectedDate == null)) {
                Date d = f.parse(selectedDate);
                long milliseconds = d.getTime();
                cal.setDate(milliseconds);
            }
        } catch (ParseException e) {}

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
              takePic();
              image = currentPhotoPath;
            }
        });

        String fam = intent.getStringExtra("family");
        family.setSelection(plant_family.indexOf(fam));
    }

    @Override
    public void onBackPressed() {
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(linearLayout);
        }
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File file = new File (currentPhotoPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inJustDecodeBounds=false;
            Bitmap bitmap = null;
            while(bitmap == null) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                out.flush();
                out.close();
            } catch (Exception e) {}
        }
    }

    /**
     * get permission to use camera
     * using takePic
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        {   super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == MY_CAMERA_PERMISSION_CODE)
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    takePic();
                } else
                { Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show(); }
            }
        }

    /**
     * takes picture and saves file
     * using createImageFile
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void takePic(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        { requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE); }
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile(id_num);
            } catch (IOException ex) {}

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile(int filename) throws IOException {
            String filename_id = "filename_"+filename;
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            oldFiles = storageDir.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.startsWith(filename_id);
                }
            });

            File image = File.createTempFile(
                    filename_id,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }
}
