package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.pompom.group6.models.Product;
import com.pompom.group6.models.ProductVariant;
import com.pompom.group6.models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    /**
     * Filtered + paginated query for ShopFragment.
     * Pass null/empty categoryIds to skip category filter.
     * Pass 0 for minPrice/maxPrice to skip price bounds.
     * Pass 0 for minRating to skip rating filter.
     */
    public List<Product> getProductsFiltered(Set<Integer> categoryIds,
                                              long minPrice, long maxPrice,
                                              float minRating,
                                              int limit, int offset) {
        // No sorting → keep default (natural) ordering.
        return getProductsFiltered(categoryIds, minPrice, maxPrice, minRating,
                "", "", false, false, limit, offset);
    }

    /**
     * Filtered + sorted + paginated query for ShopFragment.
     *
     * <p>Sort options are combined by priority: Phổ biến → Mới nhất → Giá → Tên,
     * with a stable {@code product_id} tiebreak so pagination stays consistent.</p>
     *
     * @param sortAlpha  "" | "asc" | "desc" — sort by product name
     * @param sortPrice  "" | "asc" | "desc" — sort by effective (sale) price
     * @param sortNewest true → newest first (by created_at)
     * @param sortPopular true → most-reviewed first
     */
    public List<Product> getProductsFiltered(Set<Integer> categoryIds,
                                              long minPrice, long maxPrice,
                                              float minRating,
                                              String sortAlpha, String sortPrice,
                                              boolean sortNewest, boolean sortPopular,
                                              int limit, int offset) {
        List<String> argList = new ArrayList<>();
        StringBuilder where = new StringBuilder("p.is_active = 1");

        // Category filter (dynamic IN clause)
        if (categoryIds != null && !categoryIds.isEmpty()) {
            StringBuilder ph = new StringBuilder();
            for (Integer id : categoryIds) {
                if (ph.length() > 0) ph.append(",");
                ph.append("?");
                argList.add(String.valueOf(id));
            }
            where.append(" AND p.category_id IN (").append(ph).append(")");
        }

        // Effective price: use sale_price when it is lower than regular price
        String effectivePrice =
                "CASE WHEN p.sale_price > 0 AND p.sale_price < p.price " +
                "THEN p.sale_price ELSE p.price END";

        if (minPrice > 0) {
            where.append(" AND (").append(effectivePrice).append(") >= ?");
            argList.add(String.valueOf(minPrice));
        }
        if (maxPrice > 0) {
            where.append(" AND (").append(effectivePrice).append(") <= ?");
            argList.add(String.valueOf(maxPrice));
        }

        // Rating filter via correlated sub-query
        String avgExpr =
                "(SELECT COALESCE(AVG(rating), 0) FROM product_reviews pr " +
                "WHERE pr.product_id = p.product_id)";
        if (minRating > 0) {
            where.append(" AND ").append(avgExpr).append(" >= ?");
            argList.add(String.valueOf(minRating));
        }

        // ── ORDER BY (combine active sorts by priority) ──────────────────────
        List<String> orderParts = new ArrayList<>();
        if (sortPopular) orderParts.add("review_count DESC");
        if (sortNewest)  orderParts.add("p.created_at DESC");
        if ("asc".equals(sortPrice))  orderParts.add("effective_price ASC");
        else if ("desc".equals(sortPrice)) orderParts.add("effective_price DESC");
        if ("asc".equals(sortAlpha))  orderParts.add("p.name COLLATE NOCASE ASC");
        else if ("desc".equals(sortAlpha)) orderParts.add("p.name COLLATE NOCASE DESC");
        orderParts.add("p.product_id ASC"); // stable tiebreak for pagination

        // LIMIT / OFFSET
        argList.add(String.valueOf(limit));
        argList.add(String.valueOf(offset));

        String query = "SELECT p.*, " +
                "(" + effectivePrice + ") as effective_price, " +
                avgExpr + " as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p " +
                "WHERE " + where +
                " ORDER BY " + TextUtils.join(", ", orderParts) +
                " LIMIT ? OFFSET ?";

        return getProductsByQuery(query, argList.toArray(new String[0]));
    }

    /**
     * Products in the same category as {@code productId} (excluding itself),
     * for the "Sản phẩm liên quan" grid on the detail screen.
     */
    public List<Product> getRelatedProducts(int productId, int limit) {
        String avgExpr =
                "(SELECT COALESCE(AVG(rating), 0) FROM product_reviews pr " +
                "WHERE pr.product_id = p.product_id)";
        String query = "SELECT p.*, " +
                avgExpr + " as avg_rating, " +
                "(SELECT COUNT(*) FROM product_reviews pr WHERE pr.product_id = p.product_id) as review_count " +
                "FROM products p " +
                "WHERE p.is_active = 1 " +
                "AND p.category_id = (SELECT category_id FROM products WHERE product_id = ?) " +
                "AND p.product_id != ? " +
                "ORDER BY review_count DESC, p.product_id ASC " +
                "LIMIT ?";
        return getProductsByQuery(query,
                new String[]{String.valueOf(productId), String.valueOf(productId), String.valueOf(limit)});
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

                if (salePriceVal > priceVal) {
                    double t = priceVal; priceVal = salePriceVal; salePriceVal = t;
                }

                String priceStr = String.format(Locale.getDefault(), "%.0fđ",
                        (salePriceVal > 0 && salePriceVal < priceVal) ? salePriceVal : priceVal);
                String originalPriceStr = (salePriceVal > 0 && salePriceVal < priceVal)
                        ? String.format(Locale.getDefault(), "%.0fđ", priceVal) : null;

                product = new Product(String.valueOf(id), name, priceStr, originalPriceStr);
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

                try {
                    try (Cursor imgCursor = db.query("product_images", new String[]{"image_url"},
                            "product_id = ?", new String[]{String.valueOf(id)},
                            null, null, "sort_order ASC", "1")) {
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
        try (Cursor cursor = db.rawQuery(
                "SELECT image_url FROM product_images WHERE product_id = ? ORDER BY sort_order ASC",
                new String[]{String.valueOf(productId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String url = cursor.getString(0);
                    if (url != null && !url.trim().isEmpty()) images.add(url.trim());
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
                "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    reviews.add(new Review(
                            cursor.getInt(cursor.getColumnIndexOrThrow("review_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("avatar_url")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                            cursor.getString(cursor.getColumnIndexOrThrow("comment")),
                            cursor.getString(cursor.getColumnIndexOrThrow("images")),
                            cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                    ));
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

    // ── Internal helper ───────────────────────────────────────────────────

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

                    if (salePriceVal > priceVal) {
                        double t = priceVal; priceVal = salePriceVal; salePriceVal = t;
                    }

                    String priceStr = String.format(Locale.getDefault(), "%.0fđ",
                            (salePriceVal > 0 && salePriceVal < priceVal) ? salePriceVal : priceVal);
                    String originalPriceStr = (salePriceVal > 0 && salePriceVal < priceVal)
                            ? String.format(Locale.getDefault(), "%.0fđ", priceVal) : null;

                    Product product = new Product(String.valueOf(id), name, priceStr, originalPriceStr);

                    int ratingIdx = cursor.getColumnIndex("avg_rating");
                    if (ratingIdx != -1) product.setRating(cursor.getFloat(ratingIdx));
                    int countIdx = cursor.getColumnIndex("review_count");
                    if (countIdx != -1) product.setReviewCount(cursor.getInt(countIdx));

                    if (salePriceVal > 0 && salePriceVal < priceVal) {
                        int discount = (int) ((1 - (salePriceVal / priceVal)) * 100);
                        product.setDiscountPercent(discount);
                    }

                    try (Cursor imgCursor = db.query("product_images", new String[]{"image_url"},
                            "product_id = ?", new String[]{String.valueOf(id)},
                            null, null, "sort_order ASC", "1")) {
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
