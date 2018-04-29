package com.uirsos.www.uirsosproject.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.uirsos.www.uirsosproject.Adapter.AdapterHistory;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.Utils.BottomNavigationViewHelper;
import com.uirsos.www.uirsosproject.Utils.GridImageDecoration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cunun12 on 07/04/2018.
 */

public class FriendActivity extends AppCompatActivity {

    private static final String TAG = "FriendActivity";
    private static final int ACTIVTY_NUM = 0;

    /*Widget*/
    CircleImageView imageFriendUser;
    TextView npmFriend, namaFriend, genderFriend, statusFriend, fakultasFriend;
    ImageView imageButton;
    String friendUserId;
    RecyclerView listHistori;
    List<DataItemStatus> post_history;
    AdapterHistory adapterHistory;
    LinearLayout lineStatus;

    /*Firebase*/
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendUserId = getIntent().getStringExtra("idUser");
        Log.d(TAG, "idUser: " + friendUserId);

        /*Firebase*/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        /*Widget*/
        imageFriendUser = findViewById(R.id.photoProfil);
        npmFriend = findViewById(R.id.txtNPM);
        namaFriend = findViewById(R.id.txtNamaPengguna);
        genderFriend = findViewById(R.id.txtJenisKelamin);
        statusFriend = findViewById(R.id.txtStatusUser);
        fakultasFriend = findViewById(R.id.txtFakultas);
        imageButton = findViewById(R.id.image_button);
        imageButton.setVisibility(View.GONE);
        listHistori = (RecyclerView) findViewById(R.id.historyUser);
        lineStatus = (LinearLayout) findViewById(R.id.statusLine);

        loadDataFriend();
        loadImageFriend();

        post_history = new ArrayList<>();
        adapterHistory = new AdapterHistory(post_history);
        listHistori.setLayoutManager(new GridLayoutManager(this, 4));
        listHistori.addItemDecoration(new GridImageDecoration(getApplicationContext(), 2, 2, 2, 2));
        listHistori.setAdapter(adapterHistory);

        setupBottomNavigationView();
    }

    private void loadImageFriend() {

        firebaseFirestore.collection("User_Post")
                .whereEqualTo("user_id", friendUserId)
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

    /*Bagian untuk mengambil data FriendUser*/
    private void loadDataFriend() {

        firebaseFirestore.collection("Users").document(friendUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: Data User" + task.getResult().getData());

                    if (task.getResult().exists()) {
                        String image = task.getResult().getString("image");
                        String nama = task.getResult().getString("name");
                        String NPM = task.getResult().getString("npm");
                        String gender = task.getResult().getString("gender");
                        String fakultas = task.getResult().getString("jurusan");
                        String status = task.getResult().getString("status");

                        namaFriend.setText(nama);
                        genderFriend.setText(gender);
                        fakultasFriend.setText(fakultas);
                        statusFriend.setText(status);
                        npmFriend.setText(NPM);
                        RequestOptions placeholderrequest = new RequestOptions();
                        placeholderrequest.placeholder(R.drawable.defaulticon);
                        Glide.with(getApplicationContext())
                                .setDefaultRequestOptions(placeholderrequest)
                                .load(image)
                                .into(imageFriendUser);

                    } else {
                        Toast.makeText(FriendActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /*
    BottomNavigationView setup
    */

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx navigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(navigationViewEx);
        BottomNavigationViewHelper.enableNavigation(FriendActivity.this, navigationViewEx);
        Menu menu = navigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVTY_NUM);
        menuItem.setChecked(true);
    }
}
