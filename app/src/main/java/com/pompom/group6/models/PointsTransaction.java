package com.pompom.group6.models;

public class PointsTransaction {
    private final int pointsChange;
    private final String reason;
    private final String createdAt;

    public PointsTransaction(int pointsChange, String reason, String createdAt) {
        this.pointsChange = pointsChange;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public int getPointsChange() { return pointsChange; }
    public String getCreatedAt() { return createdAt; }

    /** Vietnamese label for the raw reason code. */
    public String getReasonLabel() {
        if (reason == null) return "Giao dịch điểm";
        switch (reason) {
            case "earn_from_order": return "Tích điểm từ đơn hàng";
            case "redeem_voucher": return "Đổi voucher";
            case "signup_bonus": return "Điểm thưởng đăng ký";
            case "admin_adjust": return "Điều chỉnh";
            default: return reason;
        }
    }
}
