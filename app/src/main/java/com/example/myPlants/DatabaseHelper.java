package com.example.myPlants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The type Database for Plants.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "PlantDatabase9";

    /**
     * Instantiates a new Database for Plants.
     *
     * @param context the context
     */
    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE Plant ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, family TEXT, description TEXT,  irrigation INTEGER, date TEXT, next TEXT, image TEXT)";
        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sqlQuery = "DROP TABLE IF EXISTS Plant";
        sqLiteDatabase.execSQL(sqlQuery);
        onCreate(sqLiteDatabase);
    }
}
