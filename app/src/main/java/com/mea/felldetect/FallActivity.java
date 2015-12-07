package com.mea.felldetect;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FallActivity extends ActionBarActivity {
int count = 0;
    TextView text,haveText,secText;
    Button stopAlarm;
    CountDownTimer countDownTimer;
    boolean workingPreg=false;
    PreferensHandler pref;
    ImageView smile;
    TextView txtcan;
    int seconds;
    DbHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall);
        android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cc0000")));
        db = new DbHelper(getApplicationContext());

        text = (TextView) findViewById(R.id.countr);
        haveText = (TextView) findViewById(R.id.have);
        secText = (TextView) findViewById(R.id.secnd);
        smile = (ImageView) findViewById(R.id.smile);
         txtcan = (TextView) findViewById(R.id.smltxt);
        pref = new PreferensHandler(getApplicationContext());
        Toast.makeText(getApplicationContext(),"the loc is = "+pref.getLocationAddress(),Toast.LENGTH_LONG).show();
        stopAlarm = (Button) findViewById(R.id.stopa);
        workingPreg = true;

        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alm);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.start();
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mediaPlayer.stop();
                //countDownTimer.onFinish();
                countDownTimer.cancel();

                //text.setTextSize(45);
                //text.setText("Alert Cancelled");
                hideView();
                workingPreg = false;
                stopAlarm.setVisibility(View.INVISIBLE);
                smile.setVisibility(View.VISIBLE);
                txtcan.setVisibility(View.VISIBLE);

            }
        });
seconds = pref.getSec()*1000;
        countDownTimer =  new CountDownTimer(seconds, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
                text.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
               // text.setTextSize(45);
                //text.setText("Message Sent!");
                //mediaPlayer.stop();
                hideView();
                smile.setImageResource(R.drawable.tik);
                txtcan.setText("Message Send! ");
                smile.setVisibility(View.VISIBLE);
                txtcan.setVisibility(View.VISIBLE);

                stopAlarm.setVisibility(View.INVISIBLE);
                if(pref.getSMSPref()){
                    sms();
                }

            }
        }
                .start();


        /*Timer T;


        T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

//                text.setText(count);
                count++;
                if(count>30){

                    //sendsms();
                }if(count<=30){
                    text.setText(30-count+"");
                }
            }
        }, 1000, 1000);

    }*/

    }


    @Override
    protected void onPause() {
        super.onPause();
        if(workingPreg){

        }else{
            finish();
        }

    }

    public void hideView(){
    haveText.setVisibility(View.INVISIBLE);
    secText.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
}
    public void sms(){
        SmsManager smsManager = SmsManager.getDefault();

        List<Contacts> contactsList = db.getAllContacts();

        for(int i=0;i<contactsList.size();i++){

            String num =  contactsList.get(i)._phone_number;
            smsManager.sendTextMessage(num,null," ALERT!! \n Fall Detected  at "+pref.getLocationAddress(),null,null);
            Toast.makeText(getApplicationContext(),num +" sms send  content: \n "+"ALERT!! \nn Fall Detected  at "+pref.getLocationAddress(),Toast.LENGTH_LONG).show();
        }



        //smsManager.sendTextMessage(pref.getPhone(),null," ALERT!! \n Fall Detected  at "+pref.getLocationAddress(),null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
