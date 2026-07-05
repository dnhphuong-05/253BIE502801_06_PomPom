package com.pompom.group6.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.adapters.ChatAdapter;
import com.pompom.group6.databinding.ActivityProductConsultationChatBinding;
import com.pompom.group6.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductConsultationChatActivity extends SwipeBackActivity {

    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";
    public static final String EXTRA_PRODUCT_SKU  = "extra_product_sku";

    private ActivityProductConsultationChatBinding binding;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductConsultationChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        String productSku  = getIntent().getStringExtra(EXTRA_PRODUCT_SKU);

        setupHeader(productName, productSku);
        setupRecyclerView();
        setupInputTray();
        showWelcomeMessage(productName);
    }

    // ── Header ────────────────────────────────────────────────────────────────────

    private void setupHeader(String productName, String productSku) {
        binding.ivChatBack.setOnClickListener(v -> finish());

        String contextText = "Hỏi đáp về sản phẩm: " + (productName != null ? productName : "");
        if (productSku != null && !productSku.isEmpty()) {
            contextText += " (" + productSku + ")";
        }
        binding.tvChatProductContext.setText(contextText);
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(lm);
        binding.rvChat.setAdapter(chatAdapter);

        // Tap outside keyboard → dismiss
        binding.rvChat.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override public boolean onInterceptTouchEvent(@androidx.annotation.NonNull RecyclerView rv,
                                                           @androidx.annotation.NonNull MotionEvent e) {
                hideKeyboard();
                return false;
            }
            @Override public void onTouchEvent(@androidx.annotation.NonNull RecyclerView rv,
                                               @androidx.annotation.NonNull MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean b) {}
        });
    }

    // ── Input tray ────────────────────────────────────────────────────────────────

    private void setupInputTray() {
        binding.btnSendChat.setOnClickListener(v -> sendUserMessage());

        binding.etChatInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendUserMessage();
                return true;
            }
            return false;
        });
    }

    private void sendUserMessage() {
        String text = binding.etChatInput.getText().toString().trim();
        if (text.isEmpty()) return;

        binding.etChatInput.setText("");
        hideKeyboard();

        // Append user bubble
        appendMessage(new ChatMessage(text, ChatMessage.TYPE_USER));

        // Schedule mock bot reply after a short delay
        handler.postDelayed(() -> appendMessage(new ChatMessage(mockBotReply(text), ChatMessage.TYPE_BOT)),
                900 + new Random().nextInt(600));
    }

    private void appendMessage(ChatMessage msg) {
        messages.add(msg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        binding.rvChat.smoothScrollToPosition(messages.size() - 1);
    }

    // ── Welcome ───────────────────────────────────────────────────────────────────

    private void showWelcomeMessage(String productName) {
        String intro = "Xin chào! Tôi là trợ lý tư vấn sản phẩm của Pom Pom. "
                + "Bạn đang quan tâm đến "
                + (productName != null ? "\"" + productName + "\"" : "sản phẩm này")
                + ". Tôi có thể giúp gì cho bạn? 💕";
        handler.postDelayed(() -> appendMessage(new ChatMessage(intro, ChatMessage.TYPE_BOT)), 400);
    }

    // ── Mock bot responses ────────────────────────────────────────────────────────

    private static final String[] BOT_REPLIES = {
            "Sản phẩm này được làm từ chất liệu cao cấp, an toàn cho da nhạy cảm.",
            "Bạn có thể chọn nhiều màu sắc khác nhau phù hợp với sở thích của mình.",
            "Chúng tôi có chính sách đổi trả trong vòng 7 ngày nếu sản phẩm bị lỗi.",
            "Sản phẩm hiện đang có sẵn trong kho, bạn có thể đặt hàng ngay hôm nay!",
            "Giá niêm yết đã bao gồm thuế VAT, chúng tôi hỗ trợ nhiều hình thức thanh toán.",
            "Đây là sản phẩm bán chạy nhất của chúng tôi trong tháng này! 🌸",
            "Sản phẩm phù hợp với mọi loại da, kể cả da nhạy cảm và da dầu.",
            "Bạn có thể kết hợp sản phẩm này với các phụ kiện khác trong bộ sưu tập Pom Pom.",
            "Chúng tôi có chương trình tích điểm cho mỗi lần mua hàng, bạn đã đăng ký chưa?",
            "Nếu bạn cần thêm thông tin, bạn có thể liên hệ hotline 1800-POM-POM nhé! 💕"
    };

    private String mockBotReply(String userInput) {
        String lower = userInput.toLowerCase();
        if (lower.contains("giá") || lower.contains("bao nhiêu") || lower.contains("tiền")) {
            return "Giá sản phẩm đã được hiển thị trên trang chi tiết. Chúng tôi cam kết giá tốt nhất thị trường! 💕";
        }
        if (lower.contains("màu") || lower.contains("color")) {
            return "Sản phẩm có nhiều lựa chọn màu sắc. Bạn có thể xem chi tiết trong phần 'Chọn màu sắc' bên dưới.";
        }
        if (lower.contains("ship") || lower.contains("giao hàng") || lower.contains("vận chuyển")) {
            return "Chúng tôi hỗ trợ giao hàng toàn quốc. Đơn từ 500.000đ được miễn phí vận chuyển! 🚚";
        }
        if (lower.contains("đổi") || lower.contains("trả") || lower.contains("hoàn")) {
            return "Chúng tôi có chính sách đổi trả trong vòng 7 ngày kể từ ngày nhận hàng nếu sản phẩm bị lỗi từ nhà sản xuất.";
        }
        if (lower.contains("chất liệu") || lower.contains("nguyên liệu") || lower.contains("thành phần")) {
            return "Sản phẩm được làm từ chất liệu cao cấp, không chứa chất độc hại, an toàn cho sức khỏe người dùng.";
        }
        // Default: pick a random reply
        return BOT_REPLIES[new Random().nextInt(BOT_REPLIES.length)];
    }

    // ── Keyboard ──────────────────────────────────────────────────────────────────

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
