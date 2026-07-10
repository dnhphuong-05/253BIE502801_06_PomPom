package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.OrderAdapter;
import com.pompom.group6.utils.PomPomDialog;
import com.pompom.group6.databinding.ActivityOrdersBinding;
import com.pompom.group6.models.Order;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiOrder;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends SwipeBackActivity {

    /** Nhóm trạng thái cần lọc: "pending" | "packing" | "shipping" | "delivered" | "return". */
    public static final String EXTRA_STATUS_GROUP = "status_group";

    private ActivityOrdersBinding binding;
    /** Tập trạng thái gốc cần hiển thị; null = hiện tất cả. */
    private Set<String> statusFilter;
    /** Adapter tạo sẵn trong onCreate để RecyclerView layout ngay, tránh phải cuộn mới hiện. */
    private OrderAdapter adapter;
    /** Id user hiện tại (dùng để tải & hủy đơn). */
    private String userOid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        String group = getIntent().getStringExtra(EXTRA_STATUS_GROUP);
        applyStatusGroup(group);
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        adapter.setOnOrderCancel(this::confirmCancel);
        binding.rvOrders.setAdapter(adapter);
        binding.emptyState.tvEmptyText.setText(statusFilter == null
                ? "Bạn chưa có đơn hàng nào"
                : "Không có đơn hàng ở trạng thái này");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_bag);

        userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        loadOrders(userOid);
    }

    /** Hỏi xác nhận trước khi hủy đơn "Chờ xác nhận". */
    private void confirmCancel(Order order) {
        PomPomDialog.confirm(this, "📦", "Hủy đơn hàng",
                "Bạn có chắc muốn hủy đơn " + order.getOrderNumber()
                        + "?\nĐơn chỉ hủy được khi shop chưa xác nhận.",
                "Hủy đơn", "Không", () -> doCancel(order));
    }

    /** Gọi backend hủy đơn; thành công -> tải lại danh sách để cập nhật trạng thái. */
    private void doCancel(Order order) {
        if (order.getOid() == null || userOid == null) return;
        ApiClient.get().cancelOrder(order.getOid(), userOid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (binding == null) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(OrdersActivity.this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    loadOrders(userOid);
                } else {
                    Toast.makeText(OrdersActivity.this,
                            "Không thể hủy — đơn có thể đã được xác nhận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (binding == null) return;
                Toast.makeText(OrdersActivity.this, "Lỗi kết nối, thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Đặt tiêu đề + tập trạng thái theo nhóm được truyền vào. */
    private void applyStatusGroup(String group) {
        String title = "Đơn hàng của tôi";
        if (group != null) {
            switch (group) {
                case "pending":
                    statusFilter = setOf("pending"); title = "Chờ xác nhận"; break;
                case "packing":
                    statusFilter = setOf("confirmed", "processing"); title = "Chờ lấy hàng"; break;
                case "shipping":
                    statusFilter = setOf("shipping"); title = "Đang giao"; break;
                case "delivered":
                    statusFilter = setOf("delivered", "completed"); title = "Đã giao"; break;
                case "return":
                    statusFilter = setOf("returned", "refunded"); title = "Trả hàng"; break;
                default:
                    statusFilter = null; break;
            }
        }
        binding.header.tvHeaderTitle.setText(title);
    }

    private Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    /** Danh sách đơn hàng của user (từ MongoDB). */
    private void loadOrders(String userOid) {
        ApiClient.get().getOrders(userOid).enqueue(new Callback<List<ApiOrder>>() {
            @Override
            public void onResponse(Call<List<ApiOrder>> call, Response<List<ApiOrder>> resp) {
                if (binding == null) return;
                List<Order> orders = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiOrder o : resp.body()) {
                        // Lọc theo nhóm trạng thái nếu người dùng vào từ shortcut trạng thái.
                        if (statusFilter != null && (o.status == null || !statusFilter.contains(o.status))) {
                            continue;
                        }
                        orders.add(new Order(0, o.id, o.orderNumber, o.finalAmount, o.status,
                                o.paymentMethod, o.createdAt, o.itemCount, o.firstItemName, o.firstItemImage));
                    }
                }
                adapter.setOrders(orders);
                binding.emptyState.getRoot().setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiOrder>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
