package com.uirsos.www.uirsosproject.News;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uirsos.www.uirsosproject.Adapter.AdapterStatus;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.POJO.PostId;
import com.uirsos.www.uirsosproject.POJO.User;
import com.uirsos.www.uirsosproject.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cunun12
 */

public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";
    RecyclerView listStatus;
    LinearLayout addPostBtn;
    CircleImageView imgProfile;
    List<DataItemStatus> post_list;
    List<User> user_list;
    String postId;
    private AdapterStatus adapterStatus;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_id;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true; // untuk membuka halaman pertama diambil

    public StatusFragment() {
        //required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreateView: " + TAG);
        view.setTag(TAG);

        listStatus = (RecyclerView) view.findViewById(R.id.rvStatus);
        addPostBtn = (LinearLayout) view.findViewById(R.id.linear1);


        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        postId = FirebaseFirestore.getInstance().collection("User_Post").document().getId();

        imgProfile = (CircleImageView) view.findViewById(R.id.imgpost);
        firebaseFirestore.collection("Users") // ambil nama table
                .document(user_id)
                .get() // tampilkan berdasarkan id si user/pengguna
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String gambar = task.getResult().getString("image");

                                RequestOptions placeholderrequest = new RequestOptions();
                                placeholderrequest.placeholder(R.drawable.defaulticon);
                                Glide.with(getActivity())
                                        .setDefaultRequestOptions(placeholderrequest)
                                        .load(gambar)
                                        .into(imgProfile);

                                ambilStatus();
                            }
                        }
                    }
                });

        /*untuk pindah kehalaman NewPostActivity*/
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getActivity(), NewPostActivity.class);
                getActivity().startActivity(postIntent);
            }
        });

        post_list = new ArrayList<>();
        user_list = new ArrayList<>();
        adapterStatus = new AdapterStatus(post_list, user_list);
        listStatus.setLayoutManager(new LinearLayoutManager(getActivity()));
        listStatus.setAdapter(adapterStatus);

    }

    public void ambilStatus() {
        if (firebaseAuth.getCurrentUser() != null) {

            listStatus.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        loadMorePost();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("User_Post")
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            post_list.clear();
                            user_list.clear();

                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String postId = doc.getDocument().getId();

                                final DataItemStatus dataPost = doc.getDocument().toObject(DataItemStatus.class).withId(postId);

                                String blogUserId = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);


                                            if (isFirstPageFirstLoad) {
                                                user_list.add(user);
                                                post_list.add(dataPost);
                                            } else {
                                                user_list.add(0, user);
                                                post_list.add(0, dataPost);
                                            }

                                            adapterStatus.notifyDataSetChanged();
                                        }

                                    }
                                });


                            }

                        }

                    } else {
                        Log.d(TAG, "onEvent: Data Kosong");
                    }

                    isFirstPageFirstLoad = false;

                }
            });

        }
    }

    public void loadMorePost() {

        if (firebaseAuth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("User_Post")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String postId = doc.getDocument().getId();
                                final DataItemStatus dataPost = doc.getDocument().toObject(DataItemStatus.class).withId(postId);
                                String blogUserId = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);

                                            user_list.add(user);
                                            post_list.add(dataPost);

                                            adapterStatus.notifyDataSetChanged();
                                        }

                                    }
                                });


                            }

                        }
                    }

                }
            });

        }
    }
}
