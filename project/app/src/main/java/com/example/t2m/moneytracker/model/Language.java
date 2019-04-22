package com.example.t2m.moneytracker.model;

import com.example.t2m.moneytracker.App;
import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.data.Constants;
import com.example.t2m.moneytracker.utils.SharedPrefs;

public class Language {

    private int mId;
    private String mName;
    private String mCode;

    public Language(int id, String name, String code) {
        mId = id;
        mName = name;
        mCode = code;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getCode() {
        return mCode;
    }


}
