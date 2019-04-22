package com.example.t2m.moneytracker.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.t2m.moneytracker.MainActivity;
import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.wallet.AddWalletActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmailLo,txtPassWordLo;
    Button btnSignupLo,btnForgotPasswordLo,btnSingInLo;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(LoginActivity.this);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// [END initialize_auth]
        addControls();
        addEvents();
    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser !=null) {
            // check wallet
            IWalletsDAO iWalletsDAO = new WalletsDAOImpl(this);
            if(iWalletsDAO.hasWallet(currentUser.getUid()) ) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else  {
                Intent intent = new Intent(LoginActivity.this, AddWalletActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    private void addControls() {
        txtEmailLo =(EditText)findViewById(R.id.txtEmailLo);
        txtPassWordLo = (EditText)findViewById(R.id.txtPassWordLo);
        btnSingInLo = (Button)findViewById(R.id.btnSigninLo);
        btnSignupLo = (Button)findViewById(R.id.btnSignupLo);
        btnForgotPasswordLo = (Button)findViewById(R.id.btnForgotpasswordLo);

    }

    private void addEvents() {
        btnForgotPasswordLo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnSignupLo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivityForResult(intent,11);
                txtEmailLo.setError(null);
                txtPassWordLo.setError(null);
                txtPassWordLo.setText("");
            }
        });
        btnSingInLo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = txtEmailLo.getText().toString();
                String password = txtPassWordLo.getText().toString();
                if (!isNetworkConnected()) {
                    Toast.makeText(LoginActivity.this, "Bạn cần kết nối Internet", Toast.LENGTH_LONG).show();
                } else {
                    if (email.isEmpty()){
                        txtEmailLo.setError("Không được để trống");
                        txtEmailLo.requestFocus();
                        return;
                    }
                    if (password.isEmpty()){
                        txtPassWordLo.setError("Không được để trống");
                        txtPassWordLo.requestFocus();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("LoginActivity", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("signInWithEmail:failure", task.getException());
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidUserException e) {
                                            txtEmailLo.setError("Email chưa đăng ký tài khoản");
                                            txtEmailLo.requestFocus();
                                            return;
                                        } catch (Exception e) {
                                        }
                                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác,vui lòng thử lại.",
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 11 && resultCode == 11){
                txtEmailLo.setText(data.getStringExtra("username"));
                Toast.makeText(this, "Bạn đã đăng ký thành công tài khoản "+data.getStringExtra("username"), Toast.LENGTH_LONG).show();
            }

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
