package com.mdy.android.dailythreethanks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mdy.android.dailythreethanks.util.PermissionControl;

public class AuthActivity extends AppCompatActivity implements PermissionControl.CallBack{

    // 파이어베이스 인증처리를 하기 위한 객체(인스턴스)
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // 콜백처리를 위한 리스너
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("Auth", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("Auth", "onAuthStateChanged:signed_out");
            }
        }
    };

    // 위젯 정의
    EditText editTextEmail, editTextPassword;
//    Button btnSignIn, btnSignUp;

    // 프로그래스바 정의
    private ProgressDialog authDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authDialog = new ProgressDialog(this);
        authDialog.setTitle("Sign In");
        authDialog.setMessage("Signing In...");
        authDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        PermissionControl.checkVersion(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionControl.onResult(this, requestCode, grantResults);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // 여기에 코드 작성
    @Override
    public void init() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
    }

    // onClick
    public void signUp(View view){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Auth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this, "사용자 등록이 되지 않았습니다!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthActivity.this, "사용자 등록이 되었습니다!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // onClick
    public void signIn(View view){
        authDialog.show();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Auth", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("Auth", "signInWithEmail:failed", task.getException());
                            Toast.makeText(AuthActivity.this, "로그인이 되지 않았습니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthActivity.this, "로그인이 되었습니다!", Toast.LENGTH_SHORT).show();
                            authDialog.dismiss();
                            goFeed();
                        }

                        // ...
                    }
                });
    }

    public void goFeed(){
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void cancel() {
        Toast.makeText(this, "권한을 요청을 승인하셔야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
