package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    //views from xml
    Toolbar chat_toolbar;
    RecyclerView chat_recyclerView;
    ImageView chat_profileImageView;
    TextView chat_name,chat_status;
    EditText chat_messageEt;
    ImageView sendBtn;

    private static final String TAG = "ChatActivity";// 로그찍을때 태그

    //파이어베이스 인증
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String memberUid;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.v(TAG, "onCreate");

        //init views
        Toolbar chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        chat_toolbar.setTitle(""); // 채팅 툴바에 닉넴 표시하기
        chat_recyclerView=findViewById(R.id.chat_recyclerView); //  채팅 대화상자들
        chat_profileImageView=findViewById(R.id.chat_profileImageView);// 프로필 이미지
        chat_name=findViewById(R.id.chat_name); // 채팅 이름
        chat_status=findViewById(R.id.chat_status); // 사용자 온 or 오프라인
        chat_messageEt=findViewById(R.id.chat_messageEt); // 메세지 입력 창 박스
        sendBtn=findViewById(R.id.sendBtn); // 보내기 버튼



        /* On clicking user from users Memberlist we have passed that user's UID using intent
            * So get that uid here to get the profile picture, name and start chat with that user*/

            Intent intent = getIntent();
            memberUid = intent.getStringExtra("memberUid"); // MemberInfoAdapter에서 데이터를 가져옴
        Log.v(TAG, "memberUid : "+memberUid);

        //상대방 Uid를 통해 데이터 베이스에서 있는 member의 닉네임과
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
                        String url= (String) documentSnapshot.getData().get("photoUrl");

                        Log.v(TAG, "name data: " + name);
                        Log.v(TAG, "url data: " + url);

                        chat_name.setText(name);
                        try{
                            Picasso.get().load(url).placeholder(R.drawable.unnamed).into(chat_profileImageView);
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


        //firebase auth instance
        firebaseAuth=FirebaseAuth.getInstance();

    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

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

                    //get text from edit text
                    String message = chat_messageEt.getText().toString().trim();

                    //check if text is empty or not
                    if(TextUtils.isEmpty(message)){

                        //text empty
                        startToast(" 보낼 내용이 없습니다. ");

                    }else{
                        //text not empty
                    }

                    sendMessage(message);

                    break;

                //case R.id.loginbutton2:

                //    break;

            }
        }
    };

    private void sendMessage(String message) {
        /*"Chats" node will be created that will contain all chats
         * Whenever user sends message it will create new child in "Chats" node and that child will contain
         * the following key values
         * sender : UID of sender
         * receiver : UID if receiver
         * message : the actual message */

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender", myUid);
        hashMap.put("receiver",memberUid);
        hashMap.put("message",message);


    }

    private void myStartActivity(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
