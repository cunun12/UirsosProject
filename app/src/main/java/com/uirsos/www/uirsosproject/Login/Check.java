package com.uirsos.www.uirsosproject.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsosproject.R;

import java.util.concurrent.TimeUnit;

public class Check extends AppCompatActivity {

    private static final String TAG = "check";
    //widget
    Button btnProgres;
    EditText textNpm, textPhone, textVerifikasi;
    TextView infoErrorNpm, infoErrorPhone;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int btnType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_npm);

        //widget
        textNpm = (EditText) findViewById(R.id.text_Npm);
        textPhone = (EditText) findViewById(R.id.text_phone);
        textVerifikasi = (EditText) findViewById(R.id.kode_verifikasi);
        infoErrorNpm = (TextView) findViewById(R.id.info_errorNPM);
        infoErrorNpm.setVisibility(View.GONE);
        infoErrorPhone = (TextView) findViewById(R.id.info_kodeVerifikasi);
        infoErrorPhone.setVisibility(View.GONE);
        btnProgres = (Button) findViewById(R.id.btn_check);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        btnProgres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getNpm = textNpm.getText().toString();

                if (textNpm.getText().toString().length() == 0) {
                    textNpm.setError("Silakan isikan kolom");
                } else {
                    firebaseFirestore.collection("Users")
                            .whereEqualTo("npm", getNpm)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.getResult().isEmpty()) {

                                        Log.d("data", "onComplete: " + task.getResult().toString());
                                        infoErrorNpm.setVisibility(View.GONE);
                                        String tampungNpm = textNpm.getText().toString();
                                        layoutPhone(tampungNpm);
                                        Toast.makeText(Check.this, "Data tidak ditemukan" + tampungNpm, Toast.LENGTH_SHORT).show();

                                    } else {

                                        infoErrorNpm.setVisibility(View.VISIBLE);
                                        infoErrorNpm.setText(R.string.error_npm);
                                        Toast.makeText(Check.this, "Data ditemukan", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }

    private void layoutPhone(String npm) {
        Log.d("npm", "TampunNpm: " + npm);

        textNpm.setVisibility(View.GONE);
        textPhone.setVisibility(View.VISIBLE);
        textPhone.requestFocus();

        btnProgres.setText("Kirim No");
        btnProgres.setEnabled(true);

        btnProgres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType == 0) {
                    textPhone.setEnabled(false);
                    btnProgres.setEnabled(false);

                    String getTextPhone = textPhone.getText().toString();

                    if (getTextPhone.length() == 0) {
                        textPhone.setError("Silakan isi kolom");
                    } else {

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                getTextPhone,
                                60,
                                TimeUnit.SECONDS,
                                Check.this,
                                mCallback
                        );

                    }
                } else {

                    btnProgres.setEnabled(false);

                    String verifyCode = textVerifikasi.getText().toString();

                    if (verifyCode.length() == 0) {
                        textVerifikasi.setError("Silakan masukan kode");
                    } else {

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifyCode);
                        signInWithPhoneAuthCredential(credential);

                    }
                }
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                infoErrorPhone.setVisibility(View.VISIBLE);
                infoErrorPhone.setText("Kode tidak sesuai");

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                btnType = 1;

                textVerifikasi.setVisibility(View.VISIBLE);
                infoErrorPhone.setVisibility(View.VISIBLE);

                btnProgres.setText("Verify Code");
                btnProgres.setEnabled(true);
                // ...
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            String datanpm = textNpm.getText().toString();
                            String dataphone = textPhone.getText().toString();

                            Log.d(TAG, "onComplete: "+ datanpm + "dan "+dataphone);

                            Intent setupIntent = new Intent(Check.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            infoErrorPhone.setVisibility(View.VISIBLE);
                            infoErrorPhone.setText("Kode tidak sesuai");

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
