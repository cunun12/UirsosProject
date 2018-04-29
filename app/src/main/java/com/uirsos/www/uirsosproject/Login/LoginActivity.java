package com.uirsos.www.uirsosproject.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uirsos.www.uirsosproject.News.BeritaActivity;
import com.uirsos.www.uirsosproject.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textRegist;

    private EditText loginEmailtext;
    private EditText loginPassText;
    private Button logginBtn;
    private ProgressBar loginProgres;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        textRegist = (TextView) findViewById(R.id.txtViewRegist);
        logginBtn = (Button) findViewById(R.id.btn_login);
        loginEmailtext = (EditText) findViewById(R.id.textEmail);
        loginPassText = (EditText) findViewById(R.id.textPassword);
        loginProgres = (ProgressBar) findViewById(R.id.progresbarLogin);

        logginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = loginEmailtext.getText().toString();
                String loginPassword = loginPassText.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
                    loginProgres.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                    @Override
                                    public void onSuccess(GetTokenResult getTokenResult) {

                                        String token_id = FirebaseInstanceId.getInstance().getToken();
                                        String id_baru = mAuth.getCurrentUser().getUid();

                                        Map<String, Object> tokenMap = new HashMap<>();
                                        tokenMap.put("token_id", token_id);

                                        mFirestore.collection("Users").document(id_baru).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendToMain();
                                            }
                                        });

                                    }
                                });



                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error " + errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            loginProgres.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }
        });

        textRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == textRegist) {
            startActivity(new Intent(this, Check.class));
            finish();
        }
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

        }

    }

    private void sendToMain() {

        Intent beritaIntent = new Intent(LoginActivity.this, BeritaActivity.class);
        startActivity(beritaIntent);
        finish();

    }
}
