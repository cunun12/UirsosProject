package com.uirsos.www.uirsosproject.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsosproject.News.BeritaActivity;
import com.uirsos.www.uirsosproject.Profile.ProfileActivity;
import com.uirsos.www.uirsosproject.R;

/**
 * Created by cunun12
 */

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting Up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.ic_home:
                        Intent berita = new Intent(context, BeritaActivity.class);//ACTIVTY_NUM = 0
                        berita.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(berita);
                        break;

                    case R.id.ic_profile:
                        Intent profile = new Intent(context, ProfileActivity.class);//ACTIVTY_NUM = 1
                        profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(profile);

                        break;

//                    case R.id.ic_search:
//                        Intent timeline = new Intent(context, SearchActivity.class);//ACTIVTY_NUM = 1
//                        context.startActivity(timeline);
//                        break;
//
//                    case R.id.ic_tambah:
//                        Intent tambah = new Intent(context, ShareActivity.class);//ACTIVTY_NUM = 2
//                        context.startActivity(tambah);
//                        break;

//                    case R.id.ic_notification:
//                        Intent notification = new Intent(context, NotificationActivity.class);//ACTIVTY_NUM = 3
//                        context.startActivity(notification);
//                        break;

                }

                return false;
            }
        });
    }


}
