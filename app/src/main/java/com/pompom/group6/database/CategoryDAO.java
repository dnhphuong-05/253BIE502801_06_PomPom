package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String TAG = "CategoryDAO";
    private final DatabaseHelper dbHelper;

    public CategoryDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Lấy các danh mục cấp cao nhất (parent_id is NULL) sắp xếp theo sort_order
            cursor = db.rawQuery("SELECT category_id, category_name, image_url FROM categories WHERE parent_id IS NULL ORDER BY sort_order ASC", null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                    categories.add(new Category(id, name, imageUrl));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching categories: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return categories;
    }
}
