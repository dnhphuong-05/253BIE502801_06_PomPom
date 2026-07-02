package com.pompom.group6.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pompom.group6.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @SuppressLint("Range")
    public List<Order> getOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT o.order_id, o.order_number, o.final_amount, o.status, o.payment_method, o.created_at, " +
                "(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count, " +
                "(SELECT p.name FROM order_items oi JOIN products p ON oi.product_id = p.product_id " +
                "   WHERE oi.order_id = o.order_id LIMIT 1) AS first_name, " +
                "(SELECT COALESCE(p.thumbnail_url, (SELECT image_url FROM product_images pi " +
                "     WHERE pi.product_id = p.product_id ORDER BY sort_order ASC LIMIT 1)) " +
                "   FROM order_items oi JOIN products p ON oi.product_id = p.product_id " +
                "   WHERE oi.order_id = o.order_id LIMIT 1) AS first_img " +
                "FROM orders o WHERE o.user_id = ? ORDER BY o.created_at DESC";
        try (Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                orders.add(new Order(
                        c.getInt(c.getColumnIndex("order_id")),
                        c.getString(c.getColumnIndex("order_number")),
                        c.getDouble(c.getColumnIndex("final_amount")),
                        c.getString(c.getColumnIndex("status")),
                        c.getString(c.getColumnIndex("payment_method")),
                        c.getString(c.getColumnIndex("created_at")),
                        c.getInt(c.getColumnIndex("item_count")),
                        c.getString(c.getColumnIndex("first_name")),
                        c.getString(c.getColumnIndex("first_img"))));
            }
        } catch (Exception e) {
            android.util.Log.e("OrderDAO", "getOrders error: " + e.getMessage());
        }
        return orders;
    }

    public int getOrderCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM orders WHERE user_id = ?",
                new String[]{String.valueOf(userId)})) {
            if (c.moveToFirst()) return c.getInt(0);
        } catch (Exception e) {
            android.util.Log.e("OrderDAO", "getOrderCount error: " + e.getMessage());
        }
        return 0;
    }
}
