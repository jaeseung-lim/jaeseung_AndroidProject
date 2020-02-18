package org.androidtown.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidtown.sns_project.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private ArrayList<String> mDataset;
    private static final String TAG = "GalleryAdapter";// 로그찍을때 태그
    private Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GalleryAdapter(Activity activity,ArrayList<String> myDataset) {

        mDataset = myDataset;
        this.activity=activity;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        // create a new view
        CardView item_cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false); //아이템 하나의 레이아웃
            //...
       // GalleryViewHolder galleryViewHolder = new GalleryViewHolder(item_cardView);

        return new GalleryViewHolder(item_cardView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {

        CardView cardView=holder.cardView;// GalleryActivity에서 보내준 mDataset을 받아옴 mDataset.get(position) 은 경로를 뜻함

        cardView.setOnClickListener(new View.OnClickListener() { // 클릭한 아이템의 포지션을 통하여 해당 이미지를
            @Override
            public void onClick(View view) {
                Intent resultIntent=new Intent();
                resultIntent.putExtra("profilePath", mDataset.get(holder.getAdapterPosition()));
                Log.v(TAG, "(CameraActivity)profilePath : "+mDataset.get(holder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK,resultIntent);  // --> MemberinitActivity의 onActivityResult로 값을 보내줌

                activity.finish();// 아이템을 클릭시 엑티비티를 종료 시킨다.
            }
        });

        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        ImageView item_gallery_imageView=cardView.findViewById(R.id.item_gallery_imageView);// GalleryActivity에서 보내준 mDataset을 받아옴 mDataset.get(position) 은 경로를 뜻함
        Log.v(TAG, "profilePath : " + mDataset.get(position));
        //Bitmap bmp= BitmapFactory.decodeFile(mDataset.get(position));
        //item_gallery_imageView.setImageBitmap(bmp);
        Glide.with(activity).load(mDataset.get(position)).centerCrop().thumbnail(0.1f).override(840).into(item_gallery_imageView); // 이미지 보정하여 보여주는 라이브러리  (이미지 로딩 )

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}

