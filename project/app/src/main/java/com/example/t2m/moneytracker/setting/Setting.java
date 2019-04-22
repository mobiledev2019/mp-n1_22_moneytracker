package com.example.t2m.moneytracker.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.t2m.moneytracker.MainActivity;
import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.account.LoginActivity;
import com.example.t2m.moneytracker.changelanguage.ChangeLanguageActivity;
import com.example.t2m.moneytracker.data.Constants;
import com.example.t2m.moneytracker.databinding.ActivitySettingBinding;
import com.example.t2m.moneytracker.utils.LanguageUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Setting extends AppCompatActivity {
    private FirebaseAuth mAtuth;
    FirebaseUser user;
    TextView txtUsername;
    Button btnLanguage,btnLogout,btnBack;
    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        FirebaseApp.initializeApp(Setting.this);
        mAtuth = FirebaseAuth.getInstance();

        LanguageUtils.loadLocale();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mBinding.setMain(Setting.this);


        addControls();
        addEvents();
    }

    private void addControls() {
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        btnLanguage = (Button) findViewById(R.id.btnLanguage);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnBack = (Button)findViewById(R.id.btnback);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            txtUsername.setText(user.getEmail());
        }
    }

    private void addEvents() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutAccount();
            }

            private void LogoutAccount() {
                mAtuth.signOut();
                Intent intent = new Intent(Setting.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.RequestCode.CHANGE_LANGUAGE:
                if (resultCode == RESULT_OK) {
                    updateViewByLanguage();
                }
                break;
        }
    }
    private void updateViewByLanguage() {

        recreate();
        //        mBinding.btnLanguage.setText(getString(R.string.change_language));
//        mBinding.btnLogout.setText(getString(R.string.signout));
//        mBinding.btnback.setText(getString(R.string.back));
//        mBinding.textView8.setText(getString(R.string.name_account));
//        mBinding.textview09.setText(getString(R.string.action_settings));

    }
    public void openLanguageScreen() {
        Intent intent = new Intent(Setting.this, ChangeLanguageActivity.class);
        startActivityForResult(intent, Constants.RequestCode.CHANGE_LANGUAGE);
    }

}
