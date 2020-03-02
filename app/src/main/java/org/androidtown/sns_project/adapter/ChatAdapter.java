package org.androidtown.sns_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.object.Chatinfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

        int viewType_cho=viewType;

        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        //get data
        String message=chatlist.get(position).getMessage();
        String timeStamp=chatlist.get(position).getTimestamp(); // ChatActivity에서 저장된 timestamp를 받아온다.

        //convert time stamp to yyyy/mm/dd hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime= DateFormat.format("yyyy/MM/dd HH:mm:ss",cal).toString();

        //Date now=new Date();

        //DateFormat dateTime = DateFormat.getDateInstance(DateFormat.SHORT);

        //현재 시간을 msec 으로 구한다.
        //long now = System.currentTimeMillis();

        //long long_timeStamp= Long.parseLong(timeStamp);
        //현재 시간을 date 변수에 저장한다.
        //Date date =  new Date(long_timeStamp);
        //시간을 나타내는 포맷을 정한다.
        //SimpleDateFormat sdfNow=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //nowDate 변수에 값을 저장한다.
        //String formatDate=sdfNow.format(sdfNow);


        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        try{
            Picasso.get().load(imageUrl).into(holder.profileIv);
        } catch (Exception e){

        }

        //String myUID=FirebaseAuth.getInstance().getCurrentUser().getUid();// 현재 로그인된 사용자의 uid 받아옴


            //click to show delete dialog
            holder.messageLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //show delete message confirm dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("수정 / 삭제");
                    builder.setMessage("메세지를 수정 또는 삭제 하시겠습니까?");


                    //cancel delete button
                    builder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            //dismiss dialog
                            dialog.dismiss();


                            editMessage(position);
                        }
                    });

                    //delte button
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deleteMessage(position);
                        }
                    });


                    //create and show dialog
                    builder.create().show();

                }
            });

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

    private void editMessage(final int position) {

        final String myUID=FirebaseAuth.getInstance().getCurrentUser().getUid();// 현재 로그인된 사용자의 uid 받아옴



        AlertDialog.Builder edit_builder1=new AlertDialog.Builder(context);
        //다이얼 로그를 보여주기 위해 edit_box.xml을 사용
        View edit_view1 = LayoutInflater.from(context).inflate(R.layout.edit_box,null,false );

        edit_builder1.setView(edit_view1);

        final Button chat_edit_button = (Button) edit_view1.findViewById(R.id.chat_edit_button);
        final EditText chat_edit_box=edit_view1.findViewById(R.id.chat_edit_box);

        final AlertDialog dialog = edit_builder1.create();
        dialog.show();


        chat_edit_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {



                final String edit_chat=chat_edit_box.getText().toString();

                /*Logic
                 * Get timestamp of clicked message
                 * Compare the timestamp of the clicked message with all messages in Chats
                 * Where both values matches delete that message*/

                String msgTimeStamp = chatlist.get(position).getTimestamp();
                DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Chats");
                Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){

                            /*if you want to allow sender to delete only member message then
                             * compare sender value with current user's uid
                             * if they match means its the message of sender that is trying to delete*/
                            if(ds.child("sender").getValue().equals(myUID)){
                                /*We can do one of two things here
                                 * 1) Remove the message from Chats
                                 * 2) Set the value of message "This message was deleted.."
                                 * So do whatever you want*/

                                // 1) Remove the message from Chats - 리사이클러뷰 아이템 삭제
                                //ds.getRef().removeValue();

                                // 2) Set the value of message "This message was deleted..." - 리사이클러뷰 아이템 내용 수정
                                HashMap<String,Object> hashMap = new HashMap<>();
                               // hashMap.put("message","(!) 삭제된 메세지 입니다. (!)");
                                hashMap.put("message",edit_chat);
                                ds.getRef().updateChildren(hashMap);
                                Toast.makeText(context,"메세지가 수정 되었습니다...", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(context,"상대방 메세지를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }

    private void deleteMessage(int position){

        final String myUID=FirebaseAuth.getInstance().getCurrentUser().getUid();// 현재 로그인된 사용자의 uid 받아옴



        /*Logic
        * Get timestamp of clicked message
        * Compare the timestamp of the clicked message with all messages in Chats
        * Where both values matches delete that message*/

        String msgTimeStamp = chatlist.get(position).getTimestamp();
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    /*if you want to allow sender to delete only member message then
                    * compare sender value with current user's uid
                    * if they match means its the message of sender that is trying to delete*/
                    if(ds.child("sender").getValue().equals(myUID)){
                        /*We can do one of two things here
                         * 1) Remove the message from Chats
                         * 2) Set the value of message "This message was deleted.."
                         * So do whatever you want*/

                        // 1) Remove the message from Chats - 리사이클러뷰 아이템 삭제
                        ds.getRef().removeValue();

                        // 2) Set the value of message "This message was deleted..." - 리사이클러뷰 아이템 내용 수정
                        //HashMap<String,Object> hashMap = new HashMap<>();
                        //hashMap.put("message","(!) 삭제된 메세지 입니다. (!)");
                        //ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context,"메세지가 삭제 되었습니다...", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"상대방 메세지를 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
        LinearLayout messageLayout; // for click listener to show delete

        public MyHolder(@NonNull View itemView){
            super(itemView);

            //init views
            profileIv=itemView.findViewById(R.id.profileIv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
           // isSeenTv=itemView.findViewById(R.id.isSeenTv);
            messageLayout=itemView.findViewById(R.id.messageLayout);
        }
    }


}
