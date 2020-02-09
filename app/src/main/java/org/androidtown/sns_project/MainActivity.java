package org.androidtown.sns_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    public boolean 로그아웃=false;

    private static final String TAG = "SNS_MainActivity";// 로그찍을때 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.v(TAG, "onCreate");

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener); // 로그아웃 버튼

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인하기

        if(user==null){ // 만약 현재 로그인한 유저가 없다면..

            Log.v(TAG, "로그인 유저 없음");
            Log.v(TAG, "로그인 엑티비티로 이동");
            myStartActivity(LoginActivity.class,0); // 로그인된 유저가 없다면 로그인 페이지로 (로그인 유저가 없다면 0 / 있다면 1)

            //startSignUpActivity();
            // 앱의 흐름
            // 1. 메인 화면을 처음 시작하고
            // 2. 로그인된 유저가 없다면 회원가입 화면으로 전환됌
        }else{ // 로그인 완료되었다면...

            Log.v(TAG, "로그인 유저 : "+user);
            startToast("로그인 되었습니다.");

            for (UserInfo profile : user.getProviderData()) { // 로그인된 유저의 회원정보를 받아온다.
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
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ MainActivity 보임");
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
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

                case R.id.logoutButton:

                    FirebaseAuth.getInstance().signOut(); // 로그아웃 함수

                    myStartActivity(LoginActivity.class,1);

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


        if(i==1) {
            Intent intent = new Intent(this, c);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("로그아웃", 1);
            Log.v(TAG, "로그아웃!");
            startActivity(intent);
        }else {// 현재 로그인된 유저가 없음
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
