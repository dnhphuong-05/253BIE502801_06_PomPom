package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityOrderDetailBinding;
import com.pompom.group6.databinding.ItemOrderDetailProductBinding;
import com.pompom.group6.databinding.ItemOrderStepBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.dto.ApiOrderDetail;
import com.pompom.group6.utils.StatusBarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Chi tiết một đơn hàng + timeline theo dõi tiến độ.
 * Timeline dựng từ trạng thái hiện tại và lịch sử trạng thái (status_history) trả về từ backend.
 */
public class OrderDetailActivity extends SwipeBackActivity {

    private static final String EXTRA_ORDER_ID = "order_id";

    /** Mở màn chi tiết cho một đơn theo id (chuỗi ObjectId từ backend). */
    public static void start(Context context, String orderId) {
        Intent i = new Intent(context, OrderDetailActivity.class);
        i.putExtra(EXTRA_ORDER_ID, orderId);
        context.startActivity(i);
    }

    /** Kiểu đánh dấu chấm/đường của một bước timeline. */
    private static final int MARK_NORMAL = 0; // xanh nếu đã đạt, xám nếu chưa
    private static final int MARK_GREY = 1;   // đơn đã hủy
    private static final int MARK_RED = 2;    // đơn trả hàng/hoàn tiền

    /** Các bước tiến độ chuẩn của một đơn (theo thứ tự). */
    private static final String[] STEP_LABELS = {"Chờ xác nhận", "Chờ lấy hàng", "Đang giao", "Đã giao"};
    @SuppressWarnings("unchecked")
    private static final Set<String>[] STEP_STATUSES = new Set[]{
            setOf("pending"),
            setOf("confirmed", "processing"),
            setOf("shipping"),
            setOf("delivered", "completed"),
    };

    private ActivityOrderDetailBinding binding;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Chi tiết đơn hàng");
        binding.header.btnBack.setOnClickListener(v -> finish());
        binding.btnRetry.setOnClickListener(v -> load());

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        load();
    }

    private void showState(boolean loading, boolean error) {
        if (binding == null) return;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.errorView.setVisibility(error ? View.VISIBLE : View.GONE);
        binding.contentView.setVisibility(!loading && !error ? View.VISIBLE : View.GONE);
    }

    private void load() {
        if (orderId == null) {
            showState(false, true);
            return;
        }
        showState(true, false);
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        ApiClient.get().getOrder(orderId, userOid).enqueue(new Callback<ApiOrderDetail>() {
            @Override
            public void onResponse(@NonNull Call<ApiOrderDetail> call, @NonNull Response<ApiOrderDetail> resp) {
                if (binding == null) return;
                if (!resp.isSuccessful() || resp.body() == null) {
                    showState(false, true);
                    return;
                }
                bind(resp.body());
                showState(false, false);
            }

            @Override
            public void onFailure(@NonNull Call<ApiOrderDetail> call, @NonNull Throwable t) {
                if (binding == null) return;
                showState(false, true);
            }
        });
    }

    // ------------------------------------------------------------------ binding

    private void bind(ApiOrderDetail o) {
        binding.tvDetailOrderNumber.setText(o.orderNumber != null ? o.orderNumber : "Đơn hàng");
        binding.tvDetailDate.setText("Đặt ngày " + formatDate(o.createdAt, "dd/MM/yyyy"));
        applyStatusChip(o.status);

        buildTimeline(o);
        buildItems(o);
        setupCancelButton(o.status);

        binding.tvSubtotal.setText(money(o.totalAmount));
        binding.tvShipping.setText(o.shippingFee > 0 ? money(o.shippingFee) : "Miễn phí");
        // Thuế: ẩn với đơn cũ (chưa có tax_amount) để không hiện "0đ" thừa.
        if (o.taxAmount > 0) {
            binding.rowTax.setVisibility(View.VISIBLE);
            binding.tvTax.setText(money(o.taxAmount));
        } else {
            binding.rowTax.setVisibility(View.GONE);
        }
        if (o.discountAmount > 0) {
            binding.rowDiscount.setVisibility(View.VISIBLE);
            binding.tvDiscount.setText("-" + money(o.discountAmount));
        } else {
            binding.rowDiscount.setVisibility(View.GONE);
        }
        binding.tvTotal.setText(money(o.finalAmount));
        binding.tvPayment.setText("Phương thức: " + (o.paymentMethod != null ? o.paymentMethod : "COD"));
    }

    /** Chip trạng thái: xanh khi đã giao/hoàn thành, xám khi hủy, hồng khi đang xử lý. */
    private void applyStatusChip(String status) {
        binding.tvDetailStatus.setText(statusLabel(status));
        int bg, text;
        if (isIn(status, "delivered", "completed")) {
            bg = R.drawable.bg_label_green; text = R.color.status_green;
        } else if (isIn(status, "cancelled")) {
            bg = R.drawable.bg_label_grey; text = R.color.text_secondary;
        } else if (isIn(status, "returned", "refunded")) {
            bg = R.drawable.bg_label_red; text = R.color.status_red;
        } else {
            bg = R.drawable.bg_label_pink; text = R.color.white;
        }
        binding.tvDetailStatus.setBackgroundResource(bg);
        binding.tvDetailStatus.setTextColor(color(text));
    }

    /** Dựng timeline: tô đậm (xanh) các bước đã đạt, kèm mốc thời gian từ lịch sử trạng thái. */
    private void buildTimeline(ApiOrderDetail o) {
        binding.timelineContainer.removeAllViews();
        List<ApiOrderDetail.History> history = o.statusHistory != null ? o.statusHistory : new ArrayList<>();

        boolean cancelled = isIn(o.status, "cancelled");
        boolean returned = isIn(o.status, "returned", "refunded");

        // Chỉ số bước hiện tại theo trạng thái (trả hàng coi như đã qua "Đã giao").
        int reached;
        if (returned) reached = 3;
        else if (cancelled) reached = maxReachedFromHistory(history);
        else reached = currentStepIndex(o.status);

        // Dữ liệu từng bước.
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < STEP_LABELS.length; i++) {
            boolean done = i <= reached || matchesHistory(history, STEP_STATUSES[i]);
            String time = timeForStatuses(history, STEP_STATUSES[i]);
            if (time == null && i == 0) time = formatDate(o.createdAt, "dd/MM/yyyy HH:mm");
            steps.add(new Step(STEP_LABELS[i], time, done, MARK_NORMAL));
        }
        // Bước kết thúc đặc biệt cho đơn hủy (xám) / trả hàng (đỏ).
        if (cancelled) {
            steps.add(new Step("Đã hủy", timeForStatuses(history, setOf("cancelled")), true, MARK_GREY));
        } else if (returned) {
            steps.add(new Step("Trả hàng", timeForStatuses(history, setOf("returned", "refunded")), true, MARK_RED));
        }

        for (int i = 0; i < steps.size(); i++) {
            addStepRow(steps, i);
        }
    }

    private void addStepRow(List<Step> steps, int i) {
        Step s = steps.get(i);
        ItemOrderStepBinding row = ItemOrderStepBinding.inflate(
                LayoutInflater.from(this), binding.timelineContainer, false);

        row.tvStepTitle.setText(s.title);
        row.tvStepTitle.setTextColor(color(s.done ? R.color.text_primary : R.color.text_secondary));
        if (s.time != null && !s.time.isEmpty()) {
            row.tvStepTime.setVisibility(View.VISIBLE);
            row.tvStepTime.setText(s.time);
        } else {
            row.tvStepTime.setVisibility(View.GONE);
        }

        row.dot.setBackgroundResource(dotDrawable(s));

        // Đường nối: màu theo bước ở mỗi đầu (xanh = đã đạt, đỏ = trả hàng, xám = chưa/đã hủy).
        row.lineTop.setVisibility(i == 0 ? View.INVISIBLE : View.VISIBLE);
        row.lineBottom.setVisibility(i == steps.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        if (i > 0) {
            row.lineTop.setBackgroundColor(lineColor(s));
        }
        if (i < steps.size() - 1) {
            row.lineBottom.setBackgroundColor(lineColor(steps.get(i + 1)));
        }

        binding.timelineContainer.addView(row.getRoot());
    }

    /** Drawable chấm timeline theo trạng thái bước. */
    private int dotDrawable(Step s) {
        if (s.mark == MARK_RED) return R.drawable.bg_circle_red;
        if (s.mark == MARK_GREY) return R.drawable.bg_circle_grey;
        return s.done ? R.drawable.bg_circle_green : R.drawable.bg_circle_grey;
    }

    /** Màu đường nối tương ứng một bước. */
    private int lineColor(Step s) {
        if (s.mark == MARK_RED) return color(R.color.status_red);
        if (s.mark == MARK_GREY) return color(R.color.status_grey);
        return color(s.done ? R.color.status_green : R.color.status_grey);
    }

    private void buildItems(ApiOrderDetail o) {
        binding.itemsContainer.removeAllViews();
        if (o.items == null) return;
        for (ApiOrderDetail.Item it : o.items) {
            ItemOrderDetailProductBinding row = ItemOrderDetailProductBinding.inflate(
                    LayoutInflater.from(this), binding.itemsContainer, false);
            row.tvProductName.setText(it.productName != null ? it.productName : "Sản phẩm");
            row.tvProductQtyPrice.setText("x" + it.quantity + "  ·  " + money(it.price));
            row.tvLineTotal.setText(money(it.price * it.quantity));
            Glide.with(this)
                    .load(it.productThumbnail)
                    .placeholder(R.drawable.logo_pompom)
                    .into(row.ivProduct);
            binding.itemsContainer.addView(row.getRoot());
        }
    }

    /** Chỉ cho huỷ khi đơn chưa được bàn giao vận chuyển. */
    private void setupCancelButton(String status) {
        boolean cancellable = isIn(status, "pending", "confirmed", "processing");
        binding.btnCancelOrder.setVisibility(cancellable ? View.VISIBLE : View.GONE);
        if (cancellable) {
            binding.btnCancelOrder.setOnClickListener(v -> confirmCancel());
        }
    }

    /** Hỏi xác nhận trước khi hủy đơn. */
    private void confirmCancel() {
        com.pompom.group6.utils.PomPomDialog.confirm(this, "📦", "Hủy đơn hàng",
                "Bạn có chắc muốn hủy đơn này?\nĐơn chỉ hủy được khi shop chưa xác nhận.",
                "Hủy đơn", "Không", this::doCancel);
    }

    private void doCancel() {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        java.util.Map<String, String> body = new java.util.HashMap<>();
        if (userOid != null) body.put("user_id", userOid);
        binding.btnCancelOrder.setEnabled(false);
        ApiClient.get().cancelOrder(orderId, body).enqueue(new Callback<com.pompom.group6.network.dto.ApiOrder>() {
            @Override
            public void onResponse(@NonNull Call<com.pompom.group6.network.dto.ApiOrder> call,
                                    @NonNull Response<com.pompom.group6.network.dto.ApiOrder> resp) {
                if (binding == null) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                    load();
                } else {
                    binding.btnCancelOrder.setEnabled(true);
                    Toast.makeText(OrderDetailActivity.this, "Không thể huỷ đơn ở trạng thái này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.pompom.group6.network.dto.ApiOrder> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.btnCancelOrder.setEnabled(true);
                Toast.makeText(OrderDetailActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------------------------------------------------------ helpers

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private int currentStepIndex(String status) {
        for (int i = 0; i < STEP_STATUSES.length; i++) {
            if (status != null && STEP_STATUSES[i].contains(status)) return i;
        }
        return 0;
    }

    /** Bước cao nhất từng đạt được, suy ra từ lịch sử (dùng cho đơn đã hủy). */
    private int maxReachedFromHistory(List<ApiOrderDetail.History> history) {
        int max = 0;
        for (ApiOrderDetail.History h : history) {
            for (int i = 0; i < STEP_STATUSES.length; i++) {
                if (h.status != null && STEP_STATUSES[i].contains(h.status)) max = Math.max(max, i);
            }
        }
        return max;
    }

    private boolean matchesHistory(List<ApiOrderDetail.History> history, Set<String> statuses) {
        for (ApiOrderDetail.History h : history) if (h.status != null && statuses.contains(h.status)) return true;
        return false;
    }

    /** Thời điểm (mới nhất) một trong các trạng thái xảy ra, theo lịch sử. */
    private String timeForStatuses(List<ApiOrderDetail.History> history, Set<String> statuses) {
        String result = null;
        for (ApiOrderDetail.History h : history) {
            if (h.status != null && statuses.contains(h.status)) {
                result = formatDate(h.createdAt, "dd/MM/yyyy HH:mm");
            }
        }
        return result;
    }

    private String statusLabel(String status) {
        if (status == null) return "Không rõ";
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "processing": return "Đang xử lý";
            case "shipping": return "Đang giao";
            case "delivered": return "Đã giao";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            case "returned": return "Trả hàng";
            case "refunded": return "Đã hoàn tiền";
            default: return status;
        }
    }

    private boolean isIn(String status, String... options) {
        if (status == null) return false;
        for (String o : options) if (o.equals(status)) return true;
        return false;
    }

    private String money(double amount) {
        return String.format(Locale.getDefault(), "%,.0fđ", amount);
    }

    /** Chuỗi ISO UTC ("2026-07-05T09:22:53.577Z") -> định dạng hiển thị theo giờ máy. */
    private String formatDate(String iso, String pattern) {
        if (iso == null || iso.length() < 19) return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = in.parse(iso.substring(0, 19));
            if (d == null) return "";
            return new SimpleDateFormat(pattern, Locale.getDefault()).format(d);
        } catch (Exception e) {
            return "";
        }
    }

    private int color(int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /** Một mốc trên timeline. */
    private static class Step {
        final String title;
        final String time;
        final boolean done;
        final int mark; // MARK_NORMAL / MARK_GREY (hủy) / MARK_RED (trả hàng)

        Step(String title, String time, boolean done, int mark) {
            this.title = title;
            this.time = time;
            this.done = done;
            this.mark = mark;
        }
    }
}
