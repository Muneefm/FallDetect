package com.mea.felldetect;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service implements SensorEventListener{

    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized,min,max;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 2.0;
    int count = 0;
    int i;
    PreferensHandler pref;
    Context c;
    PowerManager mgr;
    PowerManager.WakeLock wakeLock;
//   public AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
    public MyService(){

    }

    public MyService(Context context ) {

    this.c = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        pref = new PreferensHandler(getApplicationContext());
         mgr = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
         wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double gvt = SensorManager.STANDARD_GRAVITY;
            float vals[] = event.values;
            //int sensor=arg0.sensor.getType();
            double xx = event.values[0];
            double yy = event.values[1];
            double zz = event.values[2];
            double aaa = Math.round(Math.sqrt(Math.pow(xx, 2)
                    + Math.pow(yy, 2)
                    + Math.pow(zz, 2)));
            //Log.e("this is log", "value of aaa is  " + aaa);

            if (aaa <= 1.0) {
                min = true;
            }

            if (min == true) {
                //Toast.makeText(getApplicationContext(),"min true   " ,Toast.LENGTH_SHORT).show();
                i++;
                if (aaa >= 10) {
                    //  Toast.makeText(getApplicationContext(),"max true   " ,Toast.LENGTH_SHORT).show();
                    max = true;
                }
            }

            if (min == true && max == true) {
                wakeLock.acquire();
                if(pref.getOption()) {
                    Toast.makeText(getApplicationContext(), "FALL DETECTED!!!!!", Toast.LENGTH_LONG).show();
                    MediaPlayer player = MediaPlayer.create(this,
                            Settings.System.DEFAULT_RINGTONE_URI);
                    //player.start();
                    Notification(getApplicationContext(), "Press this");
                    Intent strt = new Intent(MyService.this, FallActivity.class);
                    strt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(strt);
                }
                i=0;
                min=false;
                max=false;
            }

            if (i>4) {
                i=0;
                min=false;
                max=false;
            }
        }



    }






    public void Notification(final Context context, String message) {
        // Set Notification Title
        String strtitle = "Title  ";
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, MainActivity.class);
        Intent callBack = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(message));

        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", message);
        // Open NotificationView.java Activity
       /* PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendint = PendingIntent.getActivity(context, 0, callBack,
                PendingIntent.FLAG_UPDATE_CURRENT);
*/

        // Create Notification using NotificationCompat.Builder
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_launcher)
                        // Set Ticker Message
                .setTicker("Fall Detected!!")
                        // Set Title
                .setContentTitle("Fall Alert! ")
                        // Set Text
                .setContentText("You have 30 seconds to cancel the alarm")
                        // Add an Action Button below Notification
                //.addAction(R.drawable.ic_launcher, "", pendint)
                        // Set PendingIntent into Notification
               // .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);
        final NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(1, builder.build());


       /* Timer T;
        T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                //text.setText(count);
                count++;

                if(count>30){
//                    sendsms();

                }

                if(count<=30){

                    builder.setContentTitle("Fall Alert!!  "+(30-count) +" seconds to cancel");
                    notificationmanager.notify(1, builder.build());
                }
            }
        }, 1000, 1000);*/


        // Create Notification Manager


    }

}
