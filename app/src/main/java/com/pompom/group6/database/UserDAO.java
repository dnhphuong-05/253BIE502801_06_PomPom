package com.pompom.group6.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pompom.group6.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private final DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @SuppressLint("Range")
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            // Defensive check for tables
            boolean hasVouchers = tableExists(db, "user_vouchers");
            boolean hasHistory = tableExists(db, "membership_history");

            StringBuilder queryBuilder = new StringBuilder("SELECT u.* ");
            if (hasVouchers) {
                queryBuilder.append(", (SELECT COUNT(*) FROM user_vouchers WHERE user_id = u.user_id AND used_at IS NULL) as voucher_count ");
            } else {
                queryBuilder.append(", 0 as voucher_count ");
            }
            
            if (hasHistory) {
                queryBuilder.append(", (SELECT level FROM membership_history WHERE user_id = u.user_id ORDER BY changed_at DESC LIMIT 1) as membership_level ")
                           .append(", (SELECT points FROM membership_history WHERE user_id = u.user_id ORDER BY changed_at DESC LIMIT 1) as points ");
            } else {
                queryBuilder.append(", 'Bronze Member' as membership_level, 0 as points ");
            }
            
            queryBuilder.append("FROM users u WHERE u.user_id = ?");

            cursor = db.rawQuery(queryBuilder.toString(), new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                user.setFullName(cursor.getString(cursor.getColumnIndex("full_name")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
                user.setAvatarUrl(cursor.getString(cursor.getColumnIndex("avatar_url")));
                user.setBio(cursor.getString(cursor.getColumnIndex("bio")));
                user.setVoucherCount(cursor.getInt(cursor.getColumnIndex("voucher_count")));
                
                String level = cursor.getString(cursor.getColumnIndex("membership_level"));
                user.setMembershipLevel(level != null ? level : "Bronze Member");
                
                user.setPoints(cursor.getInt(cursor.getColumnIndex("points")));
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "Error getting user by id: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return user;
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Map<String, Integer> getOrderCounts(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, Integer> counts = new HashMap<>();
        
        String query = "SELECT status, COUNT(*) as count FROM orders WHERE user_id = ? GROUP BY status";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
            @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex("count"));
            counts.put(status, count);
        }
        cursor.close();
        return counts;
    }
}
