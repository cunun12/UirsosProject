package com.uirsos.www.uirsosproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uirsos.www.uirsosproject.POJO.DataItemStatus;
import com.uirsos.www.uirsosproject.Profile.ViewPost;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.Utils.SquareImageView;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by cunun12 on 02/04/2018.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.HistoryHolder> {

    public List<DataItemStatus> listHistory;
    public Context mContext;

    public AdapterHistory(List<DataItemStatus> listHistory) {
        this.listHistory = listHistory;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_imageview, parent, false);
        mContext = parent.getContext();
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {

        final String imageUrl = listHistory.get(position).getImage_thumb();
        final String userid = listHistory.get(position).getUser_id();
        Log.d(TAG, "onBindViewHolder user: "+userid+ " image : "+imageUrl);
        holder.setPostImage(imageUrl);

        holder.rela_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "bagian "+userid + "ini image"+imageUrl, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(v.getContext(), ViewPost.class);
                i.putExtra("idPost", imageUrl);
                i.putExtra("idUser", userid);
                v.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {

        private View iView;

        private SquareImageView postImage;
        RelativeLayout rela_Post;

        public HistoryHolder(View itemView) {
            super(itemView);

            iView = itemView;
            rela_Post = iView.findViewById(R.id.relapost);
        }


        public void setPostImage(final String downloadUri) {
            postImage = iView.findViewById(R.id.image_grid);
            Glide.with(mContext)
                    .load(downloadUri)
                    .into(postImage);

//            postImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(v.getContext(), ViewPost.class);
//                    i.putExtra("idPost", downloadUri);
//                    v.getContext().startActivity(i);
//                }
//            });
        }
    }
}
