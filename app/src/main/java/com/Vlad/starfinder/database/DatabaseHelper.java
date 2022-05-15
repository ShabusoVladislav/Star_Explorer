package com.Vlad.starfinder.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.Vlad.starfinder/databases/";
    private static final String DB_NAME = "stars.db";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "stars";

    private Context myContext;


    public DatabaseHelper(@Nullable Context context ) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public void createDB(){
        boolean dbExist = checkDataBase();
        if(!dbExist){
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                //Открываем локальную БД как входящий поток
                InputStream myInput = myContext.getAssets().open(DB_NAME);

                //Путь ко вновь созданной БД
                String outFileName = DB_PATH + DB_NAME;

                //Открываем пустую базу данных как исходящий поток
                OutputStream myOutput = new FileOutputStream(outFileName);

                //перемещаем байты из входящего файла в исходящий
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer))>0){
                    myOutput.write(buffer, 0, length);
                }

                //закрываем потоки
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public SQLiteDatabase open()throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

}
