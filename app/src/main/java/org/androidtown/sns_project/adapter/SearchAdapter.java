package org.androidtown.sns_project.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.activity.SearchActivity;
import org.androidtown.sns_project.object.NewsData_business;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private static final String TAG = "SearchAdapter";// 로그찍을때 태그

    private List<NewsData_business> mDataset;
    private static View.OnClickListener onClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        //아이템의 요소를 연결해주는 부분
        // each data item is just a string in this case

        public TextView news_title;
        public TextView news_discription;
        public SimpleDraweeView news_images;
        public View rootView;

        public SearchViewHolder(View v) {
            super(v);
            //받아온 view를 통하여 view에 속해있는 요소를 할당해줘야함
            news_title = v.findViewById(R.id.news_title);
            news_discription=v.findViewById(R.id.news_discription);
            news_images = v.findViewById(R.id.news_image);
            rootView=v;

            v.setClickable(true);// 클릭을 할 수 있다. 없다.
            v.setEnabled(true); // 클릭 활성 상태 ( 클릭을 할 수 있게 활성화 되어있다. )
            v.setOnClickListener(onClickListener);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(List<NewsData_business> myDataset, Context context,View.OnClickListener onClick) {

        Fresco.initialize(context);
        mDataset = myDataset;
        onClickListener=onClick;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //item 레이아웃 연결 하는 것
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_news, parent, false);

        SearchViewHolder vh = new SearchViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        NewsData_business newsData_business=mDataset.get(position);

        holder.news_title.setText(newsData_business.getTitle());

        String description = newsData_business.getDescription();
        if(description != null && description.length() >0){
            holder.news_discription.setText(newsData_business.getDescription());
        }else {
            holder.news_discription.setText("-----( 중 략 )-----");
        }

        Uri uri = Uri.parse(newsData_business.getUrlToImage());
        Log.v(TAG, "uri : "+ uri);
        holder.news_images.setImageURI(uri);

        //tag - label - 아이템의 클릭 번호를 뜻함
        holder.rootView.setTag(position);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public NewsData_business getNews(int position){
        return mDataset != null ? mDataset.get(position) : null ;
    }
}
