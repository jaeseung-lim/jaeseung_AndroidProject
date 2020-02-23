package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.androidtown.sns_project.object.Memberinfo;
import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.MemberInfoAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemberlistActivity extends AppCompatActivity {

    private static final String TAG = "MemberlistActivity";
    private RecyclerView recyclerView;
    private MemberInfoAdapter memberInfoAdapter;
    private List<Memberinfo> memberinfoList;
    private RelativeLayout loaderLayout_memberlist;

    private FirebaseFirestore db;
    private DocumentSnapshot document;

    //private List<String> items= Arrays.asList("어벤져스","배트맨","배트맨2","배구","슈퍼맨");// SearchView에서 검색된 리스트를 표시할 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlist);
        Log.v(TAG, "onCreate");

        loaderLayout_memberlist =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
        loaderLayout_memberlist.setVisibility(View.VISIBLE); //로딩 화면 보여주기

        recyclerView= findViewById(R.id.members_memberlist_recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        memberinfoList=new ArrayList<Memberinfo>();

        getAllUsers();


        ////////////////////////////////////////
        SearchView searchView = findViewById(R.id.memberlist_searchview); // SearchView
        //final TextView resultTextView=findViewById(R.id.textView2);// 입력 결과 TextView
        //resultTextView.setText(getResultSearch());//


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {// SearchView에 text입력한 값을 받아옴

            @Override
            public boolean onQueryTextSubmit(String query) { // SearchView에 text를 입력하고 엔터를 눌렀을때 호출되는 부분

                //검색 쿼리가 비어 있지 않으면 검색을 시작한다.
                if(!TextUtils.isEmpty(query.trim())){
                    // 쿼리가 있다면 해당 쿼리 검색
                    searchMembers(query);
                }else{
                    //검색어가 없다면, 모든 유저 불러오기
                    getAllUsers();
                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String query) { // 유저가 text를 입력하는 순간 불려진다.

                //resultTextView.setText(search(newText));// 입력한 값을 바꿔줌 텍스트 뷰에 표시

                //검색 쿼리가 비어 있지 않으면 검색을 시작한다.
                if(!TextUtils.isEmpty(query.trim())){
                    // 쿼리가 있다면 해당 쿼리 검색
                    searchMembers(query);
                }else{
                    //검색어가 없다면, 모든 유저 불러오기
                    getAllUsers();
                }


                return true; // 잘 전달 되었다는 것을 알려주기 위해 true 사용

            }
        });


        ////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.memberlist_navi);// res - menu - item이름
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

                        return true;

                    case R.id.chatlist_navi:

                        startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                        overridePendingTransition(0, 0);

                        return true;


                }
                return false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////


    }

    private void searchMembers(final String query) {

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

                                //검색 조건
                                // 1) 현재 유저는 검색 x
                                // 2) The member name or email contains text entered in SearchView (case insensitive)


                                Log.v(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                String name= (String) documentSnapshot.getData().get("name");
                                String intro= (String) documentSnapshot.getData().get("introduce");
                                String url= (String) documentSnapshot.getData().get("photoUrl");
                                String uid= (String) documentSnapshot.getData().get("memberUid");


                                Memberinfo memberinfo=new Memberinfo(name,intro,url,uid);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

                                //모든 맴버 불러오기  (현재 접속된 유저는 제외하고 )
                                assert user != null;
                                if(!memberinfo.getMemberUid().equals(user.getUid())) {

                                    if(memberinfo.getName().toLowerCase().contains(query.toLowerCase())
                                            || memberinfo.getIntroduce().toLowerCase().contains(query.toLowerCase())){

                                        memberinfoList.add(memberinfo);

                                    }



                                }

                            }
                            Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                            //어댑터
                            memberInfoAdapter = new MemberInfoAdapter(MemberlistActivity.this, memberinfoList);
                            //어뎁터 새로고침
                            memberInfoAdapter.notifyDataSetChanged();
                            // 어뎁터 리사이클러뷰에 연결
                            recyclerView.setAdapter(memberInfoAdapter);

                            loaderLayout_memberlist.setVisibility(View.GONE); //로딩 화면 보여주기
                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }

                        /*Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                        memberInfoAdapter = new MemberInfoAdapter(MemberlistActivity.this, memberinfoList);
                        recyclerView.setAdapter(memberInfoAdapter);*/
                    }


                });

    }

   /* private String search(String query){

        StringBuilder sb=new StringBuilder();

        for(int i = 0; i<items.size();i++){

            String item = items.get(i);

            if(item.toLowerCase().contains(query.toLowerCase())) {
                sb.append(item);

                if (i != items.size() - 1) {
                    sb.append("\n");
                }
            }

        }
        return sb.toString();

    }*/

    /*private String getResultSearch(){

        StringBuilder sb=new StringBuilder();//문자열을 합쳐주는 거

        for(int i = 0; i<items.size();i++){
            String item = items.get(i);
            sb.append(item);

            if(i != items.size()-1 ){
                sb.append("\n");
            }
        }

        return sb.toString(); // for문을 통해 합친 문자열들을 리턴

    }*/

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

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

                                //모든 맴버 불러오기  (현재 접속된 유저는 제외하고 )
                                assert user != null;
                                if(!memberinfo.getMemberUid().equals(user.getUid())){
                                    memberinfoList.add(memberinfo);
                                }

                            }
                            Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                            memberInfoAdapter = new MemberInfoAdapter(MemberlistActivity.this, memberinfoList);
                            recyclerView.setAdapter(memberInfoAdapter);
                            loaderLayout_memberlist.setVisibility(View.GONE); //로딩 화면 보여주기
                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }

                        /*Log.v(TAG, "데이터 불러오기 반복문 끝! ");
                        memberInfoAdapter = new MemberInfoAdapter(MemberlistActivity.this, memberinfoList);
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

