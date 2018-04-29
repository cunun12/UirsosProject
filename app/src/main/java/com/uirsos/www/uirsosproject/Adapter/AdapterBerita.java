package com.uirsos.www.uirsosproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uirsos.www.uirsosproject.News.DetailBerita;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.POJO.DataItemBerita;

import java.util.List;

/**
 * Created by cunun12
 */

public class AdapterBerita extends RecyclerView.Adapter<AdapterBerita.HolderBerita> {

    Context mContext;
    List<DataItemBerita> itemBerita;

    public AdapterBerita(Context mContext, List<DataItemBerita> itemBerita) {
        this.mContext = mContext;
        this.itemBerita = itemBerita;
    }

    @Override
    public HolderBerita onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_berita, null);
        return new HolderBerita(v);
    }

    @Override
    public void onBindViewHolder(HolderBerita holder, final int position) {
        holder.tanggal.setText(itemBerita.get(position).getTanggal());
        holder.fakultas.setText(itemBerita.get(position).getFakultas());
        holder.judul.setText(itemBerita.get(position).getJudul());
        holder.hari.setText(itemBerita.get(position).getHari());
        holder.deskripsi.setText(itemBerita.get(position).getIsi_berita());

        Glide.with(mContext)
                .load(itemBerita.get(position).getImg())
                .into(holder.imageberita);

        holder.mItemBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DetailBerita.class);
                i.putExtra("id_tanggal", itemBerita.get(position).getTanggal());
                i.putExtra("id_hari", itemBerita.get(position).getHari());
                i.putExtra("id_fakultas", itemBerita.get(position).getFakultas());
                i.putExtra("id_judul", itemBerita.get(position).getJudul());
                i.putExtra("id_deskripsi", itemBerita.get(position).getIsi_berita());
                i.putExtra("id_img", itemBerita.get(position).getImg());
                v.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemBerita.size();
    }

    class HolderBerita extends RecyclerView.ViewHolder {

        /**
         * class ini untuk casting sebuah variable untuk object dengan id tertentu.
         *
         * @param itemView
         */

        CardView mItemBerita;
        TextView hari, tanggal, fakultas, judul, deskripsi;
        ImageView imageberita;

        public HolderBerita(View itemView) {
            super(itemView);

            mItemBerita = (CardView) itemView.findViewById(R.id.itemBerita);
            imageberita = (ImageView) itemView.findViewById(R.id.imgBerita);
            hari        = (TextView) itemView.findViewById(R.id.hariBerita);
            tanggal     = (TextView) itemView.findViewById(R.id.tanggalBerita);
            fakultas    = (TextView) itemView.findViewById(R.id.tvFakultas);
            judul       = (TextView) itemView.findViewById(R.id.tvJudulBerita);
            deskripsi   = (TextView) itemView.findViewById(R.id.tvDeskripsiBerita);
        }
    }
}
