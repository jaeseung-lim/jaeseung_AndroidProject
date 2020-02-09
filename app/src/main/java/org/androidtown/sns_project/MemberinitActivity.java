package org.androidtown.sns_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MemberinitActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SNS_MemberinitActivity";// 로그찍을때 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) { //일반 로그인 되어있을때 회원정보 입력
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        mAuth= FirebaseAuth.getInstance();

        findViewById(R.id.profileUpdateButton).setOnClickListener(onClickListener);

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
        Log.v(TAG, "onResume/ 다음 엑티비티 보임");
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


    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){

            switch (v.getId()){

                case R.id.profileUpdateButton:

                    profileUpdate();



                    break;
            }

        }
    };

    private void profileUpdate(){ // 회원 가입 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)

        Log.v(TAG, "프로필 업데이트 함수 실행");

        String profile_name=((EditText)findViewById(R.id.profile_name)).getText().toString(); // 프로필 입력창에 이름 입력된 값을 받아오는 작업


        Log.v(TAG, "profile_name : "+profile_name);

        if(profile_name.length()>0){ // 프로필 정보를 업데이트 하였다면

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인한 회원 파악

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profile_name)
                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build();


            if(user !=null) { // 로그인된 유저가 있다면

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    startToast("회원정보가 업데이트 되었습니다.");
                                    finish(); // 입력이 완료되면 MemberinitActivity 종료
                                    myStartActivity(MainActivity.class); // 메인 엑티비티로 이동
                                }

                            }

                        });

            }else if(user ==null){ // 로그인 되어 있지 않다면 로그인 액티비티로 이동
                finish();
                myStartActivity(LoginActivity.class);
            }

        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..

            startToast("회원정보를 입력해주세요.");
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
