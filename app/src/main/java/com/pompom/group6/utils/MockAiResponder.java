package com.pompom.group6.utils;

import java.util.Locale;
import java.util.Random;

/**
 * Trả lời AI giả lập, chạy hoàn toàn local — không gọi API Gemini (key hiện tại là placeholder,
 * luôn lỗi). Dùng cho AI Dermatologist, AI Chat/mascot: nhận diện vài từ khoá trong câu hỏi để
 * trả lời có vẻ "hiểu" người dùng, dùng cho demo ổn định, không phụ thuộc mạng/API key thật.
 */
public final class MockAiResponder {
    private MockAiResponder() {}

    private static final Random RANDOM = new Random();

    private static final String[] ACNE_REPLIES = {
            "Với tình trạng mụn hiện tại, bạn nên dùng sữa rửa mặt dịu nhẹ và tránh nặn mụn nhé. Sản phẩm chứa salicylic acid sẽ giúp ích nhiều đấy!",
            "Da đang có mụn thì mình khuyên bạn ưu tiên các bước dưỡng đơn giản, tránh đắp mặt nạ quá nhiều lần trong tuần, và luôn tẩy trang kỹ trước khi ngủ.",
    };
    private static final String[] DRY_SKIN_REPLIES = {
            "Da khô cần được cấp ẩm sâu đó! Bạn thử dùng serum HA rồi khoá ẩm bằng kem dưỡng chứa ceramide xem sao nhé.",
            "Với da khô, mình gợi ý bạn giảm tần suất tẩy tế bào chết lại, chỉ 1-2 lần/tuần thôi, và luôn thoa kem dưỡng ngay sau khi rửa mặt.",
    };
    private static final String[] OILY_SKIN_REPLIES = {
            "Da dầu thì bạn nên chọn toner kiềm dầu và kem dưỡng dạng gel, tránh sản phẩm quá đặc gây bí da nhé.",
            "Mình thấy da bạn khá nhiều dầu, giấy thấm dầu và sữa rửa mặt dịu nhẹ 2 lần/ngày sẽ giúp kiểm soát tốt hơn đó!",
    };
    private static final String[] PRODUCT_REPLIES = {
            "PomPom có vài sản phẩm rất hợp với nhu cầu này, bạn ghé mục Shop xem thử phần \"Gợi ý cho bạn\" nhé, mình đã chọn lọc kỹ rồi đó!",
            "Bạn xem thử các sản phẩm best-seller ở trang chủ nha, được đánh giá cao và phù hợp với nhiều loại da lắm đó!",
    };
    private static final String[] GENERIC_REPLIES = {
            "Cảm ơn bạn đã chia sẻ! Mình luôn ở đây để tư vấn làm đẹp cho bạn, cứ hỏi thêm nhé 🌸",
            "Đó là một câu hỏi hay đấy! Bạn có thể mô tả rõ hơn về làn da hoặc nhu cầu của mình để mình tư vấn chính xác hơn nhé.",
            "Mình hiểu rồi! Để chăm sóc da tốt nhất, hãy nhớ duy trì 3 bước cơ bản: làm sạch, dưỡng ẩm và chống nắng mỗi ngày nhé.",
    };

    /** Trả lời cho một câu hỏi/tin nhắn của người dùng — nhận diện từ khoá đơn giản. */
    public static String reply(String userText) {
        String text = userText == null ? "" : userText.toLowerCase(Locale.ROOT);
        if (containsAny(text, "mụn", "acne")) return pick(ACNE_REPLIES);
        if (containsAny(text, "khô", "nứt nẻ", "bong tróc")) return pick(DRY_SKIN_REPLIES);
        if (containsAny(text, "dầu", "nhờn", "bóng dầu")) return pick(OILY_SKIN_REPLIES);
        if (containsAny(text, "sản phẩm", "mua", "giá", "gợi ý", "mua gì")) return pick(PRODUCT_REPLIES);
        return pick(GENERIC_REPLIES);
    }

    private static final String[] BLEND_REPLIES = {
            "Để tán nền đều, bạn dùng mút ẩm hoặc cọ tán tròn, vỗ nhẹ theo hướng từ trong ra ngoài nhé!",
            "Mẹo nhỏ: tán một lớp mỏng trước, đợi vài giây rồi tán thêm lớp thứ hai để lên màu tự nhiên hơn đó!",
    };
    private static final String[] EYE_REPLIES = {
            "Với mắt, bạn nên đánh phấn mắt tông trung tính trước rồi mới kẻ eyeliner sát chân mi nhé!",
            "Kẻ mắt thì bạn kéo một đường mảnh sát chân mi, đuôi mắt hất nhẹ lên là xinh lắm đó!",
    };
    private static final String[] LIP_REPLIES = {
            "Với môi, bạn tán một lớp son dưỡng trước rồi mới đánh son màu để môi luôn mịn màng nhé!",
            "Bạn thử dùng cọ môi để tán đều màu son, viền môi sẽ sắc nét hơn đó!",
    };
    private static final String[] MAKEUP_GENERIC_REPLIES = {
            "Cảm ơn câu hỏi của bạn! Cứ từ từ làm theo từng bước nhé, mình luôn ở đây hỗ trợ bạn 💄",
            "Bạn làm rất tốt rồi đó! Cứ tiếp tục theo hướng dẫn, có gì cứ hỏi mình nhé.",
    };

    /** Trả lời câu hỏi trong lúc trang điểm — nhận diện từ khoá riêng cho makeup (khác reply()
     * vốn dành cho tư vấn da liễu), tránh trả lời lạc đề khi hỏi về cách trang điểm. */
    public static String makeupReply(String userText) {
        String text = userText == null ? "" : userText.toLowerCase(Locale.ROOT);
        if (containsAny(text, "tán", "nền", "phấn nền", "kem nền")) return pick(BLEND_REPLIES);
        if (containsAny(text, "mắt", "kẻ mắt", "eyeliner", "chân mày")) return pick(EYE_REPLIES);
        if (containsAny(text, "môi", "son")) return pick(LIP_REPLIES);
        return pick(MAKEUP_GENERIC_REPLIES);
    }

    /** Lời khuyên chăm sóc da dựa trên các chỉ số hiện có — dùng khi kết thúc cuộc gọi Dermatologist. */
    public static String skincareRecommendation(int oil, int acne, int pores, int wrinkles, int tone) {
        StringBuilder sb = new StringBuilder();
        if (acne >= 55) {
            sb.append("Da bạn đang có dấu hiệu mụn khá rõ. Nên dùng sữa rửa mặt dịu nhẹ, tránh nặn mụn và ưu tiên sản phẩm chứa salicylic acid hoặc tea tree oil. ");
        } else if (oil >= 65) {
            sb.append("Da bạn khá nhiều dầu. Hãy dùng toner kiềm dầu và kem dưỡng dạng gel, tránh các sản phẩm quá đặc gây bí da. ");
        } else if (wrinkles >= 45) {
            sb.append("Da bắt đầu có dấu hiệu lão hoá nhẹ. Nên bổ sung retinol hoặc peptide vào chu trình dưỡng da buổi tối. ");
        } else if (pores >= 65) {
            sb.append("Lỗ chân lông hơi to. Nên dùng thêm sản phẩm chứa niacinamide để se khít lỗ chân lông. ");
        } else {
            sb.append("Nhìn chung làn da của bạn khá ổn định và khoẻ mạnh. ");
        }
        if (tone < 60) {
            sb.append("Da chưa đều màu lắm, bạn nên dùng thêm serum vitamin C vào buổi sáng. ");
        }
        sb.append("Đừng quên chống nắng mỗi ngày để bảo vệ da nhé!");
        return sb.toString();
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    private static String pick(String[] options) {
        return options[RANDOM.nextInt(options.length)];
    }
}
