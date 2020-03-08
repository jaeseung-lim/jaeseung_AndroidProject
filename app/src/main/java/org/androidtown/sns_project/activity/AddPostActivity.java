package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.androidtown.sns_project.R;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";// 로그찍을때 태그
    EditText titleEt,descriptionEt;
    ImageView imageIv;
    Button uploadBtn;

    private FirebaseFirestore db;
    private DocumentSnapshot document;
    private FirebaseUser user;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //image picked will be samed in this uri
    Uri image_uri=null;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    //user info
    String name, email, uid, dp;

    //progress bar
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();



        db= FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Members").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.v(TAG, "DocumentSnapshot data: " + document.getData());

                        name= (String) document.getData().get("name");
                        dp= (String) document.getData().get("photoUrl");
                        Log.v(TAG, "document.getData().get(\"photoUrl\") : " + document.getData().get("photoUrl"));
                        Log.v(TAG, "document.getData().get(\"name\") : " + document.getData().get("name"));


                    } else {
                        Log.v(TAG, "No such document");
                    }
                } else {
                    Log.v(TAG, "get failed with ", task.getException());
                }
            }
        });

        //get some info of current user to include in post
        /*userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    dp=""+ds.child("image").getValue();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        //init views
        titleEt=findViewById(R.id.pTitleEt);
        descriptionEt=findViewById(R.id.pDescriptonEt);
        imageIv=findViewById(R.id.pImageIv);
        uploadBtn=findViewById(R.id.pUploadBtn);

        //get image from camera / gallery on click


    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

                return result;
    }

    private void requestStoragePermission(){

        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);


    }

    private boolean checkCameraPermission(){
        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){

        //request runtime camera permission
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);


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

                case R.id.pUploadBtn:
                    Log.v(TAG, "업로드 버튼 누름");

                    //get data(title,description) from EditTexts
                    String title = titleEt.getText().toString().trim();
                    String description=descriptionEt.getText().toString().trim();

                    if(TextUtils.isEmpty(title)){
                        Toast.makeText(AddPostActivity.this,"제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(TextUtils.isEmpty(description)){
                        Toast.makeText(AddPostActivity.this,"내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if(image_uri==null){
                        //post without image
                        uploadData(title,description,"이미지 없음");
                    }else {
                        //post with image
                        uploadData(title,description,String.valueOf(image_uri));
                    }

                    break;

                case R.id.pImageIv:
                    Log.v(TAG, "이미지 버튼 누름");

                    //show image pick dialog
                    showImagePickDialog();
                    
                    break;

            }
        }
    };

    private void uploadData(final String title, final String description, String uri) {
        pd.setMessage("게시물 게시중");
        pd.show();

        //for post-image name, post-id, post -publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/"+"post"+timeStamp;

        if(!uri.equals("noImage")){
            //post with image
            StorageReference reference= FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase storage, now get it's url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();


                            while(uriTask.isSuccessful()){
                                //url is received upload post to firebase database

                                HashMap<Object,String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("uid",uid );
                                hashMap.put("uName",name );
                                hashMap.put("uEmail",email );
                                hashMap.put("uDp",dp );
                                hashMap.put("pId",timeStamp);
                                hashMap.put("pTitle",title);
                                hashMap.put("pDescr",description );
                                hashMap.put("pImage",downloadUri );
                                hashMap.put("pTime",timeStamp );

                                //path to store post data
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
                                //put data in this reference
                                reference1.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added in database
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this,"게시물 업로드 완료",Toast.LENGTH_SHORT).show();
                                                //reset views
                                                titleEt.setText("");
                                                descriptionEt.setText("");
                                                imageIv.setImageURI(null);
                                                image_uri=null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            //post without image

            HashMap<Object,String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid",uid );
            hashMap.put("uName",name );
            hashMap.put("uEmail",email );
            hashMap.put("uDp",dp );
            hashMap.put("pId",timeStamp);
            hashMap.put("pTitle",title);
            hashMap.put("pDescr",description );
            hashMap.put("pImage","noImage" );
            hashMap.put("pTime",timeStamp );

            //path to store post data
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
            //put data in this reference
            reference1.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,"게시물 업로드 완료",Toast.LENGTH_SHORT).show();
                            titleEt.setText("");
                            descriptionEt.setText("");
                            imageIv.setImageURI(null);
                            image_uri=null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void showImagePickDialog() {

        //options(camera,gallery) to show in dialog
        String[] options = {"카메라","앨범"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 선택");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //item click handle
                if(i==0){
                    //camera clicked
                    //we need to check permissions first

                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                }
                if(i==1){
                    //gallery clicked
                    //we need to check permissions first

                    if(!checkStoragePermission()){

                    }else{
                        pickFromGallery();
                    }
                }

            }
        });
        //create and show dialog
        builder.create().show();

    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery(){
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //this method is called when user press Allow or Deny from permission request dialog
        //here we will handle permission cases (allowed and denied)

        switch (requestCode){

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        //both permission are granted
                        pickFromCamera();
                    }
                    else{
                        //camera or gallery or both permissions were denied
                        Toast.makeText(this,"카메라와 앨범의 권한을 모두 허용해야합니다.", Toast.LENGTH_SHORT).show();
                    }

                }else{

                }
            }
            break;

            case STORAGE_REQUEST_CODE:{

                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //storage permission granted
                        pickFromGallery();
                    }
                    else{
                        //camera or gallery or both permissions were denied
                        Toast.makeText(this,"앨범의 권한을 허용해야합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //this method will be called after picking image from camera or gallery

        if(resultCode==RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery , get uri of image
                image_uri = data.getData();

                //set to imageview
                imageIv.setImageURI(image_uri);

            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image

                imageIv.setImageURI(image_uri);

            }


        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    private void checkUserStatus(){
        //get current user
        // FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// 현재 유저가 있는지 없는지 확인 + 현재 유저 정보 가져옴
        Log.v(TAG, "checkUserStatus 의 FirebaseUser user : "+user);

        if(user != null){
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();

            Log.v(TAG, "checkUserStatus 의 FirebaseUser email : "+email);
            Log.v(TAG, "checkUserStatus 의 FirebaseUser uid : "+uid);

        }else{
            //user not signed in, go to main activity

            startActivity(new Intent(this,MainActivity.class));
            finish();
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
