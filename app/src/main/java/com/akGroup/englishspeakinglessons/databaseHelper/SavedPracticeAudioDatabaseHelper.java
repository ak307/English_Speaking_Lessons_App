package com.akGroup.englishspeakinglessons.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SavedPracticeAudioDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "practice_conversation_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "conversation_url";
    private static final String COL3 = "conversation_with";
    private static final String COL4 = "saved_date_time";



    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL2 + " TEXT," +
                    COL3 + " TEXT," +
                    COL4 + " TEXT)";


    public SavedPracticeAudioDatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean addData(String conversation_url, String conversation_with, String saved_date_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, conversation_url);
        contentValues.put(COL3, conversation_with);
        contentValues.put(COL4, saved_date_time);

        Log.d(TAG, "addData: Adding to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }





    public void deleteData(String conversation_url){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "conversation_url=?", new String[]{conversation_url});
        db.close();
    }



}
