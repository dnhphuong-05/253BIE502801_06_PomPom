package com.pompom.group6.models;

/**
 * Một lượt dùng tính năng AI đã lưu (ai_sessions + bảng chi tiết theo loại
 * ai_dermatologist/ai_makeup_artist). Dùng cho cả màn Lịch sử (danh sách) và màn Kết quả
 * phân tích (xem lại chi tiết một lượt).
 */
public class AiSession {
    public static final String TYPE_DERMATOLOGIST = "dermatologist";
    public static final String TYPE_MAKEUP_ARTIST = "makeup_artist";

    private long id;
    private String aiType;
    private String createdAt;
    private String outputData; // tóm tắt/nhận xét chính — hiển thị trực tiếp trong list lịch sử

    // Chi tiết riêng theo loại — chỉ populate cột tương ứng với aiType.
    private String skinAnalysis;          // dermatologist: JSON "Oil:85,Acne:62,..."
    private String recommendationSkincare; // dermatologist: lời khuyên của AI
    private float confidence;              // dermatologist: độ tin cậy 0..1
    private String makeupProductsUsed;      // makeup_artist: các tông đã thử, phân tách bởi dấu phẩy

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getAiType() { return aiType; }
    public void setAiType(String aiType) { this.aiType = aiType; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }
    public String getSkinAnalysis() { return skinAnalysis; }
    public void setSkinAnalysis(String skinAnalysis) { this.skinAnalysis = skinAnalysis; }
    public String getRecommendationSkincare() { return recommendationSkincare; }
    public void setRecommendationSkincare(String recommendationSkincare) { this.recommendationSkincare = recommendationSkincare; }
    public float getConfidence() { return confidence; }
    public void setConfidence(float confidence) { this.confidence = confidence; }
    public String getMakeupProductsUsed() { return makeupProductsUsed; }
    public void setMakeupProductsUsed(String makeupProductsUsed) { this.makeupProductsUsed = makeupProductsUsed; }
}
