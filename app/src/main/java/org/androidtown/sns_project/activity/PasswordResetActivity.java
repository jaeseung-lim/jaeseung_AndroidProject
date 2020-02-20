package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.androidtown.sns_project.R;

public class PasswordResetActivity extends AppCompatActivity {

    private static final String TAG = "PasswordResetActivity";// 로그찍을때 태그

    private FirebaseAuth mAuth; //1. 파이어 베이스 인스턴스 생성
    private RelativeLayout loaderLayout_passwordreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "비번찾기 액티비티 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mAuth = FirebaseAuth.getInstance(); // 2. 파이어 베이스 인스턴스 초기화

        findViewById(R.id.sendPasswordButton).setOnClickListener(onClickListener); // 버튼 아이디
        findViewById(R.id.loginbutton2).setOnClickListener(onClickListener); // 버튼 아이디
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
                    Log.v(TAG, "비번 찾기 버튼 누름");
                    loaderLayout_passwordreset =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
                    loaderLayout_passwordreset.setVisibility(View.VISIBLE); //로딩 화면 보여주기
                 sendPassword();
                    break;

                case R.id.loginbutton2:
                    Log.v(TAG, "로그인 버튼 누름");
                    myStartActivity(LoginActivity.class);
                    finish(); // LoginActivity 화면 종료
                    break;

            }
        }
    };

    private void sendPassword(){ // 회원 가입 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)
        Log.v(TAG, "비번 찾는 함수 실행");
        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString(); // 이메일 입력창에 입력된 값을 받아오는 작업

        if(email.length()>0){

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Log.d(TAG, "Email sent.");
                                loaderLayout_passwordreset.setVisibility(View.GONE); //로딩 화면 보여주기
                                startToast("비밀번호 재설정 이메일이 발송되었습니다.");
                                finish(); // SignUpActivity 화면 종료
                            }else if(task.isSuccessful()!=true){
                                loaderLayout_passwordreset.setVisibility(View.GONE); //로딩 화면 보여주기
                                startToast("유효하지 않는 이메일 입니다.");
                            }
                        }
                    });

        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..
            startToast("이메일을 입력해주세요.");
        }
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
