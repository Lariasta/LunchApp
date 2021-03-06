package com.hfda.LunchApp.helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LunchDBhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HSN_LUNCH_APP";
    private static final int DB_VERSION = 1;

    public LunchDBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Laupet", "onCreate Database method was called ");

        //Creating database
        String sqlCreateUserTable = "CREATE TABLE user(" +
                " id INTEGER PRIMARY KEY, "
                + "email TEXT, "
                + "uid TEXT UNIQUE, "
                + "coffee INTEGER, "
                + "student INTEGER)";

        db.execSQL(sqlCreateUserTable);

        Log.d("Laupet", "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        Log.d("Loggg", "onUpgrade Database method was called");

        //delete database
        db.execSQL("DROP TABLE IF EXISTS user");

        //create database
        onCreate(db);
    }

    public void addUser(String email, String uid, Integer coffee, Integer student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("uid", uid);
        values.put("coffee", coffee);
        values.put("student", student);

        // Inserting Row
        long id = db.insert("user", null, values);
        db.close(); // Closing database connection

        Log.d("Laupet", "New user inserted into sqlite: " + id);
    }

    public Integer getCoffee() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT coffee from user", null);

        int coffee = 0;

        if (cursor2 != null) {
            cursor2.moveToFirst();

            coffee = cursor2.getInt(0);

            cursor2.close();
        }

        return coffee;
    }

    public String getUuid() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT uid from user", null);

        String uuid = "";

        if (cursor2 != null) {
            cursor2.moveToFirst();

            uuid = cursor2.getString(0);

            cursor2.close();
        }
        return uuid;
    }

    public boolean getStudent() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT student from user", null);

        boolean student = false;

        if (cursor2 != null) {
            cursor2.moveToFirst();

            student = cursor2.getInt(0) > 0;

            cursor2.close();
        }
        return student;
    }

    public void setCoffee(Integer coffee) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE user SET coffee=" + coffee);

        Log.d("Laupet", "Coffee changed in sqlLite");
    }

    public void setStudent(int isStudent) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE user SET student=" + isStudent);

        Log.d("Laupet", "Student status changed in sqlLite");
    }

    //deleting user from sqlLite DB. this happens when the user logs out
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete("user", null, null);
        db.close();

        Log.d("Laupet", "Deleted all user info from sqlite");
    }
}