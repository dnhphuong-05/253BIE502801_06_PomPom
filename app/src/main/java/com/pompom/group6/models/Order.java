package com.pompom.group6.models;

public class Order {
    private int orderId;
    /** Id chuỗi của đơn trong MongoDB — dùng để mở màn chi tiết/theo dõi. */
    private String oid;
    private String orderNumber;
    private double finalAmount;
    private String status;
    private String paymentMethod;
    private String createdAt;
    private int itemCount;
    private String firstItemName;
    private String firstItemImage;

    public Order(int orderId, String oid, String orderNumber, double finalAmount, String status,
                 String paymentMethod, String createdAt, int itemCount,
                 String firstItemName, String firstItemImage) {
        this.orderId = orderId;
        this.oid = oid;
        this.orderNumber = orderNumber;
        this.finalAmount = finalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.itemCount = itemCount;
        this.firstItemName = firstItemName;
        this.firstItemImage = firstItemImage;
    }

    public int getOrderId() { return orderId; }
    public String getOid() { return oid; }
    public String getOrderNumber() { return orderNumber; }
    public double getFinalAmount() { return finalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCreatedAt() { return createdAt; }
    public int getItemCount() { return itemCount; }
    public String getFirstItemName() { return firstItemName; }
    public String getFirstItemImage() { return firstItemImage; }

    /** Vietnamese label for the raw status string. */
    public String getStatusLabel() {
        if (status == null || status.trim().isEmpty()) return "Không rõ";
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "processing": return "Đang xử lý";
            case "shipping": return "Đang giao";
            case "delivered": return "Đã giao";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            case "returned": return "Trả hàng";
            default: return status;
        }
    }
}
