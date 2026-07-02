package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.Product;
import com.pompom.group6.models.ProductVariant;
import com.pompom.group6.models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDAO {
    private static final String TAG = "ProductDAO";
    private final DatabaseHelper dbHelper;

    public ProductDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<Product> getAllProducts() {
        String query = "SELECT p.*, " +
                "(SELECT AVG(rating) FROM product_reviews pr WHERE pr.product_id = p.product_id) as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p WHERE p.is_active = 1";
        return getProductsByQuery(query, null);
    }

    public List<Product> getBestSellers(int limit) {
        String query = "SELECT p.*, " +
                "(SELECT AVG(rating) FROM product_reviews pr WHERE pr.product_id = p.product_id) as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count, " +
                "SUM(oi.quantity) as total_sold " +
                "FROM products p " +
                "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
                "WHERE p.is_active = 1 " +
                "GROUP BY p.product_id " +
                "ORDER BY total_sold DESC LIMIT ?";
        return getProductsByQuery(query, new String[]{String.valueOf(limit)});
    }

    public List<Product> searchProducts(String keyword) {
        String query = "SELECT p.*, " +
                "(SELECT AVG(rating) FROM product_reviews pr WHERE pr.product_id = p.product_id) as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p WHERE p.is_active = 1 AND p.name LIKE ?";
        return getProductsByQuery(query, new String[]{"%" + keyword + "%"});
    }

    public List<Product> getProductsPaginated(int limit, int offset) {
        String query = "SELECT p.*, " +
                "(SELECT AVG(rating) FROM product_reviews pr WHERE pr.product_id = p.product_id) as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p WHERE p.is_active = 1 LIMIT ? OFFSET ?";
        return getProductsByQuery(query, new String[]{String.valueOf(limit), String.valueOf(offset)});
    }

    public Product getProductById(int productId) {
        String query = "SELECT p.*, c.category_name, " +
                "(SELECT AVG(rating) FROM product_reviews pr WHERE pr.product_id = p.product_id) as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "WHERE p.product_id = ?";
        
        Product product = null;
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getProductById: " + e.getMessage(), e);
            return null;
        }

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)})) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double priceVal = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                double salePriceVal = cursor.getDouble(cursor.getColumnIndexOrThrow("sale_price"));
                
                // Resilience for swapped data
                if (salePriceVal > priceVal) {
                    double t = priceVal; priceVal = salePriceVal; salePriceVal = t;
                }

                String priceStr = String.format(Locale.getDefault(), "%.0fđ", (salePriceVal > 0 && salePriceVal < priceVal) ? salePriceVal : priceVal);
                String originalPriceStr = (salePriceVal > 0 && salePriceVal < priceVal) ? String.format(Locale.getDefault(), "%.0fđ", priceVal) : null;

                product = new Product(id, name, priceStr, originalPriceStr);
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setSku(cursor.getString(cursor.getColumnIndexOrThrow("sku")));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
                product.setBrandName(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
                product.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                product.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow("avg_rating")));
                product.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow("review_count")));
                
                if (salePriceVal > 0 && priceVal > 0 && salePriceVal < priceVal) {
                    int discount = (int) ((1 - (salePriceVal / priceVal)) * 100);
                    product.setDiscountPercent(discount);
                }

                // Fetch first image
                try {
                    try (Cursor imgCursor = db.query("product_images", new String[]{"image_url"},
                            "product_id = ?", new String[]{String.valueOf(id)}, null, null, "sort_order ASC", "1")) {
                        if (imgCursor != null && imgCursor.moveToFirst()) {
                            product.setImageUrl(imgCursor.getString(0));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching product image: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting product by id: " + e.getMessage());
        }
        return product;
    }

    public List<String> getProductImages(int productId) {
        List<String> images = new ArrayList<>();
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getProductImages", e);
            return images;
        }
        try (Cursor cursor = db.rawQuery("SELECT image_url FROM product_images WHERE product_id = ? ORDER BY sort_order ASC", new String[]{String.valueOf(productId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(0);
                    if (url != null && !url.trim().isEmpty()) {
                        images.add(url.trim());
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting product images for " + productId, e);
        }
        return images;
    }

    public List<Review> getReviewsForProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getReviewsForProduct", e);
            return reviews;
        }
        String query = "SELECT r.*, u.full_name, u.avatar_url FROM product_reviews r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.product_id = ? " +
                "ORDER BY r.created_at DESC";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Review review = new Review(
                            cursor.getInt(cursor.getColumnIndexOrThrow("review_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("avatar_url")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                            cursor.getString(cursor.getColumnIndexOrThrow("comment")),
                            cursor.getString(cursor.getColumnIndexOrThrow("images")),
                            cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                    );
                    reviews.add(review);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Loaded " + reviews.size() + " reviews for product " + productId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting reviews for product " + productId, e);
        }
        return reviews;
    }

    public List<ProductVariant> getVariantsForProduct(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in getVariantsForProduct", e);
            return variants;
        }
        try (Cursor cursor = db.query("product_variants", null, "product_id = ?",
                new String[]{String.valueOf(productId)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    variants.add(new ProductVariant(
                            cursor.getInt(cursor.getColumnIndexOrThrow("variant_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("variant_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("sku")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("additional_price")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                            cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting variants for product " + productId, e);
        }
        return variants;
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
                    double priceVal = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    double salePriceVal = cursor.getDouble(cursor.getColumnIndexOrThrow("sale_price"));

                    // Swap if needed
                    if (salePriceVal > priceVal) {
                        double t = priceVal; priceVal = salePriceVal; salePriceVal = t;
                    }

                    String priceStr = String.format(Locale.getDefault(), "%.0fđ", 
                            (salePriceVal > 0 && salePriceVal < priceVal) ? salePriceVal : priceVal);
                    String originalPriceStr = (salePriceVal > 0 && salePriceVal < priceVal) ? 
                            String.format(Locale.getDefault(), "%.0fđ", priceVal) : null;

                    Product product = new Product(id, name, priceStr, originalPriceStr);

                    int ratingIdx = cursor.getColumnIndex("avg_rating");
                    if (ratingIdx != -1) product.setRating(cursor.getFloat(ratingIdx));
                    int countIdx = cursor.getColumnIndex("review_count");
                    if (countIdx != -1) product.setReviewCount(cursor.getInt(countIdx));

                    if (salePriceVal > 0 && salePriceVal < priceVal) {
                        int discount = (int) ((1 - (salePriceVal / priceVal)) * 100);
                        product.setDiscountPercent(discount);
                    }
                    
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
            if (cursor != null) cursor.close();
        }
        return products;
    }
}
