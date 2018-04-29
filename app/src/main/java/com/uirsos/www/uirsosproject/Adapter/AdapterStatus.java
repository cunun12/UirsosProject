package com.uirsos.www.uirsosproject.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsosproject.News.KomentarActivity;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.POJO.User;
import com.uirsos.www.uirsosproject.Profile.FriendActivity;
import com.uirsos.www.uirsosproject.Profile.ProfileActivity;
import com.uirsos.www.uirsosproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12
 */

public class AdapterStatus extends RecyclerView.Adapter<AdapterStatus.StatusHolder> {

    private final static String LOG_TAG = "AdapterStatus";

    public List<DataItemStatus> post_list;
    public List<User> userList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public AdapterStatus(List<DataItemStatus> post_list, List<User> user_List) {
        this.post_list = post_list;
        this.userList = user_List;

    }

    @Override
    public StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(final StatusHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = post_list.get(position).PostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = post_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String imageUrl = post_list.get(position).getImage_post();
        String thumbUrl = post_list.get(position).getImage_thumb();
        holder.setPostImage(imageUrl, thumbUrl);

        final String user_id = post_list.get(position).getUser_id();

        String userName = userList.get(position).getName();
        String userImage = userList.get(position).getImage();
        holder.setUserData(userName, userImage);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String postTimestamp = post_list.get(position).getTimestamp();
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
            holder.setTime(postDetik + " Detik yang Lalu");
        } else if (menit < 60) {
            Log.d(TAG, "onBindViewHolder: menit" + menit);
            String postMenit = String.valueOf(menit);
            holder.setTime(postMenit + " Menit yang lalu");
        } else if (jam < 24) {
            Log.d(TAG, "onBindViewHolder: jam" + jam);
            String postJam = String.valueOf(jam);
            holder.setTime(postJam + " Jam yang Lalu");
        } else if (hari < jam) {
            Log.d(TAG, "onBindViewHolder: hari" + hari);
            String postHari = String.valueOf(hari);
            holder.setTime(postHari + " Hari yang lalu");
        } else {
            holder.setTime(postTimestamp);
        }


        //getCount
        firebaseFirestore.collection("User_Post/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null) {

                    int count = documentSnapshots.size();
                    holder.updateLikeCount(count);

                } else {

                    holder.updateLikeCount(0);

                }

            }
        });


        //getLikes
        firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (firebaseAuth.getCurrentUser() != null) {
                    if (e != null) {
                        Log.w(LOG_TAG, ":onEvent", e);
                        return;
                    }
                    if (documentSnapshot.exists()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.postLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                        }

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.postLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));
                        }
                    }
                }
            }
        });


        //Like Feature
        holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currentUserId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {

                            Map<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currentUserId).set(likeMap);

                        } else {

                            firebaseFirestore.collection("User_Post/" + postId + "/Likes").document(currentUserId).delete();

                        }
                    }
                });
            }
        });


        holder.lineUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClickUser: " + user_id);
//                Toast.makeText(context, user_id, Toast.LENGTH_SHORT).show();
                String currentUser = firebaseAuth.getCurrentUser().getUid();
//                Log.d(TAG, "onClickActif: " + currentUser);

                if (!currentUser.equals(user_id)) {

                    Log.d(TAG, "onClickFriend: ");
                    Intent komentar = new Intent(v.getContext(), FriendActivity.class);
                    komentar.putExtra("idUser", user_id);
                    v.getContext().startActivity(komentar);
                } else {
                    Log.d(TAG, "onClickProfile: ");
                    Intent komentar = new Intent(v.getContext(), ProfileActivity.class);
                    komentar.putExtra("idUser", user_id);
                    v.getContext().startActivity(komentar);
                }
            }
        });

        holder.postKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent komentar = new Intent(v.getContext(), KomentarActivity.class);
                komentar.putExtra("idPost", post_list.get(position).PostId);
                v.getContext().startActivity(komentar);
            }
        });
    }


    @Override
    public int getItemCount() {
        return post_list.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView postImage;
        private TextView postDate;
        private TextView postUserName;
        private CircleImageView postUserImage;

        private ImageView postLikeBtn;
        private TextView postLikeCount;
        private LinearLayout postKomentar;
        private LinearLayout lineUser;

        public StatusHolder(View itemView) {
            super(itemView);
            mView = itemView;

            postLikeBtn = mView.findViewById(R.id.post_Like);
            postKomentar = mView.findViewById(R.id.post_komentar);

            lineUser = mView.findViewById(R.id.line_User);
        }

        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.desc_post);
            descView.setText(descText);
        }


        public void setPostImage(String downloadUri, String downloadThumb) {

            postImage = mView.findViewById(R.id.post_image);
            Glide.with(context)
                    .load(downloadUri)
                    .thumbnail(Glide.with(context).load(downloadThumb))
                    .into(postImage);

        }

        public void setTime(String date) {
            postDate = mView.findViewById(R.id.waktu_post);
            postDate.setText(date);
        }

        @SuppressLint("CheckResult")
        public void setUserData(String name, String image) {
            postUserName = mView.findViewById(R.id.nama_Pengguna);
            postUserImage = mView.findViewById(R.id.img_Profile);

            postUserName.setText(name);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.defaulticon);
            Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(image).into(postUserImage);
        }

        @SuppressLint("SetTextI18n")
        public void updateLikeCount(int count) {
            postLikeCount = mView.findViewById(R.id.post_likeCount);
            postLikeCount.setText(count + " Likes");
        }
    }

//    private Context mContext;
//    private List<DataItemStatus> itemStatus;
//
//
//    public AdapterStatus(Context mContext, List<DataItemStatus> itemStatus) {
//        this.mContext = mContext;
//        this.itemStatus = itemStatus;
//    }
//
//    @Override
//    public StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
//
//        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.cardview_status, parent, false);
//        StatusHolder SH = new StatusHolder(mainGroup);
//        return SH;
//    }
//
//    @Override
//    public void onBindViewHolder(StatusHolder holder, int position) {
////        holder.namaPengguna.setText(itemStatus.get(position).getNamaPengguna());
////        holder.deskripsi.setText(itemStatus.get(position).getDeskripsi());
////        holder.imgAvatar.setText(itemStatus.get(position).getNamaPengguna());
////        holder.imgPost.setText(itemStatus.get(position).getNamaPengguna());
//
//        holder.komentar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(v.getContext(), KomentarActivity.class);
//                v.getContext().startActivity(i);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemStatus.size();
//    }
//
//    class StatusHolder extends RecyclerView.ViewHolder{
//
//        /*
//        Class ini berfungsi untuk mencasting sebuah variable objek dengan id tertentu
//         */
//
////        ImageView imgAvatar, imgPost;
//        TextView namaPengguna, deskripsi;
//        LinearLayout komentar;
//
//
//        public StatusHolder(View itemView) {
//            super(itemView);
//
////            imgAvatar = (ImageView) itemView.findViewById(R.id.img_Profile);
////            imgPost = (ImageView) itemView.findViewById(R.id.post_Pengguna);
//            namaPengguna = (TextView) itemView.findViewById(R.id.nama_Pengguna);
//            deskripsi = (TextView) itemView.findViewById(R.id.deskripsi_Pengguna);
//            komentar = (LinearLayout) itemView.findViewById(R.id.komentar_Pengguna);
//
//        }
//    }
}
