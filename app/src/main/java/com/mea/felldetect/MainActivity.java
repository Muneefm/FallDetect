package com.mea.felldetect;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorEventListener;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized,min,max;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 2.0;
    Switch aSwitch;
    int count = 0;
    int i;
    PreferensHandler pref;
    EditText seconds;
    Button num;
    Button save;
    CheckBox smsCh;
    GoogleApiClient googleApiClient;
    protected LocationManager locationManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new PreferensHandler(getApplicationContext());
        smsCh = (CheckBox) findViewById(R.id.checkBox);
        smsCh.setChecked(pref.getSMSPref());
        save = (Button) findViewById(R.id.saveNum);
        num = (Button) findViewById(R.id.edit);
        seconds = (EditText) findViewById(R.id.secedit);

        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(MainActivity.this,ContactActivity.class);
                startActivity(add);
            }
        });

//            seconds.setText(pref.getSec());

       // AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

        aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setChecked(pref.getOption());
        android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0091EA")));
        //startService(new Intent(getBaseContext(), MyService.class));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        if(pref.getOption()){
            startService(new Intent(getBaseContext(), MyService.class));
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             pref.setServicePref(isChecked);
                if(pref.getOption()){
                    startService(new Intent(getBaseContext(), MyService.class));
                }else {
                    stopService(new Intent(getBaseContext(),MyService.class));

                }
            }
        });
save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        pref.setSMSPref(smsCh.isChecked());
       pref.setSec(Integer.parseInt(seconds.getText().toString()));
        Toast.makeText(getApplicationContext(),"Saved Succesfully!.",Toast.LENGTH_LONG).show();
    }
});




    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }









    private void sendsms() {
        Geocoder gCoder = new Geocoder(MainActivity.this);
        ArrayList<Address> addresses = null;
        LatLng k;
        k = getLocation();
        try {
            addresses = (ArrayList<Address>) gCoder.getFromLocation(k.lat, k.lon, 1);
//9895940989
            SmsManager.getDefault().sendTextMessage("+918113946638", null, addresses.get(0).getCountryName()+addresses.get(0).getAddressLine(0)+addresses.get(0).getAddressLine(1)
                    , null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        (new GetAddressTask(this)).execute(location);

        //Toast.makeText(this,location.getLatitude()+" jhj" ,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {
       // Toast.makeText(this," Conected!!  ", Toast.LENGTH_LONG).show();




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class GetAddressTask extends
            AsyncTask<Location, Void, String> {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }
        //   ...
        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @params params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list

            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;




            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text


                //add = addressText;
//                Toast.makeText(getApplicationContext(),"succes man",Toast.LENGTH_LONG).show();
                pref.setLocationAddress(addressText);
                return addressText;
            } else {
                return "No address found";
            }
        }
        // ...
    }



public class LatLng{
        Double lat,lon;
        public LatLng(Double lat,Double lng) {
            this.lat = lat;
            this.lon = lng;
        }
    }
    public LatLng getLocation()
    {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

}
       /* TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
        ImageView iv = (ImageView)findViewById(R.id.image);
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        Log.e("this is log", "obj is " + x);


        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));
            Toast.makeText(getApplicationContext()," deltax"+deltaX,Toast.LENGTH_LONG);

            if(deltaX >40){

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_dialog);
                dialog.setTitle("Title...");

                Timer T;
                // set the custom dialog components - text, image and button
                final TextView text = (TextView) dialog.findViewById(R.id.text);

                T=new Timer();
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        text.setText(count);
                        count++;
                        if(count>30){
                            sendsms();
                        }
                    }
                }, 1000, 1000);


                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }




            iv.setVisibility(View.VISIBLE);
            if (deltaX > deltaY) {
                iv.setImageResource(R.drawable.horizontal);
            } else if (deltaY > deltaX) {
                iv.setImageResource(R.drawable.vertical);
            } else {
                iv.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void sendsms() {
        Geocoder gCoder = new Geocoder(MainActivity.this);
        ArrayList<Address> addresses = null;
        LatLng k;
        k = getLocation();
        try {
            addresses = (ArrayList<Address>) gCoder.getFromLocation(k.lat, k.lon, 1);

        SmsManager.getDefault().sendTextMessage("9895940989", null, addresses.get(0).getCountryName()+addresses.get(0).getAddressLine(0)+addresses.get(0).getAddressLine(1)
                , null, null);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

public class LatLng{
    Double lat,lon;
    public LatLng(Double lat,Double lng) {
        this.lat = lat;
        this.lon = lng;
    }
}
    public LatLng getLocation()
    {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }
}
       */
