package com.example.myPlants;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * The type PlantHandler. Handles Database to save plant details.
 * CRUD  C create, R read, U update, D delete
 */
public class PlantHandler extends DatabaseHelper {

    /**
     * Instantiates a new Plant handler.
     *
     * @param context the context
     */
    public PlantHandler(Context context) {
        super(context);
    }

    /**
     * Create database, return true if successful..
     *
     * @param plant the plant
     * @return isf Successful
     */
    public boolean create(Plant plant) {
        ContentValues values = new ContentValues();
        values.put("name", plant.getName());
        values.put("description", plant.getDescription());
        values.put("irrigation", plant.getIrrigation());
        values.put("date", plant.getPlant_birthdate());
        values.put("family", plant.getFamily());
        values.put("next",plant.getNextIrr());
        values.put("image",plant.getImage());

        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.insert("Plant", null, values) > 0;
        db.close();
        return isSuccessfull;
    }

    /**
     * Read plants from database to arraylist.
     *
     * @return arraylist of plants
     */
    public ArrayList<Plant> readPlants() {
        ArrayList<Plant> plants = new ArrayList<>();

        String sqlQuery = "SELECT * FROM Plant ORDER BY id ASC";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                int irrigation = cursor.getInt(cursor.getColumnIndex("irrigation"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String family = cursor.getString(cursor.getColumnIndex("family"));
                String next = cursor.getString(cursor.getColumnIndex("next"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                Plant plant = new Plant(name,family, description,irrigation, date, next,image);
                plant.setId(id);
                plants.add(plant);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return plants;
    }

    /**
     * Reading single plant from database by id.
     *
     * @param id the id of specific plant
     * @return the plant
     */
    public Plant ReadingSinglePlant(int id) {
        Plant plant = null;
        String sqlQuery = "SELECT * FROM Plant WHERE id=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            int plantId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String family = cursor.getString(cursor.getColumnIndex("family"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            int irrigation = cursor.getInt(cursor.getColumnIndex("irrigation"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String next = cursor.getString(cursor.getColumnIndex("next"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            plant = new Plant(name, family, description, irrigation, date,next,image);
            plant.setId(plantId);
        }
        cursor.close();
        db.close();
        return plant;
    }

    /**
     * Update plant.
     *
     * @param plant the plant to update
     * @return if Successful
     */
    public boolean update(Plant plant) {

        ContentValues values = new ContentValues();
        values.put("name", plant.getName());
        values.put("family", plant.getFamily());
        values.put("description", plant.getDescription());
        values.put("irrigation", plant.getIrrigation());
        values.put("date", plant.getPlant_birthdate());
        values.put("next", plant.getNextIrr());
        values.put("image", plant.getImage());
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.update("Plant", values, "id='" + plant.getId() + "'", null) > 0;
        db.close();
        return isSuccessfull;
    }

    /**
     * Delete plant by id.
     *
     * @param id the id
     */
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Plant", "id='" + id + "'", null);
        db.close();
    }


    /**
     * Gets all next irrigation dates from database to set alarms.
     *
     * @return the alarms
     */
    public HashSet<Date> getAlarms() {
        HashSet<Date> hashSet = new HashSet<>();
        String sqlQuery = "SELECT * FROM Plant ORDER BY id ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        String d;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        if (cursor.moveToFirst()) {
            d = cursor.getString(cursor.getColumnIndex("next"));
            Calendar calendar=Calendar.getInstance();
            try {
                Date al = f.parse(d);
                calendar.setTime(al);
                calendar.set(Calendar.HOUR_OF_DAY, 23);//end of day
                al=calendar.getTime();
                hashSet.add(al);

            } catch (ParseException e) {}

        while (cursor.moveToNext()) {
            d = cursor.getString(cursor.getColumnIndex("next"));
            try {
                Date al = f.parse(d);
                calendar.setTime(al);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                al=calendar.getTime();
                hashSet.add(al);
            } catch (ParseException e) { }
        }
    }
        cursor.close();
        db.close();
        return hashSet;
    }
}
