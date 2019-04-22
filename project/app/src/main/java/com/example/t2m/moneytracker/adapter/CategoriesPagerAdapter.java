package com.example.t2m.moneytracker.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.dataaccess.CategoriesDAOImpl;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.wallet.ListCategoryFragment;

import java.util.ArrayList;
import java.util.List;

public class CategoriesPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    Context  mContext;

    public CategoriesPagerAdapter(FragmentManager fm,Context context){
        super(fm);
        mContext =context;
    }
    @Override
    public Fragment getItem(int i) {
        List<Category> categories = new ArrayList<>();
        CategoriesDAOImpl categoriesDAO = new CategoriesDAOImpl(mContext);
        switch (i) {
            case 0 :
                categories.addAll(categoriesDAO.getCategoriesByType(3));
                categories.addAll(categoriesDAO.getCategoriesByType(4));
                break;
            case 1 :
                categories.addAll(categoriesDAO.getCategoriesByType(1));
                //categories = categories.subList(0,5);
                break;
            case 2 :
                categories.addAll(categoriesDAO.getCategoriesByType(2));

        }
        return ListCategoryFragment.newInstance(categories);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getString(R.string.debt_loan_category_title);
            case 1: return mContext.getString(R.string.expense_category_title);
            case 2: return mContext.getString(R.string.income_category_title);
        }
        return null;
    }

}
