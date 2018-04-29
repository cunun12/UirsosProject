package com.uirsos.www.uirsosproject.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by cunun12 on 05/02/2018.
 */

public class RecyclerItemClickListiner implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListin";

    private GestureDetector gestureDetector;
    private OnItemClickListener clickListener;

    public RecyclerItemClickListiner(Context context, final RecyclerView recyclerView, final OnItemClickListener onItemClickListener) {
        Log.d(TAG, "RecyclerItemClickListiner: " );

        this.clickListener = onItemClickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: " + e);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child!=null && onItemClickListener != null){

                    onItemClickListener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                }

                Log.d(TAG, "onLongPress: "+e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child =rv.findChildViewUnder(e.getX(), e.getY());
        if (child!= null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onItemClick(child, rv.getChildAdapterPosition(child));
        }
        Log.d(TAG, "onInterceptTouchEvent: ");
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onTouchEvent: " + gestureDetector.onTouchEvent(e)+""+e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.d(TAG, "onRequestDisallowInterceptTouchEvent: " + disallowIntercept);
    }

    public static interface OnItemClickListener{
        public void onItemClick(View view, int position);
        public void onLongClick(View view, int position);
    }


//    private OnItemClickListener mListener;
//
//    public interface OnItemClickListener {
//        public void onItemClick(View view, int position);
//    }
//
//    GestureDetector mGestureDetector;
//
//    public RecyclerItemClickListiner(Context context, OnItemClickListener listener) {
//        mListener = listener;
//        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
//        View childView = view.findChildViewUnder(e.getX(), e.getY());
//        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
//        }
//        return false;
//    }
//
//    @Override
//    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
//    }
//
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//    }
}
