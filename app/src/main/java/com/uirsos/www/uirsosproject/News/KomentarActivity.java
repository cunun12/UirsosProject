package com.uirsos.www.uirsosproject.News;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.uirsos.www.uirsosproject.Adapter.AdapterKomentar;
import com.uirsos.www.uirsosproject.POJO.DataKomentarStatus;
import com.uirsos.www.uirsosproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class KomentarActivity extends AppCompatActivity {

    private final static String TAG = "Komentar";

    List<DataKomentarStatus> komentarList;
    SwipeRefreshLayout refreshKomentar;
    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private EditText message;
    private ImageView send;
    private ImageView back;
    private String postId;
    private RecyclerView listKomentar;
    private AdapterKomentar adapterKomentar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentar);

        back = findViewById(R.id.btn_back);
        listKomentar = findViewById(R.id.list_komentar);
        message = findViewById(R.id.messageKomentar);
        send = findViewById(R.id.sendKomentar);
        send.setEnabled(false);
        postId = getIntent().getStringExtra("idPost");
        Log.d(TAG, "ini Post " + postId);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        // fungsi back ini berguna untuk kembali ke halaman postingan/BeritaActivity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backStatus = new Intent(KomentarActivity.this, BeritaActivity.class);
                startActivity(backStatus);
                finish();
            }
        });

        //jika edittext belum terisi maka send disabled
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.action_send));
                        send.setEnabled(false);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.action_send_green));
                        send.setVisibility(View.VISIBLE);
                        send.setEnabled(true);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.action_send));
                        send.setEnabled(false);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        send.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.action_send_green));
                        send.setVisibility(View.VISIBLE);
                        send.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String komentar = message.getText().toString();
                komentarAdd(komentar);

            }
        });

        komentarList = new ArrayList<>();
        adapterKomentar = new AdapterKomentar(komentarList);
        listKomentar.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listKomentar.setAdapter(adapterKomentar);

        ambilData();

        refreshKomentar = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshKomentar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                komentarList.clear();
                ambilData();
                refreshKomentar.setRefreshing(false);
            }
        }, 1000);
    }

    private void ambilData() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("User_Post/" + postId + "/Komentar").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(KomentarActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        DataKomentarStatus komentarStatus = doc.getDocument().toObject(DataKomentarStatus.class).withId(postId);
                                        komentarList.add(komentarStatus);
                                    }
                                }
                                adapterKomentar.notifyDataSetChanged();

                            }
                        }
                    });
        }
    }

    // mengirim data
    public void komentarAdd(final String komentar) {
        if (!TextUtils.isEmpty(komentar)) {

            DataKomentarStatus komentarStatus = new DataKomentarStatus();
            komentarStatus.setKomentar(komentar);
            komentarStatus.setUser_id(user_id);
            komentarStatus.setTimestamp(getTimestamp());


            firebaseFirestore.collection("User_Post/" + postId + "/Komentar").add(komentarStatus)
                    .addOnCompleteListener(KomentarActivity.this, new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()) {
                                message.setText("");
                                message.clearFocus();
                                send.clearFocus();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    message.setKeyboardNavigationCluster(true);
                                }
                                Toast.makeText(KomentarActivity.this, "Komentar Berhasil di kirim", Toast.LENGTH_SHORT).show();
                            } else {
                                message.setText("");
                                message.clearFocus();
                                send.clearFocus();
                                Toast.makeText(KomentarActivity.this, "Pending Mengirim Pesan", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            Toast.makeText(KomentarActivity.this, "Apa Komentar Anda?", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return sdf.format(new Date());
    }

}
