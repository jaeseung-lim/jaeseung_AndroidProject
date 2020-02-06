package org.androidtown.sns_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
private static final String TAG = "SNS_LoginActivity";// 로그찍을때 태그
    private FirebaseAuth mAuth; //1. 파이어 베이스 인스턴스 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "로그인 엑티비티");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance(); // 2. 파이어 베이스 인스턴스 초기화

        findViewById(R.id.loginButton).setOnClickListener(onClickListener); // 로그인 버튼
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener); // 비밀번호 재설정 버튼
        findViewById(R.id.signupButton).setOnClickListener(onClickListener);//회원가입 버튼
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        //3. 파이어 베이스 인스턴스를 초기화 할때 현재 로그인이 되어 있는지 확인
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
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

                case R.id.loginButton: // 로그인 버튼 클릭
                    Log.v(TAG, "로그인 버튼 클릭");
                    login(); // 회원가입 버튼 클릭시 함수 실행
                    break;

                case R.id.gotoPasswordResetButton: // 비밀번호 찾기 버튼 클릭
                    Log.v(TAG, "비번찾기 버튼 클릭");
                    myStartActivity(PasswordResetActivity.class);

                    break;

                case R.id.signupButton: // 회원가입 버튼 클릭
                    Log.v(TAG, "회원가입 버튼 클릭");
                    myStartActivity(SignUpActivity.class);

                    break;


            }
        }
    };

    private void login(){ // 회원 가입 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)
        Log.v(TAG, "로그인 함수 실행");

        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString(); // 이메일 입력창에 입력된 값을 받아오는 작업
        String password=((EditText)findViewById(R.id.passwordEditText)).getText().toString();// 비밀번호 입력창에 입력된 값을 받아오는 작업


        if(email.length()>0 && password.length()>0){

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공하였습니다.");
                                myStartActivity(MainActivity.class);

                            } else {

                                if(task.getException() != null){//아무것도 입력하지 않았을때 + 형식에 맞지 않았을때 오류문자를 보내줌
                                    startToast(task.getException().toString());
                                    startToast("아이디 또는 비밀번호를 확인해주세요.");
                                }

                            }
                        }
                    });

        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void myStartActivity(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}
