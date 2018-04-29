package com.uirsos.www.uirsosproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uirsos.www.uirsosproject.Login.LoginActivity;
import com.uirsos.www.uirsosproject.Login.SetupActivity;
import com.uirsos.www.uirsosproject.News.BeritaActivity;

public class ScreenActivity extends AppCompatActivity {
    //Logo
    public ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        imgLogo = (ImageView) findViewById(R.id.ivLogo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        imgLogo.setAnimation(anim);

    }

    /*
------------------------------ Firebase -------------------------------
 */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            sendToMain();

        }else{
            final Intent i = new Intent(this, LoginActivity.class);

            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(i);
                        finish();
                    }
                }
            };

            timer.start();
        }

    }

    private void sendToMain() {

        Intent beritaIntent = new Intent(ScreenActivity.this, BeritaActivity.class);
        beritaIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(beritaIntent);
        finish();

    }
}
