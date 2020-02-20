package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.androidtown.sns_project.Memberinfo;
import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.MemberInfoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {

    private static final String TAG = "ChattingActivity";
    private RecyclerView recyclerView;
    private MemberInfoAdapter memberInfoAdapter;
    private List<Memberinfo> memberinfoList;
    private RelativeLayout loaderLayout_memberlist;

    private FirebaseFirestore db;
    private DocumentSnapshot document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Log.v(TAG, "onCreate");

        loaderLayout_memberlist =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
        loaderLayout_memberlist.setVisibility(View.VISIBLE); //로딩 화면 보여주기

        recyclerView= findViewById(R.id.members_chatting_recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        memberinfoList=new ArrayList<Memberinfo>();
        
        getAllUsers();
        

        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.chatting_navi);// res - menu - item이름
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

                    case R.id.chatting_navi:

                        return true;


                }
                return false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////


    }

    private void getAllUsers() {

        Log.v(TAG, "getAllUsers 함수로 넘어옴");

        db=FirebaseFirestore.getInstance();
        db.collection("Members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            memberinfoList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                                Log.v(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                String name= (String) documentSnapshot.getData().get("name");
                                String intro= (String) documentSnapshot.getData().get("introduce");
                                String url= (String) documentSnapshot.getData().get("photoUrl");
                                String uid= (String) documentSnapshot.getData().get("memberUid");


                                Memberinfo memberinfo=new Memberinfo(name,intro,url,uid);

                                memberinfoList.add(memberinfo);

                            }
                            Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                            memberInfoAdapter = new MemberInfoAdapter(ChattingActivity.this, memberinfoList);
                            recyclerView.setAdapter(memberInfoAdapter);
                            loaderLayout_memberlist.setVisibility(View.GONE); //로딩 화면 보여주기
                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }

                        /*Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                        memberInfoAdapter = new MemberInfoAdapter(ChattingActivity.this, memberinfoList);
                        recyclerView.setAdapter(memberInfoAdapter);*/
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
        Log.v(TAG, "onResume/ PasswordResetActivity 보임");
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

