package org.androidtown.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.object.Chatinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;

    List<Chatinfo> chatlist;
    String imageUrl;
    FirebaseUser fUser; // = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

    public ChatAdapter(Context context, List<Chatinfo> chatlist, String imageUrl) {
        this.context = context;
        this.chatlist = chatlist;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts : row_chat_left.xml for receiver, row_chat_right.xml for sender

        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //get data
        String message=chatlist.get(position).getMessage();
        String timeStamp=chatlist.get(position).getTimestamp();

        //convert time stamp to yyyy/mm/dd hh:mm am/pm
        //Calendar calendar = Calendar.getInstance(Locale.KOREA);
        //calendar.setTimeInMillis(Long.parseLong(timeStamp));
        //String dateTime= DateFormat.format("").toString();

        //Date now=new Date();

        //DateFormat dateTime = DateFormat.getDateInstance(DateFormat.SHORT);

        //현재 시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        //현재 시간을 date 변수에 저장한다.
        Date date =  new Date(now);
        //시간을 나타내는 포맷을 정한다.
        SimpleDateFormat sdfNow=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //nowDate 변수에 값을 저장한다.
        String formatDate=sdfNow.format(date);


        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(formatDate);

        try{
            Picasso.get().load(imageUrl).into(holder.profileIv);
        } catch (Exception e){

        }

        //set seen/delivered status of message
        /*if(position==chatlist.size()-1){

            if(chatlist.get(position).isSeen()){
                holder.isSeenTv.setText("읽음");
            }else {
                holder.isSeenTv.setText("전송");
            }

        }else{
            holder.isSeenTv.setVisibility(View.GONE);
        }*/


    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currenty signed in user

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if(chatlist.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            //init views
            profileIv=itemView.findViewById(R.id.profileIv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
           // isSeenTv=itemView.findViewById(R.id.isSeenTv);
        }
    }

}
