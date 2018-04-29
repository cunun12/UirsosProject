package com.uirsos.www.uirsosproject.News;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uirsos.www.uirsosproject.Login.SetupActivity;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private ImageView back;
    private TextView btnSimpan;
    private TextView namaPengguna;
    private CircleImageView imgProfile;
    private EditText newPostDesc;
    private ImageView newPostImage;
    private Button btnTambahPhoto;
    private ImageView cancelImage;
    private ProgressBar progressBarPost;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String user_id;

    private Uri postImageURI;
    private Bitmap compresImageFile;
    private double postPorgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        back = (ImageView) findViewById(R.id.btn_back);
        btnSimpan = (TextView) findViewById(R.id.btn_simpan);
        btnSimpan.setEnabled(false);
        cancelImage = (ImageView) findViewById(R.id.cancel_image);
        cancelImage.setVisibility(View.GONE);
        namaPengguna = (TextView) findViewById(R.id.nama_Pengguna);
        imgProfile = (CircleImageView) findViewById(R.id.img_Profile);
        newPostDesc = (EditText) findViewById(R.id.text_Post);
        newPostImage = (ImageView) findViewById(R.id.image_post);
        newPostImage.setVisibility(View.GONE);
        btnTambahPhoto = (Button) findViewById(R.id.btn_tambahPhoto);
        progressBarPost = (ProgressBar) findViewById(R.id.progressBar);
        progressBarPost.setVisibility(View.GONE);

        //berfungsi untuk kembali ke halaman berita
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(NewPostActivity.this, BeritaActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
        //berfungsi untuk mengahpus image yang dipilih
        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                newPostImage.setImageResource(0);
                if (newPostImage != null) {
                    newPostImage.setImageDrawable(null);
                    cancelImage.setVisibility(View.GONE);
                    btnSimpan.setEnabled(false);
                }

            }
        });

//        /*matikan text simpan sebelum textPost belum terisi*/
//        newPostDesc.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (s.toString().equals("")) {
//                    btnSimpan.setEnabled(false);
//                } else {
//                    btnSimpan.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().equals("")) {
//                    btnSimpan.setEnabled(false);
//                } else {
//                    btnSimpan.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        /* Bagian Firebase */
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        //untuk mengambil gambar dan nama user/pengguna
        firebaseFirestore.collection("Users")
                .document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String imageProfile = task.getResult().getString("image");
                            String textName = task.getResult().getString("name");

                            namaPengguna.setText(textName);

                            RequestOptions placeholderrequest = new RequestOptions();
                            placeholderrequest.placeholder(R.drawable.defaulticon);
                            Glide.with(getApplicationContext())
                                    .setDefaultRequestOptions(placeholderrequest)
                                    .load(imageProfile)
                                    .into(imgProfile);
                        }
                    }
                });
        /*batas pengambilan Image dan nama*/
        /*Batas Firebase*/

        btnTambahPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        /* Bagian ini berfungsi untuk menyimpan data Postingan ke Firebase */
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent beritaIntent = new Intent(NewPostActivity.this, BeritaActivity.class);
//                startActivity(beritaIntent);
//                finish();

                final String desc = newPostDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) && postImageURI != null) {

                    progressBarPost.setVisibility(View.VISIBLE);
                    // untuk membuat nama image berdasarkan waktu
                    final String randomNameImage = UUID.randomUUID().toString();

                    StorageReference filePath = storageReference.child("post_images").child(randomNameImage + ".jpg");
                    filePath.putFile(postImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()) {

                                File newImagefile = new File(postImageURI.getPath());
                                try {
                                    compresImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImagefile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compresImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = null;
                                uploadTask = storageReference.child("post_images/thumbs")
                                        .child(randomNameImage + ".jpg")
                                        .putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        progressBarPost.setVisibility(View.VISIBLE);
                                        String downloadThumnUri = taskSnapshot.getDownloadUrl().toString();

                                        DataItemStatus status = new DataItemStatus();
                                        status.setUser_id(user_id);
                                        status.setImage_post(downloadUri);
                                        status.setImage_thumb(downloadThumnUri);
                                        status.setDesc(desc);
                                        status.setTimestamp(getTimestamp());
//
//                                        Map<String, String> postMap = new HashMap<>();
//                                        postMap.put("image_post", downloadUri);
//                                        postMap.put("image_thumb", downloadThumnUri);
//                                        postMap.put("desc", desc);
//                                        postMap.put("user_id", user_id);
//                                        postMap.put("timestamp", getTimestamp());

                                        firebaseFirestore.collection("User_Post") // nama Table
                                                .add(status) // Value diambil dari variable
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(NewPostActivity.this, "Upload Berhasil", Toast.LENGTH_LONG).show();
                                                            Intent beritaIntent = new Intent(NewPostActivity.this, BeritaActivity.class);
                                                            startActivity(beritaIntent);
                                                            finish();

                                                        } else {

                                                            String Error = task.getException().getMessage();
                                                            Toast.makeText(NewPostActivity.this, "Uploaod Gagal" + Error, Toast.LENGTH_SHORT).show();

                                                        }

                                                    }
                                                });

                                        progressBarPost.setVisibility(View.GONE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewPostActivity.this, "Photo to loser Progress ", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @SuppressLint("DefaultLocale")
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progres = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        if (progres - 15 > postPorgress) {
                                            Toast.makeText(NewPostActivity.this, "Photo to Upload Progress " + String.format("%.0f", progres) + " %", Toast.LENGTH_SHORT).show();
                                            postPorgress = progres;
                                        }
                                    }
                                });

                            } else {
                                String Error = task.getException().getMessage();
                                Toast.makeText(NewPostActivity.this, "Error tu =" + Error, Toast.LENGTH_SHORT).show();
                            }
                            progressBarPost.setVisibility(View.GONE);
                        }
                    });

                }

            }
        });
        /*Batas mematikan text simpan*/
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return sdf.format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageURI = result.getUri();
                newPostImage.setImageURI(postImageURI);
                if (newPostImage != null) {
                    btnSimpan.setEnabled(true);
                    cancelImage.setVisibility(View.VISIBLE);
                    newPostImage.setVisibility(View.VISIBLE);
                } else {
                    btnSimpan.setEnabled(false);
                    cancelImage.setVisibility(View.GONE);
                    newPostImage.setVisibility(View.GONE);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "why?" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
