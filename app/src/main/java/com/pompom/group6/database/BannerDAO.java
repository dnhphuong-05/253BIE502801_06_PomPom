package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerDAO {
    private static final String TAG = "BannerDAO";
    private final DatabaseHelper dbHelper;

    public BannerDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<Banner> getAllBanners() {
        List<Banner> banners = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            Log.d(TAG, "Database opened successfully.");

            // Check if table exists
            Cursor tableCheckCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='banners'", null);
            if (!tableCheckCursor.moveToFirst()) {
                Log.w(TAG, "Banners table does not exist!");
                tableCheckCursor.close();
                return banners;
            }
            tableCheckCursor.close();
            Log.d(TAG, "Banners table exists.");

            // Query with detailed logging
            cursor = db.query(
                    "banners",
                    new String[]{"banner_id", "image_url", "title"},
                    "is_active = ?",
                    new String[]{"1"},
                    null,
                    null,
                    "sort_order ASC, banner_id ASC"
            );

            Log.d(TAG, "Query executed. Cursor count: " + (cursor != null ? cursor.getCount() : "null"));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("banner_id"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    banners.add(new Banner(id, imageUrl, title));
                    Log.d(TAG, "Loaded banner: ID=" + id + ", Title=" + title + ", URL=" + (imageUrl != null ? imageUrl.substring(0, Math.min(50, imageUrl.length())) : "null"));
                } while (cursor.moveToNext());
                Log.d(TAG, "Total banners loaded: " + banners.size());
            } else {
                Log.w(TAG, "No active banners found in database (cursor null or empty).");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying banners: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && !db.isReadOnly()) {
                // Don't close here as it's managed by SQLiteOpenHelper
            }
        }
        return banners;
    }
}
