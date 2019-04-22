package com.example.t2m.moneytracker.changelanguage;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.data.ItemClickListener;
import com.example.t2m.moneytracker.databinding.ActivityChangeLanguageBinding;
import com.example.t2m.moneytracker.model.Language;
import com.example.t2m.moneytracker.utils.LanguageUtils;

public class ChangeLanguageActivity extends AppCompatActivity {

    private LanguageAdapter mLanguageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChangeLanguageBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_change_language);
        mLanguageAdapter = new LanguageAdapter(LanguageUtils.getLanguageData());
        mLanguageAdapter.setListener(new ItemClickListener<Language>() {
            @Override
            public void onClickItem(int position, Language language) {
                if (!language.getCode().equals(LanguageUtils.getCurrentLanguage().getCode())) {
                    onChangeLanguageSuccessfully(language);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChangeLanguageActivity.this);
        binding.recyclerViewLanguage.setLayoutManager(layoutManager);
        binding.recyclerViewLanguage.setAdapter(mLanguageAdapter);
    }

    public void onChangeLanguageSuccessfully(final Language language) {
        mLanguageAdapter.setCurrentLanguage(language);
        LanguageUtils.changeLanguage(language);
        setResult(RESULT_OK, new Intent());
        finish();
    }
}