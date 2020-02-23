package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidtown.sns_project.object.Memberinfo;
import org.androidtown.sns_project.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberinitActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinitActivity";// 로그찍을때 태그
    private ImageView profileImageView; // 카메라 엑티비티에서 받아온 파일의 경로를 이미지 뷰에 표시해주기
    private String profilePath; // 파일의 경로를 받아주는 변수
    private FirebaseUser user;
    //private Activity activity;
    private CardView cardView;
    private RelativeLayout loaderLayout_memberinit;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //일반 로그인 되어있을때 회원정보 입력
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        mAuth= FirebaseAuth.getInstance();


        profileImageView=findViewById(R.id.profileimageView);// 프로필 사진 선택 버튼
        profileImageView.setOnClickListener(onClickListener);// 프로필 사진 선택 버튼

        findViewById(R.id.profileUpdateButton).setOnClickListener(onClickListener); // 회원정보 업데이트 버튼
        findViewById(R.id.picture_button).setOnClickListener(onClickListener); // 사진
        findViewById(R.id.gallery_button).setOnClickListener(onClickListener); // 갤러리


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

                if (resultCode == Activity.RESULT_OK) { // 사진의 경로
                    profilePath = data.getStringExtra("profilePath");
                    Log.v(TAG, "profilePath : " + profilePath);
                    Bitmap bmp= BitmapFactory.decodeFile(profilePath);
                    //profileImageView.setImageBitmap(bmp);
                    Glide.with(this).load(bmp).centerCrop().override(400).into(profileImageView);
                }
            }
            break;


        }

    }


    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){

            switch (v.getId()){

                case R.id.profileimageView:  //프로필 이미지 클릭시 (카메라 , 갤러리 버튼 보여지는 유무)

                    cardView = findViewById(R.id.buttonsCardView);


                    if(cardView.getVisibility()==View.VISIBLE){ // 만약 카드뷰가 보인다면
                        Log.v(TAG, "카드뷰 안보이게 하기");
                        cardView.setVisibility(View.GONE); // 이미지 프로필을 눌렀을때 카드뷰가 사라진다.

                    }else if(cardView.getVisibility()==View.GONE){ // 만약 카드뷰가 안 보인다면
                        Log.v(TAG, "카드뷰 보이게 하기");
                        cardView.setVisibility(View.VISIBLE); // 이미지 프로필을 눌렀을때 카드뷰가 보인다.

                    }

                    //

                    break;

                case R.id.profileUpdateButton: // 회원정보 제출 버튼
                    loaderLayout_memberinit =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
                    loaderLayout_memberinit.setVisibility(View.VISIBLE); //로딩 화면 보여주기

                    profileUpdate();

                    break;

                case R.id.picture_button: // 사진촬영 버튼


                    cardView.setVisibility(View.GONE);
                    myStartActivity(CameraActivity.class); // 넘겨주는 값을 startActivityForResult를 통해 받아옴


                    break;

                case R.id.gallery_button: // 갤러리 버튼

                    cardView.setVisibility(View.GONE);

                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(MemberinitActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 권한이 없을때
                        // Permission is not granted
                        // Should we show an explanation?
                        //////////////////////////////////
                        //사용자가 권한을 거부하고 다시 들어왔을때 권한을 다시 요청해주는 것
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MemberinitActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1); //MY_PERMISSIONS_REQUEST_READ_CONTACTS

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberinitActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.


                            Log.v(TAG, "권한을 허용해야 앨범에 접근이 가능합니다.");
                            startToast("권한을 허용해야 앨범에 접근이 가능합니다.");


                        } else { // 권한 거절시

                            Log.v(TAG, "권한을 허용해야 앨범에 접근이 가능합니다.");
                            startToast("권한을 허용해야 앨범에 접근이 가능합니다.");

                        }
                    } else { // 이미 권한이 있을때
                        // Permission has already been granted
                        startToast("앨범 접근 권한이 허용되어 있습니다.");
                        myStartActivity(GalleryActivity.class);
                    }

                    break;
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: { //MY_PERMISSIONS_REQUEST_READ_CONTACTS
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    myStartActivity(GalleryActivity.class);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.v(TAG, "권한을 허용해야 앨범에 접근이 가능합니다.");
                }
                //return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void profileUpdate(){ // 프로필 업데이트 함수 (회원가입할때 이메일,비밀번호 사용가능한지 확인)
        //프로필 사진을 받아올때 핸드폰에 저장되어 있는 경로에서 해당 이미지를 가져와야 한다.
        Log.v(TAG, "프로필 업데이트 함수 실행");

        user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴

        final String profile_uid=user.getUid();
        final String profile_name=((EditText)findViewById(R.id.profile_name)).getText().toString(); // 프로필 입력창에 이름 입력된 값을 받아오는 작업
        final String profile_introduce=((EditText)findViewById(R.id.profile_introduce)).getText().toString();

        Log.v(TAG, "profile_name : "+profile_name);

        if(profile_name.length()>0){ // 프로필 정보를 업데이트 하였다면

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            //StorageReference mountainsRef = storageRef.child("mountains.jpg");

            // Create a reference to 'images/mountains.jpg'


            final StorageReference mountainImagesRef = storageRef.child("members/"+user.getUid()+"/profileimage.jpg"); //storage

            if(profilePath == null){ // 사진이 없다면 그냥 올리고

                Memberinfo memberInfo = new Memberinfo(profile_name,profile_introduce);
                uploader(memberInfo);

            }else{ // 사진이 있다면 다같이 올려라

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

                                Memberinfo memberInfo = new Memberinfo(profile_name,profile_introduce,downloadUri.toString(),profile_uid);
                                uploader(memberInfo);
                                loaderLayout_memberinit.setVisibility(View.GONE); //로딩 화면 보여주기
                                Log.v(TAG, "Storage로 사진 업로드 성공" + downloadUri);

                            } else {
                                // Handle failures
                                // ...
                                loaderLayout_memberinit.setVisibility(View.GONE); //로딩 화면 보여주기
                                Log.v(TAG, "Storage로 사진 업로드 실패 ");
                            }
                        }
                    });

                }catch (FileNotFoundException e){
                    loaderLayout_memberinit.setVisibility(View.GONE); //로딩 화면 보여주기
                    Log.v(TAG,"에러 : "+e.toString());
                }
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

    private void uploader(Memberinfo memberinfo){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("Members").document(user.getUid()).set(memberinfo)
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
