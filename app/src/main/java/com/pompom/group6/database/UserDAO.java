package com.pompom.group6.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pompom.group6.models.Address;
import com.pompom.group6.models.PointsTransaction;
import com.pompom.group6.models.Product;
import com.pompom.group6.models.User;
import com.pompom.group6.models.Voucher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                user.setBirthDate(cursor.getString(cursor.getColumnIndex("birth_date")));
                user.setSkinType(cursor.getString(cursor.getColumnIndex("skin_type")));
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

    /**
     * Verify credentials against the users table. Returns the full User on success, null otherwise.
     */
    public User authenticate(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int userId = -1;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT user_id FROM users WHERE email = ? AND password_hash = ? AND status = 'active'",
                    new String[]{email, password});
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "authenticate error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return userId == -1 ? null : getUserById(userId);
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM users WHERE email = ? LIMIT 1", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * Create a new user (and an initial Bronze membership row). Returns the new user_id, or -1 on failure.
     */
    public long registerUser(String fullName, String email, String password, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newId = -1;
        String now = currentTimestamp();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("email", email);
            values.put("password_hash", password);
            values.put("phone_number", phone);
            values.put("avatar_url", "");
            values.put("bio", "");
            values.put("join_date", now);
            values.put("role", "user");
            values.put("status", "active");
            values.put("last_login", now);
            values.put("created_at", now);
            values.put("updated_at", now);

            newId = db.insertOrThrow("users", null, values);

            if (newId != -1) {
                ContentValues membership = new ContentValues();
                membership.put("user_id", newId);
                membership.put("level", "Bronze Member");
                membership.put("points", 0);
                membership.put("changed_at", now);
                db.insert("membership_history", null, membership);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "registerUser error: " + e.getMessage());
            newId = -1;
        } finally {
            db.endTransaction();
        }
        return newId;
    }

    public boolean updateProfile(int userId, String fullName, String bio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("bio", bio);
            values.put("updated_at", currentTimestamp());
            int rows = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
            return rows > 0;
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "updateProfile error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change the password after verifying the current one.
     * @return true if the current password matched and the update succeeded.
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM users WHERE user_id = ? AND password_hash = ?",
                    new String[]{String.valueOf(userId), oldPassword});
            if (!cursor.moveToFirst()) {
                return false; // current password does not match
            }
            ContentValues values = new ContentValues();
            values.put("password_hash", newPassword);
            values.put("updated_at", currentTimestamp());
            int rows = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
            return rows > 0;
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "changePassword error: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /** Update the editable account fields. */
    public boolean updateAccount(int userId, String fullName, String phone, String bio,
                                 String gender, String birthDate, String skinType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("phone_number", phone);
            values.put("bio", bio);
            values.put("gender", gender);
            values.put("birth_date", birthDate);
            values.put("skin_type", skinType);
            values.put("updated_at", currentTimestamp());
            return db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)}) > 0;
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "updateAccount error: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Addresses
    // ------------------------------------------------------------------

    @SuppressLint("Range")
    public List<Address> getAddresses(int userId) {
        List<Address> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT * FROM user_addresses WHERE user_id = ? ORDER BY is_default DESC, address_id ASC",
                new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                list.add(new Address(
                        c.getInt(c.getColumnIndex("address_id")),
                        c.getString(c.getColumnIndex("label")),
                        c.getString(c.getColumnIndex("recipient_name")),
                        c.getString(c.getColumnIndex("phone")),
                        c.getString(c.getColumnIndex("address_line")),
                        c.getString(c.getColumnIndex("ward")),
                        c.getString(c.getColumnIndex("district")),
                        c.getString(c.getColumnIndex("city")),
                        c.getInt(c.getColumnIndex("is_default")) == 1));
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "getAddresses error: " + e.getMessage());
        }
        return list;
    }

    public long addAddress(int userId, String label, String recipientName, String phone,
                           String addressLine, String ward, String district, String city, boolean makeDefault) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (makeDefault) {
                ContentValues clear = new ContentValues();
                clear.put("is_default", 0);
                db.update("user_addresses", clear, "user_id = ?", new String[]{String.valueOf(userId)});
            }
            ContentValues v = new ContentValues();
            v.put("user_id", userId);
            v.put("label", label);
            v.put("recipient_name", recipientName);
            v.put("phone", phone);
            v.put("address_line", addressLine);
            v.put("ward", ward);
            v.put("district", district);
            v.put("city", city);
            v.put("is_default", makeDefault ? 1 : 0);
            return db.insert("user_addresses", null, v);
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "addAddress error: " + e.getMessage());
            return -1;
        }
    }

    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("user_addresses", "address_id = ?", new String[]{String.valueOf(addressId)}) > 0;
    }

    public boolean setDefaultAddress(int userId, int addressId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues clear = new ContentValues();
            clear.put("is_default", 0);
            db.update("user_addresses", clear, "user_id = ?", new String[]{String.valueOf(userId)});

            ContentValues set = new ContentValues();
            set.put("is_default", 1);
            db.update("user_addresses", set, "address_id = ?", new String[]{String.valueOf(addressId)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "setDefaultAddress error: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    // ------------------------------------------------------------------
    // Vouchers owned by the user
    // ------------------------------------------------------------------

    @SuppressLint("Range")
    public List<Voucher> getUserVouchers(int userId, boolean onlyAvailable) {
        List<Voucher> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String usedFilter = onlyAvailable ? "AND uv.used_at IS NULL " : "";
        String sql = "SELECT v.voucher_id, v.code, v.discount_type, v.discount_value, v.min_order_amount, " +
                "v.end_date, v.usage_limit, v.used_count " +
                "FROM user_vouchers uv JOIN vouchers v ON uv.voucher_id = v.voucher_id " +
                "WHERE uv.user_id = ? " + usedFilter +
                "ORDER BY uv.assigned_at DESC";
        try (Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                int limit = c.getInt(c.getColumnIndex("usage_limit"));
                int used = c.getInt(c.getColumnIndex("used_count"));
                int remaining = Math.max(limit - used, 0);
                list.add(new Voucher(
                        c.getInt(c.getColumnIndex("voucher_id")),
                        c.getString(c.getColumnIndex("code")),
                        c.getString(c.getColumnIndex("discount_type")),
                        c.getDouble(c.getColumnIndex("discount_value")),
                        c.getDouble(c.getColumnIndex("min_order_amount")),
                        c.getString(c.getColumnIndex("end_date")),
                        remaining));
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "getUserVouchers error: " + e.getMessage());
        }
        return list;
    }

    // ------------------------------------------------------------------
    // Wishlist products
    // ------------------------------------------------------------------

    @SuppressLint("Range")
    public List<Product> getWishlistProducts(int userId) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT p.product_id, p.name, p.price, p.sale_price, " +
                "COALESCE(p.thumbnail_url, (SELECT image_url FROM product_images pi " +
                "   WHERE pi.product_id = p.product_id ORDER BY sort_order ASC LIMIT 1)) AS thumbnail_url " +
                "FROM wishlists w JOIN products p ON w.product_id = p.product_id " +
                "WHERE w.user_id = ? ORDER BY w.created_at DESC";
        try (Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                double price = c.getDouble(c.getColumnIndex("price"));
                double sale = c.getDouble(c.getColumnIndex("sale_price"));
                if (sale > price) { double t = price; price = sale; sale = t; }
                boolean hasSale = sale > 0 && sale < price;
                String priceStr = String.format(Locale.getDefault(), "%,.0fđ", hasSale ? sale : price);
                String originalStr = hasSale ? String.format(Locale.getDefault(), "%,.0fđ", price) : null;

                Product product = new Product(
                        c.getInt(c.getColumnIndex("product_id")),
                        c.getString(c.getColumnIndex("name")),
                        priceStr, originalStr);
                product.setImageUrl(c.getString(c.getColumnIndex("thumbnail_url")));
                if (hasSale) {
                    product.setDiscountPercent((int) ((1 - (sale / price)) * 100));
                }
                list.add(product);
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "getWishlistProducts error: " + e.getMessage());
        }
        return list;
    }

    public boolean removeFromWishlist(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("wishlists", "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)}) > 0;
    }

    // ------------------------------------------------------------------
    // Points transactions
    // ------------------------------------------------------------------

    @SuppressLint("Range")
    public List<PointsTransaction> getPointsTransactions(int userId) {
        List<PointsTransaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT points_change, reason, created_at FROM points_transactions " +
                        "WHERE user_id = ? ORDER BY created_at DESC",
                new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                list.add(new PointsTransaction(
                        c.getInt(c.getColumnIndex("points_change")),
                        c.getString(c.getColumnIndex("reason")),
                        c.getString(c.getColumnIndex("created_at"))));
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "getPointsTransactions error: " + e.getMessage());
        }
        return list;
    }

    public int getWishlistCount(int userId) {
        return countRows("SELECT COUNT(*) FROM wishlists WHERE user_id = ?", userId);
    }

    public int getAddressCount(int userId) {
        return countRows("SELECT COUNT(*) FROM user_addresses WHERE user_id = ?", userId);
    }

    /** Returns the default (or first) address as a readable line, or null if the user has none. */
    @SuppressLint("Range")
    public String getDefaultAddressText(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT recipient_name, phone, address_line, ward, district, city " +
                            "FROM user_addresses WHERE user_id = ? ORDER BY is_default DESC LIMIT 1",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex("recipient_name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String line = cursor.getString(cursor.getColumnIndex("address_line"));
                String ward = cursor.getString(cursor.getColumnIndex("ward"));
                String district = cursor.getString(cursor.getColumnIndex("district"));
                String city = cursor.getString(cursor.getColumnIndex("city"));
                return name + " · " + phone + "\n" + line + ", " + ward + ", " + district + ", " + city;
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "getDefaultAddressText error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private int countRows(String sql, int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            android.util.Log.e("UserDAO", "countRows error: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    private String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
    }
}
