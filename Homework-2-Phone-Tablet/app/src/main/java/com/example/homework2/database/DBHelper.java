package com.example.homework2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "credentials.db";
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME
                + "(" + " _id integer primary key autoincrement, "
                + COLUMN_USERNAME + ", "
                + COLUMN_PASSWORD + ", "
                + COLUMN_EMAIL + ","
                + COLUMN_PHONE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean addNewUser (String username, String phone, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select password from users where username='"+username+"'", null );
        return res;
    }

    public Cursor getUsernames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select username from users", null );
        return res;
    }

}
