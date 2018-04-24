package in.nishant.sensorlogger;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.*;
import java.text.*;
import java.util.*;

public class MyDatabase extends SQLiteOpenHelper {
    File exportDir, fmicrophone, faccelerometer, fgyroscope, fgps, fwifi;
    Cursor cursoRmicrophone, cursoRwifi, cursoRgps, cursoRgyro, cursoRacc;
//    File fmicrophone = new File(exportDir, "microphone.csv");
//    File faccelerometer = new File(exportDir, "accelerometer.csv");
//    File fgyroscope = new File(exportDir, "gyroscope.csv");
//    File fgps = new File(exportDir, "gps.csv");
//    File fwifi = new File(exportDir, "wifi.csv");

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table gyroscope (_id integer primary key, gyro_x text, gyro_y text, gyro_z text, timestamp text)");
        db.execSQL("create table  gps  (_id integer primary key, latitude text, longitude text, timestamp text)");
        db.execSQL("create table  wifi  (_id integer primary key, SSID text, timestamp text)");
        db.execSQL("create table  microphone  (_id integer primary key, microphone text, timestamp text)");
        db.execSQL("create table  cell  (_id integer primary key, details text, timestamp text)");
        db.execSQL("create table accelerometer  (_id integer primary key, acc_x text, acc_y text, acc_z text, timestamp text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void initFiles(File fmicrophone, File faccelerometer, File fgyroscope, File fgps, File fwifi) throws IOException {
        fmicrophone.createNewFile();
        fwifi.createNewFile();
        fgps.createNewFile();
        fgyroscope.createNewFile();
        faccelerometer.createNewFile();
    }

    void setUpMicrophone() throws IOException {
        fmicrophone = new File(exportDir, "microphone.csv");
        cursoRmicrophone = db.rawQuery("SELECT * FROM microphone",null);
        CSVWriter csvWrite_microphone = new CSVWriter(new FileWriter(fmicrophone));
        csvWrite_microphone.writeNext(cursoRmicrophone.getColumnNames());
        while(cursoRmicrophone.moveToNext())
        {
            String arrStr[] ={ cursoRwifi.getString(0),cursoRwifi.getString(1),cursoRwifi.getString(2) };
            csvWrite_microphone.writeNext(arrStr);
        }
        csvWrite_microphone.close();
        cursoRmicrophone.close();
    }

    void setUpAccelerometer() throws IOException {
        faccelerometer = new File(exportDir, "accelerometer.csv");
        cursoRacc = db.rawQuery("SELECT * FROM accelerometer",null);
        CSVWriter csvWrite_acc = new CSVWriter(new FileWriter(faccelerometer));
        csvWrite_acc.writeNext(cursoRacc.getColumnNames());
        while(cursoRacc.moveToNext())
        {
            String arrStr[] ={cursoRacc.getString(0),cursoRacc.getString(1),cursoRacc.getString(2),cursoRacc.getString(3),cursoRacc.getString(4)};
            csvWrite_acc.writeNext(arrStr);
        }
        csvWrite_acc.close();
        cursoRacc.close();
    }

    void setUpGyroscope() throws IOException {
        fgyroscope = new File(exportDir, "gyroscope.csv");
        cursoRgyro = db.rawQuery("SELECT * FROM gyroscope",null);
        CSVWriter csvWrite_gyro = new CSVWriter(new FileWriter(fgyroscope));
        csvWrite_gyro.writeNext(cursoRgyro.getColumnNames());
        while(cursoRgyro.moveToNext())
        {
            String arrStr[] ={ cursoRgyro.getString(0),cursoRgyro.getString(1),cursoRgyro.getString(2),cursoRgyro.getString(3),cursoRgyro.getString(4)};
            csvWrite_gyro.writeNext(arrStr);
        }
        csvWrite_gyro.close();
        cursoRgyro.close();
    }

    void setUpWifi() throws IOException {
        fwifi = new File(exportDir, "wifi.csv");
        cursoRwifi = db.rawQuery("SELECT * FROM wifi",null);
        CSVWriter csvWrite_wifi = new CSVWriter(new FileWriter(fwifi));
        csvWrite_wifi.writeNext(cursoRwifi.getColumnNames());
        while(cursoRwifi.moveToNext())
        {
            String arrStr[] ={ cursoRwifi.getString(0),cursoRwifi.getString(1),cursoRwifi.getString(2) };
            csvWrite_wifi.writeNext(arrStr);
        }
        csvWrite_wifi.close();
        cursoRwifi.close();

    }

    void setUpGps() throws IOException {
        fgps = new File(exportDir, "gps.csv");
        cursoRgps = db.rawQuery("SELECT * FROM gps",null);
        CSVWriter csvWrite_gps = new CSVWriter(new FileWriter(fgps));
        csvWrite_gps.writeNext(cursoRgps.getColumnNames());
        while(cursoRgps.moveToNext())
        {
            String arrStr[] ={ cursoRgps.getString(0),cursoRgps.getString(1),cursoRgps.getString(2),cursoRgps.getString(3) };
            csvWrite_gps.writeNext(arrStr);
        }
        csvWrite_gps.close();
        cursoRgps.close();
    }

    SQLiteDatabase db;
    public void export_to_csv() throws IOException {
        db = this.getWritableDatabase();
        exportDir = new File(Environment.getExternalStorageDirectory(), "");
        initFiles(fmicrophone, faccelerometer, fgyroscope, fgps, fwifi);
        setUpAccelerometer();
        setUpGps();setUpGyroscope();setUpMicrophone();setUpWifi();
    }

    public void insert_in_microphone(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("microphone", s);
        db.insert("microphone", null, contentValues);
    }

    public void insert_in_wifi(String ssid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("SSID", ssid);
        db.insert("WIFI", null, contentValues);
    }

    public void insert_in_cell(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("details", s);
        db.insert("cell", null, contentValues);
    }

    public void insert_in_accelerometer(String accx, String accy, String accz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("acc_x", accx);
        contentValues.put("acc_y",accy);
        contentValues.put("acc_z", accz);
        db.insert("accelerometer", null, contentValues);
    }

    public void insert_in_gyroscope(String gx, String gy, String gz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("gyro_x", gx);
        contentValues.put("gyro_y",gy);
        contentValues.put("gyro_z", gz);
        db.insert("gyroscope", null, contentValues);
    }

    public void insert_in_gps(String s, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        contentValues.put("latitude", s);
        contentValues.put("longitude",s1);
        db.insert("gps", null, contentValues);
    }
}