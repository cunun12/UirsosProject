package com.uirsos.www.uirsosproject.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uirsos.www.uirsosproject.News.BeritaActivity;
import com.uirsos.www.uirsosproject.R;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText confrimPassword;
    private Button btnRegister;
    private TextView textViewLogin;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.textEmail);
        editTextPassword = (EditText) findViewById(R.id.textPassword);
        confrimPassword = (EditText) findViewById(R.id.confrimPassText);


        /*pindah halaman*/
        btnRegister = (Button) findViewById(R.id.btn_register);
        textViewLogin = (TextView) findViewById(R.id.txtViewLogin);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString();
                String passw = editTextPassword.getText().toString();
                String confrimPass = confrimPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passw) && !TextUtils.isEmpty(confrimPass)) {
                    if (passw.equals(confrimPass)) {

                        mAuth.createUserWithEmailAndPassword(email, passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Email sudah digunakan!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {

                        Toast.makeText(RegisterActivity.this, "Password dan Confrim Password tidak cocok", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
        textViewLogin.setOnClickListener(this);
    }

    /*bagian ini dinonaktifkan karna ingin mencoba register ke firebase*/

//    public void RegisterUser() {
//        final String npm = editTextNPM.getText().toString().trim();
//        final String email = editTextEmail.getText().toString().trim();
//        final String password = editTextPassword.getText().toString().trim();
//        final String jeniskelamin = kelamin.getText().toString().trim();
//
//
//        if (TextUtils.isEmpty(npm) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(jeniskelamin)) {
//            Toast.makeText(this, "Mohon lengkapi data anda", Toast.LENGTH_SHORT).show();
//        }
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                APIServer.REGISTER,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(getApplicationContext(), SetupActivity.class);
//                            Bundle isi = new Bundle();
//                            isi.putString("key_npm", npm);
//                            i.putExtras(isi);
//                            startActivity(i);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(RegisterActivity.this, "Data tidak dapat disimpan" + e + response, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
////                params.put("token", SessionManager.getInstance(getApplicationContext()).getToken());
//                params.put("npm", npm);
//                params.put("email", email);
//                params.put("password", password);
//                params.put("jenisKelamin", jeniskelamin);
//                return params;
//            }
//        };
//
//        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//
//    }

    @Override
    public void onClick(View v) {
        if (v == textViewLogin)
            startActivity(new Intent(this, LoginActivity.class));
        finish();
//        if (v == btnRegister) {
//            RegisterUser();
//        }

    }
}
