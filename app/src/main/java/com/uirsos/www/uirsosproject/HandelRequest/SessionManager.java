package com.uirsos.www.uirsosproject.HandelRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by cunun12
 */

public class SessionManager {

    public static final String PREF_NAME = "preferen";
    public static final String KEY_NPM = "npmuser";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_RESPONSE = "response";
    public static Context mCtx;
    private static SessionManager mInstance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        mCtx = context;
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    public void Response(String Response) {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString(KEY_RESPONSE, Response);
        editor.commit();
    }

    public String getKeyResponse() {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_RESPONSE, null);

    }

    public boolean userLogin(String npm) {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString(KEY_NPM, npm);
        editor.apply();
        return true;
    }

    public boolean register(String npm) {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString(KEY_NPM, npm);
        editor.apply();
        return true;
    }

    public boolean isLoggin() {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (pref.getString(KEY_NPM, null) != null) {
            return true;
        }
        return false;
    }

    public boolean Logout() {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.clear();
        editor.apply();
        return true;
    }


    public boolean simpanToken(String token) {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString(KEY_TOKEN, token);
        editor.apply();
        return true;
    }
//
//    public String getToken() {
//        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        return pref.getString(KEY_TOKEN, null);
//        //return KEY_TOKEN;
//    }

    public String getNPM() {
        pref = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_NPM, null);
    }

}
