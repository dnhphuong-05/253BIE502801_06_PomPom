package com.pompom.group6.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.ItemProductCartBinding;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.models.Product;
import com.pompom.group6.models.ProductVariant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemove(CartItem item);
    }

    /** Thông báo số lượng sản phẩm đang được chọn thay đổi. */
    public interface SelectionListener {
        void onSelectionChanged(int selectedCount);
    }

    /** Một sản phẩm đã bị xoá tạm (kèm vị trí cũ) để hoàn tác. */
    public static class RemovedEntry {
        public final int index;
        public final CartItem item;
        public RemovedEntry(int index, CartItem item) { this.index = index; this.item = item; }
    }

    private final List<CartItem> items;
    private final CartItemListener listener;
    private final Set<Integer> expandedIds = new HashSet<>();
    private final Map<Integer, String> selectedVariant = new HashMap<>();

    private boolean selectionEnabled = false;
    private final Set<Integer> selectedIds = new HashSet<>();
    private SelectionListener selectionListener;

    public CartItemAdapter(List<CartItem> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /** Cập nhật dữ liệu hiển thị (fix: line không biến mất sau khi xoá). */
    public void setItems(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        // bỏ chọn những id không còn tồn tại
        selectedIds.retainAll(collectIds());
        notifyDataSetChanged();
    }

    public List<CartItem> getItems() { return items; }

    public void setSelectionEnabled(boolean enabled) {
        this.selectionEnabled = enabled;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener l) { this.selectionListener = l; }

    // ── Selection helpers ─────────────────────────────────────────────────────────

    private Set<Integer> collectIds() {
        Set<Integer> ids = new HashSet<>();
        for (CartItem it : items) ids.add(it.getProductId());
        return ids;
    }

    private void toggleSelection(int productId) {
        if (selectedIds.contains(productId)) selectedIds.remove(productId);
        else selectedIds.add(productId);
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(selectedIds.size());
    }

    public void selectAll() {
        selectedIds.clear();
        selectedIds.addAll(collectIds());
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(selectedIds.size());
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(0);
    }

    public int getSelectedCount() { return selectedIds.size(); }
    public boolean isAllSelected() { return !items.isEmpty() && selectedIds.size() == items.size(); }

    /** Xoá tạm các sản phẩm đang chọn khỏi danh sách, trả về entries để hoàn tác. */
    public List<RemovedEntry> removeSelected() {
        java.util.List<RemovedEntry> removed = new java.util.ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            if (selectedIds.contains(items.get(i).getProductId())) {
                removed.add(new RemovedEntry(i, items.get(i)));
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
        selectedIds.clear();
        if (selectionListener != null) selectionListener.onSelectionChanged(0);
        return removed;
    }

    /** Khôi phục các sản phẩm đã xoá tạm về đúng vị trí cũ. */
    public void restore(List<RemovedEntry> entries) {
        if (entries == null) return;
        // chèn theo thứ tự index tăng dần
        java.util.List<RemovedEntry> sorted = new java.util.ArrayList<>(entries);
        java.util.Collections.sort(sorted, (a, b) -> Integer.compare(a.index, b.index));
        for (RemovedEntry e : sorted) {
            int idx = Math.min(e.index, items.size());
            items.add(idx, e.item);
            notifyItemInserted(idx);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductCartBinding binding = ItemProductCartBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        Context ctx = holder.itemView.getContext();

        // ── Image ────────────────────────────────────────────────────────────────
        Glide.with(ctx)
                .load(item.getImageUrl())
                .placeholder(android.R.color.transparent)
                .error(com.pompom.group6.R.drawable.favicon_pompom)
                .into(holder.binding.ivCartProduct);

        // ── Title ────────────────────────────────────────────────────────────────
        holder.binding.tvCartTitle.setText(item.getTitle());

        // ── Variant label ────────────────────────────────────────────────────────
        String vname = selectedVariant.get(item.getProductId());
        holder.binding.tvCartVariant.setText("Màu sắc: " + (vname != null ? vname : "Mặc định"));

        // ── Quantity ─────────────────────────────────────────────────────────────
        holder.binding.tvCartQuantity.setText(String.valueOf(item.getQuantity()));

        // ── Dynamic price (unit × qty) ────────────────────────────────────────────
        updatePrice(holder, item);

        // ── Minus button ─────────────────────────────────────────────────────────
        holder.binding.btnCartMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            if (newQty <= 0) {
                confirmDelete(ctx, item);
            } else {
                if (listener != null) listener.onQuantityChanged(item, newQty);
            }
        });

        // ── Plus button ──────────────────────────────────────────────────────────
        holder.binding.btnCartPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            if (listener != null) listener.onQuantityChanged(item, newQty);
        });

        // ── Delete button ────────────────────────────────────────────────────────
        holder.binding.btnDeleteItem.setOnClickListener(v -> confirmDelete(ctx, item));

        // ── Chọn nhiều (checkbox + long-click) + highlight ──
        // Checkbox CHỈ hiện khi line được chọn (long-click để chọn, bỏ tick để ẩn).
        boolean selected = selectedIds.contains(item.getProductId());
        holder.binding.cbSelect.setVisibility(selected ? View.VISIBLE : View.GONE);
        holder.binding.cbSelect.setOnCheckedChangeListener(null);
        holder.binding.cbSelect.setChecked(selected);
        holder.binding.cbSelect.setOnClickListener(v -> toggleSelection(item.getProductId()));

        MaterialCardView card = (MaterialCardView) holder.itemView;
        if (selected) {
            card.setCardBackgroundColor(Color.parseColor("#FFF0F5"));
            card.setStrokeColor(ContextCompat.getColor(ctx, R.color.brand_pink));
        } else {
            card.setCardBackgroundColor(Color.WHITE);
            card.setStrokeColor(Color.parseColor("#F5E6EE"));
        }

        holder.binding.mainContent.setOnLongClickListener(selectionEnabled ? v -> {
            toggleSelection(item.getProductId());
            return true;
        } : null);

        // ── Ô thông tin xổ ra khi click line (đẩy line khác xuống) ──
        boolean expanded = expandedIds.contains(item.getProductId());
        holder.binding.expandBox.setVisibility(expanded ? View.VISIBLE : View.GONE);
        if (expanded) populateExpand(holder, item, ctx);

        holder.binding.mainContent.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            int pid = item.getProductId();
            if (expandedIds.contains(pid)) expandedIds.remove(pid);
            else expandedIds.add(pid);
            notifyItemChanged(pos);
        });
    }

    /** Nạp thông tin cơ bản + danh sách biến thể màu vào ô xổ ra. */
    private void populateExpand(@NonNull CartViewHolder holder, CartItem item, Context ctx) {
        // Sản phẩm cloud (có ObjectId) → lấy mô tả/danh mục từ MongoDB.
        if (item.getProductOid() != null) {
            com.pompom.group6.network.ApiClient.get().getProduct(item.getProductOid())
                    .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiProduct>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call,
                                               retrofit2.Response<com.pompom.group6.network.dto.ApiProduct> resp) {
                            if (!resp.isSuccessful() || resp.body() == null) {
                                holder.binding.tvExpandDesc.setText("Không tải được thông tin sản phẩm.");
                                holder.binding.tvExpandMeta.setText("");
                                return;
                            }
                            com.pompom.group6.network.dto.ApiProduct a = resp.body();
                            holder.binding.tvExpandDesc.setText(a.description != null ? a.description : "Chưa có mô tả cho sản phẩm này.");
                            String cat = a.categoryName != null ? a.categoryName : "";
                            String brand = a.brand != null ? a.brand : "";
                            String meta = cat;
                            if (!brand.isEmpty()) meta += (meta.isEmpty() ? "" : "  •  ") + brand;
                            holder.binding.tvExpandMeta.setText(meta);
                        }
                        @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call, Throwable t) {
                            holder.binding.tvExpandDesc.setText("Không tải được thông tin sản phẩm.");
                            holder.binding.tvExpandMeta.setText("");
                        }
                    });
            return;
        }
        // Sản phẩm local (id số) → SQLite.
        ProductDAO dao = new ProductDAO(ctx);
        Product p = dao.getProductById(item.getProductId());
        if (p != null) {
            holder.binding.tvExpandDesc.setText(
                    p.getDescription() != null ? p.getDescription() : "Chưa có mô tả cho sản phẩm này.");
            String cat = p.getCategoryName() != null ? p.getCategoryName() : "";
            String brand = p.getBrandName() != null ? p.getBrandName() : "";
            String meta = cat;
            if (!brand.isEmpty()) meta += (meta.isEmpty() ? "" : "  •  ") + brand;
            holder.binding.tvExpandMeta.setText(meta);
        } else {
            holder.binding.tvExpandDesc.setText("Không tải được thông tin sản phẩm.");
            holder.binding.tvExpandMeta.setText("");
        }

        List<ProductVariant> variants = dao.getVariantsForProduct(item.getProductId());
        if (variants != null && !variants.isEmpty()) {
            holder.binding.rvExpandVariants.setVisibility(View.VISIBLE);
            VariantAdapter va = new VariantAdapter(variants, variant -> {
                selectedVariant.put(item.getProductId(), variant.getName());
                holder.binding.tvCartVariant.setText("Màu sắc: " + variant.getName());
            });
            holder.binding.rvExpandVariants.setLayoutManager(
                    new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            holder.binding.rvExpandVariants.setAdapter(va);
        } else {
            holder.binding.rvExpandVariants.setVisibility(View.GONE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private void updatePrice(@NonNull CartViewHolder holder, CartItem item) {
        long unitPrice = parsePriceLong(item.getPrice());
        long total = unitPrice * item.getQuantity();
        holder.binding.tvCartItemPrice.setText(formatPriceSpan(total));
    }

    private long parsePriceLong(String priceStr) {
        if (priceStr == null) return 0;
        String digits = priceStr.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        try { return Long.parseLong(digits); } catch (NumberFormatException e) { return 0; }
    }

    private SpannableString formatPriceSpan(long value) {
        String formatted = String.format(Locale.US, "%,d", value).replace(",", ".");
        String full = formatted + "đ";
        SpannableString span = new SpannableString(full);
        span.setSpan(new RelativeSizeSpan(0.65f), full.length() - 1, full.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    private void confirmDelete(Context ctx, CartItem item) {
        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_remove_item, null);
        ((TextView) content.findViewById(R.id.tvRemoveMsg))
                .setText("Bạn có chắc muốn xoá \"" + item.getTitle() + "\" khỏi giỏ hàng?");
        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(content).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        content.findViewById(R.id.btnCancelRemove).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btnConfirmRemove).setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onRemove(item);
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────────

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ItemProductCartBinding binding;

        CartViewHolder(ItemProductCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
