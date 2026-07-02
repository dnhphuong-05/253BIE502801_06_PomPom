package com.pompom.group6.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.PromotionProduct;
import com.pompom.group6.models.Voucher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PromotionDAO {
    private static final String TAG = "PromotionDAO";
    private final DatabaseHelper dbHelper;

    public PromotionDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<PromotionProduct> getFlashSaleProducts(int limit) {
        List<PromotionProduct> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Join promotions with promotion_details and products to get sale details
        String query = "SELECT p.product_id, p.name, p.price as original_price, " +
                      "pd.discount_percent, pd.discount_amount, pi.image_url " +
                      "FROM promotions pr " +
                      "JOIN promotion_details pd ON pr.promotion_id = pd.promotion_id " +
                      "JOIN products p ON pd.product_id = p.product_id " +
                      "LEFT JOIN product_images pi ON p.product_id = pi.product_id AND pi.sort_order = 1 " +
                      "WHERE pr.type = 'flash_sale' AND pr.is_active = 1 " +
                      "LIMIT ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("original_price"));
                    int discountPercent = cursor.getInt(cursor.getColumnIndexOrThrow("discount_percent"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                    double salePrice = originalPrice;
                    if (discountPercent > 0) {
                        salePrice = originalPrice * (1 - discountPercent / 100.0);
                    } else {
                        double discountAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("discount_amount"));
                        if (discountAmount > 0) {
                            salePrice = originalPrice - discountAmount;
                            discountPercent = (int) ((discountAmount / originalPrice) * 100);
                        }
                    }
                    
                    // Mock stock data for design consistency
                    int totalStock = 400;
                    int soldCount = (int) (Math.random() * 200 + 50);

                    products.add(new PromotionProduct(id, name, imageUrl, originalPrice, salePrice, discountPercent, totalStock, soldCount));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching flash sale products", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        // Fallback for demo if database query returns nothing
        if (products.isEmpty()) {
            products.add(new PromotionProduct(1, "Unicorn Magic Palette", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344448/PomPom_Unicorn_Magic_Palette_p82y28.webp", 249000, 124500, 50, 400, 120));
            products.add(new PromotionProduct(3, "Cloud Cushion", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344277/PomPom_Cloud_Cushion_kwewua.webp", 399000, 199500, 50, 400, 85));
            products.add(new PromotionProduct(7, "Butterfly Highlight", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/ma_hong_r2373s.webp", 189000, 94500, 50, 400, 210));
            products.add(new PromotionProduct(18, "Heart Brush Set", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/co_trang_diem_zgi4dz.webp", 450000, 225000, 50, 400, 45));
        }

        return products;
    }

    public List<String> getActivePromotionMessages() {
        List<String> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM promotions WHERE is_active = 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    messages.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching promotion messages", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        
        if (messages.isEmpty()) {
            messages.add("Giảm 50% cho bộ sưu tập Knight Unicorn mới");
            messages.add("Miễn phí vận chuyển cho đơn hàng từ 500.000đ!");
            messages.add("Tặng ngay túi Rosy Pouch cho đơn hàng từ 1.000.000đ");
        }
        return messages;
    }

    public List<Voucher> getAllVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getAllVouchers", e);
            return vouchers;
        }
        String query = "SELECT * FROM vouchers WHERE is_active = 1";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Voucher voucher = new Voucher(
                            cursor.getInt(cursor.getColumnIndexOrThrow("voucher_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("code")),
                            cursor.getString(cursor.getColumnIndexOrThrow("discount_type")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("discount_value")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("min_order_amount")),
                            cursor.getString(cursor.getColumnIndexOrThrow("end_date")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("usage_limit")) - cursor.getInt(cursor.getColumnIndexOrThrow("used_count"))
                    );
                    vouchers.add(voucher);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Loaded " + vouchers.size() + " vouchers");
        } catch (Exception e) {
            Log.e(TAG, "Error fetching vouchers: " + e.getMessage());
        }
        return vouchers;
    }

    /**
     * Returns the ids of vouchers this user has already saved (from the
     * existing {@code user_vouchers} table).
     */
    public Set<Integer> getSavedVoucherIds(int userId) {
        Set<Integer> ids = new HashSet<>();
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getSavedVoucherIds", e);
            return ids;
        }
        try (Cursor cursor = db.rawQuery(
                "SELECT voucher_id FROM user_vouchers WHERE user_id = ?",
                new String[]{String.valueOf(userId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching saved voucher ids: " + e.getMessage());
        }
        return ids;
    }

    /**
     * Saves a voucher to the current user's account by inserting into the
     * existing {@code user_vouchers} table. The (user_id, voucher_id) primary
     * key means a duplicate save is ignored rather than failing.
     *
     * @return true if the voucher is now saved (either newly inserted or
     *         already present), false only on a real DB error.
     */
    public boolean saveVoucherForUser(int userId, int voucherId) {
        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in saveVoucherForUser", e);
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("voucher_id", voucherId);
            // The (user_id, voucher_id) primary key makes a duplicate insert a no-op
            // (CONFLICT_IGNORE), so re-saving is safe and still counts as "saved".
            db.insertWithOnConflict("user_vouchers", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving voucher for user: " + e.getMessage());
            return false;
        }
    }
}