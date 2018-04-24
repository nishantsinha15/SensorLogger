package in.nishant.sensorlogger;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.log10;


public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    SensorManager sm;
    Sensor mGyroscope ,mAccelerometer;
    MyDatabase db;
    WifiManager wifiManager;
    TelephonyManager telephonyManager;
    MediaRecorder mediaRecorder;
    LocationManager locationManager;
    TextView mic, acc, gyro, gps, wifi, cell;
    double longitude;
    double latitude;
    int flag = -1;
    int clicked = 1;
    String volume;
    boolean isRecording()
    {
        boolean ret = (flag == 0)?true:false;
        return ret;
    }
    public void startSensing( View view ) throws IOException // Handle all the cases separately
    {
            Log.d("Nishant", "Clicked");
            flag = 0;
            clicked = 0;
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("dev/null");
            mediaRecorder.prepare();
            mediaRecorder.start();
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        Double max;
                        if( mediaRecorder != null )
                        {
                        max = 20 * log10(mediaRecorder.getMaxAmplitude());
                        if( flag == 0 ) {
//                            Log.d("Nishant", "Microphone " + String.valueOf(max));
//                            db.insert_in_microphone(String.valueOf(max));
                            volume = String.valueOf(max);
                        }
                    }
                }
            }, 0, 1000);

            List<CellInfo> cell = null;
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    cell = telephonyManager.getAllCellInfo();
                    String ret = "";
                    for (int i = 0; i < cell.size(); i++) {
                        if( flag == 0 ) {
//                            db.insert_in_cell(String.valueOf(cell.get(i).toString()));
                            ret = ret + " " + String.valueOf(cell.get(i).toString());
                            Log.d("Nishant", "Cell Info " + cell.get(i).toString());
                        }
                    }
                    this.cell.setText(ret);
                }
            }

            wifiManager.startScan();
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<ScanResult> wifi1 = wifiManager.getScanResults();
                    String wifiRet = "";
                    for (int i = 0; i < wifi1.size(); i++) {
                        if( flag == 0 ) {
//                            db.insert_in_wifi(wifi1.get(i).SSID);
                            wifiRet = wifiRet + " " + wifi1.get(i).SSID;
//                            Log.d("Nishant", "WIFI " + wifi.get(i).SSID);
                        }
                    }
                    wifi.setText(wifiRet);
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void stopSensing( View view )
    {
        mediaRecorder.stop();
        mediaRecorder.release();
//        mediaRecorder = null;
        clicked = 1;
        flag = 1;
        Log.d("Nishant", "Stop");
        mic.setText("");
        acc.setText("");
        gyro.setText("");
        gps.setText("");
        wifi.setText("");
        cell.setText("");

    }

    protected void init()
    {
        db = new MyDatabase(this, "nishantSensors.db", null, 2);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mediaRecorder = new MediaRecorder();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        volume = "";
        sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mic = findViewById(R.id.db);
        gyro = findViewById(R.id.gy);
        acc = findViewById(R.id.acc);
        gps = findViewById(R.id.gps);
        cell = findViewById(R.id.cell);
        wifi = findViewById(R.id.wifi);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    @Override
    public void onProviderDisabled(String provider) {
    }


    public void export( View view ) throws IOException {
//        db.export_to_csv();
        Log.d("Nishant", "Exporting");
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if( flag == 0 ) {
//            db.insert_in_gps(String.valueOf(latitude), String.valueOf(longitude));
            gps.setText(String.valueOf(latitude) + " " + String.valueOf(longitude));
            Log.d("Nishant", "Location" + String.valueOf(latitude) + " " + String.valueOf(longitude));
        }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        int type = sensor.getType();
        String accx, accy,accz;
        String gx, gy, gz;
        if( type == Sensor.TYPE_ACCELEROMETER )
        {
            accz = String.valueOf(sensorEvent.values[2]);
            accy = String.valueOf(sensorEvent.values[1]);
            accx = String.valueOf(sensorEvent.values[0]);
            if( flag == 0 ) {
//                db.insert_in_accelerometer((accx), (accy), (accz));
                acc.setText(accx + " " + accy + " " + accz);
                Log.d("Nishant", " Accelorometer " + accx + " " + accy + " " + accz);
            }
        }
        else if( type == Sensor.TYPE_GYROSCOPE )
        {
            gz= String.valueOf(sensorEvent.values[2]);
            gy= String.valueOf(sensorEvent.values[1]);
            gx= String.valueOf(sensorEvent.values[0]);
            if( flag == 0 ) {
//                db.insert_in_gyroscope((gx), (gy), (gz));
                gyro.setText(gx + "- " + gy + " -" + gz);
                Log.d("Nishant", " Gyroscope " + gx + "- " + gy + " -" + gz);
                mic.setText(volume);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
