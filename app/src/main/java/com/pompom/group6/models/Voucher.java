package com.pompom.group6.models;

public class Voucher {
    private int id;
    private String code;
    private String discountType;
    private double discountValue;
    private double minOrderAmount;
    private String expiryDate;
    private int remainingCount;

    public Voucher(int id, String code, String discountType, double discountValue, double minOrderAmount, String expiryDate, int remainingCount) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.expiryDate = expiryDate;
        this.remainingCount = remainingCount;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public String getExpiryDate() { return expiryDate; }
    public int getRemainingCount() { return remainingCount; }
}
