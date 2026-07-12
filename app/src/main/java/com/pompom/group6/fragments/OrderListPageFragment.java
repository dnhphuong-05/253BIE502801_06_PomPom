package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.activities.CartActivity;
import com.pompom.group6.activities.OrderReviewActivity;
import com.pompom.group6.adapters.OrderAdapter;
import com.pompom.group6.databinding.FragmentOrderListPageBinding;
import com.pompom.group6.models.Order;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiOrder;
import com.pompom.group6.network.dto.CartItemRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Một trang tab trong màn "Đơn hàng của tôi".
 * tabType: 0=Tất cả, 1=Chờ xác nhận, 2=Chờ lấy hàng, 3=Chờ giao hàng, 4=Đã giao, 5=Trả hàng, 6=Đã huỷ.
 */
public class OrderListPageFragment extends Fragment {

    private static final String ARG_TAB_TYPE = "tab_type";

    public static final int TAB_ALL = 0;
    public static final int TAB_PENDING = 1;
    public static final int TAB_PACKING = 2;
    public static final int TAB_SHIPPING = 3;
    public static final int TAB_DELIVERED = 4;
    public static final int TAB_RETURN = 5;
    public static final int TAB_CANCELLED = 6;

    private FragmentOrderListPageBinding binding;
    private OrderAdapter adapter;
    private int tabType;
    private String userOid;

    public static OrderListPageFragment newInstance(int tabType) {
        OrderListPageFragment fragment = new OrderListPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_TYPE, tabType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabType = getArguments() != null ? getArguments().getInt(ARG_TAB_TYPE, TAB_ALL) : TAB_ALL;

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderAdapter();
        adapter.setOnOrderActionListener(new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onReview(Order order) {
                if (order.getOid() == null) return;
                OrderReviewActivity.start(requireContext(), order.getOid());
            }

            @Override
            public void onRebuy(Order order) {
                rebuy(order);
            }

            @Override
            public void onCancel(Order order) {
                confirmCancel(order);
            }
        });
        binding.rvOrders.setAdapter(adapter);
        binding.emptyState.tvEmptyText.setText(emptyMessage(tabType));
        applyEmptyStateIllustration();

        userOid = Session.getUserOid(requireContext());
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
        }
    }

    /** Nguồn nạp dữ liệu duy nhất: chạy lần đầu hiện tab và mỗi lần quay lại tab
     * (VD: sau khi đánh giá/huỷ đơn ở màn khác) để nút hành động cập nhật đúng. */
    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && userOid != null) {
            load(userOid, tabType);
        }
    }

    private void load(String userOid, int tabType) {
        ApiClient.get().getOrders(userOid).enqueue(new Callback<List<ApiOrder>>() {
            @Override
            public void onResponse(Call<List<ApiOrder>> call, Response<List<ApiOrder>> resp) {
                if (binding == null) return;
                List<Order> orders = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    Set<String> filter = statusesFor(tabType);
                    for (ApiOrder o : resp.body()) {
                        if (filter != null && (o.status == null || !filter.contains(o.status))) continue;
                        orders.add(new Order(0, o.id, o.orderNumber, o.finalAmount, o.status,
                                o.paymentMethod, o.createdAt, o.itemCount, o.firstItemName, o.firstItemImage,
                                o.isReviewed, o.reorderItems));
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

    /** include_empty_state.xml mặc định là icon nhỏ tô màu hồng — đơn hàng dùng hẳn tranh minh hoạ
     * logistics đầy màu, cần bỏ tint + phóng to để không bị bóp méo thành chấm hồng nhạt. */
    private void applyEmptyStateIllustration() {
        android.widget.ImageView icon = binding.emptyState.ivEmptyIcon;
        icon.setImageResource(R.drawable.logistics);
        icon.setImageTintList(null);
        icon.setAlpha(1f);
        int size = (int) (150 * getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams lp = icon.getLayoutParams();
        lp.width = size;
        lp.height = size;
        icon.setLayoutParams(lp);
    }

    private void confirmCancel(Order order) {
        if (order.getOid() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Huỷ đơn hàng?")
                .setMessage("Bạn có chắc chắn muốn huỷ đơn hàng này không?")
                .setPositiveButton("Huỷ đơn", (dialog, which) -> doCancel(order))
                .setNegativeButton("Không", null)
                .show();
    }

    private void doCancel(Order order) {
        if (userOid == null) return;
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("user_id", userOid);
        ApiClient.get().cancelOrder(order.getOid(), body).enqueue(new Callback<ApiOrder>() {
            @Override
            public void onResponse(Call<ApiOrder> call, Response<ApiOrder> resp) {
                if (binding == null) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                    load(userOid, tabType);
                } else {
                    Toast.makeText(requireContext(), "Không thể huỷ đơn ở trạng thái này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiOrder> call, Throwable t) {
                if (binding == null) return;
                Toast.makeText(requireContext(), "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Bộ trạng thái gốc ứng với từng tab; null = không lọc (Tất cả). */
    @Nullable
    private Set<String> statusesFor(int tabType) {
        switch (tabType) {
            case TAB_PENDING: return setOf("pending");
            case TAB_PACKING: return setOf("confirmed", "processing");
            case TAB_SHIPPING: return setOf("shipping");
            case TAB_DELIVERED: return setOf("delivered", "completed");
            case TAB_RETURN: return setOf("returned", "refunded");
            case TAB_CANCELLED: return setOf("cancelled");
            case TAB_ALL:
            default: return null;
        }
    }

    private String emptyMessage(int tabType) {
        switch (tabType) {
            case TAB_PENDING: return "Không có đơn chờ xác nhận";
            case TAB_PACKING: return "Không có đơn chờ lấy hàng";
            case TAB_SHIPPING: return "Không có đơn chờ giao hàng";
            case TAB_DELIVERED: return "Không có đơn đã giao";
            case TAB_RETURN: return "Không có đơn trả hàng";
            case TAB_CANCELLED: return "Không có đơn đã huỷ";
            case TAB_ALL:
            default: return "Bạn chưa có đơn hàng nào";
        }
    }

    private Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    /** Thêm lại từng sản phẩm của đơn vào giỏ (nối tiếp) rồi mở màn Giỏ hàng. */
    private void rebuy(Order order) {
        String userOid = Session.getUserOid(requireContext());
        if (userOid == null || order.getReorderItems() == null || order.getReorderItems().isEmpty()) {
            Toast.makeText(requireContext(), "Không có sản phẩm để mua lại", Toast.LENGTH_SHORT).show();
            return;
        }
        addNext(userOid, order.getReorderItems(), 0);
    }

    private void addNext(String userOid, List<ApiOrder.ReorderItem> items, int index) {
        if (index >= items.size()) {
            if (binding == null) return;
            Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), CartActivity.class));
            return;
        }
        ApiOrder.ReorderItem item = items.get(index);
        ApiClient.get().addCartItem(new CartItemRequest(userOid, item.productId, Math.max(1, item.quantity)))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> resp) {
                        addNext(userOid, items, index + 1);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        addNext(userOid, items, index + 1);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
