package com.uirsos.www.uirsosproject.News;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uirsos.www.uirsosproject.Adapter.AdapterBerita;
import com.uirsos.www.uirsosproject.HandelRequest.APIServer;
import com.uirsos.www.uirsosproject.HandelRequest.RequestHandler;
import com.uirsos.www.uirsosproject.R;
import com.uirsos.www.uirsosproject.POJO.DataItemBerita;
import com.uirsos.www.uirsosproject.Utils.GridImageDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by cunun12
 */

public class BeritaFragment extends Fragment {
    private static final String TAG = "BeritaFragment";
    RecyclerView listBerita;
    GridLayoutManager gridBerita;
    List<DataItemBerita> itemsberita;
    AdapterBerita adapterBerita;
    ProgressDialog pd;
    SwipeRefreshLayout refreshBerita;
    LinearLayout koneksiLine, jaringanLine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_berita, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pd = new ProgressDialog(getActivity());
        listBerita = (RecyclerView) view.findViewById(R.id.rvBerita);
        gridBerita = new GridLayoutManager(getActivity(), 2);
        koneksiLine = view.findViewById(R.id.koneksi);
        koneksiLine.setVisibility(View.GONE);
        jaringanLine = view.findViewById(R.id.jaringan);
        jaringanLine.setVisibility(View.GONE);

        listBerita.setLayoutManager(gridBerita);
        listBerita.setHasFixedSize(true);

        itemsberita = new ArrayList<>();

        loadBerita();

        adapterBerita = new AdapterBerita(this.getActivity(), itemsberita);
        listBerita.setAdapter(adapterBerita);
        adapterBerita.notifyDataSetChanged();

        refreshBerita = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refreshBerita.setColorSchemeColors(R.color.colorGreen);
        refreshBerita.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                itemsberita.clear();

                if (checkInternet()) {

                    loadBerita();
                    jaringanLine.setVisibility(View.GONE);

                } else {

                    Log.d(TAG, "run: Koneksi gagal");
                    jaringanLine.setVisibility(View.VISIBLE);

                }

                refreshBerita.setRefreshing(false);
            }
        }, 1000);
    }

    /*
    mengambil data dari API webServer menggunakan Volley
     */
    private void loadBerita() {

        StringRequest request = new StringRequest(Request.Method.GET, APIServer.Berita,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        koneksiLine.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: " + response.toString());
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                Log.d(TAG, "onResponse: " + data.toString());

                                DataItemBerita berita = new DataItemBerita();
                                berita.setJudul(data.getString("judul"));
                                berita.setIsi_berita(data.getString("isi_berita"));
                                berita.setFakultas(data.getString("fakultas"));
                                berita.setHari(data.getString("hari"));
                                berita.setTanggal(data.getString("tanggal"));
                                berita.setImg(data.getString("gambar"));
                                itemsberita.add(berita);
                            }
                            adapterBerita.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.getMessage();
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ini kesalahannya", error.toString());

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            // Is thrown if there's no network connection or server is down
                            koneksiLine.setVisibility(View.VISIBLE);
                            // We return to the last fragment
//                            if (getFragmentManager().getBackStackEntryCount() != 0) {
//                                getFragmentManager().popBackStack();
//                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            koneksiLine.setVisibility(View.VISIBLE);
                            // We return to the last fragment
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
    }

    public boolean checkInternet() {
        boolean connectstatus;

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            connectstatus = true;
        } else {
            connectstatus = false;
        }

        return connectstatus;
    }
}
