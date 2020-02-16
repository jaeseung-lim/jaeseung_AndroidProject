package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.sns_project.Memberinfo;
import org.androidtown.sns_project.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {


    private static final String TAG = "ProfileActivity";// 로그찍을때 태그
    private String profilePath; // 파일의 경로를 받아주는 변수
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ImageView MyprofileImage;
    private TextView MyprofileName;
    private TextView MyprofileIntroduce;
    private DocumentSnapshot document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MyprofileImage= findViewById(R.id.Myprofile_image);
        MyprofileName=findViewById(R.id.Myprofile_name);
        MyprofileIntroduce=findViewById(R.id.Myprofile_introduce);


        user = FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Members").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.v(TAG, "DocumentSnapshot data: " + document.getData());
                        //document.getData().get("photoUrl");
                        document.getData().get("introduce");
                        document.getData().get("name");
                        //Log.v(TAG, "document.getData().get(\"photoUrl\") : " + document.getData().get("photoUrl"));
                        Log.v(TAG, "document.getData().get(\"introduce\") : " + document.getData().get("introduce"));
                        Log.v(TAG, "document.getData().get(\"name\") : " + document.getData().get("name"));
                        MyprofileIntroduce.setText((CharSequence) document.getData().get("introduce"));
                        MyprofileName.setText((CharSequence) document.getData().get("name"));

                    } else {
                        Log.v(TAG, "No such document");
                    }
                } else {
                    Log.v(TAG, "get failed with ", task.getException());
                }
            }
        });
        storage = FirebaseStorage.getInstance(); // 스토리지 인스턴스

        // Create a storage reference from our app
        storageRef = storage.getReference().child("members/"+user.getUid()+"/profileimage.jpg");

        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {// 스토리지에서 이미지 파일 가져오기
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(ProfileActivity.this)
                            .load(task.getResult())
                            .centerCrop()
                            .override(400)
                            .into(MyprofileImage);
                    //Glide.with(this).load(bmp).centerCrop().override(400).into(profileImageView);
                } else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.about);// res - menu - item이름
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.dashboard :

                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.home :

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.about :



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

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume/ 다른 보임");
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
