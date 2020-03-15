package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.ChatAdapter;
import org.androidtown.sns_project.adapter.MemberInfoAdapter;
import org.androidtown.sns_project.notifications.APIService;
import org.androidtown.sns_project.notifications.Client;
import org.androidtown.sns_project.notifications.Data;
import org.androidtown.sns_project.notifications.Response;
import org.androidtown.sns_project.notifications.Sender;
import org.androidtown.sns_project.notifications.Token;
import org.androidtown.sns_project.object.Chatinfo;
import org.androidtown.sns_project.object.Memberinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    //views from xml
    Toolbar chat_toolbar;
    RecyclerView chat_recyclerView;
    ImageView chat_profileImageView;
    TextView chat_name,chat_status;
    EditText chat_messageEt;
    ImageView sendBtn;

    private FirebaseUser user;
    private DocumentSnapshot document;

    private static final String TAG = "ChatActivity";// 로그찍을때 태그

    //파이어베이스 인증
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db,userRefForSeen;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    // chatting
    String memberUid;
    String myUid;
    String memberImage;

    //for checking if use has seen message or not
    //ValueEventListener seenListener;
    //FirebaseFirestore userRefForSeen;

    List<Chatinfo> chatList;
    ChatAdapter ChatAdapter;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.v(TAG, "onCreate");


        firebaseAuth=FirebaseAuth.getInstance();
        checkUserStatus();//로그인 한사람의 uid를 넣어줌

        //init views
        Toolbar chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        chat_toolbar.setTitle(""); // 채팅 툴바에 닉넴 표시하기
        chat_recyclerView=findViewById(R.id.chat_recyclerView); //  채팅 대화상자들
        chat_profileImageView=findViewById(R.id.chat_profileImageView);// 프로필 이미지
        chat_name=findViewById(R.id.chat_name); // 채팅 이름
        chat_status=findViewById(R.id.chat_status); // 사용자 온 or 오프라인
        chat_messageEt=findViewById(R.id.chat_messageEt); // 메세지 입력 창 박스
        findViewById(R.id.sendBtn).setOnClickListener(onClickListener); // 보내기 버튼


        //Layout (LinearLayout) for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // 리사이클러뷰 뒤에 부터 보여줌
        //recyclerView properties
        chat_recyclerView.setHasFixedSize(true);
        chat_recyclerView.setLayoutManager(linearLayoutManager);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        /* On clicking user from users Memberlist we have passed that user's UID using intent
            * So get that uid here to get the profile picture, name and start chat with that user*/

            Intent intent = getIntent();
            memberUid = intent.getStringExtra("memberUid"); // MemberInfoAdapter에서 member Uid 데이터를 가져옴
            Log.v(TAG, "memberUid : "+memberUid);

            //firebase auth instance
            firebaseAuth=FirebaseAuth.getInstance();

            //firebaseDatabase=FirebaseDatabase.getInstance();
            //usersDbRef=firebaseDatabase.getReference("Users");

            //search user to get that user's info
        //Query userQuery=usersDbRef.orderByChild("uid").equalTo(memberUid);
        //get user picture and name

        //상대방 Uid를 통해 데이터 베이스에서 있는 member의 닉네임과 이미지 불러오기
        db=FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Members").document(memberUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.v(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                        String name= (String) documentSnapshot.getData().get("name");
                        memberImage= (String) documentSnapshot.getData().get("photoUrl");

                        Log.v(TAG, "name data: " + name);
                        Log.v(TAG, "url data: " + memberImage);

                        chat_name.setText(name);
                        try{
                            Picasso.get().load(memberImage).placeholder(R.drawable.unnamed).into(chat_profileImageView);
                        } catch (Exception e){
                            Picasso.get().load(R.drawable.unnamed).placeholder(R.drawable.unnamed).into(chat_profileImageView);
                        }

                    } else {
                        Log.v(TAG, "No such document");
                    }
                } else {
                    Log.v(TAG, "get failed with ", task.getException());
                }
            }
        });
        
        readMessages();


    }



    private void readMessages() {

        Log.v(TAG, "readMessages");

        chatList=new ArrayList<>();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chatinfo chatinfo=ds.getValue(Chatinfo.class);
                    if(chatinfo.getReceiver().equals(myUid) && chatinfo.getSender().equals(memberUid) ||
                            chatinfo.getReceiver().equals(memberUid)&& chatinfo.getSender().equals(myUid))
                    chatList.add(chatinfo);
                }

                //adapter
                ChatAdapter = new ChatAdapter(ChatActivity.this,chatList,memberImage);
                ChatAdapter.notifyDataSetChanged();
                //set adapter to recyclerview
                chat_recyclerView.setAdapter(ChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       /* chatList=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        db.collection("Chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            chatList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                                String receiver= (String) documentSnapshot.getData().get("receiver");
                                String sender= (String) documentSnapshot.getData().get("sender");
                                String message= (String) documentSnapshot.getData().get("message");
                                Log.v(TAG, "receiver => " + receiver);
                                Log.v(TAG, "sender => " + sender);
                                Log.v(TAG, "message => " + message);



                                if(receiver.equals(myUid) && sender.equals(memberUid) || receiver.equals(memberUid) && sender.equals(myUid)){
                                    Chatinfo chatinfo = new Chatinfo();
                                    chatinfo.setReceiver(receiver);
                                    chatinfo.setSender(sender);
                                    chatinfo.setMessage(message);

                                    chatList.add(chatinfo);
                                    Log.v(TAG, "chatList.size() => " + chatList.size());

                                }

                                //Chatinfo chatinfo = (Chatinfo) documentSnapshot.getData();
                                //Log.v(TAG, "chatinfo => " + chatinfo);

                                ChatAdapter=new ChatAdapter(ChatActivity.this,chatList,memberImage);
                                ChatAdapter.notifyDataSetChanged();
                                //set adapter to recyclerView
                                chat_recyclerView.setAdapter(ChatAdapter);


                            }
                            Log.v(TAG, "데이터 불러오기 반복문 끝! ");

                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }

                    }


                });*/
    }

    private void sendMessage(final String message) {

        Log.v(TAG, "sendMessage 메서드 시작");


        //현재 시간을 msec 으로 구한다.
        //long now = System.currentTimeMillis();
        //현재 시간을 date 변수에 저장한다.
        //Date date =  new Date(now);
        //시간을 나타내는 포맷을 정한다.
        //SimpleDateFormat sdfNow=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //nowDate 변수에 값을 저장한다.
        //String formatDate=sdfNow.format(date);

        String formatDate = String.valueOf(System.currentTimeMillis()); // 현재 시간을 파이어베이스에 저장해주고

        Log.v(TAG, "formatDate : " + formatDate);

        //firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", memberUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", formatDate);

        databaseReference.child("Chats").push().setValue(hashMap);


        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Members").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.v(TAG, "sendMessage 메서드의 DocumentSnapshot data: " + document.getData());
                        //document.getData().get("photoUrl");

                        String membername = (String) document.getData().get("name");

                        Log.v(TAG, "sendMessage 메서드의 document.getData().get(\"name\") : " + membername);

                        Log.v(TAG, "notify  : " + notify);
                        if (notify) {

                            //sendNotification(memberUid,memberinfo.getName(),message);
                            Log.v(TAG, "sendNotification 파라메터 memberUid : " + memberUid);
                            Log.v(TAG, "sendNotification 파라메터 membername : " + membername);
                            Log.v(TAG, "sendNotification 파라메터 message: " + message);

                            sendNotification(memberUid, membername, message);
                        }

                        notify = false;
                        Log.v(TAG, "notify  : " + notify);

                    } else {
                        Log.v(TAG, "No such document");
                    }
                } else {
                    Log.v(TAG, "get failed with ", task.getException());
                }
            }
        });


        //String msg=message;
        //내 uid 가져오기
        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference("members").child(myUid);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Memberinfo memberinfo = dataSnapshot.getValue(Memberinfo.class);

            if(notify){
                //sendNotification(memberUid,memberinfo.getName(),message);
                sendNotification(memberUid,memberinfo.getName(),message);
            }

            notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*"Chats" node will be created that will contain all chats
         * Whenever user sends message it will create new child in "Chats" node and that child will contain
         * the following key values
         * sender : UID of sender
         * receiver : UID if receiver
         * message : the actual message */

        /*db=FirebaseFirestore.getInstance();

        //현재 시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        //현재 시간을 date 변수에 저장한다.
        Date date =  new Date(now);
        //시간을 나타내는 포맷을 정한다.
        SimpleDateFormat sdfNow=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //nowDate 변수에 값을 저장한다.
        String formatDate=sdfNow.format(date);

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender", myUid);
        hashMap.put("receiver",memberUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",formatDate);
        //hashMap.put("isSeen",false);

        Log.v(TAG, "Chats :"+hashMap);

        String chatroomUID=setOneToOneChat(memberUid, myUid);

        db.collection("Chats").document(chatroomUID).set(hashMap);

        //reset edittext after sending message
        chat_messageEt.setText("");*/

        /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender",myUid );
        hashMap.put("receiver",memberUid );
        hashMap.put("message",message );
        databaseReference.child("Chats").push().setValue(hashMap);

        //reset edittecxt after sending message
        chat_messageEt.setText("");*/

        //////////////////////////////////////////////////////////////////////////////////////////////////

        //create chatlist node/child in firebase database
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUid)
                .child(memberUid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(memberUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(memberUid)
                .child(myUid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(myUid);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void sendNotification(final String memberUid, final String notiName, final String message){

        Log.v(TAG, " sendNotification 함수 시작 ");
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(memberUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.v(TAG, " dataSnapshot.getChildren() 함수 시작 ");
                    Token token = ds.getValue(Token.class);
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 token  "+ token.getToken());
                    Data data = new Data(myUid,notiName+" : "+message,"새로운 메시지가 도착했습니다.",memberUid,R.drawable.unnamed);
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 data data.getUser() : "+data.getUser());
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 data data.getBody() : "+data.getBody());
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 data data.getTitle() : "+data.getTitle());
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 data data.getSent() : "+data.getSent());

                    Sender sender = new Sender(data,token.getToken());
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 sender "+sender.getData());
                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 sender "+sender.getTo());

                    apiService.SendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Log.v(TAG, " dataSnapshot.getChildren() 함수의 onResponse 응답 성공 ");
                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String setOneToOneChat(String uid1, String uid2)
    {
        //Check if user1’s id is less than user2's

        /*int uid1length=uid1.length();
        int uid2length=uid2.length();

        Log.v(TAG, "uid1length : "+uid1length);
        Log.v(TAG, "uid2length : "+uid2length);


        if(uid1length < uid2length){
            Log.v(TAG, "UID1 가 더 길다. ");
            return uid1+uid2;
        }
        else{
            Log.v(TAG, "UID2 가 더 길다.");
            return uid2+uid1;
        }*/

        /*String[] strings = {uid1,uid2};

        return Arrays.sort(strings, String.CASE_INSENSITIVE_ORDER);*/

        String str = uid1+uid2;
        char[] sol = str.toCharArray();
        Arrays.sort(sol);

        String transStr=new StringBuilder(new String(sol)).reverse().toString();

        Log.v(TAG, "Uid1 :"+uid1);
        Log.v(TAG, "Uid2 :"+uid2);
        Log.v(TAG, "uid1+uid2 :"+str);
        Log.v(TAG, "transStr :"+transStr);

        return transStr;

    }

    private void checkUserStatus(){
        //get current user
       // FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

        if(user != null){
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
            myUid=user.getUid();// currently signed in user's uid

        }else{
            //user not signed in, go to main activity

        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ 다른 보임");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }



    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){

                case R.id.sendBtn:
                    notify=true;
                    Log.v(TAG, "sendBtn 버튼 클릭 ");
                    //get text from edit text
                    String message = chat_messageEt.getText().toString().trim();

                    //check if text is empty or not
                    if(TextUtils.isEmpty(message)){

                        //text empty
                        startToast(" 보낼 내용이 없습니다. ");

                    }else{
                        //text not empty
                        sendMessage(message);
                    }

                    //reset edittecxt after sending message
                    chat_messageEt.setText("");

                    break;

                //case R.id.loginbutton2:

                //    break;

            }
        }
    };



    private void myStartActivity(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
