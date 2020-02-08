package org.androidtown.sns_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public boolean 로그아웃=false;

    private static final String TAG = "SNS_MainActivity";// 로그찍을때 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.v(TAG, "onCreate");

        if(FirebaseAuth.getInstance().getCurrentUser()==null){ // 만약 현재 로그인한 유저가 없다면..

            Log.v(TAG, "로그인 유저 없음");
            Log.v(TAG, "로그인 엑티비티로 이동");
            myStartActivity(LoginActivity.class,0); // 로그인된 유저가 없다면 로그인 페이지로 (로그인 유저가 없다면 0 / 있다면 1)

            //startSignUpActivity();
            // 앱의 흐름
            // 1. 메인 화면을 처음 시작하고
            // 2. 로그인된 유저가 없다면 회원가입 화면으로 전환됌
        }else{
            FirebaseUser 현재유저 = FirebaseAuth.getInstance().getCurrentUser();
            Log.v(TAG, "로그인 유저 : "+현재유저);
            startToast("로그인 되었습니다.");
        }

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
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

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startSignUpActivity(){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

}
