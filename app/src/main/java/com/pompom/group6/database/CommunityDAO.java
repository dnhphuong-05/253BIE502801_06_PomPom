package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.CommunityPost;

import java.util.ArrayList;
import java.util.List;

public class CommunityDAO {
    private static final String TAG = "CommunityDAO";
    private final DatabaseHelper dbHelper;

    public CommunityDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<CommunityPost> getTopHighlights(int limit) {
        List<CommunityPost> posts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT cp.*, u.full_name, u.avatar_url " +
                      "FROM community_posts cp " +
                      "JOIN users u ON cp.user_id = u.user_id " +
                      "WHERE cp.is_hidden = 0 " +
                      "ORDER BY (cp.like_count + cp.comment_count) DESC LIMIT ?";
        
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String images = cursor.getString(cursor.getColumnIndexOrThrow("images"));
                    int likes = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
                    int comments = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    String userAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url"));
                    
                    posts.add(new CommunityPost(id, userId, content, images, likes, comments, type, userName, userAvatar));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching community highlights", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return posts;
    }
}
