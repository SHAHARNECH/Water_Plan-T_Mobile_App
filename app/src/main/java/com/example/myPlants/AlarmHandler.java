package com.example.myPlants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * The type Alarm handler. Handles Database to save alarm notification preferences
 */
class AlarmHandler extends DatabaseAlarm{

    public AlarmHandler(Context context) {
        super(context);
    }

    /**
     * Create database, return true if successful.
     *
     * @param notify the notify- 1 to notify 0 to cancel
     * @param hour   the hour of alarm
     * @param minute the minute of alarm
     * @return successful
     */
    public boolean create(boolean notify, int hour, int minute) {
        ContentValues values = new ContentValues();
        int note = notify? 1:0;
        values.put("notify", note);
        values.put("hour", hour);
        values.put("minute", minute);
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.insert("AppAlarm", null, values) > 0;
        db.close();
        return isSuccessfull;
    }

    /**
     * Read notification from Database
     *
     * @return the notification preferences
     */
    public Notification read() {
        int note=0, hour=0, minute=0;
        String sqlQuery = "SELECT * FROM AppAlarm ORDER BY id ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            note = Integer.parseInt(cursor.getString(cursor.getColumnIndex("notify")));
            hour = Integer.parseInt(cursor.getString(cursor.getColumnIndex("hour")));
            minute =Integer.parseInt(cursor.getString(cursor.getColumnIndex("minute")));
            cursor.close();
            db.close();
        }
        return new Notification(note,hour,minute);
    }

    /**
     * Update database.
     *
     * @param notify the notify- 1 to notify 0 to cancel
     * @param hour   the hour of alarm
     * @param minute the minute of alarm
     * @return successful
     */
    public boolean update(boolean notify, int hour, int minute) {

        ContentValues values = new ContentValues();
        int note = notify? 1:0;
        values.put("notify", note);
        values.put("hour", hour);
        values.put("minute", minute);
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.update("AppAlarm", values,"id='1'" , null) > 0;
        db.close();
        return isSuccessfull;
    }

}