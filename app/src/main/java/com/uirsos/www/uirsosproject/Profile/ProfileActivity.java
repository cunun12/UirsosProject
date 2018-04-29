package com.uirsos.www.uirsosproject.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsosproject.Adapter.AdapterHistory;
import com.uirsos.www.uirsosproject.Login.LoginActivity;
import com.uirsos.www.uirsosproject.Login.SetupActivity;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.Utils.BottomNavigationViewHelper;
import com.uirsos.www.uirsosproject.Utils.GridImageDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cunun12 on
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVTY_NUM = 1;
    //variable
    ImageView imageProfile_Users;

    TextView namaPengguna, jenisKelamin, statusUser, fakultasUser, npm;
    LinearLayout lineStatus;
    RecyclerView listHistori;
    List<DataItemStatus> post_history;
    AdapterHistory adapterHistory;

    private Context mContext = ProfileActivity.this;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private String user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*Firebase*/
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getUid();
        user = getIntent().getStringExtra("idUser");
        Log.d(TAG, "iniprofile: ");

        /*Toolbar*/
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        /*Widget*/
        imageProfile_Users = (ImageView) findViewById(R.id.photoProfil);
        namaPengguna = (TextView) findViewById(R.id.txtNamaPengguna);
        jenisKelamin = (TextView) findViewById(R.id.txtJenisKelamin);
        listHistori = (RecyclerView) findViewById(R.id.historyUser);
        fakultasUser = (TextView) findViewById(R.id.txtFakultas);
        npm = (TextView) findViewById(R.id.txtNPM);
        statusUser = (TextView) findViewById(R.id.txtStatusUser);
        lineStatus = (LinearLayout) findViewById(R.id.statusLine);

        lineStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(ProfileActivity.this, StatusUser.class);
                startActivity(statusIntent);
            }
        });

        loadProfile();

        post_history = new ArrayList<>();
        adapterHistory = new AdapterHistory(post_history);
        listHistori.setLayoutManager(new GridLayoutManager(this, 4));
        listHistori.addItemDecoration(new GridImageDecoration(getApplicationContext(), 2, 2, 2, 2));
        listHistori.setAdapter(adapterHistory);

        loadImage();

        setupBottomNavigationView();
    }

    private void loadProfile() {

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {
                        String image = task.getResult().getString("image");
                        String nama = task.getResult().getString("name");
                        String NPM = task.getResult().getString("npm");
                        String gender = task.getResult().getString("gender");
                        String fakultas = task.getResult().getString("jurusan");
                        String status = task.getResult().getString("status");

                        namaPengguna.setText(nama);
                        jenisKelamin.setText(gender);
                        fakultasUser.setText(fakultas);
                        statusUser.setText(status);
                        npm.setText(NPM);
                        RequestOptions placeholderrequest = new RequestOptions();
                        placeholderrequest.placeholder(R.drawable.defaulticon);
                        Glide.with(getApplicationContext())
                                .setDefaultRequestOptions(placeholderrequest)
                                .load(image)
                                .into(imageProfile_Users);

                    } else {
                        Toast.makeText(mContext, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Data tidak tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadImage() {

        firebaseFirestore.collection("User_Post")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " pajako => " + document.getData());

                                DataItemStatus dataitem = new DataItemStatus();
                                dataitem.setUser_id(document.getString("user_id"));
                                dataitem.setImage_post(document.getString("image_post"));
                                dataitem.setImage_thumb(document.getString("image_thumb"));
                                post_history.add(dataitem);
                            }
                            adapterHistory.notifyDataSetChanged();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.accountSetting:

                Intent accountIntent = new Intent(ProfileActivity.this, SetupActivity.class);
                accountIntent.putExtra("update", 1);
                accountIntent.putExtra("npm", npm.getText());
                accountIntent.putExtra("nama", namaPengguna.getText());
                accountIntent.putExtra("fakultas", fakultasUser.getText());
                accountIntent.putExtra("jeniskelamin", jenisKelamin.getText());
                accountIntent.putExtra("status", statusUser.getText());
                startActivity(accountIntent);

                return true;

            case R.id.logout:

                logout();

                return true;

            default:

                return false;
        }

    }

    private void logout() {


        Map<String, Object> tokenRemove = new HashMap<>();
        tokenRemove.put("token_id", FieldValue.delete());

        firebaseFirestore.collection("Users").document(user_id).update(tokenRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mAuth.signOut();
                finish();
                sendToLogin();
            }
        });


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
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
