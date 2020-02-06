package org.androidtown.sns_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SNS_LoginActivity";// 로그찍을때 태그
    private FirebaseAuth mAuth; //1. 파이어 베이스 인스턴스 생성
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate 로그인 엑티비티");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ///////////////////////////// 파이어 베이스 인증 관련 엑티비티를 띄우기 위한 것
        // List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build());
        //                new AuthUI.IdpConfig.PhoneBuilder().build(),
        //                new AuthUI.IdpConfig.FacebookBuilder().build(),
        //                new AuthUI.IdpConfig.TwitterBuilder().build()
        // Create and launch sign-in intent
        /*startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);*/ // 액티비티를 호출 시켜서 setResult에 결과를 담아 onActivityResult()로 결과를 넘겨준다. (onActivityResult()에서는 결과를 확인한다.)
        ///////////////////////////// 파이어 베이스 인증 관련 엑티비티를 띄우기 위한 것

        //사용자 데이터 요청해주는것
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 파이어베이스에서 아이디 토큰이 필요하기때분에 아이디 토큰도 요청한다.
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance(); // 2. 파이어 베이스 인스턴스 초기화

        findViewById(R.id.loginButton).setOnClickListener(onClickListener); // 로그인 버튼
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener); // 비밀번호 재설정 버튼
        findViewById(R.id.signupButton).setOnClickListener(onClickListener);//회원가입 버튼

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD); // 버튼 사이즈 조정

        findViewById(R.id.sign_in_button).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        //3. 파이어 베이스 인스턴스를 초기화 할때 현재 로그인이 되어 있는지 확인
        // Check if user is signed in (non-null) and update UI accordingly.

        // 현재 로그인 되어있는 사용자가 있는지 확인해줌
        Intent 메인로그아웃=getIntent();
        String 로그아웃확인=메인로그아웃.getExtras().getString("로그아웃");
        Log.v(TAG, "onStart의 로그아웃확인 : " + 로그아웃확인);

        if(로그아웃확인!="로그아웃") {


        }else if (로그아웃확인 == "처음로그인"){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                String idToken = account.getId();
                Log.v(TAG, "onStart의 idTokem" + idToken);
                startToast("구글 로그인이 되어있습니다.");
                finish();//
            }
        }



        //FirebaseUser currentUser = mAuth.getCurrentUser();
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
        Log.v(TAG, "onResume/ LoginActivity 보임");
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

                case R.id.sign_in_button: // 구글 로그인 버튼
                    Log.v(TAG, "구글 로그인 버튼 클릭");
                    signIn();
                    break;


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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // 구글로만 로그인한것 (파이어베이스는 따로!)
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); // firebaseAuthWithGoogle(GoogleSignInAccount acct)  여기로 값을 넘겨줌
                String email=account.getEmail();
                String idToken=account.getIdToken();
                Log.v(TAG, idToken);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v(TAG, "Google sign in failed", e);
                startToast("구글 로그인에 실패하였습니다.");
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) { // 구글 로그인에서 idToken을 받아와서 파이어베이스에 넘겨줘야한다.
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();// 구글로그인 정보가 파이어베이스로 넘어가면 로그인 성공으로 간주하여 LoginActivity를 닫아준다.

                            String uid = user.getUid(); // 여기로 uid가 넘어오면 파이어베이스에 구글 로그인 정보가 들어간 것!
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            startToast("구글 로그인의 정보가 파이어베이스에 들어가기를 실패하였습니다.");
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

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
 /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 파이어베이스 (로그인 과정이 완료되면 onActivityResult로 결과가 수신됩니다.-결과 확인하기 위해서 )
        super.onActivityResult(requestCode, resultCode, data); // 세번째 인수는 호출한 액티비티에서 넘겨준 결과값에 해당

        if (requestCode == RC_SIGN_IN) { // requestCode는 StartActivityForResult의 두번째 인수에 해당 .createSignInIntentBuilder()
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) { // setResult를 통해 전달된 결과값을 학인해줍니다.
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String email = user.getEmail();
                String uid=user.getUid(); // uid를 데이터베이스에 저장하여 사용자의 고유키 값으로 설정

                String dispName=user.getDisplayName();
                String photoUrl=user.getPhotoUrl().toString();
                //파이어베이스에서 유저 정보를 가져옴
                // ...
            } else {

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }*/