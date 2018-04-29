package com.uirsos.www.uirsosproject.News;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsosproject.Login.LoginActivity;
import com.uirsos.www.uirsosproject.Login.SetupActivity;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.Utils.BottomNavigationViewHelper;
import com.uirsos.www.uirsosproject.Utils.SectionPagerAdapter;

public class BeritaActivity extends AppCompatActivity {

    private static final String TAG = "BeritaActivity";
    private static final int ACTIVTY_NUM = 0;

    /*Inisial*/
    private Context mContext = BeritaActivity.this;

    /*Firebase*/
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berita);
        Log.d(TAG, "onCreate : starting. ");

        setupFirebaseAuth();
        setupViewPager();
        setupBottomNavigationView();
    }

    /**
     * viewpager berita dan status
     */
    private void setupViewPager() {
        SectionPagerAdapter view = new SectionPagerAdapter(getSupportFragmentManager());
        view.addFragment(new StatusFragment());// fragment 1
        view.addFragment(new BeritaFragment());//fragment 0

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(view);

        TabLayout tab = (TabLayout) findViewById(R.id.tabAtas);
        tab.setupWithViewPager(viewPager);

        tab.getTabAt(1).setText(getString(R.string.news));
        tab.getTabAt(0).setText(getString(R.string.status));

    }

    /*
    * BottomNavigationView setup
    */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx navigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(navigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, navigationViewEx);
        Menu menu = navigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVTY_NUM);
        menuItem.setChecked(true);
    }

    /*
    ----------------------------- Firebase ----------------------------
    */
    /*
    Settup Firebase auth Object
     */
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Setting Firebase Auth");

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            Intent loginIntent = new Intent(BeritaActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();

        } else {

            current_userId = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){

                            Intent setupIntent = new Intent(BeritaActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }
                    } else{

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(mContext, "Error : " +errorMessage, Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }

    }
}
