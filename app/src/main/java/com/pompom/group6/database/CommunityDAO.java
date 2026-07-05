package com.pompom.group6.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.Comment;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CommunityDAO {
    private static final String TAG = "CommunityDAO";
    private final DatabaseHelper dbHelper;

    public CommunityDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertPost(int userId, String content, String images, String postType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("content", content);
        values.put("images", images);
        values.put("post_type", postType);
        values.put("like_count", 0);
        values.put("comment_count", 0);
        
        return db.insert("community_posts", null, values);
    }

    public List<CommunityPost> getTopHighlights(int limit) {
        return getPostsByType(null, limit);
    }

    public List<CommunityPost> getPostsByType(String type, int limit) {
        List<CommunityPost> posts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        StringBuilder query = new StringBuilder(
            "SELECT cp.*, u.full_name, u.avatar_url " +
            "FROM community_posts cp " +
            "JOIN users u ON cp.user_id = u.user_id "
        );
        
        List<String> args = new ArrayList<>();
        if (type != null && !type.equalsIgnoreCase("For you") && !type.equalsIgnoreCase("Bài viết")) {
            query.append("WHERE cp.post_type = ? ");
            args.add(type);
        }
        
        query.append("ORDER BY cp.created_at DESC LIMIT ?");
        args.add(String.valueOf(limit));

        try (Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            if (cursor.moveToFirst()) {
                do {
                    int postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("images"));
                    int likes = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
                    int comments = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
                    String pType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                    String uName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    String uAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url"));

                    posts.add(new CommunityPost(String.valueOf(postId), userId, content, imageUrl, likes, comments, pType, uName, uAvatar));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching posts: " + e.getMessage());
        }
        
        return posts;
    }

    public List<CommunityPost> searchPosts(String keyword, int limit) {
        List<CommunityPost> posts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT cp.*, u.full_name, u.avatar_url " +
                      "FROM community_posts cp " +
                      "JOIN users u ON cp.user_id = u.user_id " +
                      "WHERE cp.content LIKE ? OR u.full_name LIKE ? " +
                      "ORDER BY cp.created_at DESC LIMIT ?";
        
        String keywordArg = "%" + keyword + "%";
        
        try (Cursor cursor = db.rawQuery(query, new String[]{keywordArg, keywordArg, String.valueOf(limit)})) {
            if (cursor.moveToFirst()) {
                do {
                    int postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("images"));
                    int likes = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
                    int comments = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
                    String pType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                    String uName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    String uAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url"));

                    posts.add(new CommunityPost(String.valueOf(postId), userId, content, imageUrl, likes, comments, pType, uName, uAvatar));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching posts: " + e.getMessage());
        }
        return posts;
    }

    public CommunityPost getPostById(int postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT cp.*, u.full_name, u.avatar_url " +
                      "FROM community_posts cp " +
                      "JOIN users u ON cp.user_id = u.user_id " +
                      "WHERE cp.post_id = ?";
        
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)})) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("images"));
                int likes = cursor.getInt(cursor.getColumnIndexOrThrow("like_count"));
                int comments = cursor.getInt(cursor.getColumnIndexOrThrow("comment_count"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                String uName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String uAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url"));

                return new CommunityPost(String.valueOf(id), userId, content, imageUrl, likes, comments, type, uName, uAvatar);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching post by id: " + e.getMessage());
        }
        return null;
    }

    public List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT c.*, u.full_name, u.avatar_url " +
                      "FROM comments c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "WHERE c.post_id = ? " +
                      "ORDER BY c.created_at DESC";
        
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)})) {
            if (cursor.moveToFirst()) {
                do {
                    int commentId = cursor.getInt(cursor.getColumnIndexOrThrow("comment_id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                    String uName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                    String uAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url"));

                    Comment comment = new Comment(commentId, userId, uName, uAvatar, content, createdAt);
                    // Mock some data for UI consistency
                    comment.setUserRank("VIP");
                    comment.setLikes((int)(Math.random() * 50));
                    comments.add(comment);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching comments: " + e.getMessage());
        }
        return comments;
    }

    public List<Product> getTaggedProducts(int postId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // The community_posts table has a product_tag column
        String query = "SELECT p.* FROM products p " +
                      "JOIN community_posts cp ON cp.product_tag = p.product_id " +
                      "WHERE cp.post_id = ?";
        
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)})) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                    
                    Product product = new Product(String.valueOf(id), name, price + "$", null);
                    
                    // Get first image
                    try (Cursor imgCursor = db.query("product_images", new String[]{"image_url"}, 
                            "product_id = ?", new String[]{String.valueOf(id)}, null, null, "sort_order ASC", "1")) {
                        if (imgCursor.moveToFirst()) {
                            product.setImageUrl(imgCursor.getString(0));
                        }
                    }
                    products.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching tagged products: " + e.getMessage());
        }
        return products;
    }
}
