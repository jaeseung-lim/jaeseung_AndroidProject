package org.androidtown.sns_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class MemberinitActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinitActivity";// 로그찍을때 태그
    private ImageView profileImageView; // 카메라 엑티비티에서 받아온 파일의 경로를 이미지 뷰에 표시해주기
    private String profilePath; // 파일의 경로를 받아주는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) { //일반 로그인 되어있을때 회원정보 입력
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        mAuth= FirebaseAuth.getInstance();

        profileImageView=findViewById(R.id.profileimageView);// 프로필 사진 선택 버튼
        profileImageView.setOnClickListener(onClickListener);// 프로필 사진 선택 버튼

        findViewById(R.id.profileUpdateButton).setOnClickListener(onClickListener); // 회원정보 업데이트 버튼


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

    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){ //CameraActivity에서 값을 넘겨 받음
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult 함수로 들어옴 ");

        switch (requestCode){

            case 0: {

                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Log.v(TAG, "profilePath : " + profilePath);

                    Bitmap bmp= BitmapFactory.decodeFile(profilePath);
                    profileImageView.setImageBitmap(bmp);


                }
            }
            break;


        }

    }


    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){

            switch (v.getId()){

                case R.id.profileimageView:

                    myStartActivity(CameraActivity.class); // 넘겨주는 값을 startActivityForResult를 통해 받아옴

                    break;

                case R.id.profileUpdateButton:

                    profileUpdate();

                    break;
            }

        }
    };

    private void profileUpdate(){ // 프로필 업데이트 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)
        //프로필 사진을 받아올때 핸드폰에 저장되어 있는 경로에서 해당 이미지를 가져와야 한다.
        Log.v(TAG, "프로필 업데이트 함수 실행");

        final String profile_name=((EditText)findViewById(R.id.profile_name)).getText().toString(); // 프로필 입력창에 이름 입력된 값을 받아오는 작업
        final String profile_introduce=((EditText)findViewById(R.id.profile_introduce)).getText().toString();

        Log.v(TAG, "profile_name : "+profile_name);

        if(profile_name.length()>0){ // 프로필 정보를 업데이트 하였다면
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            //StorageReference mountainsRef = storageRef.child("mountains.jpg");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

            // Create a reference to 'images/mountains.jpg'
            final StorageReference mountainImagesRef = storageRef.child("members/"+user.getUid()+"/profileimage.jpg");

            try{
                InputStream stream = new FileInputStream(new File(profilePath));
                UploadTask uploadTask = mountainImagesRef.putStream(stream);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() { // 데이터 베이스에 저장되어 있는 Uri 파일 받기 및 저장
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.v(TAG, "Storage로 사진 Uri 다운 실패");
                            throw task.getException();
                        }else
                            Log.v(TAG, "Storage로 사진 Uri 다운 성공");

                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.v(TAG, "Storage로 사진 업로드 성공" + downloadUri);
                            final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            Memberinfo memberInfo = new Memberinfo(profile_name,profile_introduce,downloadUri.toString());
                            db.collection("Members").document(user.getUid()).set(memberInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.v(TAG, "firestore에 저장되었습니다.");
                                            startToast("회원정보를 등록하였습니다.");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.v(TAG, "저장실패 !!", e);
                                            startToast("회원정보 등록에 실패 하였습니다.");
                                        }
                                    });
                            /*if(user != null){

                            }*/

                        } else {
                            // Handle failures
                            // ...
                            Log.v(TAG, "Storage로 사진 업로드 실패 ");
                        }
                    }
                });

            }catch (FileNotFoundException e){
                Log.v(TAG,"에러 : "+e.toString());
            }

            /////////////////////////

            /*UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profile_name)
                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build();*/
            // 로그인된 유저가 있다면
            /*user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    startToast("회원정보가 업데이트 되었습니다.");
                                    finish(); // 입력이 완료되면 MemberinitActivity 종료
                                    myStartActivity(MainActivity.class); // 메인 엑티비티로 이동
                                }

                            }

                        });*/
        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..

            startToast("닉네임을  입력해주세요.");
        }

    }


    private void startToast(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){

        Intent intent=new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent,0);

    }



    @Override public void onBackPressed() { // 바로 앱 종료

        super.onBackPressed();
        startToast("회원정보를 입력해주세요.");
        finish(); // 회원정보 입력창에서 뒤로가기 버튼을 누르면 memberinitActivity 종료시키기

        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }


}
