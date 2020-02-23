package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;

public class MainActivity extends AppCompatActivity {

    public boolean 로그아웃=false;

    private static final String TAG = "MainActivity";// 로그찍을때 태그

    private RelativeLayout loaderLayout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "onCreate");

        loaderLayout_main =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
        loaderLayout_main.setVisibility(View.VISIBLE); //로딩 화면 보여주기

        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home_navi);// res - menu - item이름
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.search_navi :

                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.home_navi :

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

                        startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                        overridePendingTransition(0, 0);

                        return true;


                }
                return false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴
        Log.v(TAG, "user : "+user);
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어베이스 DB 초기화


        if(user==null){ // 만약 현재 로그인한 유저가 없다면..

            Log.v(TAG, "로그인 유저 없음");
            Log.v(TAG, "로그인 엑티비티로 이동");
            loaderLayout_main.setVisibility(View.GONE); //로딩 화면 보여주기
            myStartActivity(LoginActivity.class,0); // 로그인된 유저가 없다면 로그인 페이지로 (로그인 유저가 없다면 0 / 있다면 1)

            //startSignUpActivity();
            // 앱의 흐름
            // 1. 메인 화면을 처음 시작하고
            // 2. 로그인된 유저가 없다면 회원가입 화면으로 전환됌

        }else{ // 로그인 완료되었다면...
            //myStartActivity1(MemberinitActivity.class); // 임시로
            //myStartActivity1(CameraActivity.class);

            Log.v(TAG, "로그인 유저 : "+user);
            startToast("로그인 되었습니다.");

            DocumentReference docRef = db.collection("Members").document(user.getUid());//DB의 유저 아이디 값을 통해 해당 문서를 가져옴
            // 파이어 베이스 DB 참조 -- "Members"라는 collection의 user고유키값으로 파악
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) { // 유저 아이디의 문서 가져오기를 성공하면

                        DocumentSnapshot document = task.getResult(); // 가져온 문서의 값 넣어줌 (유저아이디의 - 닉네임,자기소개,프로필사진) key , data(introduce,name,photoUrl)

                        Log.d(TAG, "document = task.getResult(): " + document);

                        if(document!=null){ // 해당 문서의 존재가 있다면

                            if (document.exists()) { // 유저아이디의 - (닉네임,자기소개,프로필사진) key , data(introduce,name,photoUrl)가 존재 한다면 그냥 메인 엑티비에 남기기
                                Log.d(TAG, "유저의 데이터가 있다. DocumentSnapshot data: " + document.getData());
                                loaderLayout_main.setVisibility(View.GONE); //로딩 화면 보여주기
                            } else { //없으면 회원정보 등록하는 액티비티로 이동
                                Log.d(TAG, "유저의 데이터가 없다.");
                                myStartActivity1(MemberinitActivity.class);
                            }
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            /*for (UserInfo profile : user.getProviderData()) { // 로그인된 유저의 회원정보를 받아온다.
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId(); // 구글 로그인 하였을 경우시

                String 구글="google.com";
                String 패스워드="password";

                Log.v(TAG, "providerId ["+providerId+"]");
                if(구글.equals(providerId)){ //구글 로그인이 되어있으면 넘어감
                    startToast("구글 로그인 되었습니다.");
                    Log.v(TAG, "구글로그인으로 넘어옴 (google.com) ");
                }else if(패스워드.equals(providerId)){// 구글 로그인이 안돼어 있다면 ... (일반 로그인시)
                    Log.v(TAG, "일반로그인으로 넘어옴 (password) ");
                    // UID specific to the provider
                    //String uid = profile.getUid();

                    // Name, email address, and profile photo Url
                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    Uri photoUrl = profile.getPhotoUrl();

                    Log.v(TAG, "로그인 name : "+name);

                    if(name ==null || name.length()==0){ // 일반로그인
                        // 회원정보의 이름이 없다면...
                        // 회원정보의 이름이 없다면 회원정보 엑티비티로 이동
                        myStartActivity1(MemberinitActivity.class);

                    }else
                        startToast("회원정보가 입력 되어 있습니다.");
                }
            }*/

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ MainActivity 보임");
        //findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
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


    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){

            switch (v.getId()){

                case 1:

                    break;
            }

        }
    };

    @Override public void onBackPressed() { // 바로 앱 종료
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void myStartActivity(Class c,int i){

        if(i==1) { //i=1 로그인을 했던 경우

            Intent intent = new Intent(this, c);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("로그아웃", 1);
            Log.v(TAG, "로그아웃!");
            startActivity(intent);

        }else {// i=0 현재 로그인된 유저가 없음

            Intent intent = new Intent(this, c);
            intent.putExtra("로그아웃", 0);
            Log.v(TAG, "처음로그인!");
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }

    private void myStartActivity1(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startSignUpActivity(){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

}
