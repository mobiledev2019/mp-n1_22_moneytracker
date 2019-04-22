package com.example.t2m.moneytracker.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.t2m.moneytracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";


    ImageView imgUserLogin;
    EditText txtPassWord, txtConfirmPassword, txtEmail,txtUsername;
    Button btnSignin, btnForgotPassword, btnRegistration;
    ProgressDialog progressDialog;

    // [START declare_auth]
    private FirebaseAuth mAuth;
// [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        addControls();
        addEvents();
        FirebaseApp.initializeApp(RegistrationActivity.this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// [END initialize_auth]

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //updateUI(currentUser);
//    }


//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
//
//    private void updateUI(FirebaseUser currentUser) {
//
//    }

    private void addEvents() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassWord.getText().toString();

                View review = null;

                if (!isNetworkConnected()) {
                    Toast.makeText(RegistrationActivity.this, "Bạn cần kết nối Internet", Toast.LENGTH_LONG).show();
                } else {
                    if (username.isEmpty()){
                        txtUsername.setError("Không được để trống");
                        if (review == null){
                            review = txtUsername;
                        }
                    }
                    if (email.isEmpty()) {
                        txtEmail.setError("Không được để trống");
                        if (review ==null){
                            review = txtEmail;
                        }
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            txtEmail.setError("Định dạng email không hợp lệ");
                            if (review ==null){
                                review = txtEmail;
                            }
                        }

                    if (password.isEmpty()) {
                        txtPassWord.setError("Không được để trống");
                        if (review ==null){
                            review = txtPassWord;
                        }

                    }
                    else if (password.length()<8){
                        txtPassWord.setError("Độ dài mật khẩu phải lớn hơn hoặc bằng 8 ký tự");
                        if (review ==null){
                            review = txtPassWord;
                        }
                    }

                    if (review!=null){
                        review.requestFocus();
                        return;
                    }

                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassWord.getText().toString())
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String username = txtUsername.getText().toString();
                                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest
                                                .Builder()
                                                .setDisplayName(username)
                                                .build();
                                        user.updateProfile(changeRequest);
                                        updateUI(user);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthUserCollisionException ex) {
                                            txtEmail.setError("Tài khoản đã tồn tại");
                                            txtEmail.requestFocus();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                    }

                                    // ...
                                    progressDialog.dismiss();
                                }
                            });

                }

            }
        });
//        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegistrationActivity.this,ForgotPasswordActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mAuth.signOut();
            getIntent().putExtra("username", user.getEmail());
            setResult(11,getIntent());
            finish();
        }
    }

    private void addControls() {
//        imgUserLogin = (ImageView) findViewById(R.id.imgUserLoginRe);
        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassWord = (EditText) findViewById(R.id.txtPassWordRe);
        txtEmail = (EditText) findViewById(R.id.txtEmailRe);
        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnSignin = (Button) findViewById(R.id.btnSigninRe);
       // btnForgotPassword = (Button) findViewById(R.id.btnForgotPasswordRe);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Thông báo: ");
        progressDialog.setMessage("Đang đăng ký, vui lòng chờ trong giây lát...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

}
