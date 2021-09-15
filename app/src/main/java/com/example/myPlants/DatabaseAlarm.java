package com.example.myPlants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * The type Database for notifications.
 */
public class DatabaseAlarm extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "AppDatabase";

    /**
     * Instantiates a new Database for notifications.
     *
     * @param context the context
     */
    public DatabaseAlarm(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE AppAlarm (id INTEGER PRIMARY KEY AUTOINCREMENT, notify INTEGER, hour INTEGER, minute INTEGER)";
        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sqlQuery = "DROP TABLE IF EXISTS AppAlarm";
        sqLiteDatabase.execSQL(sqlQuery);
        onCreate(sqLiteDatabase);
    }
}
