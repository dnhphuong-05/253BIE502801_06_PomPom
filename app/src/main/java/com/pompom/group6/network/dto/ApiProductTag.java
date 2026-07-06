package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Sản phẩm được gắn thẻ trong Reel/bài viết tips (khớp product_tags đã resolve ở backend). */
public class ApiProductTag implements Serializable {
    @SerializedName("id") public String id;
    @SerializedName("name") public String name;
    @SerializedName("thumbnail_url") public String thumbnailUrl;
    @SerializedName("price") public double price;
    @SerializedName("sale_price") public double salePrice;
}
