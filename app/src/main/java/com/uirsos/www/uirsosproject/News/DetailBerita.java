package com.uirsos.www.uirsosproject.News;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codesgood.views.JustifiedTextView;
import com.uirsos.www.uirsosproject.R;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by cunun12
 */

public class DetailBerita extends AppCompatActivity {

    TextView hari, tanggal, fakultas, judul;
    JustifiedTextView deskripsi;
    ImageView imageberita;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_berita);

        hari= (TextView) findViewById(R.id.hariBerita);
        tanggal = (TextView) findViewById(R.id.tanggalBerita);
        fakultas = (TextView) findViewById(R.id.tvFakultas);
        judul = (TextView) findViewById(R.id.tvJudulBerita);
        deskripsi = (JustifiedTextView) findViewById(R.id.tvDeskripsiBerita);
        imageberita = (ImageView) findViewById(R.id.imgBerita);

        ambilData();

        //kembali ke "ProfileActivity"
        ImageView back = (ImageView) findViewById(R.id.backArrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ambilData() {

        hari.setText(getIntent().getStringExtra("id_hari"));
        tanggal.setText(getIntent().getStringExtra("id_tanggal"));
        fakultas.setText(getIntent().getStringExtra("id_fakultas"));
        judul.setText(getIntent().getStringExtra("id_judul"));
        deskripsi.setText(getIntent().getStringExtra("id_deskripsi"));

        Glide.with(this)
                .load(getIntent().getStringExtra("id_img"))
                .into(imageberita);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
