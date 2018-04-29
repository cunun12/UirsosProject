package com.uirsos.www.uirsosproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsosproject.POJO.DataKomentarStatus;
import com.uirsos.www.uirsosproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class AdapterKomentar extends RecyclerView.Adapter<AdapterKomentar.Komentarholder> {

    public Context context;
    public List<DataKomentarStatus> listKomentar;

    private FirebaseFirestore firebaseFirestore;

    public AdapterKomentar(List<DataKomentarStatus> listKomentar) {
        this.listKomentar = listKomentar;
    }

    @Override
    public Komentarholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_komentar, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new Komentarholder(view);
    }

    @Override
    public void onBindViewHolder(final Komentarholder holder, final int position) {

        String user_id = listKomentar.get(position).getUser_id();
        firebaseFirestore.collection("Users")
                .document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        holder.setDataUser(name, image);

                    }
                });

        String komentar = listKomentar.get(position).getKomentar();
        holder.setKomentarUser(komentar);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = listKomentar.get(position).getTimestamp();
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

        if (detik < 1) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            holder.setKomentarDate(" Baru saja");
        } else if (detik < 60) {
            Log.d(TAG, "onBindViewHolder: detik" + detik);
            String postDetik = String.valueOf(detik);
            holder.setKomentarDate(postDetik + " Detik yang Lalu");
        } else if (menit < 60) {
            Log.d(TAG, "onBindViewHolder: menit" + menit);
            String postMenit = String.valueOf(menit);
            holder.setKomentarDate(postMenit + " Menit yang lalu");
        } else if (jam < 24) {
            Log.d(TAG, "onBindViewHolder: jam" + jam);
            String postJam = String.valueOf(jam);
            holder.setKomentarDate(postJam + " Jam yang Lalu");
        } else {
            Log.d(TAG, "onBindViewHolder: hari" + hari);
            String postHari = String.valueOf(hari);
            holder.setKomentarDate(postHari + " Hari yang lalu");
        }

//        try {
//            String waktuKemarin = listKomentar.get(position).getTimestamp();
//            holder.setKomentarDate(waktuKemarin);
//        } catch (Exception e) {
//            Log.d(TAG, "onBindViewHolder: " + e);
//        }

    }

    @Override
    public int getItemCount() {
        return listKomentar.size();
    }

    public class Komentarholder extends RecyclerView.ViewHolder {

        View mView;
        private TextView komentarUser;
        private TextView komentarDate;
        private TextView komentarUserName;
        private CircleImageView komentarUserImage;

        public Komentarholder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDataUser(String username, String image) {
            komentarUserName = mView.findViewById(R.id.nama_komentar);
            komentarUserImage = mView.findViewById(R.id.image_avatar);

            komentarUserName.setText(username);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.defaulticon);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(komentarUserImage);
        }

        public void setKomentarUser(String komentar) {
            komentarUser = mView.findViewById(R.id.komentar);
            komentarUser.setText(komentar);
        }

        public void setKomentarDate(String date) {
            komentarDate = mView.findViewById(R.id.waktu_komentar);
            komentarDate.setText(date);
        }


    }
}
