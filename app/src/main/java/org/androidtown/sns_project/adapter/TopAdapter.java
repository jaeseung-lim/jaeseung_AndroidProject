package org.androidtown.sns_project.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.object.NewsData_health;
import org.androidtown.sns_project.object.Topitem_Data;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.MyViewHolder> {

    private ArrayList<Topitem_Data> Top_Dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        // each data item is just a string in this case
        //public TextView textView;

        ImageView top_item_imageview;
        TextView top_item_title,top_item_date;

        public MyViewHolder(View v) {
            super(v);
            //textView = v;

            top_item_imageview=v.findViewById(R.id.top_item_imageview);
            top_item_title=v.findViewById(R.id.top_item_title);
            top_item_date=v.findViewById(R.id.top_item_date);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록 / ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 된다.

            MenuItem Edit=contextMenu.add(Menu.NONE,1001,1,"편집");
            MenuItem Delte=contextMenu.add(Menu.NONE,1002,2,"삭제");

            view.setOnCreateContextMenuListener(this);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TopAdapter(ArrayList<Topitem_Data> data) {
        Top_Dataset = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TopAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_maintop, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Topitem_Data topitem_data=Top_Dataset.get(position);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.top_item_title.setText(topitem_data.getItem_title());
        holder.top_item_date.setText(topitem_data.getItem_date());
        Uri uri = Uri.parse(topitem_data.getItem_imageURL());
        holder.top_item_imageview.setImageURI(uri);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return null !=Top_Dataset ? Top_Dataset.size() : 0 ;

    }

    private final MenuItem.OnMenuItemClickListener onMenuItemClickListener=new MenuItem.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()){

                case 1001:
                    break;

                case 1002:

                    break;
            }


            return false;
        }
    };


}