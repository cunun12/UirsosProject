package com.uirsos.www.uirsosproject.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uirsos.www.uirsosproject.News.BeritaActivity;
import com.uirsos.www.uirsosproject.POJO.KategoriFakultas;
import com.uirsos.www.uirsosproject.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private int requestCode=1;

    int sukses;
    String[] kategori = {
            "Fakultas Teknik",
            "Fakultas Hukum",
            "Fakultas Pertanian",
            "Fakultas Ilmu Keguruan dan Ilmu Pendidikan",
            "Fakultas Ilmu Sosial dan Politik",
            "Fakultas Agama Islam",
            "Fakultas Ilmu Komunikasi",
            "Fakultas Psikologi",
            "Fakultas Ekonomi"
    };
    String choice;
    private EditText setupNPM;
    private EditText setupTextNama;
    private EditText setupTextJurusan;
    private TextView setupjenisKelamin, choiceText, statushint;
    private Button btnSimpan;
    private CircleImageView image;
    private CircleImageView setupImage;
    private Spinner spinner;
    private Uri mainImageURI = null;

    // firebase
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //inisialisasi
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getUid();

        statushint = (TextView) findViewById(R.id.statushint);
        setupImage = (CircleImageView) findViewById(R.id.imageProfil);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        setupNPM = (EditText) findViewById(R.id.npm);
        setupTextNama = (EditText) findViewById(R.id.namaPengguna);
        setupjenisKelamin = (TextView) findViewById(R.id.gender);
        setupjenisKelamin.setVisibility(View.INVISIBLE);
        spinner = (Spinner) findViewById(R.id.kategorifakultas);
        choiceText = findViewById(R.id.choice_Text);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Activity", "onItemSelected: " + kategori[position]);

                choice = kategori[position];
                choiceText.setText(choice);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> fakultas = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, kategori);
        fakultas.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(fakultas);

        /**
         * Kita set data dari firebse ke android
         */

        Intent data = getIntent();
        final int update = data.getIntExtra("update", 0);
        String npm = data.getStringExtra("npm");
        String nama = data.getStringExtra("nama");
        String kategori = data.getStringExtra("fakultas");
        String status = data.getStringExtra("status");


        if (update == 1) {
            btnSimpan.setText("Update Data");
            statushint.setText(status);
            statushint.setVisibility(View.GONE);
            setupNPM.setText(npm);
            setupNPM.setVisibility(View.GONE);
            setupTextNama.setText(nama);
            choiceText.setText(kategori);

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("CheckResult")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String image = task.getResult().getString("image");
                            String jk = task.getResult().getString("gender");

                            RadioButton laki = (RadioButton) findViewById(R.id.laki_laki);
                            RadioButton perempuan = (RadioButton) findViewById(R.id.perempuan);
                            if (jk.equals("laki-laki")) {
                                laki.setChecked(true);
                            } else {
                                perempuan.setChecked(true);
                            }
                            if (image != null) {
                                mainImageURI = Uri.parse(image);
                                RequestOptions placeholderrequest = new RequestOptions();
                                placeholderrequest.placeholder(R.drawable.defaulticon);
                                Glide.with(getApplicationContext())
                                        .setDefaultRequestOptions(placeholderrequest)
                                        .load(image)
                                        .into(setupImage);
                            } else {
                                mainImageURI = null;
                                setupImage.setImageURI(mainImageURI);
                            }

                        }
                    }
                }
            });

            spinner.setVisibility(View.GONE);
        }

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);

                    } else {

                        BringImagePicker();

                    }
                } else {

                    BringImagePicker();

                }
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (update == 1) {
                    update_data();
                } else {
                    tambah();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "why?" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void update_data() {
        final String username = setupTextNama.getText().toString();
        final String NPM = setupNPM.getText().toString();
        final String jurusan = choiceText.getText().toString();
        final String jenisKelamin = setupjenisKelamin.getText().toString();
        final String status = statushint.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(NPM) && !TextUtils.isEmpty(jurusan) &&
                !TextUtils.isEmpty(jenisKelamin) && mainImageURI != null) {
            if (isChanged) {
                user_id = firebaseAuth.getCurrentUser().getUid();

                StorageReference imageUrl = storageReference.child("profile_image").child(user_id + ".jpg");
                imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            storeUpdateFirebase(task, status, username, NPM, jurusan, jenisKelamin);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "(Image Error) apa ya? " + error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else {
                storeUpdateFirebase(null, status, username, NPM, jurusan, jenisKelamin);
            }
        }
    }

    private void tambah() {
        final String username = setupTextNama.getText().toString();
        final String NPM = setupNPM.getText().toString();
        final String jurusan = choiceText.getText().toString();
        final String jenisKelamin = setupjenisKelamin.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(NPM) && !TextUtils.isEmpty(jurusan) &&
                !TextUtils.isEmpty(jenisKelamin) && mainImageURI != null) {
            if (isChanged) {
                user_id = firebaseAuth.getCurrentUser().getUid();

                StorageReference imageUrl = storageReference.child("profile_image").child(user_id + ".jpg");
                imageUrl.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            storeTambahFirebase(task, username, NPM, jurusan, jenisKelamin);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "(Image Error) apa ya? " + error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else {
                storeTambahFirebase(null, username, NPM, jurusan, jenisKelamin);
            }
        }
    }


    private void storeUpdateFirebase(Task<UploadTask.TaskSnapshot> task, String status, String username, String npm, String jurusan, String jenisKelamin) {

        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }

        String token_id = FirebaseInstanceId.getInstance().getToken();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("status", status);
        userMap.put("name", username);
        userMap.put("npm", npm);
        userMap.put("jurusan", jurusan);
        userMap.put("gender", jenisKelamin);
        userMap.put("token_id", token_id);
        userMap.put("image", download_uri != null ? download_uri.toString() : null);


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SetupActivity.this, "User Settings Are Updated", Toast.LENGTH_SHORT).show();
                    Intent beritaIntent = new Intent(SetupActivity.this, BeritaActivity.class);
                    startActivity(beritaIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FireStore Error) " + error, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void storeTambahFirebase(Task<UploadTask.TaskSnapshot> task, String username, String npm, String jurusan, String jenisKelamin) {

        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }

        String token_id = FirebaseInstanceId.getInstance().getToken();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("npm", npm);
        userMap.put("jurusan", jurusan);
        userMap.put("gender", jenisKelamin);
        userMap.put("token_id", token_id);
        userMap.put("image", download_uri != null ? download_uri.toString() : null);


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SetupActivity.this, "User Settings Are Updated", Toast.LENGTH_SHORT).show();
                    Intent beritaIntent = new Intent(SetupActivity.this, BeritaActivity.class);
                    startActivity(beritaIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FireStore Error) " + error, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }


    public void gender(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.laki_laki:
                if (checked) {
                    setupjenisKelamin.setVisibility(View.INVISIBLE);
                    setupjenisKelamin.setText(R.string.cowok);
                    setupjenisKelamin.setEnabled(true);
                    Toast.makeText(this, "Laki-laki", Toast.LENGTH_SHORT).show();
                } else {
                    setupjenisKelamin.setEnabled(false);
                }
                break;

            case R.id.perempuan:
                if (checked) {
                    setupjenisKelamin.setVisibility(View.INVISIBLE);
                    setupjenisKelamin.setText(R.string.perempuan);
                    setupjenisKelamin.setEnabled(true);
                    Toast.makeText(this, "Perempuan", Toast.LENGTH_SHORT).show();
                } else {
                    setupjenisKelamin.setEnabled(false);
                }
                break;
        }
    }


}
