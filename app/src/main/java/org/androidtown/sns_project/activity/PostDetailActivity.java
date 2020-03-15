package org.androidtown.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.androidtown.sns_project.R;
import org.androidtown.sns_project.adapter.AdapterComments;
import org.androidtown.sns_project.notifications.Data;
import org.androidtown.sns_project.object.ModelComment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    //to get detail of user and post
    String hisUid,myUid, myEmail, myName,
            myDp,postId,pLikes,hisDp,hisName,pImage;

    boolean mProcessComment = false;
    boolean mProcessLike = false;

    //views
    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTimeTv,pTitleTv, pDescriptionTv,pLikesTv, pCommentsTv;
    ImageButton moreBtn;
    Button likeBtn,shareBtn;
    LinearLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComment> commentList;
    AdapterComments adapterComments;

    //progress bar
    ProgressDialog pd;

    //add comments views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;


    private FirebaseFirestore db;
    private static final String TAG = "PostDetailActivity";// 로그찍을때 태그


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //get id of post using intent
        Intent intent = getIntent();
        postId= intent.getStringExtra("postId");


        //init views
        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        //profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);


        loadPostInfo();

        checkUserStatus();

        loadUserInfo();

        //set likes for each post
        setLikes();

        loadComments();

        //send comment button click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });

        //like button click handle
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost();
            }
        });

        //more button click handle
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions();
            }
        });

    }

    private void loadComments() {
        //layout(Linear) for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comments list
        commentList = new ArrayList<>();

        //path of the post, to get it's comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    commentList.add(modelComment);

                    //setup adapter
                    adapterComments = new AdapterComments(getApplicationContext(),commentList);

                    //set adapter
                    recyclerView.setAdapter(adapterComments);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMoreOptions() {

        //creating popup menu currently having option Delete, we will add more options later
        PopupMenu popupMenu = new PopupMenu(this,moreBtn, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if(hisUid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE,0,0,"삭제");
            popupMenu.getMenu().add(Menu.NONE,1,0,"편집");
        }
        popupMenu.getMenu().add(Menu.NONE,2,0,"게시물 상세보기");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id==0){
                    //delete is clicked
                    beginDelete();
                }
                else if (id==1){
                    //Edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",postId);
                    startActivity(intent);
                }

                return false;
            }
        });

        //show menu
        popupMenu.show();
    }

    private void beginDelete() {
        //post can be with or without image

        if(pImage.equals("noImage")){
            //post is without image
            deleteWithoutImage();
        }else{
            deleteWithImage();
        }
    }

    private void deleteWithImage() {

        //progress bar
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("삭제중..");

        /*Steps:
         * 1) Delete Image using url
         * 2) Delete from database using post id*/

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deletd, now delete database

                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    //remove values from firebase where pid matches
                                }
                                //deleted
                                Toast.makeText(PostDetailActivity.this,"삭제 성공",Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this,"삭제오류 : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void deleteWithoutImage() {

        //progress bar
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("삭제중..");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                    //remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(PostDetailActivity.this,"삭제 성공",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setLikes() {
        //when the details of post is loading, also check if current user has liked it or not
        final DatabaseReference likesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postId).hasChild(myUid)){
                    //user has liked this post
                    /*To indicate that the post is liked by this(Signed In) user
                     * Change drawable left icon of like button
                     * Change text of like button from "like" to "좋아요":*/
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_black,0,0,0);
                }
                else{
                    //user has not liked this post
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black,0,0,0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {

        //get total number of likes for the post, whose like button clicked
        //if currently signed in user has not liked it before
        //increase value by 1 , otherwise decrese value by 1

        mProcessLike = true;
        //get id of the post clicked
        final DatabaseReference likesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mProcessLike){// already liked
                    if(dataSnapshot.child(postId).hasChild(myUid)){
                        //already liked, so remove
                        postsRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike=false;


                    }
                    else {
                        //not liked , like it
                        postsRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postId).child(myUid).setValue("Liked");// set any value
                        mProcessLike = false;


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void postComment() {
        pd=new ProgressDialog(this);
        pd.setMessage("댓글 추가중..");

        Log.v(TAG, "postComment 함수 시작 ");

        //get data from comment edit text
        String comment = commentEt.getText().toString().trim();

        //validate
        if(TextUtils.isEmpty(comment)){
            //no value is entered
            Toast.makeText(this,"댓글을 입력하세요..",Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "Comments" the will contain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();

        //put info in hashmap
        hashMap.put("cId",timeStamp);
        hashMap.put("comment",comment);
        hashMap.put("timestamp",timeStamp);
        hashMap.put("uid",myUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myDp);
        hashMap.put("uName",myName);

        Log.v(TAG, "postComment 함수 timeStamp "+timeStamp);
        Log.v(TAG, "postComment 함수 comment "+comment);
        Log.v(TAG, "postComment 함수 myUid "+myUid);
        Log.v(TAG, "postComment 함수 myEmail "+myEmail);
        Log.v(TAG, "postComment 함수 myDp "+myDp);
        Log.v(TAG, "postComment 함수 myName "+myName);


        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added

                        Toast.makeText(PostDetailActivity.this,"댓글 등록 성공!!",Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        pd.dismiss();
                        updateCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, not added
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void updateCommentCount() {

        //whenever user adds comment increase the commnet count as we did for like count
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mProcessComment){
                    String comments = ""+dataSnapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadUserInfo() {

        Log.v(TAG, "loadUserInfo 함수 시작: ");
        //get current user info
        //상대방 Uid를 통해 데이터 베이스에서 있는 member의 닉네임과 이미지 불러오기
        db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Members").document(myUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.v(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                        myName= (String) documentSnapshot.getData().get("name");
                        String memberImage= (String) documentSnapshot.getData().get("photoUrl");

                        Log.v(TAG, "name data: " + myName);
                        Log.v(TAG, "url data: " + memberImage);

                        try{
                            Picasso.get().load(memberImage).placeholder(R.drawable.unnamed).into(cAvatarIv);
                        } catch (Exception e){
                            Picasso.get().load(R.drawable.unnamed).placeholder(R.drawable.unnamed).into(cAvatarIv);
                        }

                        Log.v(TAG, "loadUserInfo 함수 데이터 불러오기 성공");

                    } else {
                        Log.v(TAG, "No such document");
                    }
                } else {
                    Log.v(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void loadPostInfo() {
        //get post using the id of the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //keep checking the posts until get the required post
                for(DataSnapshot ds: dataSnapshot.getChildren()) {

                    String pTitle = "" + ds.child("pTitle").getValue();
                    String pDescr = "" + ds.child("pDescr").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    hisUid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String commentCount = ""+ds.child("pComments").getValue();


                    //convert timestamp to dd/mm/yyyy hh:mm am/pm
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();

                    //set data
                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText("좋아요 "+pLikes+"개");
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText("댓글 "+commentCount+"개");

                    uNameTv.setText(hisName);

                    //set image of the user who posted
                    //set post image
                    //if there is no image i.e. pImage.equals("noImage") then hide ImageView
                    if (pImage.equals("noImage")){
                        //hide imageview
                        pImageIv.setVisibility(View.GONE);
                    }else {

                        //show imageview
                        pImageIv.setVisibility(View.VISIBLE);

                        try{
                            Picasso.get().load(pImage).into(pImageIv);
                        }
                        catch (Exception e){

                        }

                        //set user image in comment part
                        try {
                            Picasso.get().load(hisDp).placeholder(R.drawable.unnamed).into(uPictureIv);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.unnamed).into(uPictureIv);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        }
        else{
            //user not signed in, go to main activity
            startActivity(new Intent(this,MainActivity.class));
            finish();

        }
    }

}
