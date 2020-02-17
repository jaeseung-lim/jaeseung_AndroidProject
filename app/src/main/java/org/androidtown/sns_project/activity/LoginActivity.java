package org.androidtown.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
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

import org.androidtown.sns_project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";// 로그찍을때 태그
    private FirebaseAuth mAuth; //1. 파이어 베이스 인스턴스 생성
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate 로그인 엑티비티");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //구글 사용자 데이터 요청해주는것
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 파이어베이스에서 아이디 토큰이 필요하기때분에 아이디 토큰도 요청한다.
                .requestEmail()
                .build();
        Log.v(TAG, "구글 gso : "+gso.toString());
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.v(TAG, "구글 mGoogleSignInClient : "+mGoogleSignInClient.toString());

        mAuth = FirebaseAuth.getInstance(); // 2. 파이어 베이스 인스턴스 초기화
        Log.v(TAG, "mAuth : "+mAuth.toString());

        findViewById(R.id.loginButton).setOnClickListener(onClickListener); // 로그인 버튼
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener); // 비밀번호 재설정 버튼
        findViewById(R.id.signupButton).setOnClickListener(onClickListener);//회원가입 버튼

        SignInButton signInButton = findViewById(R.id.google_login_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD); // 버튼 사이즈 조정

        findViewById(R.id.google_login_button).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(TAG, "onStart");
        //3. 파이어 베이스 인스턴스를 초기화 할때 현재 로그인이 되어 있는지 확인
        // Check if user is signed in (non-null) and update UI accordingly.


        // 현재 로그인 되어있는 사용자가 있는지 확인해줌 (메인엑티비티에서 넘겨준 인텐트를 받아옴)///////////////////////
        Intent 메인로그아웃=getIntent();
        int 로그아웃확인=메인로그아웃.getExtras().getInt("로그아웃");
        Log.v(TAG, "onStart의 로그아웃확인 : " + 로그아웃확인);
        // 현재 로그인 되어있는 사용자가 있는지 확인해줌 (메인엑티비티에서 넘겨준 인텐트를 받아옴)///////////////////////


        if(로그아웃확인==1) { // 로그아웃 하였다면
            //startToast("로그아웃 하셨습니다.");

        }else if (로그아웃확인 == 0){ // 로그인된 유져가 없다면

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this); // 마지막으로 로그인된 사용자 데이터 가져옴
            //Log.v(TAG, "onStart의 구글 account : " + account.toString());

            if (account != null) {

                String idToken = account.getId();
                Log.v(TAG, "onStart의 idToken" + idToken);
                startToast("구글 로그인이 되어있습니다.");
                finish();

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

                case R.id.google_login_button: // 구글 로그인 버튼 // 주석달기 성공

                    Log.v(TAG, "구글 로그인 버튼 클릭");
                    signIn(); //구글 로그인
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

    private void signIn() {//구글 로그인
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.v(TAG, "signIn - 구글 로그인 버튼 클릭 : "+signInIntent.toString() + "/ RC_SIGN_IN : "+RC_SIGN_IN);
        startActivityForResult(signInIntent, RC_SIGN_IN); // 구글 로그인 액티비티로 이동하고 처리된 결과 값을 onActivityResult 로 값을 받게 된다.

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //구글 로그인을 통해 처리된 결과값을 받아서 처리하는 부분

        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "signIn - onActivityResult 시작 / requestCode : "+requestCode+"/ resultCode : "+resultCode+"/ data : "+data.toString()+"/ RC_SIGN_IN : "+RC_SIGN_IN);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); // 구글 로그인을 통해 받은 데이터를 받아옴

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); // firebaseAuthWithGoogle(GoogleSignInAccount acct)  여기로 결과 값을 넘겨줌
                //String email=account.getEmail();
                String idToken=account.getIdToken();

                Log.v(TAG, "idToken : "+idToken);

            } catch (ApiException e) {

                Log.v(TAG, "Google sign in failed", e);
                startToast("구글 로그인에 실패하였습니다.");

            }

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) { // (onActivityResult를 통해 받은 결과값 처리해주는 함수)구글 로그인에서 idToken을 받아와서 파이어베이스에 넘겨줘야한다.

        Log.d(TAG, "firebaseAuthWithGoogle : " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null); // 아이디 값을 잘 받아왔는지 확인해주는 함수

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { // 받아온 결과값이 성공적인지 확인

                            //쉐어드 이용하여 구글 로그인한 경우 식별자 넣어주기
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "signInWithCredential:success ");
                            FirebaseUser user = mAuth.getCurrentUser();//mAuth에 현재 사용자를 구글 아이디로 보여줌
                            finish();// 구글로그인 정보가 파이어베이스로 넘어가면 로그인 성공으로 간주하여 LoginActivity를 닫아준다. 메인 엑티비티로 이동

                            String uid = user.getUid(); // 여기로 uid가 넘어오면 파이어베이스에 구글 로그인 정보가 들어간 것!
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v(TAG, "signInWithCredential:failure", task.getException());
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

        Log.v(TAG, "email : "+email+" password : "+password);

        if(email.length()>0 && password.length()>0){ // 로그인 정보 입력 (email,password 값 입력을 했는지 확인)

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.v(TAG, "task.isSuccessful() "+task.isSuccessful());
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser(); // 현재 사용자를 유저 값에 넣어줌
                                Log.v(TAG, "login() - user : "+user);
                                startToast("로그인에 성공하였습니다.");
                                myStartActivity(MainActivity.class);

                            } else if(task.isSuccessful()!=true){

                                if(task.getException() != null){//아무것도 입력하지 않았을때 + 형식에 맞지 않았을때 오류문자를 보내줌
                                    startToast(task.getException().toString());
                                    startToast("아이디 또는 비밀번호를 확인해주세요.");
                                }else if(task.getException()==null){
                                    startToast("아이디 또는 비밀번호를 입력하지 않았습니다.");
                                }

                            }
                        }
                    });

        }else { // 이메일,비밀번호,비밀번호확인 값을 중 최소 1개라도 입력하지 않는다면..
            startToast("이메일 와 비밀번호를 입력해주세요.");
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