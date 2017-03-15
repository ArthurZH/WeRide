package com.example.deanwen.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchang on 8/1/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "recordsManager";

    // Contacts table name
    private static final String TABLE_RECORDS = "records";

    // Contacts Table Columns names
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";

    public DatabaseHandler(Context context){
        super(context, TABLE_RECORDS, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_EMAIL + " TEXT PRIMARY KEY, " + KEY_NAME + " TEXT, "
                + KEY_START_TIME + " TEXT, " + KEY_END_TIME + " TEXT " + ")";
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

        // Create tables again
        onCreate(db);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                    + KEY_EMAIL + " TEXT PRIMARY KEY, " + KEY_NAME + " TEXT, "
                    + KEY_START_TIME + " TEXT, " + KEY_END_TIME + " TEXT " + ")";
            db.execSQL(CREATE_RECORDS_TABLE);
        } catch (Exception e) {

        }
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        } catch (Exception e) {
            
        }
    }

    public void addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, record.get_email());
        values.put(KEY_NAME, record.get_name());
        values.put(KEY_START_TIME, record.get_start_time());
        values.put(KEY_END_TIME, record.get_end_time());

        try {
            db.insert(TABLE_RECORDS, null, values);
        } catch (Exception e) {
            updateRecord(record);
        }
        db.close();
    }

    public Record getRecord(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECORDS, new String[]{
                        KEY_EMAIL,
                        KEY_NAME,
                        KEY_START_TIME,
                        KEY_END_TIME
                },
                KEY_EMAIL + "=?",
                new String[]{
                        email
                },
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Record record = new Record(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return record;
    }

    public List<Record> getAllRecords() {
        List<Record> recordList = new ArrayList<Record>();

        String query = "SELECT * FROM " + TABLE_RECORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.set_email(cursor.getString(0));
                record.set_name(cursor.getString(1));
                record.set_start_time(cursor.getString(2));
                record.set_end_time(cursor.getString(3));

                recordList.add(record);
            } while(cursor.moveToNext());
        }

        return recordList;
    }

    public int updateRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, record.get_name());
        values.put(KEY_START_TIME, record.get_start_time());
        values.put(KEY_END_TIME, record.get_end_time());

        return db.update(TABLE_RECORDS, values, KEY_EMAIL + " = ? ", new String[] {
                record.get_email()
        });
    }
}
