package com.pompom.group6.network;

import com.pompom.group6.models.Product;
import com.pompom.group6.network.dto.ApiProduct;

import java.util.Locale;

/**
 * Chuyển ApiProduct (JSON từ backend) sang model Product mà UI/adapter đang dùng,
 * định dạng giá GIỐNG hệt ProductDAO (SQLite) để hiển thị đồng nhất.
 */
public final class ProductMapper {
    private ProductMapper() {}

    public static Product toProduct(ApiProduct a) {
        double priceVal = a.price;
        double salePriceVal = a.salePrice;
        // Dữ liệu seed đôi khi để sale_price > price → hoán đổi cho đúng (giống ProductDAO).
        if (salePriceVal > priceVal) {
            double t = priceVal; priceVal = salePriceVal; salePriceVal = t;
        }
        boolean onSale = salePriceVal > 0 && salePriceVal < priceVal;

        String priceStr = String.format(Locale.getDefault(), "%.0fđ", onSale ? salePriceVal : priceVal);
        String originalStr = onSale ? String.format(Locale.getDefault(), "%.0fđ", priceVal) : null;

        Product p = new Product(a.id, a.name, priceStr, originalStr);
        p.setDescription(a.description);
        p.setSku(a.sku);
        p.setStock(a.stock);
        p.setBrandName(a.brand);
        p.setCategoryName(a.categoryName != null ? a.categoryName : "");
        p.setRating((float) a.ratingAvg);
        p.setReviewCount(a.reviewCount);
        p.setImageUrl(a.thumbnailUrl);
        if (onSale && priceVal > 0) {
            p.setDiscountPercent((int) ((1 - (salePriceVal / priceVal)) * 100));
        }
        return p;
    }
}
