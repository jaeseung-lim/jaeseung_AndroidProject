package org.androidtown.sns_project;

import android.content.Intent;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {
private static final String TAG = "SignUpActivity";// 로그찍을때 태그
    private FirebaseAuth mAuth; //1. 파이어 베이스 인스턴스 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "회원가입 액티비티 실행");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance(); // 2. 파이어 베이스 인스턴스 초기화

        findViewById(R.id.signUpConfirmButton).setOnClickListener(onClickListener); // 회원가입 버튼
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener); // 로그인 버튼
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
        Log.v(TAG, "onResume/ SignUpActivity 보임");
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

                case R.id.signUpConfirmButton: // 회원가입 버튼
                    Log.v(TAG, "회원가입 버튼 클릭");
                    signUp(); // 회원가입 버튼 클릭시 함수 실행
                    break;

                case R.id.gotoLoginButton: // 로그인 페이지로 이동 버튼
                    //Log.e("클릭", "로그인 버튼 클릭");
                    Log.v(TAG, "로그인 버튼 클릭");

                    myStartActivity(LoginActivity.class);

                    break;
            }
        }
    };

    private void signUp(){ // 회원 가입 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)

        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString(); // 이메일 입력창에 입력된 값을 받아오는 작업
        String password=((EditText)findViewById(R.id.passwordEditText)).getText().toString();// 비밀번호 입력창에 입력된 값을 받아오는 작업
        String passwordCheck=((EditText)findViewById(R.id.passwordCheckditText)).getText().toString(); // 비밀번호 확인하기 위해 값을 받아오는 작업

        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0){

            if(password.equals(passwordCheck)){// 패스워드 값과 패스워드 값이 같다면 ...

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {



                                Log.d(TAG, "onComplete 함수 실행");

                                if (task.isSuccessful()) {//회원가입 성공시
                                    Log.d(TAG, "회원가입 성공시 (task.isSuccessful - ok) ");
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "회원가입성공");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입에 성공하였습니다.");

                                    finish(); // signUpActivity종료

                                    //Toast.makeText(this, "회원가입을 성공했습니다. ", Toast.LENGTH_SHORT).show();
                                    //updateUI(user);
                                } else {//회원가입 실패시
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "회원가입 실패 ", task.getException());
                                    Log.d(TAG, "회원가입 실패시 (task.isSuccessful) : "+task.isSuccessful());

                                    if(task.getException() != null){//아무것도 입력하지 않았을때; + 형식에 맞지 않았을때 오류문자를 보내줌
                                        Log.d(TAG, "회원가입 실패시 (task.getException : "+task.getException());
                                        startToast(task.getException().toString());
                                        startToast("비밀번호를 6자리 이상 입력해주세요.");
                                    }else
                                        Log.d(TAG, "회원가입 실패시 (task.getException : "+task.getException());

                                    //Toast.makeText(this, "회원가입 실패 ",Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                            } // onComplete 함수

                        });

            }else{ // 비밀번호랑 비밀번호 확인 값이 다를때 사용자에게 토스트메시지로 알려줌
                startToast("비밀번호가 일치하지 않습니다. ");
                //Toast.makeText(this, "비밀번호가 일치하지 않습니다. ", Toast.LENGTH_SHORT).show();
            }

        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..
            startToast("이메일 또는 비밀번호,비밀번호 확인(을)를 입력해주세요.");
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

    private void startLoginActivity(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);

    }


}
