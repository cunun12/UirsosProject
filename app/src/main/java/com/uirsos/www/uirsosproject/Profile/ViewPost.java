package com.uirsos.www.uirsosproject.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsosproject.News.KomentarActivity;
import com.uirsos.www.uirsosproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cunun12 on 05/04/2018.
 */

public class ViewPost extends AppCompatActivity {

    private final static String TAG = "ViewPost";
    ImageView imagePost, back, postLike;
    CircleImageView imageUser;
    TextView desc, namaUser, timePost, countLike;
    String idPost;
    String userPost;
    String postId;
    LinearLayout likeLinear, komentLinear;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String currendUserId;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view_post);

        /*Toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        postLike = findViewById(R.id.post_Like);
        countLike = findViewById(R.id.post_likeCount);
        imagePost = findViewById(R.id.image_post);
        desc = findViewById(R.id.desc_post);
        timePost = findViewById(R.id.waktu_post);
        back = findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currendUserId.equals(userId)) {
                    Intent back = new Intent(ViewPost.this, ProfileActivity.class);
                    back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(back);
                    finish();
                }else{
                    Intent back = new Intent(ViewPost.this, FriendActivity.class);
                    back.putExtra("idUser", userId);
                    back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(back);
                    finish();
                }

            }
        });

        likeLinear = findViewById(R.id.like_Pengguna);
        komentLinear = findViewById(R.id.post_komentar);

        komentLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewPost.this, KomentarActivity.class);
                i.putExtra("idPost", postId);
                startActivity(i);
                finish();
            }
        });

        //id yang dikirimkan dari adapterHistory
        idPost = getIntent().getStringExtra("idPost");
        userId = getIntent().getStringExtra("idUser");
        Log.d(TAG, "onCreateuserId: " + userId);

        namaUser = findViewById(R.id.nama_user);
        imageUser = findViewById(R.id.image_user);

        /*--- Firebase ---*/
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currendUserId = firebaseAuth.getCurrentUser().getUid();

        ambilData();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* -------> mendapatkan data dari Firebase<-------- */
    private void ambilData() {

        firebaseFirestore.collection("User_Post")
                .whereEqualTo("image_thumb", idPost)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {

                                Log.d(TAG, doc.getId() + "<=ID cobacoy: " + doc.getData());

                                postId = doc.getId();
                                userPost = doc.getString("user_id");

                                Log.w(TAG, "onComplete: " + userPost);
                                firebaseFirestore.collection("Users").document(userPost).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String nameUserPost = task.getResult().getString("name");
                                            String imageUserPost = task.getResult().getString("image");

                                            namaUser.setText(nameUserPost);

                                            Glide.with(ViewPost.this)
                                                    .load(imageUserPost)
                                                    .into(imageUser);

                                        } else {
                                            Toast.makeText(ViewPost.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                LikesToggle();

                                String postImage = doc.getString("image_post");
                                String Deskripsi = doc.getString("desc");
                                String tanggal = doc.getString("timestamp");

                                desc.setText(Deskripsi);
                                Glide.with(getApplicationContext())
                                        .load(postImage)
                                        .into(imagePost);

                                tanggalDate(tanggal);

                            }
                        } else {

                            Toast.makeText(ViewPost.this, "Data ini error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void LikesToggle() {

        /*Jumlah Likes*/
        //getCount
        firebaseFirestore.collection("User_Post/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null) {

                    int count = documentSnapshots.size();
                    countLike.setText(count + " " + getString(R.string.Likes));

                } else {

                    countLike.setText(0 + " " + getString(R.string.Likes));

                }

            }
        });

        //getLikes
        firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currendUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {
                    if (e != null) {
                        Log.w(TAG, ":onEvent", e);
                        return;
                    }
                    if (documentSnapshot.exists()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            postLike.setImageDrawable(getDrawable(R.mipmap.action_like_accent));
                        }

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            postLike.setImageDrawable(getDrawable(R.mipmap.action_like_grey));
                        }
                    }
                }
            }
        });

        likeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + postId);
//                Toast.makeText(ViewPost.this, "ini apa => "+postId, Toast.LENGTH_SHORT).show();
                firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currendUserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {

                            Map<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currendUserId).set(likeMap);

                        } else {

                            firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currendUserId).delete();

                        }
                    }
                });
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (currendUserId.equals(userId)) {
            getMenuInflater().inflate(R.menu.post_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.deletePost:

                DeletePost();

                return true;

            default:

                return false;
        }

    }

    private void DeletePost() {
        firebaseFirestore.collection("User_Post").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    Log.d(TAG, "Datanya: " + task.getResult().getData());

                    firebaseFirestore.collection("User_Post").document(postId).delete();
                    Intent iProfile = new Intent(ViewPost.this, ProfileActivity.class);
                    startActivity(iProfile);
                    finish();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void tanggalDate(String tanggal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = tanggal;
        Date timestamp;
        long detik = 0;
        long menit = 0;
        long jam = 0;
        long hari = 0;

        try {
            timestamp = sdf.parse(postTimestamp);
            Date now = new Date();

            detik = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime());
            menit = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - timestamp.getTime());
            jam = TimeUnit.MILLISECONDS.toHours(now.getTime() - timestamp.getTime());
            hari = TimeUnit.MILLISECONDS.toDays(now.getTime() - timestamp.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (detik < 60) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            String postDetik = String.valueOf(detik);
            timePost.setText(postDetik + " Detik yang lalu");
        } else if (menit < 60) {
            Log.d(TAG, "onBindViewHolder: menit" + menit);
            String postMenit = String.valueOf(menit);
            timePost.setText(postMenit + " Menit yang lalu");
        } else if (jam < 24) {
            Log.d(TAG, "onBindViewHolder: jam" + jam);
            String postJam = String.valueOf(jam);
            timePost.setText(postJam + " Jam yang lalu");
        } else if (hari < jam) {
            Log.d(TAG, "onBindViewHolder: hari" + hari);
            String postHari = String.valueOf(hari);
            timePost.setText(postHari + " Hari yang lalu");
        } else {
            timePost.setText(postTimestamp);
//            holder.setTime(postTimestamp);
        }
    }

}
