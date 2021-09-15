package com.example.myPlants;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Main activity.
 */
public class MainActivity extends AppCompatActivity {
    private Boolean notifactions= null;
    private int hour, minute, id_img;
    private static final String CHANNEL_ID = "abcds";
    private String currentPhotoPath;
    private ImageView imageButton;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ArrayList<Plant> plants;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private String selectedDate,selectedFamily;
    private CalendarView edtCal;
    private HashMap<String, Integer> plant_list;
    private ArrayList<String> plant_family;
    private SimpleDateFormat f;
    private List<PendingIntent> mPendingIntentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPendingIntentList = new ArrayList<PendingIntent>();
        AlarmHandler alarmHandler = new AlarmHandler(this);

        /**set notification preferences**/
        if(alarmHandler.read()==null){ //new database for notifications
            alarmHandler.create(false,0,0);
        }
        Notification newNotif = alarmHandler.read();
        notifactions= newNotif.getNotify()>0?true:false;
        hour= newNotif.getHour();
        minute = newNotif.getMinute();

        /**set main visual**/
        imageButton = findViewById(R.id.img_add);
        f = new SimpleDateFormat("dd/MM/yyyy");
        imageButton.setVisibility(View.INVISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlant();
            }});

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**swipe to delete**/
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new PlantHandler(MainActivity.this).delete(plants.get(viewHolder.getAdapterPosition()).getId());
                plants.remove(viewHolder.getAdapterPosition());
                plantAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                if (plants.isEmpty()) { //get background picture back
                    recyclerView.setBackgroundResource(R.drawable.background);
                    imageButton.setVisibility(View.VISIBLE);
                }
                else{ //set start button to invisible
                    imageButton.setVisibility(View.INVISIBLE);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        loadPlants();
    }

    /**
     * Add plant- full layout
     */
    public void addPlant(){
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View viewInput = inflater.inflate(R.layout.plant_input, null, false);
                plant_list = new HashMap<>();
                plant_list.put("Custom", 0);
                plant_list.put("Cutting", 7);
                plant_list.put("Pothos", 3);
                plant_list.put("Monstera", 14);
                plant_list.put("Succulent", 30);
                plant_list.put("Orchid", 30);
                plant_list.put("Calathea", 20);
                plant_family = new ArrayList<>(plant_list.keySet());

                final Button edtImg = viewInput.findViewById(R.id.add_upload_img);
                final EditText edtName = viewInput.findViewById(R.id.edt_name);
                final EditText edtDescription = viewInput.findViewById(R.id.edt_description);
                final EditText edtIrrigation = viewInput.findViewById(R.id.edt_irrigation);
                final Spinner family = viewInput.findViewById(R.id.spinner);

                //spinner actions
                ArrayAdapter aa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, plant_family);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                family.setAdapter(aa);
                family.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedFamily = plant_family.get(i);
                        int ir = plant_list.get(selectedFamily);
                        edtIrrigation.setText(String.valueOf(ir));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                //Date actions
                edtCal = viewInput.findViewById(R.id.edt_cal);
                edtCal.setDate(new Date().getTime());
                selectedDate = f.format(edtCal.getDate());
                edtCal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int y, int m, int d) {
                        int month = m + 1;
                        selectedDate = "" + d + "/" + month + "/" + y;
                    }
                });
                currentPhotoPath = "";

                //Picture actions
                edtImg.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        id_img = plantAdapter.getItemCount() + 1;
                        takePic();
                    }
                });

                new AlertDialog.Builder(MainActivity.this)
                        .setView(viewInput)
                        .setTitle("Add Plant")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name="";
                                name = edtName.getText().toString();
                                if("".equals(name)){
                                    name = "Default "+selectedFamily;
                                }
                                String description="";
                                 description = edtDescription.getText().toString();
                                if("".equals(description)){
                                    description = "";
                                }
                                String irrigation = edtIrrigation.getText().toString();
                                if ("".equals(currentPhotoPath)) {
                                    currentPhotoPath = selectedFamily;
                                }
                                /**make new Plants from chosen and add to plantHandler**/
                                Plant plant = new Plant(name, selectedFamily, description, Integer.parseInt(irrigation), selectedDate, "", currentPhotoPath);
                                boolean isInserted = new PlantHandler(MainActivity.this).create(plant);
                                if (isInserted) {
                                    Toast.makeText(MainActivity.this, "Plant saved", Toast.LENGTH_SHORT).show();
                                    loadPlants();
                                } else {
                                    Toast.makeText(MainActivity.this, "Unable to save the Plant", Toast.LENGTH_SHORT).show();
                                }
                                dialogInterface.cancel();
                            }
                        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**menu actions**/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
               setNotification();
                return true;

            case R.id.about:
               //final View note_view = inflater.inflate(R.layout.about, null, false);
                return true;

            case R.id.add:
               addPlant();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /**
     * Read plants from database
     * if notification preferences is on- sets them
     * @return arraylist of plants
     */
    public ArrayList<Plant> readPlants() {
        PlantHandler handler = new PlantHandler(this);
        ArrayList<Plant> plants = handler.readPlants();
        if(notifactions){
            setAlarm();
        }
        return plants;
    }

    /**
     * Set alarm to push notification when needed.
     */
    public void setAlarm(){
        cancelAllAlarms(); //set new alarms need to cancel future or passed alarms
        PlantHandler handler = new PlantHandler(this);
        Calendar cal = Calendar.getInstance();
        Date today = new Date();

        //gets next irrigation dates from database
        HashSet<Date> hashSet = handler.getAlarms();

    for(Date date:hashSet){
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour); //sets time for alarm
        cal.set(Calendar.MINUTE, minute);
        Date d = cal.getTime();

        //only future or today's plants need to be inserted to alarm manager
        if(d.after(today) || !d.before(today)){
            AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, new Random().nextInt(), intent, 0);
            mPendingIntentList.add(alarmIntent); //save for cancelling later
            //set alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, d.getTime(), alarmIntent);
            } else if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, d.getTime(), alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, d.getTime(), alarmIntent);
            }
        }

    }
}

    /**
     * Cancel all alarms.
     */
    public void cancelAllAlarms(){
        AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
        //cancel all alarms
        for(int i = 0 ; i < mPendingIntentList.size() ; i++){
            alarmMgr.cancel(mPendingIntentList.get(i));
        }
    }

    /**
     * Set notification layout to get notification preferences.
     */
    public void setNotification(){
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View note_view = inflater.inflate(R.layout.edit_notification, null, false);
        AlarmHandler alarmHandler = new AlarmHandler(MainActivity.this);
        Switch switcher = (Switch)note_view.findViewById(R.id.switch1);
        TextView txtSet = note_view.findViewById(R.id.txt_set);
        EditText shour = note_view.findViewById(R.id.setTime);
        EditText sminute = note_view.findViewById(R.id.setTime2);
        shour.setEnabled(false);
        sminute.setEnabled(false);
        if(notifactions){
            switcher.setChecked(true);
            shour.setEnabled(true);
            sminute.setEnabled(true);
            txtSet.setTextColor(Color.BLACK);
            shour.setInputType(InputType.TYPE_CLASS_NUMBER);
            sminute.setInputType(InputType.TYPE_CLASS_NUMBER);
            String h="", min="";
            min = minute>9? String.valueOf(minute):"0"+minute;
            h = hour>9? String.valueOf(hour):"0"+hour;
            shour.setText(h);
            shour.setTextColor(Color.BLACK);
            sminute.setText(min);
            sminute.setTextColor(Color.BLACK);
        }

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**wants notification visual**/
                if (switcher.isChecked()) {
                    txtSet.setTextColor(Color.BLACK);
                    shour.setTextColor(Color.BLACK);
                    shour.setEnabled(true);
                    shour.setInputType(InputType.TYPE_CLASS_NUMBER);
                    sminute.setTextColor(Color.BLACK);
                    sminute.setEnabled(true);
                    sminute.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else{
                    txtSet.setTextColor(Color.GRAY);
                    shour.setEnabled(false);
                    sminute.setEnabled(false);
                    shour.setInputType(InputType.TYPE_NULL);
                    shour.setTextColor(Color.BLACK);
                    sminute.setInputType(InputType.TYPE_NULL);
                    sminute.setTextColor(Color.BLACK);

                }
            }
        });


        new AlertDialog.Builder(MainActivity.this)
                .setView(note_view)
                .setTitle("Notifications")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**wants notification**/
                        if(switcher.isChecked()) {
                            notifactions=true;
                            try {
                                hour = Integer.parseInt(shour.getText().toString());
                                minute = Integer.parseInt(sminute.getText().toString());
                                if (hour > 23 || hour < 0 || minute < 0 || minute > 59 || (hour == 0 && minute == 0)) {
                                    hour = 10;
                                    minute = 0;
                                }
                                setAlarm();

                            } catch (Exception e) { }
                            String h = "", min= "";
                            min = minute>9? String.valueOf(minute):"0"+minute;
                            h = hour>9? String.valueOf(hour):"0"+hour;

                            Toast.makeText(MainActivity.this, "Alarm set to " + h + ":" + min, Toast.LENGTH_LONG).show();
                        }
                        /**doesn't want notification**/
                        else{
                            notifactions=false;
                            cancelAllAlarms();
                            hour =0;
                            minute=0;
                            Toast.makeText(MainActivity.this, "Alarm cancelled", Toast.LENGTH_LONG).show();
                        }
                        //update database
                        alarmHandler.update(notifactions, hour,minute);
                    }
                }).show();
    }

    /**
     * Load plants.
     */
    public void loadPlants() {
        plants = readPlants();
        if (!plants.isEmpty()) {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.plant_hldr));
            imageButton.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setBackgroundResource(R.drawable.background);
            imageButton.setVisibility(View.VISIBLE);
        }
        plantAdapter = new PlantAdapter(plants, this, new PlantAdapter.ItemClicked() {
            //clicked edit plant
            @Override
            public void onClick(int position, View view) {
                editPlant(plants.get(position).getId(), view);
            }
            //clicked water plant
            public void onClickWater(int position, View view) {
                Date today = new Date();
                String selected = plants.get(position).getNextIrr();
                Date selectD;
                try{
                    selectD = f.parse(selected);
                    if (today.after(selectD) || !today.before(selectD) ) {
                        waterPlant(plants.get(position).getId(), view);
                    }
                }catch (ParseException e){}
            }
        });
        recyclerView.setAdapter(plantAdapter);
    }

    /**
     * Water plant handles when plant needs to be watered- moves to WaterPlant class
     * @param plantId
     * @param view
     */
    private void waterPlant(int plantId, View view) {
        PlantHandler plantHandler = new PlantHandler(this);
        Plant plant = plantHandler.ReadingSinglePlant(plantId);
        Intent intent = new Intent(this, WaterPlant.class);

        intent.putExtra("name", plant.getName());
        intent.putExtra("description", plant.getDescription());
        intent.putExtra("id", plant.getId());
        intent.putExtra("irrigation", plant.getIrrigation());
        intent.putExtra("date", plant.getPlant_birthdate());
        intent.putExtra("family", plant.getFamily());
        intent.putExtra("next", plant.getNextIrr());
        intent.putExtra("image", plant.getImage());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
        startActivityForResult(intent, 1, optionsCompat.toBundle());
    }

    /**
     * Edit plant handles when plant is edited- moves to EditPlant class
     * @param plantId
     * @param view
     */
    private void editPlant(int plantId, View view) {
        PlantHandler plantHandler = new PlantHandler(this);
        Plant plant = plantHandler.ReadingSinglePlant(plantId);

        Intent intent = new Intent(this, EditPlant.class);
        intent.putExtra("name", plant.getName());
        intent.putExtra("description", plant.getDescription());
        intent.putExtra("id", plant.getId());
        intent.putExtra("irrigation", plant.getIrrigation());
        intent.putExtra("date", plant.getPlant_birthdate());
        intent.putExtra("family", plant.getFamily());
        intent.putExtra("next", plant.getNextIrr());
        intent.putExtra("image", plant.getImage());

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
        startActivityForResult(intent, 1, optionsCompat.toBundle());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                loadPlants();
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    File file = new File(currentPhotoPath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = null;
                    while (bitmap == null) {
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {}
                    break;
                }
        }
    }

    /**
     * get permission to use camera
     * using takePic
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                takePic();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * takes picture and saves file
     * using createImageFile
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void takePic() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile(id_img);
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

        String filename_id = "filename_" + filename;
        String directory = Environment.DIRECTORY_PICTURES;
        File storageDir = getExternalFilesDir(directory);
        File image = File.createTempFile(
                filename_id,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}