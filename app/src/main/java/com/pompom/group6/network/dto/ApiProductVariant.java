package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Biến thể sản phẩm (nằm trong ApiProduct.variants). */
public class ApiProductVariant {
    @SerializedName("id") public String id;
    @SerializedName("variant_name") public String variantName;
    @SerializedName("additional_price") public double additionalPrice;
    @SerializedName("stock") public int stock;
    @SerializedName("image_url") public String imageUrl;
}
