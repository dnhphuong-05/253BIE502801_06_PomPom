package com.pompom.group6.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pompom.group6.models.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "pompom_cart";
    private static final String KEY_CART = "cart_items";

    private static CartManager instance;
    private final SharedPreferences prefs;
    private List<CartItem> cartItems;

    private CartManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cartItems = loadFromPrefs();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addItem(CartItem newItem) {
        // Gộp theo DÒNG (sản phẩm + biến thể): cùng sản phẩm khác biến thể -> dòng riêng.
        for (CartItem item : cartItems) {
            if (item.getLineKey().equals(newItem.getLineKey())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                saveToPrefs();
                notifyListeners();
                return;
            }
        }
        cartItems.add(newItem);
        saveToPrefs();
        notifyListeners();
    }

    /** Xoá đúng một dòng (theo sản phẩm + biến thể). */
    public void removeItem(CartItem target) {
        if (target == null) return;
        String key = target.getLineKey();
        cartItems.removeIf(item -> item.getLineKey().equals(key));
        saveToPrefs();
        notifyListeners();
    }

    /** Cập nhật số lượng đúng một dòng (theo sản phẩm + biến thể); newQty<=0 thì xoá dòng. */
    public void updateQuantity(CartItem target, int newQty) {
        if (target == null) return;
        String key = target.getLineKey();
        for (CartItem item : cartItems) {
            if (item.getLineKey().equals(key)) {
                if (newQty <= 0) {
                    removeItem(target);
                    return;
                }
                item.setQuantity(newQty);
                saveToPrefs();
                notifyListeners();
                return;
            }
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(cartItems);
    }

    public int getTotalCount() {
        int count = 0;
        for (CartItem item : cartItems) count += item.getQuantity();
        return count;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) total += item.getSubtotal();
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        saveToPrefs();
        notifyListeners();
    }

    /** Thay toàn bộ giỏ bằng danh sách mới (dùng khi khôi phục giỏ từ server). */
    public void replaceAll(List<CartItem> items) {
        cartItems = new ArrayList<>(items);
        saveToPrefs();
        notifyListeners();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // ─── Persistence ────────────────────────────────────────────────────────────

    private void saveToPrefs() {
        try {
            JSONArray array = new JSONArray();
            for (CartItem item : cartItems) {
                JSONObject obj = new JSONObject();
                obj.put("productId", item.getProductId());
                obj.put("productOid", item.getProductOid() != null ? item.getProductOid() : "");
                obj.put("title", item.getTitle());
                obj.put("price", item.getPrice());
                obj.put("imageUrl", item.getImageUrl() != null ? item.getImageUrl() : "");
                obj.put("quantity", item.getQuantity());
                obj.put("variantId", item.getVariantId() != null ? item.getVariantId() : "");
                obj.put("variantName", item.getVariantName() != null ? item.getVariantName() : "");
                array.put(obj);
            }
            prefs.edit().putString(KEY_CART, array.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<CartItem> loadFromPrefs() {
        List<CartItem> list = new ArrayList<>();
        String json = prefs.getString(KEY_CART, null);
        if (json == null) return list;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                CartItem ci = new CartItem(
                        obj.getInt("productId"),
                        obj.getString("title"),
                        obj.getString("price"),
                        obj.optString("imageUrl", ""),
                        obj.getInt("quantity")
                );
                String oid = obj.optString("productOid", "");
                if (!oid.isEmpty()) ci.setProductOid(oid);
                String variantId = obj.optString("variantId", "");
                if (!variantId.isEmpty()) ci.setVariantId(variantId);
                String variantName = obj.optString("variantName", "");
                if (!variantName.isEmpty()) ci.setVariantName(variantName);
                list.add(ci);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─── Listener ───────────────────────────────────────────────────────────────

    public interface CartChangeListener {
        void onCartChanged(int totalCount);
    }

    private final List<CartChangeListener> listeners = new ArrayList<>();

    public void addListener(CartChangeListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(CartChangeListener l) {
        listeners.remove(l);
    }

    private void notifyListeners() {
        int count = getTotalCount();
        for (CartChangeListener l : listeners) l.onCartChanged(count);
    }
}
