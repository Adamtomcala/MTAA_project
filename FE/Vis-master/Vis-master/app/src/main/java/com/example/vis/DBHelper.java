package com.example.vis;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBName = "MyDatabase.db";

    public static final String TableName = "Message";
    public static final String Page = "page";
    public static final String Title = "title";
    public static final String Sender = "sender";
    public static final String Time = "time";
    public static final String Ctx = "ctx";

    public static final String TableName2 = "Classroom";
    public static final String Name = "name";
    public static final String Subject = "subject";
    public static final String Owner = "owner";
    public static final String Students = "students";
    public static final String Materials = "materials";
    public DBHelper(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Message" +
                "(id integer PRIMARY KEY, page integer, title text, sender text, time text, ctx text)");
        sqLiteDatabase.execSQL("CREATE TABLE Classroom" +
                "(id integer PRIMARY KEY, name text, subject text, owner text, students text, materials text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Message");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Classroom");
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public boolean insertDataMessage(int page, String title, String sender, String time, String ctx){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor crs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM Message WHERE sender=? AND time=? AND ctx=?) as isIn",
                new String[] {sender, time, ctx});

        crs.moveToFirst();
        if(Integer.parseInt(crs.getString(crs.getColumnIndex("isIn"))) == 1){
            return true;
        }

        ContentValues cv = new ContentValues();
        cv.put(Page, page);
        cv.put(Title, title);
        cv.put(Sender, sender);
        cv.put(Time, time);
        cv.put(Ctx, ctx);

        db.insert(TableName, null, cv);

        return true;
    }

    @SuppressLint("Range")
    public boolean insertDataClassroom(String name, String subject, String owner, String students, String materials){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor crs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM Classroom WHERE name=? AND subject=? AND owner=? AND students=? AND materials=?) as isIn",
                new String[] {name, subject, owner, students, materials});

        crs.moveToFirst();
        if(Integer.parseInt(crs.getString(crs.getColumnIndex("isIn"))) == 1){
            return true;
        }


        ContentValues cv = new ContentValues();
        cv.put(Name, name);
        cv.put(Subject, subject);
        cv.put(Owner, owner);
        cv.put(Students, students);
        cv.put(Materials, materials);

        db.insert(TableName2, null, cv);

        return true;
    }

    @SuppressLint("Range")
    public ArrayList<MessageModel> getPage(){
        ArrayList<MessageModel> arrModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor crs = db.rawQuery("SELECT * FROM Message",null);

        crs.moveToFirst();

        while(crs.isAfterLast() == false){
            MessageModel msgModel = new MessageModel();
            msgModel.setPage(crs.getString(crs.getColumnIndex(Page)));
            msgModel.setTitle(crs.getString(crs.getColumnIndex(Title)));
            msgModel.setSender(crs.getString(crs.getColumnIndex(Sender)));
            msgModel.setTime(crs.getString(crs.getColumnIndex(Time)));
            msgModel.setCtx(crs.getString(crs.getColumnIndex(Ctx)));

            arrModels.add(msgModel);
            crs.moveToNext();
        }

        return arrModels;
    }

    @SuppressLint("Range")
    public ArrayList<ClassroomModel> getClassrooms(){
        ArrayList<ClassroomModel> arrModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor crs = db.rawQuery("SELECT * FROM Classroom",null);

        crs.moveToFirst();

        while(crs.isAfterLast() == false){
            ClassroomModel clsModel = new ClassroomModel();
            clsModel.setName(crs.getString(crs.getColumnIndex(Name)));
            clsModel.setSubject(crs.getString(crs.getColumnIndex(Subject)));
            clsModel.setOwner(crs.getString(crs.getColumnIndex(Owner)));
            clsModel.setStudents(crs.getString(crs.getColumnIndex(Students)));
            clsModel.setMaterials(crs.getString(crs.getColumnIndex(Materials)));

            arrModels.add(clsModel);
            crs.moveToNext();
        }
        return arrModels;
    }
}
