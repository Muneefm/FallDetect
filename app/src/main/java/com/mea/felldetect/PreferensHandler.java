package com.mea.felldetect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Muneef on 19/04/15.
 */
public class PreferensHandler {

    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context c;
    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "settings_pref";
    final String searchPref= "count";
    final String loc = "loc";
    final String phon = "sd";
    final String sms = "sa";
    final String sec = "sec";
    @SuppressLint("CommitPrefEdits")
    public PreferensHandler(Context context) {
        this.c = context;
        pref = c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setServicePref(Boolean count ){
        editor.putBoolean(searchPref, count);
        editor.commit();
    }


    public Boolean getOption(){
        return pref.getBoolean(searchPref, false);
    }

    public void setPhone(String count ){
        editor.putString(phon, count);
        editor.commit();
    }
    public String getPhone(){
        return pref.getString(phon, "");
    }

    public void setSMSPref(Boolean count ){
        editor.putBoolean(sms, count);
        editor.commit();
    }
    public Boolean getSMSPref(){
        return pref.getBoolean(sms, false);
    }


    public void setSec(int count ){
        editor.putInt(sec, count);
        editor.commit();
    }
    public int getSec(){
        return pref.getInt(sec, 30);
    }



    public void setLocationAddress(String count ){
        editor.putString(loc, count);
        editor.commit();
    }
    public String getLocationAddress(){
        return pref.getString(loc, "");
    }


}
