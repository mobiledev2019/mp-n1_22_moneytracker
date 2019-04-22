package com.example.t2m.moneytracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDAOImpl implements ICategoriesDAO {

    /**
     * Category table
     * int CategoryId
     * int type
     * String logo
     * String category
     * int parentType
     */

    public static final String TABLE_CATEGORY_NAME = "tbl_categories";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_TYPE = "type";
    public static final String COLUMN_CATEGORY_ICON = "icon";
    public static final String COLUMN_CATEGORY_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_PARENT_ID = "parentId";

    MoneyTrackerDBHelper dbHelper;
    public CategoriesDAOImpl(Context context) {
        dbHelper = new MoneyTrackerDBHelper(context);
    }

    // Transaction Type DataAccess

    public boolean insertCategory(Category category) {
        if (category == null) return false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_TYPE, category.getType().getValue());
        values.put(COLUMN_CATEGORY_CATEGORY, category.getCategory());
        values.put(COLUMN_CATEGORY_ICON, category.getIcon());

        int id = (int) db.insert(TABLE_CATEGORY_NAME, COLUMN_CATEGORY_PARENT_ID, values);
        db.close();
        category.setId(id);
        return id != -1;
    }

    public boolean updateCategory(Category category) {
        if (category == null) return false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_TYPE, category.getType().getValue());
        values.put(COLUMN_CATEGORY_CATEGORY, category.getCategory());
        values.put(COLUMN_CATEGORY_ICON, category.getIcon());
        //values.put(COLUMN_CATEGORY_PARENT_ID, category.getParentType().getId());
        db.update(TABLE_CATEGORY_NAME, values, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(category.getId())});
        db.close();
        return true;
    }

    @Override
    public boolean deleteCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_CATEGORY_NAME,COLUMN_CATEGORY_ID + " = ?",new String[]{String.valueOf(category.getId())});
        return true;
    }

    public Category getCategoryById(int id) {
        Cursor data = getCategoryDataById(id);
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            Category category = getCategoryFromData(data);
            return category;
        } else {
            return null;
        }
    }

    public List<Category> getAllCategory() {
        Cursor data = getAllCategoryData();
        List<Category> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Category category = getCategoryFromData(data);
                List<Category> subCategories = getSubCategories(category.getId());
                category.setSubCategories(subCategories);
                list_result.add(category);
            }
            while (data.moveToNext());
        }
        return list_result;
    }

    public List<Category> getCategoriesByType(int type) {
        Cursor data = getAllCategoryDataByType(type);
        List<Category> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Category category = getCategoryFromData(data);
                List<Category> subCategories = getSubCategories(category.getId());
                category.setSubCategories(subCategories);
                list_result.add(category);
            }
            while (data.moveToNext());
        }
        return list_result;
    }

    private Cursor getAllCategoryDataByType(int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY_NAME +
                " WHERE " + COLUMN_CATEGORY_PARENT_ID + " IS NULL " +
                " AND " + COLUMN_CATEGORY_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(type)});
        return cursor;
    }

    public List<Category> getSubCategories(int parentId) {
        Cursor data = getSubCategoriesData(parentId);
        List<Category> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Category category = getCategoryFromData(data);
                list_result.add(category);
            }
            while (data.moveToNext());
        }
        return list_result;
    }

    private Cursor getSubCategoriesData(int parentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY_NAME +
                " WHERE " + COLUMN_CATEGORY_PARENT_ID+ " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(parentId)});
        return cursor;
    }

    private Category getCategoryFromData(Cursor data) {
        int id = data.getInt(data.getColumnIndex(COLUMN_CATEGORY_ID));
        int type = data.getInt(data.getColumnIndex(COLUMN_CATEGORY_TYPE));
        String category = data.getString(data.getColumnIndex(COLUMN_CATEGORY_CATEGORY));
        String icon = data.getString(data.getColumnIndex(COLUMN_CATEGORY_ICON));
        Category parent = null;
        return new Category(id, type, category, icon, new ArrayList<Category>());
    }

    private Cursor getCategoryDataById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY_NAME +
                " WHERE " + COLUMN_CATEGORY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        return cursor;
    }

    private Cursor getAllCategoryData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY_NAME +
                " WHERE " + COLUMN_CATEGORY_PARENT_ID + " IS NULL";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

}
