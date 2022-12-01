package com.example.rcs_taci_2.Dataacces;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataaccesUserOffline {
    myDbHelper helper;

    public DataaccesUserOffline(Context context)
    {
        helper = new DataaccesUserOffline.myDbHelper(context);
    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "rcs_taci.db";
        private static final int DATABASE_Version = 1;
        private static final String UID="_id";
        private static final String NAME = "Name";
        private static final String PASSWORD= "Password";

        // private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
            // Message.message(context,"Started...");
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                String login = "";
                login = "CREATE TABLE IF NOT EXISTS SATO_USER(UserID varchar(20),UserName varchar(50),Password varchar(50) ,Authority varchar(50) ,Blokir varchar(1))";
                db.execSQL(login);
                //Message.message(context,"TABLE CREATED");
            } catch (Exception e) {
                Log.e("Error",e.toString());
            }finally {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                //create table again
                onCreate(db);
            }catch (Exception e) {
                // Message.message(context,""+e);
            }
        }

    }
    public void CreateTable(){
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String login = "";
            login = "CREATE TABLE IF NOT EXISTS SATO_USER(UserID varchar(20),UserName varchar(50),Password varchar(50) ,Authority varchar(50) ,Blokir varchar(1))";
            db.execSQL(login);
            //Message.message(context,"TABLE CREATED");
        } catch (Exception e) {
            Log.e("Error",e.toString());
        }finally {
        }
    }
    public Cursor GetDataLogin(String UserID){
        try{
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String query = "Select * from SATO_USER where UserID='" + UserID + "'";
            cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            return cursor;
        }catch (Exception e){
            DataaccesUser.Error = e.toString();
            Log.e("Error",e.toString());
            return  null    ;
        }
    }
    public String DeleteDataUser(){
        try{
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("Delete From Sato_User;");
            return  "OK";
        }catch (Exception e){
            return  "Error : " + e.toString();
        }
    }
    public String InsertDataUser(String UserID,String UserName,String Authority,String Blokir,String Password){
        try{
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("INSERT INTO SATO_USER (UserID,UserName,Authority,Blokir,PASSWORD) VALUES('" + UserID +"','" + UserName + "','" + Authority + "'," +
                    "'" + Blokir + "','" + Password + "')");
            return  "OK";
        }catch (Exception e){
            return  "Error : " + e.toString();
        }
    }
}
