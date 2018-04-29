package com.uirsos.www.uirsosproject.Utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.uirsos.www.uirsosproject.HandelRequest.SessionManager;


public class FirebaseTokenID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("message", "Token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        simpanToken(refreshedToken);
    }

    /*Untuk menyimpan Token yang didapat*/
    public void simpanToken(String token){
        SessionManager.getInstance(getApplicationContext()).simpanToken(token);
    }
}
