package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String TAG = "ProductDAO";
    private final DatabaseHelper dbHelper;

    public ProductDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<Product> getAllProducts() {
        return getProductsByQuery("SELECT * FROM products WHERE is_active = 1", null);
    }

    public List<Product> getBestSellers(int limit) {
        String query = "SELECT p.*, SUM(oi.quantity) as total_sold " +
                      "FROM products p " +
                      "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
                      "WHERE p.is_active = 1 " +
                      "GROUP BY p.product_id " +
                      "ORDER BY total_sold DESC LIMIT ?";
        return getProductsByQuery(query, new String[]{String.valueOf(limit)});
    }

    private List<Product> getProductsByQuery(String query, String[] args) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(query, args);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    
                    // Handle potential double or string price
                    String priceStr;
                    int priceIdx = cursor.getColumnIndex("price");
                    try {
                        double priceVal = cursor.getDouble(priceIdx);
                        priceStr = String.format("%.0fđ", priceVal);
                    } catch (Exception e) {
                        priceStr = cursor.getString(priceIdx);
                    }

                    Product product = new Product(id, name, priceStr, null);
                    
                    // Fetch first image from product_images
                    try (Cursor imgCursor = db.query("product_images", new String[]{"image_url"},
                            "product_id = ?", new String[]{String.valueOf(id)}, null, null, "sort_order ASC", "1")) {
                        if (imgCursor != null && imgCursor.moveToFirst()) {
                            product.setImageUrl(imgCursor.getString(0));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error fetching image for product " + id, e);
                    }
                    
                    products.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying products: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return products;
    }
}
