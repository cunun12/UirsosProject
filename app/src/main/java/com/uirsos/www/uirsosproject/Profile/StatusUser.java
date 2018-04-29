package com.uirsos.www.uirsosproject.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uirsos.www.uirsosproject.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cunun12 on 17/03/2018.
 */

public class StatusUser extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnSimpan;
    private EditText bioText;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //inisialisasi yang ada di XML
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSimpan = (TextView) findViewById(R.id.btn_simpan);
        btnSimpan.setEnabled(false);
        bioText = (EditText) findViewById(R.id.bioText);

        bioText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    btnSimpan.setEnabled(false);
                } else {
                    btnSimpan.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    btnSimpan.setEnabled(false);
                } else {
                    btnSimpan.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = bioText.getText().toString();

                if (!TextUtils.isEmpty(status)) {

                    String user_id = mAuth.getCurrentUser().getUid();

                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put("status", status);

                    firebaseFirestore.collection("Users").document(user_id).update(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(StatusUser.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                                Intent profileIntent = new Intent(StatusUser.this, ProfileActivity.class);
                                startActivity(profileIntent);
                                finish();

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(StatusUser.this, "Error = " + error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(StatusUser.this, ProfileActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backIntent);
                finish();
            }
        });
    }

}
