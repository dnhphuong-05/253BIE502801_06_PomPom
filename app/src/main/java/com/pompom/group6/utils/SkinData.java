package com.pompom.group6.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dữ liệu tĩnh cho "Hồ sơ làn da": danh sách loại da, tông da và thành phần cần tránh
 * theo từng loại da (gợi ý chăm sóc chung, không phải tư vấn y khoa).
 */
public final class SkinData {
    private SkinData() {}

    /** Một lựa chọn (mã lưu DB + nhãn hiển thị). */
    public static final class Option {
        public final String code;
        public final String label;
        public Option(String code, String label) { this.code = code; this.label = label; }
    }

    public static List<Option> skinTypes() {
        return Arrays.asList(
                new Option("oily", "Da dầu"),
                new Option("dry", "Da khô"),
                new Option("normal", "Da thường"),
                new Option("combination", "Da hỗn hợp"),
                new Option("sensitive", "Da nhạy cảm")
        );
    }

    public static List<Option> skinTones() {
        return Arrays.asList(
                new Option("cool", "Tông lạnh (hồng/đỏ)"),
                new Option("warm", "Tông ấm (vàng)"),
                new Option("neutral", "Tông trung tính"),
                new Option("olive", "Tông ô liu")
        );
    }

    public static String labelOf(List<Option> options, String code) {
        if (code == null) return null;
        for (Option o : options) if (o.code.equalsIgnoreCase(code)) return o.label;
        return code;
    }

    public static String skinTypeLabel(String code) {
        return labelOf(skinTypes(), code);
    }

    public static String skinToneLabel(String code) {
        return labelOf(skinTones(), code);
    }

    /** Thành phần cần tránh theo loại da. */
    public static List<String> avoidIngredients(String skinTypeCode) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put("oily", Arrays.asList(
                "Dầu khoáng (Mineral oil) — dễ gây bít tắc",
                "Bơ hạt mỡ / dầu dừa đậm đặc — nặng mặt",
                "Cồn khô nồng độ cao (Alcohol denat)",
                "Hương liệu tổng hợp mạnh",
                "Silicone dày gây bít lỗ chân lông"));
        map.put("dry", Arrays.asList(
                "Cồn khô (Alcohol denat, SD alcohol)",
                "Sulfate mạnh (SLS/SLES)",
                "Hương liệu nồng độ cao",
                "AHA/Retinol nồng độ cao khi da chưa quen"));
        map.put("sensitive", Arrays.asList(
                "Hương liệu & tinh dầu",
                "Cồn khô",
                "Chất tạo màu nhân tạo",
                "Paraben",
                "Acid tẩy mạnh nồng độ cao (AHA/BHA cao)"));
        map.put("combination", Arrays.asList(
                "Dầu nặng ở vùng chữ T",
                "Cồn khô làm khô vùng má",
                "Hương liệu mạnh",
                "Sản phẩm tẩy dầu quá mức"));
        map.put("normal", Arrays.asList(
                "Cồn khô nồng độ cao (nếu dễ kích ứng)",
                "Hương liệu nồng độ cao",
                "Tẩy tế bào chết quá thường xuyên"));

        List<String> list = skinTypeCode == null ? null : map.get(skinTypeCode.toLowerCase());
        return list != null ? new ArrayList<>(list) : new ArrayList<>(Arrays.asList(
                "Cồn khô nồng độ cao (Alcohol denat)",
                "Hương liệu & tinh dầu dễ kích ứng",
                "Sulfate mạnh (SLS/SLES)"));
    }
}
