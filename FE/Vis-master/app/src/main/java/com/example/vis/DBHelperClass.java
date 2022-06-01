package com.example.vis;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelperClass extends SQLiteOpenHelper {
    public static final String DBName = "MyDatabase2.db";

    public static final String TableName = "Classroom";
    public static final String Name = "name";
    public static final String Subject = "subject";
    public static final String Owner = "owner";
    public static final String Students = "students";
    public static final String Materials = "materials";

    public DBHelperClass(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Classroom" +
                "(id integer PRIMARY KEY, name text, subject text, owner text, students text, materials text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Classroom");
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public boolean insertData(String name, String subject, String owner, String students, String materials){
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

        db.insert(TableName, null, cv);

        return true;
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
