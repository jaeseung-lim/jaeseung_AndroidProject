package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.AdapterChatlist;
import org.androidtown.sns_project.adapter.MemberInfoAdapter;
import org.androidtown.sns_project.object.Chatinfo;
import org.androidtown.sns_project.object.Memberinfo;
import org.androidtown.sns_project.object.ModelChatlist;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";// 로그찍을때 태그

    //firebase auth
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    List<ModelChatlist> chatlistList;
    List<Memberinfo> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        //init
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.chatlist_navi);// res - menu - item이름
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.search_navi :

                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.home_navi :

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);


                        return true;

                    case R.id.profile_navi :

                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.memberlist_navi:
                        startActivity(new Intent(getApplicationContext(), MemberlistActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.chatlist_navi:


                        return true;


                }
                return false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void loadChats() {
        userList = new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        db.collection("Members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                Log.v(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                String name= (String) documentSnapshot.getData().get("name");
                                String intro= (String) documentSnapshot.getData().get("introduce");
                                String url= (String) documentSnapshot.getData().get("photoUrl");
                                String uid= (String) documentSnapshot.getData().get("memberUid");


                                Memberinfo memberinfo=new Memberinfo(name,intro,url,uid);

                                for(ModelChatlist chatlist: chatlistList){
                                    if(memberinfo.getMemberUid() != null && memberinfo.getMemberUid().equals(chatlist.getId())){
                                        userList.add(memberinfo);
                                        break;
                                    }
                                }

                            }
                           //adapter
                            adapterChatlist = new AdapterChatlist(ChatListActivity.this,userList);
                            //setadapter
                            recyclerView.setAdapter(adapterChatlist);
                            //set last message
                            for(int i = 0; i < userList.size();i++){
                                lastMessage(userList.get(i).getMemberUid());
                            }
                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }

                        /*Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                        memberInfoAdapter = new MemberInfoAdapter(MemberlistActivity.this, memberinfoList);
                        recyclerView.setAdapter(memberInfoAdapter);*/
                    }


                });
    }

    private void lastMessage(final String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chatinfo chatinfo = ds.getValue(Chatinfo.class);
                    if(chatinfo==null){
                        continue;
                    }
                    String sender = chatinfo.getSender();
                    String receiver = chatinfo.getReceiver();

                    if(sender == null || receiver == null){
                        continue;
                    }

                    if(chatinfo.getReceiver().equals(currentUser.getUid()) && chatinfo.getSender().equals(userId)
                            || chatinfo.getReceiver().equals(userId) && chatinfo.getSender().equals(currentUser.getUid())){

                        theLastMessage = chatinfo.getMessage();

                    }
                }

                adapterChatlist.setLastMessageMap(userId,theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                case R.id.sendPasswordButton:
                    Log.v(TAG, "비번 찾기 버튼 누름");

                    break;

                case R.id.loginbutton2:

                    break;

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
